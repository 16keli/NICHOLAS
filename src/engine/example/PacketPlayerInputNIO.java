package engine.example;

import java.io.IOException;
import java.nio.ByteBuffer;

import engine.client.Client;
import engine.networknio.packet.PacketTCP;
import engine.server.Server;

public class PacketPlayerInputNIO extends PacketTCP {
	
	public short pnum;
	
	public byte dir;
	
	public PacketPlayerInputNIO() {
	
	}
	
	public PacketPlayerInputNIO(short pnum, int i) {
		this.pnum = pnum;
		this.dir = (byte) i;
	}
	
	@Override
	public void writePacketData(ByteBuffer buff) throws IOException {
		buff.putShort(this.pnum);
		buff.put(this.dir);
	}
	
	@Override
	public void readPacketData(ByteBuffer buff) throws IOException {
		this.pnum = buff.getShort();
		this.dir = buff.get();
	}
	
	@Override
	public void processClient(Client c) {
	}
	
	@Override
	public void processServer(short player, Server s) {
		s.game.events.post(new EventInput(this.pnum, this.dir));
	}
	
}
