package engine.example;

import engine.Game;
import engine.networknio.ConnectionNIO;
import engine.networknio.packet.PacketGame;
import engine.server.Server;

public class PongServer extends Server {
	
	public PongServer(Game g, int port, int connect) {
		super(g, port, connect);
	}
	
	@Override
	protected void tickServer() {
	}
	
	@Override
	public void synchronizeClientGameData(ConnectionNIO c) {
		c.addToTCPSendQueue(new PacketGame(this.game));
	}
	
}
