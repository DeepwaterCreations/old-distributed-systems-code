import java.util.Scanner;


public class Main {
	
	//args[0] is the port to listen on.
	//The rest of args[] should be alternating ip addresses and the ports associated with them.
	public static void main(String[] args){
		
		Coordinator birdkeeper;
		int portnum; 
		
		if(args.length > 0){
			portnum = Integer.parseInt(args[0]);

			//Spawns a coordinator. Coordinator gets the command line arguments.
			birdkeeper = new Coordinator(portnum, args);

			//Send console input to the Coordinator.
			Scanner whatyousay = new Scanner(System.in);
			while(!birdkeeper.shouldQuit()){
				birdkeeper.send(new String(whatyousay.nextLine().getBytes()));
			}
		
		}
		else{
			System.out.println("Arguments: port to listen on, optional list of ip addresses to connect to and their assosciated ports.");
		}
		

		
		
	}

}
