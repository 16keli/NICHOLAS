package engine.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import engine.client.Client;
import engine.server.Server;

/**
 * The {@code Packet} sent by {@code Client}s when they are disconnected from the {@code Server} to let the
 * {@code Server} know that the {@code Client} is disconnected
 * 
 * @author Kevin
 */
public class PacketDisconnect extends Packet {

	public PacketDisconnect() {

	}

	@Override
	protected void readPacketData(DataInputStream is) throws IOException {
	}

	@Override
	protected void writePacketData(DataOutputStream os) throws IOException {
	}

	@Override
	public void processClient(Client c) {
	}

	@Override
	public void processServer(short player, Server s) {
		s.disconnect(player);
	}
}
