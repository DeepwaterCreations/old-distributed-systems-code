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
		while(!birdkeeper.shouldQuit()){ 
			System.out.println("Listening on port " + (birdkeeper.getPort()+1) + "...");
			byte[] othermsg = new byte[10000];
			DatagramPacket getmsg = new DatagramPacket(othermsg, othermsg.length);
			socket.receive(getmsg);
			System.out.println("Received message from " + getmsg.getAddress().getHostAddress() + " on port " + getmsg.getPort());
			message = new String((getmsg.getData()));
			//Messages that begin with a forward slash are special commands and must be handled by the coordinator.
			if(message.startsWith("/")){
				System.out.println("System message: " + message);
				AddrPair msgContact = new AddrPair((getmsg.getPort() + 1), getmsg.getAddress().getHostAddress());
				birdkeeper.handleCommand(message, msgContact);
			}
			else{
				System.out.println(message);
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
