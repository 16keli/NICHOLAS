package engine.example;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import engine.client.Client;
import engine.network.packet.Packet;
import engine.server.Server;

public class PacketPlayerInput extends Packet {
	
	short pnum;
	byte dir;
	
	public PacketPlayerInput() {
		
	}

	public PacketPlayerInput(short pnum, int i) {
		this.pnum = pnum;
		this.dir = (byte) i;
	}

	@Override
	protected void readPacketData(DataInputStream is) throws IOException {
		this.pnum = is.readShort();
		this.dir = is.readByte();
	}

	@Override
	protected void writePacketData(DataOutputStream os) throws IOException {
		os.writeShort(pnum);
		os.writeByte(dir);
	}

	@Override
	public void processClient(Client c) {
	}

	@Override
	public void processServer(short player, Server s) {
//		((PongLevel) s.game.level).paddles[player].velY = this.dir * 20;
		s.game.events.post(new EventInput(this.pnum, this.dir));
//		System.out.println("Received input!");
	}

}
