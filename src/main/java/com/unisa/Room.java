package com.unisa;

import java.util.Iterator;
import java.util.Random;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;


import net.tomp2p.peers.PeerAddress;

public class Room implements Serializable {

    private HashSet<PeerAddress> peers;
    private String room_name;
    private static final long serialVersionUID = 164968237569892L;

    public Room(String room_name) {
        this.room_name = room_name;
        peers = new HashSet<PeerAddress>();
    }

    public String getRoom_name() {
        return room_name;
    }

    public void setRoom_name(String room_name) {
        this.room_name = room_name;
    }

    public HashSet<PeerAddress> getPeers() {
        return peers;
    }

    public void setPeers(HashSet<PeerAddress> peers) {
        this.peers = peers;
    }

    public boolean addPeer(PeerAddress peer) {
        return this.peers.add(peer);
    }

    public boolean removePeer(PeerAddress peer) {
        return this.peers.remove(peer);
    }

    public PeerAddress getForwarderPeer(PeerAddress sourcePeer, PeerAddress destinationPeer) {
        Random r = new Random();
        Set<PeerAddress> s = new HashSet<PeerAddress>();
        s.addAll(peers);
        PeerAddress[] peerToArray =  s.toArray(new PeerAddress[s.size()]);

        while (true) {
            int random_number = r.nextInt(peers.size());
            if(!(peerToArray[random_number].peerId().equals(sourcePeer.peerId()))&&!(peerToArray[random_number].peerId().equals(destinationPeer.peerId())))
                return  peerToArray[random_number];
        }
    }

}
