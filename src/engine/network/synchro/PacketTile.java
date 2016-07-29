package engine.network.synchro;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import engine.client.Client;
import engine.level.tiled.LevelTiled;
import engine.level.tiled.Tile;
import engine.server.Server;

/**
 * A {@code Packet} that is called whenever a {@code Tile} is modified
 * @author Kevin
 *
 */
public class PacketTile extends PacketSynchro {
	
	public Tile tile;
	
	public PacketTile() {
	}
	
	public PacketTile(Tile tile) {
		this.tile = tile;
	}

	@Override
	protected void readPacketData(ObjectInputStream is) throws IOException, ClassNotFoundException {
		this.tile = (Tile) is.readObject();
	}

	@Override
	protected void writePacketData(ObjectOutputStream os) throws IOException {
		os.writeObject(this.tile);
	}

	@Override
	public void processClient(Client c) {
		((LevelTiled) c.game.level).setTile(this.tile);
	}

	@Override
	public void processServer(short player, Server s) {
		// TODO Auto-generated method stub
		
	}

}
