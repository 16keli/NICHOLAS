package engine.server;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import engine.Player;
import engine.networknio.ConnectionNIO;
import engine.networknio.packet.PacketConnection;

/**
 * The Thread that runs on the server that listens for connection requests from {@code Client}s and enables
 * them to connect to the server
 * 
 * @author Kevin
 */
public class ServerNIOListenThread extends Thread {
	
	/**
	 * Whether or not the thread is listening
	 */
	private boolean listening = true;
	
	/**
	 * The server socket of this listener thread
	 */
	private ServerSocketChannel ss;
	
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
	public ServerNIOListenThread(Server s, int port) throws IOException {
		super("Server Connection Listening Thread");
		this.ss = ServerSocketChannel.open();
		this.port = port;
		this.ss.socket().setPerformancePreferences(0, 2, 1);
		this.server = s;
		this.ip = ss.socket().getInetAddress();
	}
	
	@Override
	public void run() {
		while (listening) {
			try {// I don't believe we will run into concurrency issues because only a single thread will be
					// created and accessing nextPlayerID at a time
				SocketChannel s = ss.accept();
				ConnectionNIO c = new ConnectionNIO(s, "Server-Side");
				//TODO: Turn this on again
//				Server.SERVER_BUS.post(new ConnectionEstablishedEvent(server.game, c));
				Player p = server.game.getNewPlayerInstance();
//				p.setPlayerNumber(++server.nextPlayerID);
				server.connections.addToList(c);
				server.game.players.add(p);
				c.addToSendQueue(new PacketConnection(p.number));
				server.synchronizeClientGameData(c);
				server.game.logger.info("Server received connection from "
						+ s.socket().getInetAddress().getHostAddress() + "! Player ID is " + p.number);
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
