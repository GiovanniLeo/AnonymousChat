package com.unisa;

import com.utility.Constants;
import net.tomp2p.peers.PeerAddress;

public class MessageListenerImpl implements  MessageListener {
    private Message msg;
    private int peerID;

    public MessageListenerImpl(int peerID) {
        this.peerID = peerID;
    }

    @Override
    public Object parseMessage(Object obj) {
        this.msg = (Message) obj;
        System.out.println("peer"+peerID+" -> (Direct Message Received) "+msg.getMessage());
        return Constants.SUCCESS;
    }

    public Message getMsg() {
        return msg;
    }

    public void setMsg(Message msg) {
        this.msg = msg;
    }

    public int getPeerID() {
        return peerID;
    }

    public void setPeerID(int peerID) {
        this.peerID = peerID;
    }
}
