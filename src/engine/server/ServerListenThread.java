package engine.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import engine.Player;
import engine.event.game.ConnectionEstablishedEvent;
import engine.network.Connection;
import engine.network.packet.PacketConnection;

/**
 * The Thread that runs on the server that listens for connection requests from {@code Client}s and enables
 * them to connect to the server
 * 
 * @author Kevin
 */
public class ServerListenThread extends Thread {
	
	/**
	 * Whether or not the thread is listening
	 */
	private boolean listening = true;
	
	/**
	 * The server socket of this listener thread
	 */
	private ServerSocket ss;
	
	/**
	 * The {@code InetAddress} of the socket
	 */
	private InetAddress ip;
	
	/**
	 * The port that is open
	 */
	private int port;
	
	/**
	 * The {@code Server} instance
	 */
	private Server server;
	
	/**
	 * Creates a new Listener Thread based on the given {@code Server} and port
	 * 
	 * @param s
	 *            The {@code Server} instance
	 * @param port
	 *            The port
	 * @throws IOException
	 *             If an I/O exception occurs during socket creation
	 */
	public ServerListenThread(Server s, int port) throws IOException {
		super("Server Connection Listening Thread");
		this.ss = new ServerSocket(port, 0, null);
		this.port = port;
		this.ss.setPerformancePreferences(0, 2, 1);
		this.server = s;
		this.ip = ss.getInetAddress();
	}
	
	@Override
	public void run() {
		while (listening) {
			try {// I don't believe we will run into concurrency issues because only a single thread will be
					// created and accessing nextPlayerID at a time
				Socket s = ss.accept();
				Connection c = new Connection(s, "Server-Side");
				Server.SERVER_BUS.post(new ConnectionEstablishedEvent(server.game, c));
				Player p = server.game.getNewPlayerInstance();
//				p.setPlayerNumber(++server.nextPlayerID);
				server.connections.add(c);
				server.game.players.add(p);
				c.addToSendQueue(new PacketConnection(p.number));
				server.synchronizeClientGameData(c);
				server.game.logger.info("Server received connection from "
						+ s.getInetAddress().getHostAddress() + "! Player ID is " + p.number);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Retrieves the local {@code InetAddress} of the {@code ServerSocket}
	 * 
	 * @return The {@code InetAddress}
	 */
	public InetAddress getInetAddress() {
		return this.ip;
	}
	
	/**
	 * Retrieves the port that is open
	 * 
	 * @return The port
	 */
	public int getPort() {
		return this.port;
	}
	
	/**
	 * Shuts down the listener thread
	 */
	public void shutdown() {
		this.listening = false;
		try {
			this.ss.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.interrupt();
	}
	
}
