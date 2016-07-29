package engine.example;

import java.io.IOException;
import java.nio.ByteBuffer;

import engine.client.Client;
import engine.networknio.packet.PacketTCP;
import engine.server.Server;

public class PacketPlayerScoreNIO extends PacketTCP {
	
	public short pnum;
	
	public int score;
	
	public PacketPlayerScoreNIO() {
	
	}
	
	public PacketPlayerScoreNIO(short pnum, int score) {
		this.pnum = pnum;
		this.score = score;
	}
	
	@Override
	protected void writePacketData(ByteBuffer buff) throws IOException {
		buff.putShort(this.pnum);
		buff.putInt(this.score);
	}
	
	@Override
	protected void readPacketData(ByteBuffer buff) throws IOException {
		this.pnum = buff.getShort();
		this.score = buff.getInt();
	}
	
	@Override
	public void processClient(Client c) {
		c.game.events.post(new EventPlayerScore(this.pnum, this.score));
	}
	
	@Override
	public void processServer(short player, Server s) {
	}
	
}
