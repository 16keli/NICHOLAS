package engine.server;

import java.io.IOException;

import engine.Game;
import engine.event.EventBus;
import engine.networknio.ConnectionList;
import engine.networknio.ConnectionNIO;
import engine.networknio.packet.PacketNIO;

/**
 * Represents the Server, which manages {@code Client} connections and gives them something to do
 * <p>
 * All {@code Game}s are based on the {@code Client} and {@code Server} communication implementation. We
 * learned from Notch's mistakes.
 * 
 * @author Kevin
 */
public abstract class Server {
	
	/**
	 * The {@code EventBus} used by the {@code Server} to process {@code GameEvent}s
	 */
	public static EventBus SERVER_BUS = new EventBus();
	
	/**
	 * The connection listener thread
	 */
	private ServerListenThread listener;
	
	/**
	 * The Game instance
	 */
	public Game game;
	
	/**
	 * The {@code ConnectionList}
	 */
	public ConnectionList connections;
	
	/**
	 * The minimum number of connections before the game starts. Set to -1 to not require any.
	 */
	public int minConnects;
	
	/**
	 * Creates a new Server that starts automatically
	 * 
	 * @param g
	 *            A {@code Game} instance
	 * @param port
	 *            The port to start the server on
	 */
	public Server(Game g, int port) {
		this(g, port, -1);
	}
	
	/**
	 * Creates a new Server
	 * 
	 * @param g
	 *            A {@code Game} instance
	 * @param port
	 *            The port to start the server on
	 * @param minConnects
	 *            The minimum number of connections before the game starts
	 */
	public Server(Game g, int port, int minConnects) {
		this.game = g;
		this.minConnects = minConnects;
		Server.SERVER_BUS.register(this);
		this.startListenThread(port);
	}
	
	/**
	 * Starts the server's listener thread on the given port
	 * 
	 * @param port
	 *            The port
	 */
	public void startListenThread(int port) {
		try {
			listener = new ServerListenThread(this, port);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		listener.start();
	}
	
	/**
	 * A tick of game time on the server side.
	 * <p>
	 * Automatically calls {@code Game}'s tick method, so there is no need to call it again.
	 */
	public void tick() {
		game.tick(this);
		for (short i = 0; i < connections.getList().size(); i++) {
			PacketNIO p;
			while ((p = connections.getList().get(i).getReadPacket()) != null) {
				p.processServer(i, this);
			}
		}
//		for (Connection c : this.connections) {
//			c.wakeThreads();
//		}
		if (minConnects > this.connections.getList().size()) {
			// Not enough connections
		} else {
			this.game.start = true;
		}
		this.tickServer();
	}
	
	/**
	 * Any Server-specific tasks that need to be done on a regular schedule
	 */
	protected abstract void tickServer();
	
	/**
	 * Shuts down the server
	 */
	public void shutdown() {
		for (ConnectionNIO conn : connections.getList()) {
			conn.networkShutdown();
		}
		listener.shutdown();
	}
	
	/**
	 * Disconnects the specified {@code Player}
	 * 
	 * @param player
	 *            The number to disconnect
	 */
	public void disconnect(short player) {
		ConnectionNIO conn = connections.getList().get(player);
		conn.networkShutdown();
		connections.getList().remove(player);
		game.players.remove(player);
	}
	
	/**
	 * Called when a new {@code Client} connects to this {@code Server}. This method should synchronize any
	 * game data necessary with the {@code Client} by sending the necessary {@code Packet} (s) through the
	 * {@code Player Connection}
	 * <p>
	 * It is possible to synchronize data through use of a single {@code PacketGame} of course, but all
	 * necessary classes that must be synchronized MUST implement {@code Serializable} or
	 * {@link engine.network.synchro.Rebuildable} or be synchronized through their own {@code Packet}s
	 * 
	 * @param c
	 *            The {@code Player} to send necessary game data to
	 */
	public abstract void synchronizeClientGameData(ConnectionNIO c);
	
}
