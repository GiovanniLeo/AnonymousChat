package com.unisa;


import java.io.IOException;

/**
 * Hello world!
 *
 */
public class App
{

    public static void main( String[] args )
    {
        try {
            AnonymousChatImpl peer0 = new AnonymousChatImpl(0,"127.0.0.1", new MessageListenerImpl(0));
            AnonymousChatImpl peer1 = new AnonymousChatImpl(0,"127.0.0.1", new MessageListenerImpl(0));
            AnonymousChatImpl peer2 = new AnonymousChatImpl(0,"127.0.0.1", new MessageListenerImpl(0));
            AnonymousChatImpl peer3 = new AnonymousChatImpl(0,"127.0.0.1", new MessageListenerImpl(0));


            if (peer1.createRoom("Stanza1")) System.out.println("Stanza 1 creata");
            if(peer1.joinRoom("Stanza1")) System.out.println("peer1 join Stanza 1");

            if(peer1.destroyRoom("Stanza1")) System.out.println("peer1 destroy Stanza 1 ");
            if (peer1.createRoom("Stanza1")) System.out.println("Stanza 1 creata");
            if(peer1.joinRoom("Stanza1")) System.out.println("peer1 join Stanza 1");
            if (peer1.leaveNetwork())
                System.out.println("See you Space cowboy");
            else System.out.println("wrong");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

