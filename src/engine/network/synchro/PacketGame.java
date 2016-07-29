package engine.network.synchro;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import engine.Game;
import engine.Player;
import engine.client.Client;
import engine.level.Level;
import engine.network.packet.PacketList;
import engine.server.Server;

/**
 * A {@code Packet} that sends an entire {@code Game}. This should only be used in the {@code Server}->
 * {@code Client} direction, and not the other way around.
 * 
 * @author Kevin
 */
public class PacketGame extends PacketSynchro {

	public Level level;
	
	public List<Player> players;

	public PacketGame() {
	}

	public PacketGame(Game g) {
		this.level = g.level;
		this.players = g.players;
	}

	@Override
	protected void readPacketData(ObjectInputStream is) throws IOException, ClassNotFoundException {
		this.level = (Level) is.readObject();
		this.players = PacketList.readList(is);
//		this.players = (List<Player>) is.readObject();
	}

	@Override
	protected void writePacketData(ObjectOutputStream os) throws IOException {
		os.writeObject(this.level);
		PacketList.writeList(os, this.players);
//		os.writeObject(this.players);
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
	}

}
