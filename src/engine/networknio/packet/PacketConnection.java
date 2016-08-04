package engine.networknio.packet;

import java.io.IOException;
import java.nio.ByteBuffer;

import engine.client.Client;
import engine.server.Server;

public class PacketConnection extends PacketTCP {
	
	public short pnum;
	
	public PacketConnection() {
	
	}
	
	public PacketConnection(short pnum) {
		this.pnum = pnum;
	}
	
	@Override
	public void processClient(Client c) {
		c.setPlayerNumber(this.pnum);
	}
	
	@Override
	public void processServer(short player, Server s) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void writePacketData(ByteBuffer buff) throws IOException {
		buff.putShort(this.pnum);
	}

	@Override
	public void readPacketData(ByteBuffer buff) throws IOException {
		this.pnum = buff.getShort();
	}
}
