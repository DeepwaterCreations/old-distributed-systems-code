import java.io.*;
import java.net.*;


public class Main {

	//Arguments: GET or PUT, HashMap key, ip address, filename
	public static void main(String[] args) {
		int portnum = 1138;
		String address = args[2];
		String[] initialdata = new String[2];
		initialdata[0] = args[0];
		initialdata[1] = args[1];
		DatagramSocket socket;
		
		//First, the server is contacted and sent the relevant tokens.
		try{
		socket = new DatagramSocket(portnum);
		byte[] message = new byte[10000];
		DatagramPacket packet = new DatagramPacket(message, message.length);
		packet.setPort(portnum);
		packet.setAddress(InetAddress.getByName(address));
		socket.send(packet);
		System.out.println("Tokens sent");
		
		//If PUT was specified, an object is read from the specified file,
		//translated into a byte array, put in the packet and sent to the server.
		if(args[0] == "PUT"){
		    System.out.println("Putting file...");
			ByteArrayOutputStream baos = new ByteArrayOutputStream();	
			ObjectOutputStream ois = new ObjectOutputStream(baos);
			ois.writeObject(new ObjectInputStream(new FileInputStream(args[3])).readObject());
			byte[] putObject = baos.toByteArray();
			packet = new DatagramPacket(putObject, putObject.length);
			packet.setPort(portnum);
			packet.setAddress(InetAddress.getByName(address));
			socket.send(packet);			
		}
		
		//If GET was specified, a packet is received from the server, the data is translated
		//into an Object, and the Object is sent to a file with the specified name.
		else if(args[0] == "GET"){
		    System.out.println("Getting file...");
			Object obj;
			byte[] getObject = new byte[10000];
			packet = new DatagramPacket(getObject, getObject.length);
			socket.receive(packet);
			ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(getObject));
			obj = ois.readObject();
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(args[3]));
			oos.writeObject(obj);
		}
		
		//If some other argument showed up in args[0], then we must 
		//shout at the user for his or her foolishness.
		else{
			System.out.println("First argument should be PUT or GET");
		}
		
		}
		catch(Exception tantrum){
			System.err.println(tantrum);
		}

	}

}
