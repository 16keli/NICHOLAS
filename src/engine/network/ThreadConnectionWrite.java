package engine.network;

public class ThreadConnectionWrite extends Thread {

	private Connection connect;

	public ThreadConnectionWrite(Connection c) {
		super(c.sourceName + " Connection Writing Thread");
		this.connect = c;
	}

	@Override
	public void run() {
		try {
			while (connect.isRunning() && !connect.isTerminating()) {
				boolean flag;

				for (flag = false; Connection.sendPacket(this.connect); flag = true) {
					;
				}
				if (flag && this.connect.getOutputStream() != null) {
					this.connect.getOutputStream().flush();
				}
				sleep(2);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
