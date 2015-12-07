import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;


public class Sender{

	int portnum;
	DatagramSocket socket;
	
	
	public Sender(int port){
		portnum = port;
	}
	
	//Sends the given message to the given ip address.
	public void send(AddrPair contact, String msg){
		try{
			System.out.println("Starting a send to " + contact.ip + " on port " + contact.port);
			socket = new DatagramSocket(portnum);
			byte[] message = (msg.getBytes());
			DatagramPacket sendmsg = new DatagramPacket(message, message.length);
			sendmsg.setPort(contact.port);
			sendmsg.setAddress(InetAddress.getByName(contact.ip));
			System.out.println("Sending " + msg + " to " + contact.ip + " on port " + contact.port);
			socket.send(sendmsg);
		}
		catch(Exception e){
			System.err.println(e);
		}
		System.out.println("Closing socket: " + contact.port);
		socket.close();

	}
	
}
