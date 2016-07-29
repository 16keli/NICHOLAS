package engine.network;

public class ThreadConnectionRead extends Thread {
	
	private Connection connect;
	
	public ThreadConnectionRead(Connection c) {
		super(c.sourceName + " Connection Reading Thread");
		this.connect = c;
	}
	
	@Override
	public void run() {
		try {
			while(connect.isRunning() && !connect.isTerminating()) {
				while (true) {
					if (!Connection.readPacket(this.connect)) {
						sleep(2);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
