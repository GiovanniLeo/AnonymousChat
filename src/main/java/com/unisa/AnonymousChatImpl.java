package com.unisa;

import net.tomp2p.dht.FutureGet;
import net.tomp2p.dht.PeerBuilderDHT;
import net.tomp2p.dht.PeerDHT;
import net.tomp2p.futures.BaseFutureAdapter;
import net.tomp2p.futures.FutureBootstrap;
import net.tomp2p.futures.FutureDirect;
import net.tomp2p.p2p.Peer;
import net.tomp2p.p2p.PeerBuilder;
import net.tomp2p.peers.Number160;
import net.tomp2p.peers.PeerAddress;
import net.tomp2p.rpc.ObjectDataReply;
import net.tomp2p.storage.Data;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class AnonymousChatImpl implements AnonymousChat {
    final private Peer peer;
    final private PeerDHT _dht;
    final private int DEFAULT_MASTER_PORT = 4000;
    private ArrayList<String> registeredRooms;
    private MessageListener listener;

    public AnonymousChatImpl(int _id, String _master_peer, MessageListener _listener) throws IOException {
        this.registeredRooms = new ArrayList<String>();
        this.listener = _listener;
        this.peer = new PeerBuilder(Number160.createHash(_id)).ports(DEFAULT_MASTER_PORT + _id).start();
        this._dht = new PeerBuilderDHT(peer).start();
        FutureBootstrap fb = peer.bootstrap().inetAddress(InetAddress.getByName(_master_peer)).ports(DEFAULT_MASTER_PORT).start();
        fb.awaitUninterruptibly();
        if (fb.isSuccess()) {
            peer.discover().peerAddress(fb.bootstrapTo().iterator().next()).start().awaitUninterruptibly();
        }

        peer.objectDataReply(new ObjectDataReply() {
            public Object reply(PeerAddress sender, Object request) throws Exception {
                Message message = (Message) request;
                if (!peer.peerID().equals(message.getDestination().peerId())) {
                    //if I'm not the destination it means that I have to forward again
                    sendMessageToPeer(message, message.getDestination());
                    return null;
                } else {
                    //if I'm the destination I only have to parse message
                    return listener.parseMessage(message);
                }

            }

        });
        Room forwarderRoom;

        try {
            FutureGet f = _dht.get(Number160.createHash("forwarderRoom")).start();
            f.awaitUninterruptibly();
            if (f.isSuccess() && !f.isEmpty()) {
                forwarderRoom = (Room) f.dataMap().values().iterator().next().object();
                if (forwarderRoom.getPeers().size() < 10)
                    joinRoom("forwarderRoom");
            } else if (f.isSuccess() && f.isEmpty()) {
                createRoom("forwarderRoom");
                joinRoom("forwarderRoom");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean createRoom(String _room_name) {
        try {
            Room room = new Room(_room_name);
            FutureGet futureGet = _dht.get(Number160.createHash(_room_name)).start();
            futureGet.awaitUninterruptibly();
            if (futureGet.isSuccess() && futureGet.isEmpty()) {
                _dht.put(Number160.createHash(_room_name))
                        .data(new Data(room))
                        .start()
                        .awaitUninterruptibly();

                return true;
            }
        } catch (Exception e) {
            return false;
        }

        return false;
    }

    @Override
    public boolean joinRoom(String _room_name) {
        try {
            FutureGet futureGet = _dht.get(Number160.createHash(_room_name)).start();
            futureGet.awaitUninterruptibly();
            if (futureGet.isSuccess() && !futureGet.isEmpty()) {
                Room room = (Room) futureGet.dataMap()
                        .values()
                        .iterator()
                        .next()
                        .object();

                //The peer will join in the room only if he had not yet joined
                if (!room.getPeers().contains(peer.peerAddress())) {
                    room.addPeer(this.peer.peerAddress());
                    _dht.put(Number160.createHash(_room_name))
                            .data(new Data(room))
                            .start()
                            .awaitUninterruptibly();
                    this.registeredRooms.add(_room_name);
                    return true;
                }
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    @Override
    public boolean leaveRoom(String _room_name) {
        Room room;
        try {
            FutureGet futureGet = _dht.get(Number160.createHash(_room_name)).start();
            futureGet.awaitUninterruptibly();
            if (futureGet.isSuccess()) {
                room = (Room) futureGet.dataMap().values().iterator().next().object();
                if (room.getPeers().contains(peer.peerAddress())) {
                    room.removePeer(peer.peerAddress());
                    _dht.put(Number160.createHash(_room_name)).data((new Data(room))).start().awaitUninterruptibly();
                    registeredRooms.remove(_room_name);
                    return true;
                } else {
                    return false;
                }
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    public ArrayList<String> getRegisteredRooms() {
        return registeredRooms;
    }

    @Override
    public boolean sendMessage(String _room_name, String _text_message) {
        Message message = new Message(_text_message, _room_name);
        PeerAddress peerForwarder;
        try {
            FutureGet futureGet = _dht.get(Number160.createHash(_room_name)).start();
            futureGet.awaitUninterruptibly();
            if (futureGet.isSuccess()) {
                Room room = (Room) futureGet.dataMap().values().iterator().next().object();
                if (!room.getPeers().contains(peer.peerAddress()) || room.getPeers().size()<2) {
                    //I can't send message if i'm not in that room
                    System.out.println("He is'nt in the room "+_room_name);
                    return false;
                }
                else {
                    HashSet<PeerAddress> usersInRoom = room.getPeers();
                    for (PeerAddress peerToSend : usersInRoom) {
                        //for each peer in room
                        if (usersInRoom.size() > 2) {
                            //I'm looking for a peer in room that will forward message
                            if (!(peer.peerID().equals(peerToSend.peerId()))) {
                                //if I'm not the peer destination
                                peerForwarder = room.getForwarderPeer(peer.peerAddress(), peerToSend);
                                //perForwarder is the peer that will forward message
                                message.setDestination(peerToSend);
                                sendMessageToPeer(message, peerForwarder);
                                //in peerForwarder there is the peer that will forward message
                            }
                        } else if (usersInRoom.size() == 2) {
                            if (!(peer.peerID().equals(peerToSend.peerId()))) {
                                //I'm looking for a peer in "external room" that will forward message
                                try {
                                    FutureGet futureGetExternalRoom = _dht.get(Number160.createHash("forwarderRoom")).start();
                                    futureGetExternalRoom.awaitUninterruptibly();
                                    if (futureGetExternalRoom.isSuccess()) {
                                        Room externalRoom = (Room) futureGetExternalRoom.dataMap().values().iterator().next().object();
                                        peerForwarder = externalRoom.getForwarderPeer(peer.peerAddress(), peerToSend);
                                        message.setDestination(peerToSend);
                                        sendMessageToPeer(message, peerForwarder);
                                    }
                                } catch (Exception e) {
                                    return false;
                                }

                            }

                        }
                    }
                    return true;
                }
            }


        } catch (Exception e) {
            return false;
        }
        return false;
    }

    private void sendMessageToPeer(Message message, final PeerAddress peerForwarder) {
        FutureDirect futureDirect = _dht.peer().sendDirect(peerForwarder).object(message).start();
        futureDirect.addListener(new BaseFutureAdapter<FutureDirect>() {
            public void operationComplete(FutureDirect future) throws Exception {
                if (!future.isSuccess()) {
                    System.out.println("Send error");
                }
            }
        });
    }

    public boolean leaveNetwork() {
        boolean fault = true;


        for (int i = 0; i < registeredRooms.size(); i++) {
            boolean error = leaveRoom(registeredRooms.get(i));
            if (!error) fault = false;
        }

        if (!fault) return false;
        System.out.println("Shutdown start");
        try {
            _dht.peer().announceShutdown().start().awaitUninterruptibly();
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public boolean destroyRoom(String _room_name) {
        try {
            FutureGet futureGet = _dht.get(Number160.createHash(_room_name)).start();
            futureGet.awaitUninterruptibly();
            if (futureGet.isSuccess()) {
                Room room = (Room) futureGet.dataMap()
                        .values()
                        .iterator()
                        .next()
                        .object();
                if (room.getPeers().size() == 1) {
                    Object[] peersInRoom = room.getPeers().toArray();
                    PeerAddress peerInRoom = (PeerAddress) peersInRoom[0];

                    if (peer.peerAddress().equals(peerInRoom)) {
                        //Fist the peer leave the room then destroy it
                        leaveRoom(_room_name);
                        try {
                            _dht.remove(Number160.createHash(_room_name))
                                    .start()
                                    .awaitUninterruptibly();
                            return true;
                        } catch (Exception e) {
                            return false;
                        }
                    }
                }
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }


}
