import java.util.ArrayList;


//A central class to keep track of the other clients and pass messages along to Senders.

public class Coordinator {
	ArrayList<String> ipAddrs;
	ArrayList<AddrPair> addresses = new ArrayList<AddrPair>();
	int portnum;
	Sender pidgeon;
	Receiver pidgeonhole;
	boolean isQuit = false;
	
	/*
	//Basic constructor sets the port number and starts a Sender. 
	//A Receiver for listening for incoming connections is started in a new thread. 
	//Currently unused.
	public Coordinator(int pn){
		portnum = pn;
		pidgeon = new Sender(portnum);
		pidgeonhole = new Receiver(this);
		Thread r = new Thread(pidgeonhole);
		r.start();
	}
	*/
	
	//Takes an array of alternating ip addresses and ports. The first entry is the port being listened on.
	//The coordinator will pair the rest as AddrPairs,
	//store the pairs in a temporary array, then attempt to connect to each. 
	public Coordinator(int pn, String[] ips){ 
		boolean isFirst = true; //The first entry in the array has different behavior from the rest.
		boolean isPort = true; //Flipped after every array entry is read.
		String temp = "127.0.0.1"; //Holds port numbers temporarily for AddrPair construction. 
		ArrayList<AddrPair> toContact = new ArrayList<AddrPair>(); //Once this is filled, the entries will
																   //be sent messages.	
		portnum = Integer.parseInt(ips[0].trim());
		pidgeon = new Sender(portnum);
		System.out.println("Spawned sender");
		pidgeonhole = new Receiver(this);
		System.out.println("Spawned receiver");
		Thread r = new Thread(pidgeonhole);
		r.start();
		for(String item : ips){
			if(isFirst)
				isFirst = false;
			else if(!isPort)
				temp = item;
			else
				toContact.add(new AddrPair(Integer.parseInt(item), temp));
			isPort = !isPort;
		}
		for(AddrPair contact : toContact){
			pidgeon.send(contact, "/SYN");
			System.out.println("Sent /SYN to " + contact.ip);
		}
		
	}
	
	//Invoked to send a syn ack message, report the known ips to the new peer,
	//and also send the new ip to everyone else. 
	public void synack(AddrPair newPeer){
		pidgeon.send(newPeer, "/ACK");
		if(!addresses.isEmpty()){
			for(AddrPair eip : addresses){
				pidgeon.send(newPeer, "/ADD " + eip.port + " " + eip.ip);
			}
		}
		sendIP(newPeer);
	}
	
	//Invoked when SYN is acknowledged.
	public void ack(AddrPair contact){
		addIP(contact);
		System.out.println("Connected");
	}
	
	//Adds a new ip to the list and also tells everyone else about it.
	//NOTE: MAKE SURE I'M NOT ITERATING OVER EMPTY SPACES IN THE ARRAYLIST
	//If I need to, I can get a String[] from the ArrayList. 
	public void sendIP(AddrPair newPeer){
		if(!addresses.isEmpty()){
			sendCmd("/ADD " + newPeer.port + " " + newPeer.ip);
		}
		else
			System.out.println("Addresses is empty.");
		addIP(newPeer);
	}
	
	public void handleCommand(String command, AddrPair srcIP){
		System.out.println("Handling the command " + command);
		//If "/SYN" is sent, acknowledge.
		if(command.trim().equals("/SYN")){ 
			synack(srcIP);
		}
		//If "/SYNACK" is sent, acknowledge.
		if(command.trim().equals("/ACK")){
			ack(srcIP);
		}
		//If "/ADD" is sent, chop the "/ADD " off and add the remaining ip.
		if(command.startsWith("/ADD")){
			String[] tokens = command.substring(5).split(" ");
			addIP(Integer.parseInt(tokens[0]), tokens[1]);
		}
		//If "/CLOSING" is sent, remove the source from the address list.
		if(command.trim().equals("/CLOSING")){
			removeIP(srcIP);
		}
		//This command is sent by the user. It shuts everything down.
		if(command.trim().equals("/QUIT")){
			System.out.println("Quitting...");
			sendCmd("/CLOSING");
			isQuit = true;			
		}
	}
	
	
	//Sends a string message to all peers in the list.
	public void send(String msg){
		if(msg.startsWith("/")){
			handleCommand(msg, new AddrPair(portnum, "127.0.0.1"));
		}
		else{
			for(AddrPair eip : addresses){
				pidgeon.send(eip, msg);
			}
			System.out.println("You said: " + msg);
		}
	}
	
	//Sends a system command to all peers in the list.
	public void sendCmd(String msg){
		for(AddrPair eip : addresses){
			pidgeon.send(eip, msg);
		}
		System.out.println("You said: " + msg);
	}
	
	public int getPort(){
		return portnum;
	}
	
	public void addIP(int port, String ip){
		addresses.add(new AddrPair(port, ip));
		System.out.println("New connection from " + ip + " on port " + port);
	}
	
	public void addIP(AddrPair contact){
		addresses.add(contact);
		System.out.println("New connection from " + contact.ip + " on port " + contact.port);
	}
	
	//Iterates over the entries in addresses and removes any that match the ip and port of the given AddrPair.   
	public void removeIP(AddrPair srcIP){
		assert !addresses.isEmpty(); //We shouldn't be getting this message if we have no contacts to get it from!
		for(AddrPair eip : addresses){
			if(srcIP.port == eip.port && srcIP.ip == eip.ip){
				addresses.remove(eip);
			}
		}
	}
	
	public boolean shouldQuit(){
		return isQuit;
	}
	
	
}
