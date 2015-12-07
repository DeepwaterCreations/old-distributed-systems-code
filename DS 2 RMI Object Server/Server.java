import java.rmi.Naming;



public class Server {
	
	public static void main(String[] args){
		int portnum = 1138;
		try{
			System.setSecurityManager(new SecurityManager());
			Naming.bind(("map"), new DatabaseImpl()); 
			System.out.println("New database bound to registry");
		}
		catch(Exception fit){
			System.err.println(fit);
		}
		
		System.out.println("Ready...");
		while(true);
	}

}
