import java.io.*;

public class MacGuffin implements Serializable{

    //malty falcon object is a real cool guy. Eh serializes and doesn't afraid of anything.  
    int id;

    public MacGuffin(int i){
	id = i;
    }

    public int getid(){
	return id;
    }


}