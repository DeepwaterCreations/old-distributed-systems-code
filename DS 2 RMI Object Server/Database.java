
import java.rmi.*;
import java.util.HashMap;

public interface Database extends Remote{
    HashMap<String, Object> objectMap = new HashMap<String, Object>();
    
    public void put(String key, Object value) throws RemoteException;
    
    public Object get(String key) throws RemoteException;
}
