
public class TokenLock {
	private boolean wantCS;
	private boolean token;
	Coordinator birdkeeper;
	
	public TokenLock(Coordinator coord){
		birdkeeper = coord;
		token = false;
		wantCS = false;
	}
	
	public synchronized void lock(){
		wantCS = true;
		try{
			while(!token) wait();
		}
		catch(Exception fit){
			System.err.println("Broken lock: " + fit);
		}
	}
	
	public synchronized void receiveToken(){
		
			token = true;
			if(wantCS){
				wantCS = false;
				notify();
			}
			else
				unlock();
		
	}
	
	public void unlock(){
		token = false;
		wantCS = false;
		birdkeeper.passToken();
		
	}
	
	
	
}
