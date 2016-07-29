package engine.example;

import engine.Game;
import engine.network.Connection;
import engine.network.synchro.PacketGame;
import engine.server.Server;

public class PongServer extends Server {
	

	public PongServer(Game g, int port, int connect) {
		super(g, port, connect);
	}

	@Override
	protected void tickServer() {
	}

	@Override
	public void synchronizeClientGameData(Connection c) {
		c.addToSendQueue(new PacketGame(this.game));
	}

}
