package engine.networknio;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import engine.network.packet.Packet;
import engine.networknio.packet.PacketNIO;
import engine.networknio.packet.PacketNIOTCP;
import engine.networknio.packet.PacketNIOUDP;

/**
 * The Connection between the Client and Server sides
 * <p>
 * Used to send {@link engine.engine.networknio.packet.PacketNIO Packets} back and forth between the two sides
 * <p>
 * Also features the standard Java IO for legacy features, as well as simplifying transfer of {@code Object}s
 * between sides.
 * 
 * @author Kevin
 */
public class ConnectionNIO {
	
	/**
	 * The Logger instance
	 */
	public Logger logger;
	
	/**
	 * A synchronization lock used to synchronize sending of {@code Packet}s to avoid the issues that come
	 * with {@code Thread}s GRR I'm mad
	 */
	private Object sendQueueLock;
	
	/**
	 * The {@code Thread} that reads incoming {@code Packet} data
	 */
	private Thread readThread;
	
	/**
	 * The {@code Thread} that writes pending {@code Packet} data
	 */
	private Thread writeThread;
	
	/**
	 * The Legacy IO feature kept around for convenience
	 */
	private Socket legacySocket;
	
	/**
	 * The Channel used for TCP IO
	 */
	private SocketChannel tcpChannel;
	
	/**
	 * The Channel used for UDP IO
	 */
	private DatagramChannel udpChannel;
	
	/**
	 * The remote {@code SocketAddress}
	 */
	private SocketAddress sadd;
	
	private boolean running = true;
	
	private boolean terminating = false;
	
	public String sourceName;
	
	public long ping;
	
	/**
	 * Packets read and awaiting processing
	 */
	private List<PacketNIO> readPackets = Collections.synchronizedList(new LinkedList<PacketNIO>());
	
	/**
	 * Packets awaiting sending
	 */
	private List<PacketNIO> sendQueue = Collections.synchronizedList(new LinkedList<PacketNIO>());
	
	/**
	 * Creates a new {@code Connection} ready to send and receive data
	 * 
	 * @param s
	 *            The {@code SocketChannel} to connect to
	 * @throws IOException
	 *             If an I/O stream cannot be opened
	 */
	public ConnectionNIO(SocketChannel s, String source) throws IOException {
		this.sendQueueLock = new Object();
		this.sourceName = source;
		this.legacySocket = s.socket();
		this.sadd = this.legacySocket.getRemoteSocketAddress();
		
		this.tcpChannel.bind(legacySocket.getLocalSocketAddress());
		this.udpChannel = DatagramChannel.open();
		this.udpChannel.bind(legacySocket.getLocalSocketAddress());
		this.udpChannel.connect(this.sadd);
		
		this.readThread = new ThreadConnectionNIORead(this);
		this.writeThread = new ThreadConnectionNIOWrite(this);
		this.logger = Logger.getLogger("engine.connection." + this.sourceName);
		this.logger.info("Local Address:\t" + legacySocket.getLocalSocketAddress());
		this.logger.info("Remote Address:\t" + this.sadd);
		this.logger.info("Packet Read Thread ID:\t" + this.readThread.getId());
		this.logger.info("Packet Write Thread ID:\t" + this.writeThread.getId());
		this.readThread.start();
		this.writeThread.start();
	}
	
	/**
	 * Gets the size of the read {@code Packet} queue
	 * 
	 * @return
	 */
	public int getReadQueueSize() {
		return this.readPackets.size();
	}
	
	/**
	 * Adds a {@code Packet} to the send queue of the connection
	 * 
	 * @param p
	 *            The {@code Packet} to send in the future
	 */
	public void addToSendQueue(PacketNIO p) {
		if (!this.terminating) {
			synchronized (sendQueueLock) {
				this.sendQueue.add(p);
				if (!Packet.idtoclass.containsKey(p.getID())) {
					System.err.println("An unregistered type of PacketNIO was added to the send queue! ID is "
							+ p.getID() + ", class is " + p.getClass().getName());
				}
			}
		}
	}
	
