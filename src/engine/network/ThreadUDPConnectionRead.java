package engine.network;

public class ThreadUDPConnectionRead extends Thread {
	
	private Connection connect;
	
	public ThreadUDPConnectionRead(Connection c) {
		super(c.sourceName + " UDP Connection Reading Thread");
		this.connect = c;
	}
	
	@Override
	public void run() {
		try {
			while (connect.isRunning() && !connect.isTerminating()) {
				while (true) {
					if (!Connection.readUDPPacket(this.connect)) {
						sleep(2);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
