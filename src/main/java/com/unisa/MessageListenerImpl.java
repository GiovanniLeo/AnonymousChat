package com.unisa;

import com.utility.Constants;
import net.tomp2p.peers.PeerAddress;
import org.beryx.textio.TextTerminal;

import java.util.concurrent.atomic.AtomicBoolean;

public class MessageListenerImpl implements  MessageListener {
    private Message msg = null;
    private int peerID;
    private volatile boolean arrived = false;
    private TextTerminal terminal = null;

    public MessageListenerImpl(int peerID) {
        this.peerID = peerID;
    }

    @Override
    public Object parseMessage(Object obj) {
        this.msg = (Message) obj;
        arrived = true;
        if (terminal != null){
            terminal.println("(Direct Message Received) "+msg.getMessage());
        }
        return Constants.SUCCESS;
    }

    public void setTerminal(TextTerminal terminal) {
        this.terminal = terminal;
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

    public boolean getArrived(){
        return this.arrived;
    }
}
