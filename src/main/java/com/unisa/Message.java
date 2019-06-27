package com.unisa;

import net.tomp2p.peers.PeerAddress;

import java.io.Serializable;

public class Message implements Serializable {

    private String message;
    private PeerAddress destination;
    private String roomName;

    public Message(String message, String roomName) {
        this.message = message;
        this.roomName = roomName;
        this.destination = null;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public PeerAddress getDestination() {
        return destination;
    }

    public void setDestination(PeerAddress destination) {
        this.destination = destination;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }
}
