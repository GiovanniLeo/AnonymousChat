package com.unisa;

import net.tomp2p.utils.Pair;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Unit test for simple App.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AppTest
{
    private List<Pair<AnonymousChatImpl, MessageListenerImpl>> lista = null;
    private AnonymousChatImpl peer0,peer1,peer2,peer3,peer4;
    private MessageListenerImpl listener0,listener1,listener2,listener3,listener4;

    @Test
    public  void testMethods() throws IOException {
        boolean flag;
        lista = new ArrayList<Pair<AnonymousChatImpl, MessageListenerImpl>>();

        listener0 = new MessageListenerImpl(0);
        listener1 = new MessageListenerImpl(1);
        listener2 = new MessageListenerImpl(2);
        listener3 = new MessageListenerImpl(3);
        listener4 = new MessageListenerImpl(4);

        peer0 = new AnonymousChatImpl(0, "127.0.0.1", listener0);
        peer1 = new AnonymousChatImpl(1, "127.0.0.1", listener1);
        peer2 = new AnonymousChatImpl(2, "127.0.0.1", listener2);
        peer3 = new AnonymousChatImpl(3, "127.0.0.1", listener3);
        peer4 = new AnonymousChatImpl(4, "127.0.0.1", listener4);

        lista.add(new Pair<AnonymousChatImpl, MessageListenerImpl>(peer0, listener0));
        lista.add(new Pair<AnonymousChatImpl, MessageListenerImpl>(peer1, listener1));
        lista.add(new Pair<AnonymousChatImpl, MessageListenerImpl>(peer2, listener2));
        lista.add(new Pair<AnonymousChatImpl, MessageListenerImpl>(peer3, listener3));
        lista.add(new Pair<AnonymousChatImpl, MessageListenerImpl>(peer4, listener4));

        //This should create a room because the room not exist
        flag = peer1.createRoom("Stanza1");
        assertTrue("The room is correcly created", flag);
        //This should not create a room because the room already exist
        flag = peer1.createRoom("Stanza1");
        assertFalse("The room is correcly created", flag);

        //The peer should join to the room
        flag = peer1.joinRoom("Stanza1");
        assertTrue(flag);
        //The peer should not join to the room
        flag = peer1.joinRoom("Stanza1");
        assertFalse(flag);

        //The peer should send the message
        flag = peer4.joinRoom("Stanza1");
        assertTrue(flag);
        Message message = new Message("Hello World","Stanza1");
        peer1.sendMessage(message.getRoomName(),message.getMessage());

        for(int i = 0; i<lista.size(); i++) {
            if(i!=1 && lista.get(i).element0().getRegisteredRooms().contains("Stanza1")) {
                while(!lista.get(i).element1().getArrived());
                assertTrue(lista.get(i).element1().getMsg().getMessage().equals(message.getMessage()));
            }
        }
        //The peer should not send a message due to he isn't in the room
        flag = peer4.leaveRoom("Stanza1");
        assertTrue(flag);
        flag = peer1.sendMessage("Stanza1","Hello World!");
        assertFalse(flag);

        //The peer should leave the room
        flag = peer1.leaveRoom("Stanza1");
        assertTrue(flag);
        //The peer already leaved the room so the result will be false
        flag = peer1.leaveRoom("Stanza1");
        assertFalse(flag);

        //The peer should destroy the room
        peer1.joinRoom("Stanza1");
        flag = peer1.destroyRoom("Stanza1");
        assertTrue(flag);
        //The peer already destroyed the room so the result will be false
        flag = peer1.destroyRoom("Stanza1");
        assertFalse(flag);

        //The peer should leave the network
        flag = peer1.leaveNetwork();
        assertTrue(flag);


    }
}
