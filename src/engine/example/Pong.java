package engine.example;

import engine.Engine;
import engine.Game;
import engine.Player;
import engine.client.Client;
import engine.event.SubscribeEvent;
import engine.network.packet.Packet;
import engine.server.Server;

/**
 * An example of how to create Pong in this Engine. Some of the code is a bit of a stretch, though I hope this
 * helps.
 * 
 * @author Kevin
 */
public class Pong extends Game {
	
	public static Pong cInst;
	
	public static Pong sInst;
	
	public static PongClient client;
	
	public static PongServer server;
	
	private byte prev1 = 0;
	
	private byte prev2 = 0;
	
	public static void main(String[] args) {
		cInst = new Pong();
		client = new PongClient(cInst, 320, 180);
		Engine.startClient(client);
	}
	
	static {
		Packet.registerPacket(PacketPlayerInput.class);
		Packet.registerPacket(PacketPlayerScore.class);
	}
	
	public PongPlayer p1;
	
	public PongPlayer p2;

	public boolean p2exists = false;
	
	public Pong() {
		super("Pong");
		this.setLevel(new PongLevel(this));
//		p1 = (PongPlayer) this.getNewPlayerInstance("Player 1");
//		p2 = (PongPlayer) this.getNewPlayerInstance("Player 2");
//		this.players.add(p1);
//		this.players.add(p2);
	}
	
	@Override
	public void tickClient(Client c) {
		if (!c.menuOpen()) {
			if (c.input.up.down && !c.input.down.down) {
				if (prev1 != -1) {
					c.sendPacket(new PacketPlayerInput(c.player.number, -1));
					prev1 = -1;
				}
			} else if (c.input.down.down && !c.input.up.down) {
				if (prev1 != 1) {
					c.sendPacket(new PacketPlayerInput(c.player.number, 1));
					prev1 = 1;
				}
			} else {
				if (prev1 != 0) {
					c.sendPacket(new PacketPlayerInput(c.player.number, 0));
					prev1 = 0;
				}
			}
			if (p2exists ) {
				if (c.input.up2.down && !c.input.down2.down) {
					if (prev2 != -1) {
						c.sendPacket(new PacketPlayerInput((byte) 1, -1));
						prev2 = -1;
					}
				} else if (c.input.down2.down && !c.input.up2.down) {
					if (prev2 != 1) {
						c.sendPacket(new PacketPlayerInput((byte) 1, 1));
						prev2 = 1;
					}
				} else {
					if (prev2 != 0) {
						c.sendPacket(new PacketPlayerInput((byte) 1, 0));
						prev2 = 0;
					}
				}
			}
		}
	}
	
	@Override
	public void tickServer(Server s) {
	
	}
	
	@Override
	protected void init() {
		// TODO Auto-generated method stub
		// KEVIN HI WADDUP FAM
		// Why herro dere Trenton
	}
	
	@SubscribeEvent
	public void onScore(EventPlayerScore e) {
		server.sendPacketAll(new PacketPlayerScore(e.pnum, e.score));
	}
	
	@Override
	public void resetGame() {
		this.level.reset();
	}
	
	public static void prepareServer(int port, int connect) {
		sInst = new Pong();
		server = new PongServer(sInst, port, connect);
		Engine.startServer(server);
	}
	
	@Override
	public Class<? extends Player> getPlayerClass() {
		return PongPlayer.class;
	}
	
}
