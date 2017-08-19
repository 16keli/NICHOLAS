package engine.networknio.packet;

import java.io.IOException;
import java.nio.ByteBuffer;

import engine.client.Client;
import engine.server.Server;

public class PacketConnection extends PacketNIO {
	
	public int pnum;
	
	public PacketConnection() {
	
	}
	
	public PacketConnection(int pnum) {
		this.pnum = pnum;
	}
	
	@Override
	public void processClient(Client c) {
		c.setPlayerNumber(this.pnum);
	}
	
	@Override
	public void processServer(int player, Server s) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void writePacketData(ByteBuffer buff) throws IOException {
		buff.putInt(this.pnum);
	}
	
	@Override
	public void readPacketData(ByteBuffer buff) throws IOException {
		this.pnum = buff.getInt();
	}
}
