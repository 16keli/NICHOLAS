package engine.networknio;

public class ThreadConnectionListWrite extends Thread {
	
	private ConnectionList connects;
	
	public ThreadConnectionListWrite(ConnectionList c) {
		super("Server Connection Writing Thread");
		this.connects = c;
	}
	
	@Override
	public void run() {
		try {
			while (true) {
				for (ConnectionNIO connect : connects.connections) {
					while (connect.isRunning() && !connect.isTerminating()) {
						if (!ConnectionNIO.sendPacket(connect)) {
							break;
						}
					}
					sleep(2);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
