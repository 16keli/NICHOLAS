package engine.example;

import java.io.IOException;
import java.nio.ByteBuffer;

import engine.client.Client;
import engine.networknio.packet.PacketNIO;
import engine.server.Server;

public class PacketPlayerScoreNIO extends PacketNIO {
	
	
	public int pnum;
	
	public int score;
	
	public PacketPlayerScoreNIO() {
		
	}
	
	public PacketPlayerScoreNIO(int pnum, int score) {
		this.pnum = pnum;
		this.score = score;
	}
	
	@Override
	public void writePacketData(ByteBuffer buff) throws IOException {
		buff.putInt(this.pnum);
		buff.putInt(this.score);
	}
	
	@Override
	public void readPacketData(ByteBuffer buff) throws IOException {
		this.pnum = buff.getInt();
		this.score = buff.getInt();
	}
	
	@Override
	public void processClient(Client c) {
		c.game.events.post(new EventPlayerScore(this.pnum, this.score));
	}
	
	@Override
	public void processServer(int player, Server s) {
	}
	
}
