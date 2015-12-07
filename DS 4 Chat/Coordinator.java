import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;


//A central class to keep track of the other clients and pass messages along to Senders.

public class Coordinator {
	ArrayList<AddrPair> addresses = new ArrayList<AddrPair>();
	AddrPair[] tokenList;	
	int portnum;
	Sender pidgeon;
	Receiver pidgeonhole;
	boolean isQuit = false;
	static final String DEFAULTNAME = "Anonymous"; 
	String name = DEFAULTNAME;
	AddrPair myAddress; 
	TokenLock tLock;
	
	Date start;
	Date finish;
	long time;
	long totalTime = 0;
	boolean nextTest = true;
	
	
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
	public Coordinator(int pn, String[] ips, String n){ 
		name = n; 
		boolean isFirst = true; //The first entry in the array has different behavior from the rest.
		boolean isPort = true; //Flipped after every array entry is read.
		String temp = "127.0.0.1"; //Holds port numbers temporarily for AddrPair construction. 
		ArrayList<AddrPair> toContact = new ArrayList<AddrPair>(); //Once this is filled, the entries will
																   //be sent messages.	
		portnum = Integer.parseInt(ips[0].trim());
		myAddress = new AddrPair(portnum, "127.0.0.1", name);
		tLock = new TokenLock(this);
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
		/*//This is to contact multiple clients on start.
		 //Currently, this will probably result in duplicate addresses in the list.  
		//Could be used to find a working client if the initial connection is refused.
		for(AddrPair contact : toContact){
			pidgeon.send(contact, "/SYN");
			System.out.println("Sent /SYN to " + contact.ip);
		}
		*/
		//Only contact the first pair in the list.
		if(!toContact.isEmpty()){
			pidgeon.send(toContact.get(0), "/SYN " + name);
			//System.out.println("Sent /SYN to " + toContact.get(0).ip);
		}	
		else{
			makeTList(); //The list should contain only this client.
			assert tokenList.length == 1;
		}
	}
	
	//Invoked to send an ack message, report the known ips to the new peer,
	//and also send the new ip to everyone else. 
	public void getSyn(AddrPair newPeer){
		pidgeon.send(newPeer, "/ACK " + newPeer.ip);
		if(!addresses.isEmpty()){
			for(AddrPair eip : addresses){
				pidgeon.send(newPeer, "/ADD " + eip.port + " " + eip.ip);
			}
		}
		else{
			pidgeon.send(newPeer, "/TOKEN"); //If we have no addresses and we're getting /SYN, we must be the 
											 //first client, so we need to spawn the token. 
		}
		sendIP(newPeer);
	}
	
	//Invoked when SYN is acknowledged.
	public void getAck(AddrPair contact, String myIP){
		//synchronized(this){
			myAddress.ip = myIP;
			addIP(contact);
			System.out.println("Connected");
		//}
	}
	
	//Adds a new ip to the list and also tells everyone else about it.
	public void sendIP(AddrPair newPeer){
		if(!addresses.isEmpty()){
			sendCmd("/ADD " + newPeer.port + " " + newPeer.ip + " " + newPeer.name);
		}
		else
			System.out.println("Addresses is empty.");
		addIP(newPeer);
	}
	
