import java.io.Serializable;
import java.rmi.*;
import java.rmi.server.*;
import java.util.HashMap;


public class DatabaseImpl extends UnicastRemoteObject implements Database{


	protected DatabaseImpl() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
	}

	HashMap<String, Object> objectMap = new HashMap<String, Object>();
	
	public void put(String key, Object value){
		System.out.println("Putting " + key);
		objectMap.put(key, value);
	}
	
	public Object get(String key){
		System.out.println("Getting " + key);
		return objectMap.get(key);
	}
}
