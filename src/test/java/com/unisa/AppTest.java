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
    private List<Pair<AnonymousChatImpl, MessageListenerImpl>> lista;
    private AnonymousChatImpl peer0,peer1,peer2,peer3,peer4;
    private MessageListenerImpl listener0,listener1,listener2,listener3,listener4;

    @Before
    public  void setUp() throws IOException {
        lista = new ArrayList<Pair<AnonymousChatImpl,MessageListenerImpl>>();

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

    }

    @Test
    public void testA_ShouldCreateARoom()
    {
        //This should create a room because the room not exist
        boolean flag = peer1.createRoom("Stanza1");
        assertTrue("The room is correcly created", flag);
        //This should not create a room because the room already exist
        boolean flag1 = peer1.createRoom("Stanza1");
        assertFalse("The room is correcly created", flag1);
    }

    @Test
    public void testB_ShouldJoinToTheRoom(){
        //The peer should join to the room
        boolean flag = peer1.joinRoom("Stanza1");
        assertTrue(flag);
        //The peer should not join to the room
        boolean flag1 = peer1.joinRoom("Stanza1");
        assertFalse(flag1);
    }
    @Test
    public void testC_ShouldSendMessage() throws InterruptedException {
        //The peer should send the message
        boolean flag = peer3.joinRoom("Stanza1");
        assertTrue(flag);
        Message message = new Message("Hello World","Stanza1");
        peer1.sendMessage(message.getRoomName(),message.getMessage());
        int i=0;
        for(i = 0; i<lista.size(); i++) {
            System.out.println("Dovrei esaminare il peer"+i);
            if(i!=1 && lista.get(i).element0().getRegisteredRooms().contains("Stanza1")) {
                System.out.println("confermo che il peer "+lista.get(i).element1().getPeerID()+" abbia ricevuto il messaggio");
                while(!lista.get(i).element1().getArrived());
                System.out.println("Sono uscito dallo spinning");
                System.out.println("elemento ="+lista.get(i).element1().getMsg());
                assertEquals(message, lista.get(i).element1().getMsg());
              //  assertTrue(lista.get(i).element1().getMsg().getMessage().equals(message.getMessage()));
            }
        }
        //The peer should not send a message due to he isn't in the room
        boolean flag2 = peer3.leaveRoom("Stanza1");
        assertTrue(flag2);
        boolean flag3 = peer1.sendMessage("Stanza1","Hello World!");
        assertFalse(flag3);
    }
    @Test
    public void testD_ShouldLeaveRoom(){
        //The peer should join to the room
        boolean flag = peer1.leaveRoom("Stanza1");
        assertTrue(flag);
        //The peer should not join to the room
        boolean flag1 = peer1.leaveRoom("Stanza1");
        assertFalse(flag1);
    }

    @Test
    public void testE_ShouldDestroyRoom(){
        //The peer should join to the room
        peer1.joinRoom("Stanza1");
        boolean flag = peer1.destroyRoom("Stanza1");
        assertTrue(flag);
        //The peer should not join to the room
        boolean flag1 = peer1.destroyRoom("Stanza1");
        assertFalse(flag1);
    }

    @Test
    public void testF_ShouldLeaveTheNetwork(){
        //The peer should join to the room
        boolean flag = peer1.leaveNetwork();
        assertFalse(flag);
        //The peer should not join to the room
        boolean flag1 = peer1.createRoom("Stanza1");
        assertTrue(flag1);
        boolean flag2 = peer1.joinRoom("Stanza1");
        assertTrue(flag2);
        boolean flag3 = peer1.leaveNetwork();
        assertTrue(flag3);
    }

}