	public synchronized void handleCommand(String command, AddrPair srcIP){
		//System.out.println("Handling the command " + command);
		//If "/SYN" is sent, acknowledge.
		if(command.startsWith("/SYN")){
			String synName = command.substring(5);
			getSyn(new AddrPair(srcIP.port, srcIP.ip, synName));
		}
		//If "/ACK" is sent, put the sender in the list and get what it thinks our ip is.
		if(command.startsWith("/ACK")){
			String i = command.substring(5); //"/ACK " is 5 characters.
			getAck(srcIP, i);
		}
		//If "/ADD" is sent, chop the "/ADD " off and add the remaining ip.
		if(command.startsWith("/ADD")){
				String[] tokens = command.substring(5).split(" ");
				addIP(Integer.parseInt(tokens[0]), tokens[1], tokens[2]);	
		}
		//If "/NAME" is sent, chop the "/NAME" off and assign the remaining string to the source AddrPair.
		if(command.startsWith("/NAME")){
			String n = command.substring(6); //"/NAME " is 6 characters - we want the 7th and beyond.
			setName(n, srcIP);
		}
		//This is for the user to register a new name.
		if(command.startsWith("/REGNAME")){
			String n = command.substring(9);
			setMyName(n);
		}
		//If "/TOKEN" is sent, we have the token.
		if(command.trim().equals("/TOKEN")){
			while(tokenList.length == 0);
			tLock.receiveToken();
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
		if(command.trim().equals("/STIME")){
			System.out.println("Time request");
			pidgeon.send(srcIP, "/ETIME");
		}
		if(command.trim().equals("/ETIME")){
			finish = new Date();
			time = finish.getTime() - start.getTime();
			System.out.println(time);
			totalTime += time;
			nextTest = true;
		}
		//System.out.println("Done handling command " + command);
	}
	
	
	//Sends a string message to all peers in the list.
	public void send(String msg){
		tLock.lock();
		if(msg.startsWith("/")){
			handleCommand(msg, myAddress);
		}
		else{
			for(AddrPair eip : addresses){
				pidgeon.send(eip, msg);
			}
			System.out.println("You say: " + msg);
		}
		tLock.unlock();
	}
	

	//Sends a system command to all peers in the list.
	public void sendCmd(String msg){
		while(addresses.isEmpty());
		tLock.lock();
		for(AddrPair eip : addresses){
			pidgeon.send(eip, msg);
		}
		System.out.println("You sent CMD: " + msg);
		tLock.unlock();
	}
	
	
	public int getPort(){
		return portnum;
	}
	/*
	public void addIP(int port, String ip){
			addresses.add(new AddrPair(port, ip, DEFAULTNAME));
			System.out.println("New connection from " + ip + " on port " + port);
			makeTList();
	}
	*/
	
	public void addIP(int port, String ip, String n){
		addresses.add(new AddrPair(port, ip, n));
		System.out.println("New connection from " + ip + " on port " + port);
		makeTList();
}
	
	public void addIP(AddrPair contact){
		if(contact.name != null)
			addresses.add(new AddrPair(contact.port, contact.ip, contact.name));
		else
			addresses.add(new AddrPair(contact.port, contact.ip, DEFAULTNAME));
		System.out.println("New connection from " + contact.ip + " on port " + contact.port);
		makeTList();
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
	
	//Broadcasts name changes to everyone else
	public void setMyName(String n){
		name = n;
		myAddress.name = n;
		sendCmd("/NAME " + name); 
		makeTList();
	}
	
	//Sets the name of an AddrPair that matches the ip and port of the one given. 
	public void setName(String n, AddrPair contact){
		if(!addresses.isEmpty()){
			for(AddrPair eip : addresses){
				if(contact.nequals(eip)){
					System.out.println("Setting name!");
					eip.setName(n);
					makeTList();
				}
			}
		}
		else{
			System.out.println("Set Name - No contacts!");
		}
	}
	
	//Returns the name of an AddrPair that matches the ip and port of the one given,
	//or else the default name.
	public String getName(AddrPair contact){
		if(!addresses.isEmpty()){
			for(AddrPair eip : addresses){
				if(contact.nequals(eip)){
					if(eip.name != null)
						return eip.name;
				}
			}
		}
		else{
			System.out.println("Get Name - No contacts!");
		}
		return DEFAULTNAME;
	}
	
	//Passes a token to the next entry in the list.
	public void passToken(){
		synchronized(addresses){
		int i = 0;
		while(!tokenList[i].equals(myAddress) && i < tokenList.length)
			i++;
		i = (i + 1)%tokenList.length;
		pidgeon.send(tokenList[i], "/TOKEN");
		}
	}
	
	//Generates a token order list
	public void makeTList(){
		synchronized(addresses){
			tokenList = new AddrPair[addresses.size() + 1];
			for(int i = 0;i < addresses.size();i++){
				tokenList[i] = addresses.get(i);
			}
			tokenList[addresses.size()] = myAddress; //Put this client's address, as the other clients percieve it, in the last slot.
			Arrays.sort(tokenList);
		}
	}
	
	public boolean shouldQuit(){
		return isQuit;
	}
	
	//time testing
	public void testTime(int tests){
		for(int i = 0; i < tests; i++){ 
			while(nextTest = false);
			nextTest = false;
			start = new Date();
			sendCmd("/STIME");
		}
		reportTime(tests);
			
	}
	public long reportTime(int tests){
		System.out.println("Total time: " + totalTime + "ms");
		long avtime = totalTime/tests;
		System.out.println("Average time: " + avtime + "ms");
		totalTime = 0;
		return avtime;
	}
	
}
