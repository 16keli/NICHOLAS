package engine.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import engine.client.Client;
import engine.server.Server;

/**
 * Used to measure the Ping between the Client and Server
 * @author Kevin
 *
 */
public class PacketPing extends Packet {
	
	/**
	 * The Period in ticks between ping evaluations
	 */
	public static final int PING_PERIOD = 60;
	
	/**
	 * The current {@code System.currentTimeMillis()}
	 */
	public long millisTime;
	
	public PacketPing() {
	}
	
	public PacketPing(long nanoTime) {
		this.millisTime = nanoTime;
	}

	@Override
	protected void readPacketData(DataInputStream is) throws IOException {
		this.millisTime = is.readLong();
	}

	@Override
	protected void writePacketData(DataOutputStream os) throws IOException {
		os.writeLong(this.millisTime);
	}

	@Override
	public void processClient(Client c) {
		c.setPing(System.currentTimeMillis() - this.millisTime);
	}

	@Override
	public void processServer(short player, Server s) {
		s.sendPacket(new PacketPing(System.currentTimeMillis()), player);
	}

}
