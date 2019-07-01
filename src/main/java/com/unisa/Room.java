package com.unisa;

import java.util.Random;
import java.io.Serializable;
import java.util.HashSet;


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
        this.peers.add(peer);
    }

    public boolean removePeer(PeerAddress peer) {
        this.peers.remove(peer);
    }

    public PeerAddress getHostPeer(PeerAddress sourcePeer, PeerAddress destinationPeer) {
        Random r = new Random();
        int random_number = r.nextInt(peers.size());

        while(true){

            if(!((peers.toArray().[random_number]).peerId().equals(sourcePeer)) && !((peers.toArray().[random_number]).peerId().equals(destinationPeer)))
                return (PeerAddress) peers.toArray().[random_number];
        }
    }

}