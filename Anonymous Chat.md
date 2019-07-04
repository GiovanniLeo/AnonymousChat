# Anonymous Chat

It is an anonymous chat API based on P2P Network. Each peer can send messages on public chat room in anonymous way. The system allows the users to create new room, join in a room, leave a room and send a message on a room. As described in the [AnonymousChat Java API](https://github.com/spagnuolocarmine/distributedsystems/blob/master/challenges/AnonymousChat.java).

### Basic operations

- *createRoom*: create a public room.
- *joinRoom*: join in public room.
- *leaveRoom*: leave a public room.
- *sendMessage*: send a string message to all members of a  a public room.

### Additional operation

- *destroyRoom*: a peer can destroy a public room if it is the only member.
- *leaveNetwork*: a peer can leave netwok if he at least registred in a room.

### Technologies

The project has been implemented with:

- Java 8
- Apache Maven
- Intelij IDEA IDE
- Tom p2p
- JUnit
- Docker

## Project Structure

The main program is structured in four Java classes and two Java interfaces : 

- *AnonymousChat*: the API that define all the operations of the project.
- _AnonymousChatImpl_ : the implementation of the API.	
- _Message_: a class thet contains all the informations about a message.
- _MessageLister_: the API that define the message listener.
- _MessageListenerImpl_: a simple implpementation of a method for parsing the messages.
- _Room_: a class thet contains all the informations about a room.
- _Terminal_ a terminal to interact with the system.

The project provide also the class _AppTest_ which is a JUnit test case.

## Protocol Description



### Build app in a Docker container

First of all you can build your docker container:  
```docker build --no-cache -t auction_mechanism .```

#### Start the master peer

After that you can start the master peer, in interactive mode (-i) and with two (-e) environment variables:  
```docker run -i --name MASTER-PEER -e MASTERIP="127.0.0.1" -e ID=0 anonymousChat```

the MASTERIP environment variable is the master peer ip address and the ID environment variable is the unique id of your peer. Remember you have to run the master peer using the ID=0.

#### Start a generic peer

When master is started you have to check the ip address of your container:

- Check the docker <container ID>: ```docker ps```
- Check the IP address: ```docker inspect <container ID>```

Now you can start your peers varying the unique peer id:  
```docker run -i --name PEER-1 -e MASTERIP="172.17.0.2" -e ID=1 anonymousChat```

### Developed by:

Raffaele Ceruso: 0522500537

Giovanni Leo: 0522500538