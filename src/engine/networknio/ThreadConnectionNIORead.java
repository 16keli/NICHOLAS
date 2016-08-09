package engine.networknio;

public class ThreadConnectionNIORead extends Thread {
	
	private ConnectionNIO connect;
	
	public ThreadConnectionNIORead(ConnectionNIO c) {
		super(c.sourceName + " Connection Reading Thread");
		this.connect = c;
	}
	
	@Override
	public void run() {
		try {
			while (this.connect.isRunning() && !this.connect.isTerminating()) {
				while (true) {
					if (!ConnectionNIO.readPackets(this.connect)) {
						sleep(2);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
