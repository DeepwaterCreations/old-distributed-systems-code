import java.util.Scanner;


public class Main {
	
	//args[0] is the port to listen on.
	//The rest of args[] should be alternating ip addresses and the ports associated with them.
	public static void main(String[] args){
		
		Coordinator birdkeeper;
		int portnum; 
		String name;
		
		if(args.length > 0){
			Scanner whatyousay = new Scanner(System.in);
			
			portnum = Integer.parseInt(args[0]);
			
			//Allows the user to set a name.
			System.out.println("Input a name:");
			name = whatyousay.nextLine();

			//Spawns a coordinator. Coordinator gets the command line arguments.
			birdkeeper = new Coordinator(portnum, args, name);
			
			//Send console input to the Coordinator.			
			/*
			while(birdkeeper.shouldQuit()){
				birdkeeper.send(whatyousay.nextLine());
			}
			*/
			
			//Time testing:
			System.out.println("How many iterations?");
			birdkeeper.testTime(whatyousay.nextInt());
		
		
		}
		else{
			System.out.println("Arguments: port to listen on, optional list of ip addresses to connect to and their assosciated ports.");
		}
		

		
		
	}

}
