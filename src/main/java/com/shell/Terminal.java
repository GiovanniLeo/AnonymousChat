package com.shell;

import com.unisa.AnonymousChatImpl;
import com.unisa.MessageListenerImpl;
import org.beryx.textio.TextIO;
import org.beryx.textio.TextIoFactory;
import org.beryx.textio.TextTerminal;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.io.IOException;

public class Terminal {

	@Option(name="-m", aliases="--masterip", usage="the master peer ip address", required=true)
	private static String master;

	@Option(name="-id", aliases="--identifierpeer", usage="the unique identifier for this peer", required=true)
	private static int id;

	public static void main(String[] args) throws IOException {

		Terminal terminalIstance = new Terminal();
		final CmdLineParser parser = new CmdLineParser(terminalIstance);
		try {
			parser.parseArgument(args);

			TextIO textIO = TextIoFactory.getTextIO();
			TextTerminal terminal = textIO.getTextTerminal();

			MessageListenerImpl ml =  new MessageListenerImpl(id);
			ml.setTerminal(terminal);
			AnonymousChatImpl peer = new AnonymousChatImpl(id,master, ml);
			terminal.printf("\nStarting peer id: %d on master node: %s\n",id, master);

			while(true){
				printMenu(terminal);
				int option = textIO.newIntInputReader()
						.withMaxVal(6)
						.withMinVal(1)
						.read("Option");

				switch(option){
					case 1:
						terminal.printf("\nCreate Room: Enter Room Name\n");
						String name = textIO.newStringInputReader()
								.read("Name(The name must be specified):");
						if(peer.createRoom(name))
							terminal.printf("\nRoom %s succesifully created\n\n",name);
						else
							terminal.printf("\nError in room creation\n");
						break;

					case 2:
						terminal.printf("\nJoin Room: Enter Room Name\n");
						String sname = textIO.newStringInputReader()
								.read("Name(The name must be specified):");
						if(peer.joinRoom(sname))
							terminal.printf("\nSuccessifully joined to the room %s\n\n",sname);
						else
							terminal.printf("\nError in join\n");
						break;

					case 3:
						terminal.printf("\nLeave Room: Enter Room Name\n");
						String lname = textIO.newStringInputReader()
								.read("Name(The name must be specified):");
						if(peer.leaveRoom(lname))
							terminal.printf("\nSuccessifully leaved room %s\n\n",lname);
						else
							terminal.printf("\nError in leave room\n");
						break;

					case 4:
						terminal.printf("\nSend Message: Enter Room Name \n");
						String tname = textIO.newStringInputReader()
								.read(" Name(The name must be specified):");
						terminal.printf("\nSend Message: Enter message\n");
						String message = textIO.newStringInputReader()
								.read("Message(The message must be specified):");
						if(peer.sendMessage(tname,message))
							terminal.printf("\nSuccessifully send message in room %s\n\n",tname);
						else
							terminal.printf("\nError in send message\n");

						break;

					case 5:
						terminal.printf("\nDestroy room Room: Enter Room Name\n");
						String dname = textIO.newStringInputReader()
								.read("Name(The name must be specified):");
						if(peer.destroyRoom(dname))
							terminal.printf("\nSuccessifully destroyed room %s\n\n",dname);
						else
							terminal.printf("\nError in destroy room\n");
						break;

					case 6:
						terminal.printf("\nLeave Network");
						if(peer.leaveNetwork()){
							terminal.printf("\nSee you space cowboy\n");
							Thread.sleep(1000);
							System.exit(0);
						} else
							terminal.printf("\nError in leave network\n\n");
						break;

					default:

						break;


				}
			}
		} catch (Exception e){
			e.printStackTrace();
		}


	}

	public static void printMenu(TextTerminal terminal) {
		terminal.println("1 - Create Room");
		terminal.println("2 - Join Room");
		terminal.println("3 - Leave Room");
		terminal.println("4 - Send Message in a Room");
		terminal.println("5 - Destroy Room");
		terminal.println("6 - Leave Network");

	}
}