	/**
	 * Sends the first {@code Packet} available. Called multiple times by the writer thread, there is no need
	 * to call this method
	 * 
	 * @return Whether a {@code Packet} was successfully sent
	 */
	private boolean sendPacket() {
		try {
			PacketNIO p = getSendPacket();
			if (p != null) {
				PacketNIO.writePacket(this.getAppropriateWriteChannel(p), p);
				return true;
			}
		} catch (Exception e) {
//			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * Retrieves a {@code Packet} to be sent in the future
	 * 
	 * @return The first {@code Packet} in the send queue
	 */
	private PacketNIO getSendPacket() {
		PacketNIO p = null;
		synchronized (sendQueueLock) {
			while (!this.sendQueue.isEmpty() && p == null) {
				p = this.sendQueue.remove(0);
			}
			return p;
		}
	}
	
	/**
	 * Retrieves a {@code Packet} that needs to be processed
	 * 
	 * @return A {@code Packet} that needs to be processed
	 */
	public PacketNIO getReadPacket() {
//		synchronized (readQueueLock) {
		try {
			return this.readPackets.remove(0);
		} catch (Exception e) {
//			e.printStackTrace();
			return null;
		}
//		}
	}
	
	/**
	 * Interrupts the reading and writing {@code Threads}
	 */
	public void wakeThreads() {
		if (this.readThread != null) {
			this.readThread.interrupt();
		}
		
		if (this.writeThread != null) {
			this.writeThread.interrupt();
		}
	}
	
	/**
	 * Attempts to read a {@code PacketNIO} from each source. This method will return if a {@code PacketNIO}
	 * was read successfully from either source, first reading from the TCP channel, then the UDP one.
	 * 
	 * @return Whether a {@code PacketNIO} was successfully read
	 */
	private boolean readPacket() {
		try {
			PacketNIO tcp = PacketNIO.readPacket(this.tcpChannel);
			if (tcp != null) {
				this.readPackets.add(tcp);
				return true;
			}
			PacketNIO udp = PacketNIO.readPacket(this.udpChannel);
			if (udp != null) {
				this.readPackets.add(udp);
				return true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * Terminates this {@code Connection} as well as all associated threads
	 */
	public void networkShutdown() {
		if (this.running) {
			this.terminating = true;
			this.running = false;
			
			try {
				this.tcpChannel.close();
				this.udpChannel.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			this.sadd = null;
			
			this.wakeThreads();
		}
	}
	
	/**
	 * Gets the {@code ConnectionNIO} to read a {@code PacketNIO}
	 * 
	 * @param c
	 *            The {@code ConnectionNIO} to use
	 * @return Whether a {@code PacketNIO} was successfully read
	 */
	public static boolean readPacket(ConnectionNIO c) {
		return c.readPacket();
	}
	
	/**
	 * Gets the {@code ConnectionNIO} to write the first {@code PacketNIO} available
	 * 
	 * @param c
	 *            The {@code ConnectionNIO} to use
	 * @return Whether a {@code PacketNIO} was successfully sent
	 */
	public static boolean sendPacket(ConnectionNIO c) {
		return c.sendPacket();
	}
	
	/**
	 * Whether the {@code ConnectionNIO} is currently active
	 * 
	 * @return
	 */
	public boolean isRunning() {
		return this.running;
	}
	
	/**
	 * Whether the {@code Conncetion} is currently terminating
	 * 
	 * @return
	 */
	public boolean isTerminating() {
		return this.terminating;
	}
	
	/**
	 * Gets the appropriate Channel for the given {@code PacketNIO}
	 * 
	 * @param p
	 * @return
	 */
	public WritableByteChannel getAppropriateWriteChannel(PacketNIO p) {
		if (p instanceof PacketNIOTCP) {
			return this.tcpChannel;
		} else if (p instanceof PacketNIOUDP) {
			return this.udpChannel;
		} else {
			return null;
		}
	}
	
	/**
	 * Retrieves the remote {@code SocketAddress} used by this {@code ConnectionNIO}
	 * 
	 * @return
	 */
	public SocketAddress getSocketAddress() {
		return this.sadd;
	}
	
	/**
	 * Sets the Ping of this {@code Connection}
	 * <p>
	 * Shouldn't really be public...
	 * 
	 * @param l
	 */
	public void setPing(long l) {
		this.ping = l;
	}
	
}
