import java.io.*;
import java.net.*;
import java.util.Date;


public class Client {
    
    //Arguments: GET or PUT, HashMap key, ip address, filename
    public static void main(String[] args) {
	int portnum = 4242; 
	String address = args[2];
	String initialdata = args[0] + " " + args[1];
	DatagramPacket packet;
	System.out.println("You say you want to " + args[0]);
	//First, the server is contacted and sent the relevant tokens.
	try{
	    DatagramSocket socket = new DatagramSocket(portnum);
	    socket.connect(InetAddress.getByName(address), portnum);
	    byte[] message = initialdata.getBytes();
	    
	     
	    /*
		byte[] dateinfo = new String("PUT date").getBytes();
		packet = new DatagramPacket(dateinfo, dateinfo.length);
		packet.setPort(portnum);
		packet.setAddress(InetAddress.getByName(address));
		socket.send(packet);
		System.out.println("Date sent");
	    */
	   
		
		packet = new DatagramPacket(message, message.length);
		packet.setPort(portnum);
		packet.setAddress(InetAddress.getByName(address));
		socket.send(packet);
		System.out.println("Tokens sent");
	    
	    //System.out.println("Waiting");
	    //socket.receive(packet);
	    
	    //If PUT was specified, an object is read from the specified file,
	    //translated into a byte array, put in the packet and sent to the server.
	    if(args[0].equals("PUT")){
		if(args[1].equals("date")){ //Sending the server a Date
		    
		    
		    System.out.println("Putting date file...");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();	
		ObjectOutputStream ois = new ObjectOutputStream(baos);
		ois.writeObject(new Date());
		byte[] putObject = baos.toByteArray();
		packet = new DatagramPacket(putObject, putObject.length);
		packet.setPort(portnum);
		packet.setAddress(InetAddress.getByName(address));
		socket.send(packet);
	    }
	    else{
		System.out.println("Putting file...");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();	
		ObjectOutputStream ois = new ObjectOutputStream(baos);
		if(args[3].equals("new")){  //A new MacGuffin is created if "new" is given
		    MacGuffin temp = new MacGuffin(1);
		    ois.writeObject(temp);
		}
		else{
		    ois.writeObject(new ObjectInputStream(new FileInputStream(args[3])).readObject());
		}
		byte[] putObject = baos.toByteArray();
		packet = new DatagramPacket(putObject, putObject.length);
		packet.setPort(portnum);
		packet.setAddress(InetAddress.getByName(address));
		socket.send(packet);			
	    }
	    
	}
	
	//If GET was specified, a packet is received from the server, the data is translated
	//into an Object, and the Object is sent to a file with the specified name.
	else if(args[0].equals("GET")){

	    System.out.println("Getting file...");
	    Object obj;
	    byte[] getObject = new byte[10000];
	    packet = new DatagramPacket(getObject, getObject.length);
	    socket.receive(packet);
	    ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(getObject));
	    obj = ois.readObject();
	    ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(args[3]));
	    oos.writeObject(obj);

	    //For testing latency
	    if(args[1].equals("date")){
		Date finish = new Date();
		System.out.println(finish.getTime() - ((Date) obj).getTime());

	    }

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


