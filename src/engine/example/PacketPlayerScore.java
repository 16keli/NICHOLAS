package engine.example;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import engine.client.Client;
import engine.network.packet.Packet;
import engine.server.Server;

public class PacketPlayerScore extends Packet {

	short pnum;
	
	int score;

	public PacketPlayerScore() {
		
	}

	public PacketPlayerScore(short pnum, int score) {
		this.pnum = pnum;
		this.score = score;
	}

	@Override
	protected void readPacketData(DataInputStream is) throws IOException {
		this.pnum = is.readShort();
		this.score = is.readInt();
	}

	@Override
	protected void writePacketData(DataOutputStream os) throws IOException {
		os.writeShort(this.pnum);
		os.writeInt(this.score);
	}

	@Override
	public void processClient(Client c) {
		c.game.events.post(new EventPlayerScore(this.pnum, this.score));
	}

	@Override
	public void processServer(short player, Server s) {
		// TODO Auto-generated method stub

	}

}
