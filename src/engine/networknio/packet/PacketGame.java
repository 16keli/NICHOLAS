package engine.networknio.packet;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

import engine.Game;
import engine.Player;
import engine.client.Client;
import engine.level.Level;
import engine.server.Server;


public class PacketGame extends PacketTCP {
	
	public Level level;
	
	public byte[] levelAsBytes;
	
	public List<Player> players;
	
	public byte[] playersAsBytes;
	
	public PacketGame() {
		
	}
	
	public PacketGame(Game g) {
		this.level = g.level;
		this.players = g.players;
		this.levelAsBytes = PacketObject.objectToBytes(level);
		this.playersAsBytes = PacketObject.objectToBytes(this.players);
	}
	
	@Override
	protected void writePacketData(ByteBuffer buff) throws IOException {
		buff.putInt(levelAsBytes.length);
		buff.put(levelAsBytes);
		buff.put(playersAsBytes);
	}
	
	@SuppressWarnings ("unchecked")
	@Override
	protected void readPacketData(ByteBuffer buff) throws IOException {
		int levelSize = buff.getInt();
		byte[] data = buff.array();
		this.levelAsBytes = Arrays.copyOfRange(data, 4, 4 + levelSize);
		this.playersAsBytes = Arrays.copyOfRange(data, 4 + levelSize, data.length);
		this.level = (Level) PacketObject.bytesToObject(levelAsBytes);
		this.players = (List<Player>) PacketObject.bytesToObject(playersAsBytes);
	}
	
	@Override
	public void processClient(Client c) {
		this.level.rebuild(c.game);
		for (Player p : players) {
			p.rebuild(c.game);
		}
		c.game.level = this.level;
		c.game.players = this.players;
	}
	
	@Override
	public void processServer(short player, Server s) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public int getDataSize() {
		return 4 + levelAsBytes.length + playersAsBytes.length;
	}
	
}
