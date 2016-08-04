package engine.networknio;

public class ThreadConnectionListRead extends Thread {
	
	private ConnectionList connects;
	
	public ThreadConnectionListRead(ConnectionList c) {
		super("Server Connection List Reading Thread");
		this.connects = c;
	}
	
	@Override
	public void run() {
		try {
			while (true) {
				for (ConnectionNIO connect : connects.connections) {
					while (connect.isRunning() && !connect.isTerminating()) {
						if (!ConnectionNIO.readPackets(connect)) {
							break;
						}
					}
				}
				sleep(2);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
