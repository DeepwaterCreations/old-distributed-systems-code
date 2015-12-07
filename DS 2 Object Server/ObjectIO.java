import java.io.*;
import java.net.*;


public class ObjectIO {

	int portnum;

	
	public ObjectIO(int p){
		portnum = p;
	}
	
	//Puts the object into a byte array via an ObjectOutputStream, then sends the 
	//byte array in a packet to the given address.
	public void get(DatagramSocket socket, String address, Object obj){
		System.out.println("Preparing to send the Object to " + address); 
		try{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(obj);
		byte[] output = baos.toByteArray();
		DatagramPacket packet = new DatagramPacket(output, output.length);
		packet.setPort(portnum);
		packet.setAddress(InetAddress.getByName(address));
		socket.send(packet);
		System.out.println("Sent!");
		}
		catch(Exception tantrum){
			System.err.println(tantrum);
		}
	}
	
	//Gets a packet from the client and translates the bytes into an Object
	//via an ObjectInputStream. Then returns that object.
	//If this process fails, null is returned.
	public Object put(DatagramSocket socket){
		System.out.println("Preparing to receive the Object");
		Object obj = null;
		try{
		byte[] input = new byte[10000];
		DatagramPacket packet = new DatagramPacket(input, input.length);
		System.out.println("Receiving...");
		socket.receive(packet);
		System.out.println("Got it!");
		ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(input));
		System.out.println(new String(input));
		obj = ois.readObject();		
		}
		catch(Exception fit){
			System.err.println(fit);
		}
		return obj; 
	}
	
}
