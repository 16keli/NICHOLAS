package engine.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import engine.client.Client;
import engine.server.Server;

/**
 * The {@code Packet} sent by the {@code Server} to a {@code Client} when the {@code Client} first connects,
 * containing the {@code Client}'s player number
 * 
 * @author Kevin
 */
public class PacketConnection extends Packet {

	public short pnum;

	public PacketConnection() {

	}

	public PacketConnection(short pnum) {
		this.pnum = pnum;
	}

	@Override
	protected void readPacketData(DataInputStream is) throws IOException {
		this.pnum = is.readShort();
	}

	@Override
	protected void writePacketData(DataOutputStream os) throws IOException {
		os.writeShort(pnum);
	}

	@Override
	public void processClient(Client c) {
		c.setPlayerNumber(this.pnum);
	}

	@Override
	public void processServer(short player, Server s) {
		// TODO Auto-generated method stub

	}
}
