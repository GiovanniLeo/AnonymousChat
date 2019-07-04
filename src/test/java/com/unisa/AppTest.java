package com.unisa;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.IOException;

/**
 * Unit test for simple App.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AppTest
{

    AnonymousChatImpl peer0,peer1,peer2,peer3,peer4;

    @Before
    public  void setUp() throws IOException {
        peer0 = new AnonymousChatImpl(0,"127.0.0.1", new MessageListenerImpl(0));
        peer1 = new AnonymousChatImpl(1,"127.0.0.1", new MessageListenerImpl(1));
        peer2 = new AnonymousChatImpl(2,"127.0.0.1", new MessageListenerImpl(2));
        peer3 = new AnonymousChatImpl(3,"127.0.0.1", new MessageListenerImpl(3));
        peer4 = new AnonymousChatImpl(4,"127.0.0.1", new MessageListenerImpl(4));
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
    public void testC_ShouldSendMessage(){
        //The peer should send a message
        peer2.joinRoom("Stanza1");
        boolean flag = peer1.sendMessage("Stanza1","Hello World!");
        assertTrue(flag);
        //The peer should not send the message
        peer2.leaveRoom("Stanza1");
        boolean flag1 = peer1.sendMessage("Stanza1","Hello World!");
        assertFalse(flag1);
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
        peer1.createRoom("Stanza1");
        peer1.joinRoom("Stanza1");
        boolean flag1 = peer1.leaveNetwork();
        assertTrue(flag1);
    }

}
