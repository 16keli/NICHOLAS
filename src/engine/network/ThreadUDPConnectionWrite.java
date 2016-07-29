package engine.network;

public class ThreadUDPConnectionWrite extends Thread {
	
	private Connection connect;
	
	public ThreadUDPConnectionWrite(Connection c) {
		super(c.sourceName + " UDP Connection Writing Thread");
		this.connect = c;
	}
	
	@Override
	public void run() {
		try {
			while (connect.isRunning() && !connect.isTerminating()) {
				while (Connection.sendUDPPacket(this.connect));
				sleep(2);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
