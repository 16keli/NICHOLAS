package engine.networknio.packet;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

import engine.Game;
import engine.Player;
import engine.client.Client;
import engine.level.Level;
import engine.server.Server;

public class PacketGame extends PacketNIO {
	
	public Level level;
	
	public List<Player> players;
	
	public byte[] levelBytes;
	
	public byte[] playerBytes;
	
	public PacketGame() {
	
	}
	
	public PacketGame(Game g) {
		this.level = g.level;
		this.players = g.players;
		this.levelBytes = PacketObject.objectToBytes(this.level);
		this.playerBytes = PacketObject.objectToBytes(this.players);
	}
	
	@Override
	public void writePacketData(ByteBuffer buff) throws IOException {
		PacketNIO.writeObject(buff, this.levelBytes);
		PacketNIO.writeObject(buff, this.playerBytes);
	}
	
	@SuppressWarnings ("unchecked")
	@Override
	public void readPacketData(ByteBuffer buff) throws IOException {
		this.level = (Level) PacketNIO.readObject(buff);
		this.players = (List<Player>) PacketNIO.readObject(buff);
	}
	
	@Override
	public void processClient(Client c) {
		this.level.rebuild(c.game);
		for (Player p : this.players) {
			p.rebuild(c.game);
		}
		c.game.level = this.level;
		c.game.players = this.players;
	}
	
	@Override
	public void processServer(short player, Server s) {
		// TODO Auto-generated method stub
		
	}
	
}
