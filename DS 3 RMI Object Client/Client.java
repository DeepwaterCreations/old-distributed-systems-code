import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.rmi.registry.*;


public class Client {

	//Arguments: PUT or GET, map key, ip address, filename
	public void main(String[] args){
		int portnum = 1138;
		Registry reg;
		Database map;
		try{
			reg = LocateRegistry.getRegistry(args[2], portnum);
			map = (Database) reg.lookup("map");
		
			if(args[0].equals("GET")){
				Object obj = map.get(args[1]);
				ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(args[3]));
			    oos.writeObject(obj);

				
			}
			else if(args[0].equals("PUT")){
				ObjectInputStream ois = new ObjectInputStream(new FileInputStream(args[3]));
				Object obj = ois.readObject();
				map.put(args[1], obj);
			}			
			else{
				System.out.println("PUT or GET expected.");
			}
		}
		catch(Exception tantrum){
			System.err.println(tantrum);
		}
		
	
	}
}
