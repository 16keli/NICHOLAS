package engine.example;

import engine.Engine;
import engine.Game;
import engine.Player;
import engine.client.Client;
import engine.event.SubscribeEvent;
import engine.networknio.packet.PacketNIO;
import engine.server.Server;

/**
 * An example of how to create Pong in this Engine. Some of the code is a bit of a stretch, though I hope this
 * helps.
 * <p>
 * Some of these things WILL become deprecated in the near future, as the launch wrapper matures. For now,
 * these static fields are basically as good as it's gonna get.
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
	
	/**
	 * Most recent player score event
	 */
	private EventPlayerScore recent = null;
	
	public static void main(String[] args) {
		cInst = new Pong();
		client = new PongClient(cInst, 320, 180);
		Engine.startClient(client);
	}
	
	static {
		PacketNIO.registerPacket(PacketPlayerInputNIO.class);
		PacketNIO.registerPacket(PacketPlayerScoreNIO.class);
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
				if (this.prev1 != -1) {
					c.connection.addToSendQueue(new PacketPlayerInputNIO(c.player.number, -1));
					this.prev1 = -1;
				}
			} else if (c.input.down.down && !c.input.up.down) {
				if (this.prev1 != 1) {
					c.connection.addToSendQueue(new PacketPlayerInputNIO(c.player.number, 1));
					this.prev1 = 1;
				}
			} else {
				if (this.prev1 != 0) {
					c.connection.addToSendQueue(new PacketPlayerInputNIO(c.player.number, 0));
					this.prev1 = 0;
				}
			}
			if (this.p2exists) {
				if (c.input.up2.down && !c.input.down2.down) {
					if (this.prev2 != -1) {
						c.connection.addToSendQueue(new PacketPlayerInputNIO((short) 1, -1));
						this.prev2 = -1;
					}
				} else if (c.input.down2.down && !c.input.up2.down) {
					if (this.prev2 != 1) {
						c.connection.addToSendQueue(new PacketPlayerInputNIO((short) 1, 1));
						this.prev2 = 1;
					}
				} else {
					if (this.prev2 != 0) {
						c.connection.addToSendQueue(new PacketPlayerInputNIO((short) 1, 0));
						this.prev2 = 0;
					}
				}
			}
		}
	}
	
	@Override
	public void tickServer(Server s) {
		if (this.recent != null) {
			s.connections.sendPacketAll(new PacketPlayerScoreNIO(this.recent.pnum, this.recent.score));
			this.recent = null;
		}
	}
	
	@Override
	protected void init() {
		// TODO Auto-generated method stub
		// KEVIN HI WADDUP FAM
		// Why herro dere Trenton
	}
	
	@SubscribeEvent
	public void onScore(EventPlayerScore e) {
		this.recent = e;
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
