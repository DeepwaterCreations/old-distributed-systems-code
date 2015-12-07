
//Stores a port and an ip address
public class AddrPair implements Comparable<AddrPair>{
	
	int port;
	String ip;
	String name;
	
	public AddrPair(int p, String i){
		port = p;
		ip = i.trim();
	}
	
	public AddrPair(int p, String i, String n){
		port = p;
		ip = i.trim();
		name = n.trim();		
	}
	
	public void setName(String s){
		name = s.trim();
	}

	
	//Compares this AddrPair to another, and returns -1, 0 or 1 if this AddrPair is 
	//less than, equal to or greater than the other.
	//Names are compared lexicographically first. If they are equal, ip addresses are compared
	//lexicographically. If they are also equal, ports are compared. If all three fields are equal, 
	//0 is returned. 
	public int compareTo(AddrPair other){
		if(name.compareTo(other.name) != 0)
			return name.compareTo(other.name);
		else if(ip.compareTo(other.ip) != 0)
			return ip.compareTo(other.ip);
		else if(port < other.port)
			return -1;
		else if(port > other.port)
			return 1;
		else 
			return 0;
	}
	
	//If compareTo returns 0, the AddrPairs are equal.
	public boolean equals(AddrPair other){
		if(compareTo(other) == 0)
			return true;
		else
			return false;
	}
	
	//Doesn't compare names
	public boolean nequals(AddrPair other){
		if(ip.compareTo(other.ip) != 0)
			return ip.equals(other.ip);
		else if(port < other.port)
			return false;
		else if(port > other.port)
			return false;
		else 
			return true;
	}
	
}
