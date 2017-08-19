package engine.networknio.packet;

import java.io.IOException;
import java.nio.ByteBuffer;

import engine.Player;
import engine.client.Client;
import engine.server.Server;

/**
 * Created to synchronize data for another player in the event that a new connection is established in the
 * middle of the game.
 * 
 * @author Kevin
 */
public class PacketPlayer extends PacketNIO {
	
	public Player player;
	
	public byte[] playerAsBytes;
	
	public PacketPlayer() {
	}
	
	public PacketPlayer(Player p) {
		this.player = p;
		this.playerAsBytes = PacketObject.objectToBytes(this.player);;
	}
	
	@Override
	public void writePacketData(ByteBuffer buff) throws IOException {
		PacketNIO.writeObject(buff, this.playerAsBytes);
	}
	
	@Override
	public void readPacketData(ByteBuffer buff) throws IOException {
		this.player = (Player) PacketNIO.readObject(buff);
	}
	
	@Override
	public void processClient(Client c) {
		c.game.players.add(this.player);
	}
	
	@Override
	public void processServer(int player, Server s) {
	}
	
}
