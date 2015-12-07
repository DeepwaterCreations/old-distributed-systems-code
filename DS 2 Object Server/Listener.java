import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;


public class Listener{
	
	int portnum;

	public Listener(int p){
		portnum = p;
	}
	
	
	//Opens a socket, receives a packet, then returns the data as an array of strings.
	//Elements in the array are separated by spaces.
	//Third element in the array is the ip address of the client. 
	public String[] listen(DatagramSocket socket){
		System.out.println("Preparing to listen");
		String[] returnarray = new String[3];
		byte[] input = new byte[10000];
		DatagramPacket packet = new DatagramPacket(input, input.length);
		try{
		System.out.println("Listening...");
		socket.receive(packet); 
		}
		catch(Exception e){
			System.err.println(e);
		}
		System.out.println("Message received!");
		String inString = new String(packet.getData());
		System.out.println(inString);
		String[] data = inString.split(" ");
		if(data.length == 2){
		returnarray[0] = data[0];
		returnarray[1] = data[1];
		}
		else{
			returnarray[0] = "-1";
			returnarray[1] = "-1";
		}
		returnarray[2] = packet.getAddress().getHostAddress(); //We need this to send a packet back
		System.out.println("Client address is " + returnarray[2]);
		System.out.println("It wishes to " + returnarray[0]);
		System.out.println("an object named " + returnarray[1]);
		
		//Tells the client that we're ready
		/*if(data[0].equals("PUT")){
		byte[] goahead = new byte[1];
		goahead[0] = 1;
		try{
		packet.setPort(portnum);
		packet.setAddress(InetAddress.getByName(returnarray[2]));
		System.out.println("Sending acknowledgement");
		socket.send(packet);
		}
		catch(Exception f){
			System.err.println(f);
		}
		}*/
		return returnarray;
	}
	
}
