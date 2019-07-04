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
            AnonymousChatImpl peer1 = new AnonymousChatImpl(1,"127.0.0.1", new MessageListenerImpl(1));
            AnonymousChatImpl peer2 = new AnonymousChatImpl(2,"127.0.0.1", new MessageListenerImpl(2));
            AnonymousChatImpl peer3 = new AnonymousChatImpl(3,"127.0.0.1", new MessageListenerImpl(3));
            AnonymousChatImpl peer4 = new AnonymousChatImpl(4,"127.0.0.1", new MessageListenerImpl(4));


            if(peer1.createRoom("Stanza1")) System.out.println("Stanza 1 creata");
            if(peer4.createRoom("Stanza2")) System.out.println("Stanza 2 creata");

            if(peer1.joinRoom("Stanza1")) System.out.println("peer1 join Stanza 1");
            if(peer2.joinRoom("Stanza1")) System.out.println("peer2 join Stanza 1");
            if(peer4.joinRoom("Stanza1")) System.out.println("peer4 join Stanza 1");
            if(peer4.joinRoom("Stanza2")) System.out.println("peer4 join Stanza 2");

            if(peer1.joinRoom("Stanza1")) System.out.println("peer1 join Stanza 1");

            if(peer2.sendMessage("Stanza1","Ciao a tutti belli"))System.out.println("ho inviato il messaggio");

            if(peer1.destroyRoom("Stanza1")) System.out.println("peer1 destroy Stanza 1 ");
            if(peer1.createRoom("Stanza1")) System.out.println("Stanza 1 creata");
            if(peer1.joinRoom("Stanza1")) System.out.println("peer1 join Stanza 1");
            if(peer1.leaveNetwork())
                System.out.println("See you Space cowboy");
            else System.out.println("wrong");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

