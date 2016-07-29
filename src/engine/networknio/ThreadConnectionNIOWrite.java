package engine.networknio;

public class ThreadConnectionNIOWrite extends Thread {
	
	private ConnectionNIO connect;
	
	public ThreadConnectionNIOWrite(ConnectionNIO c) {
		super(c.sourceName + " Connection Writing Thread");
		this.connect = c;
	}
	
	@Override
	public void run() {
		try {
			while (connect.isRunning() && !connect.isTerminating()) {
				while (true) {
					if (!ConnectionNIO.sendPacket(this.connect)) {
						sleep(2);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
