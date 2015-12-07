import java.net.DatagramSocket;
import java.util.Date;
import java.util.HashMap;


public class Server {

	public static void main(String[] args) {
		int portnum = 4242;
		Date finish;
		Object obj;
		String[] data;
		HashMap<String, Object> objectMap = new HashMap<String, Object>();
		DatagramSocket socket = null;
		try{
		socket = new DatagramSocket(portnum);
		}
		catch(Exception e){
			System.out.println("Socket not connected");
		}
		Listener listener = new Listener(portnum); //Listener is the class for getting the initial tokens.
		ObjectIO inout = new ObjectIO(portnum); //ObjectIO sends or receives the object.
		
		
		//When listener gets a message, it returns the tokens and the address of the client.
		while(true){
			data = listener.listen(socket);
			
			//inout is given the object from objectMap and returns it to the client,			
			if(data[0].equals("GET")){
				//If the client is asking for a date, we give it one
				if(data[1].equals("date")){
					obj = new Date();
				}
				else{
					//Check to see if we have what they're looking for. If not, return a new object.
					if(objectMap.containsKey(data[1])){
						obj = objectMap.get(data[1]);
					}
					else{
						obj = new Date();
					}
				}
				inout.get(socket, data[2], obj);
			}
			//or else it gets the object from the client and returns it so it can be put in
			//objectMap.
			else if(data[0].equals("PUT")){
				System.out.println("Putting " + data[1]);
				objectMap.put(data[1].trim(), inout.put(socket));
				
				//For measuring latency
				if(data[1].trim().equals("date")){
					System.out.println(objectMap);
					System.out.println(objectMap.get(data[1].trim()));
					finish = new Date();	
					System.out.println(finish.getTime() - ((Date) objectMap.get(data[1].trim())).getTime());
				}
			}
			else{
				System.err.println("Unexpected Input: " + data[0]);
			}	
			
			
			
		}//The loop continues so that new data can be obtained.		
		
	}

}
