package engine.networknio.packet;

import java.io.IOException;
import java.nio.ByteBuffer;

import engine.client.Client;
import engine.server.Server;

public class PacketPing extends PacketTCP {
	
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
	public void processClient(Client c) {
		c.setPing(System.currentTimeMillis() - this.millisTime);
	}
	
	@Override
	public void processServer(short player, Server s) {
		s.connections.sendPacket(new PacketPing(System.currentTimeMillis()), player);
	}
	
	@Override
	public void writePacketData(ByteBuffer buff) throws IOException {
		buff.putLong(System.currentTimeMillis());
	}
	
	@Override
	public void readPacketData(ByteBuffer buff) throws IOException {
		this.millisTime = buff.getLong();
	}
}
