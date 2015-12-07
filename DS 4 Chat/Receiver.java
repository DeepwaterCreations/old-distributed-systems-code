import java.net.DatagramPacket;
import java.net.DatagramSocket;


public class Receiver implements Runnable{

	Coordinator birdkeeper;
	String message;
	DatagramSocket socket;
	
	public Receiver(Coordinator c){
		birdkeeper = c;
		try{
			socket = new DatagramSocket(birdkeeper.getPort() + 1);
		}
		catch(Exception tantrum){
			System.err.println(tantrum);
		}
	}
	
	public void run(){
		try{
			System.out.println("Listening on port " + (birdkeeper.getPort()+1) + "...");
		while(!birdkeeper.shouldQuit()){ 
			byte[] othermsg = new byte[10000];
			DatagramPacket getmsg = new DatagramPacket(othermsg, othermsg.length);
			socket.receive(getmsg);
			//System.out.println("Received message from " + getmsg.getAddress().getHostAddress() + " on port " + getmsg.getPort());
			message = new String((getmsg.getData()));
			//Messages that begin with a forward slash are special commands and must be handled by the coordinator.
			AddrPair msgContact = new AddrPair((getmsg.getPort() + 1), getmsg.getAddress().getHostAddress());
			if(message.startsWith("/")){
				if(!message.trim().equals("/TOKEN"))
					System.out.println("Received system message: " + message);
				birdkeeper.handleCommand(message, msgContact);
			}
			else{
				String name = birdkeeper.getName(msgContact);
				String namedmessage = new String(name + " says: " + message);				
				System.out.println(namedmessage);
			}
			//System.out.println("Closing listen socket on port " + (birdkeeper.getPort() + 1));
			//socket.close();
		}
		}
		catch(Exception fit){
			System.err.println(fit);
		}
	}
}
