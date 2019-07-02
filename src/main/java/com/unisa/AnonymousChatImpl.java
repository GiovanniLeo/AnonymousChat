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

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class AnonymousChatImpl implements AnonymousChat {
    private Peer peer;
    final private PeerDHT _dht;
    final private int DEFAULT_MASTER_PORT = 4000;
    private List<String> registeredRooms;

    public AnonymousChatImpl(int _id, String _master_peer, final MessageListener _listener) throws IOException {
        this.registeredRooms = new ArrayList<String>();
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
                    //se io peer non sono il peer destinazione, vuol dire che sono un peer forwarder
                    //dunque chiamo ancora il metodo sendMessageToPeer per inviarlo al peer destinazione
                    sendMessageToPeer(message, message.getDestination());
                    return null;
                } else {
                    //se io sono il peer destinazione non devo fare altro che il parse del messaggio ricevuto;
                    return _listener.parseMessage(message);
                }

            }

        });
    }

    @Override
    public boolean createRoom(String _room_name) {
        return false;
    }

    @Override
    public boolean joinRoom(String _room_name) {
        return false;
    }

    @Override
    public boolean leaveRoom(String _room_name) {
        return false;
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
                if (!room.getPeers().contains(peer.peerAddress()))
                    //vuol dire che sto provando ad inviare un messaggio in una room in cui non ci sono
                    return false;
                else {
                    HashSet<PeerAddress> usersInRoom = room.getPeers();
                    for (PeerAddress peerToSend : usersInRoom) {
                        //per tutti i peer presenti nella room
                        if (usersInRoom.size() > 2) {
                            //cerco il forward all'interno della stanza
                            if (!(peer.peerID().equals(peerToSend.peerId()))) {
                                //se non sono io il peer a cui inviare il messaggio
                                peerForwarder = room.getForwarderPeer(peer.peerAddress(), peerToSend);
                                //perForwarder è il peer che fa da ponte, colui che invierà al peerTosend
                                message.setDestination(peerToSend);
                                sendMessageToPeer(message, peerForwarder);
                                //in message c'è la room a cui inviare il messaggio, il testo ed il peer destinazione (peerToSend)
                                //in peerForwarder c'è il peer che abbiamo recuperato dal metodo getForwarderPeer, ovvero il peer "ponte"
                            }
                        } else if (usersInRoom.size() == 2) {
                            //cerco il forward nella room dedicata ai forward perchè
                            // all'interno della room in cui si vuole inviare un messaggio sono presenti solo due peer
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

                            }

                        }

                    }
                    return true;
                }
            }


        } catch (Exception e) {

        }
        return false;
    }

    private void sendMessageToPeer(Message message, final PeerAddress peerForwarder) {
        FutureDirect futureDirect = _dht.peer().sendDirect(peerForwarder).object(message).start();
        futureDirect.addListener(new BaseFutureAdapter<FutureDirect>() {
            public void operationComplete(FutureDirect future) throws Exception {
                if (future.isSuccess()) {
                    System.out.println("Ho inviato a " + peerForwarder);
                }
            }
        });
    }


}
