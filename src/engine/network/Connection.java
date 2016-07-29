package engine.network;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import engine.network.packet.Packet;
import engine.network.packet.udp.PacketUDP;

/**
 * The Connection between the Client and Server sides
 * <p>
 * Used to send {@link engine.network.packet.Packet Packets} back and forth between the two sides
 * 
 * @author Kevin
 */
public class Connection {
	
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
	 * The UDP send queue
	 */
	private Object udpSendLock;
	
	/**
	 * The {@code Thread} that reads incoming {@code Packet} data
	 */
	private Thread readThread;
	
	/**
	 * The {@code Thread} that writes pending {@code Packet} data
	 */
	private Thread writeThread;
	
	/**
	 * The {@code Thread} that reads incoming {@code PacketUDP} data
	 */
	private Thread udpReadThread;
	
	/**
	 * The {@code Thread} that writes pending {@code Packet} data
	 */
	private Thread udpWriteThread;
	
	/**
	 * The {@code Socket} that the {@code Connection} is currently connected to
	 */
	private Socket sock;
	
	/**
	 * The UDP Socket
	 */
	private DatagramSocket udpSock;
	
	/**
	 * The {@code SocketAddress}
	 */
	private SocketAddress sadd;
	
	private DataInputStream is;
	
	private DataOutputStream os;
	
	private boolean running = true;
	
	private boolean terminating = false;
	
	public String sourceName;
	
	public long ping;
	
	/**
	 * Packets read and awaiting processing
	 */
	private List<Packet> readPackets = Collections.synchronizedList(new LinkedList<Packet>());
	
	/**
	 * Packets awaiting sending
	 */
	private List<Packet> sendQueue = Collections.synchronizedList(new LinkedList<Packet>());
	
	/**
	 * The UDP Read Queue
	 */
	private List<PacketUDP> udpReadQueue = Collections.synchronizedList(new LinkedList<PacketUDP>());
	
	/**
	 * The UDP Write Queue
	 */
	private List<PacketUDP> udpSendQueue = Collections.synchronizedList(new LinkedList<PacketUDP>());
	
	/**
	 * Creates a new {@code Connection} ready to send and receive data
	 * 
	 * @param s
	 *            The {@code Socket} to connect to
	 * @throws IOException
	 *             If an I/O stream cannot be opened
	 */
	public Connection(Socket s, String source) throws IOException {
		this.sendQueueLock = new Object();
		this.udpSendLock = new Object();
		this.sourceName = source;
		this.sock = s;
		this.sadd = s.getRemoteSocketAddress();
		this.udpSock = new DatagramSocket(this.sock.getLocalSocketAddress());
		this.udpSock.connect(this.sadd);
		this.is = new DataInputStream(new BufferedInputStream(s.getInputStream()));
		this.os = new DataOutputStream(new BufferedOutputStream(s.getOutputStream()));
		this.readThread = new ThreadConnectionRead(this);
		this.writeThread = new ThreadConnectionWrite(this);
		this.udpReadThread = new ThreadUDPConnectionRead(this);
		this.udpWriteThread = new ThreadUDPConnectionWrite(this);
		this.logger = Logger.getLogger("engine.connection." + this.sourceName);
		this.logger.info("Local Address:\t" + this.sock.getLocalSocketAddress());
		this.logger.info("Remote Address:\t" + this.sadd);
		this.logger.info("Packet Read Thread ID:\t" + this.readThread.getId());
		this.logger.info("Packet Write Thread ID:\t" + this.writeThread.getId());
		this.readThread.start();
		this.writeThread.start();
		this.udpReadThread.start();
		this.udpWriteThread.start();
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
	 * Gets the size of the read {@code PacketUDP} queue
	 * 
	 * @return
	 */
	public int getUDPReadQueueSize() {
		return this.udpReadQueue.size();
	}
	
	/**
	 * Adds a {@code Packet} to the send queue of the connection
	 * 
	 * @param p
	 *            The {@code Packet} to send in the future
	 */
	public void addToSendQueue(Packet p) {
		if (!this.terminating) {
			synchronized (sendQueueLock) {
				this.sendQueue.add(p);
				if (!Packet.idtoclass.containsKey(p.getID())) {
					System.err.println("An unregistered type of Packet was added to the send queue! ID is "
							+ p.getID() + ", class is " + p.getClass().getName());
				}
			}
		}
	}
	
	/**
	 * Adds a {@code PacketUDP} to the send queue of the connection
	 * 
	 * @param p
	 *            The {@code PacketUDP} to send in the future
	 */
	public void addUDPToSendQueue(PacketUDP p) {
		if (!this.terminating) {
			synchronized (this.udpSendLock) {
				this.udpSendQueue.add(p);
				if (!PacketUDP.idtoclass.containsKey(p.getID())) {
					System.err.println("An unregistered type of Packet was added to the send queue! ID is "
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
			Packet p = getSendPacket();
			if (p != null) {
				Packet.writePacket(os, p);
				return true;
			}
		} catch (Exception e) {
//			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * Sends the first {@code PacketUDP} available. Called multiple times by the writer thread, there is no
	 * need to call this method
	 * 
	 * @return Whether a {@code PacketUDP} was successfully sent
	 */
	private boolean sendUDPPacket() {
		try {
			PacketUDP p = getSendUDPPacket();
			if (p != null) {
				PacketUDP.writePacket(this.udpSock, this.sadd, p);
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
	private Packet getSendPacket() {
		Packet p = null;
		synchronized (sendQueueLock) {
			while (!this.sendQueue.isEmpty() && p == null) {
				p = this.sendQueue.remove(0);
			}
			return p;
		}
	}
	
	/**
	 * Retrieves a {@code Packet} to be sent in the future
	 * 
	 * @return The first {@code Packet} in the send queue
	 */
	private PacketUDP getSendUDPPacket() {
		PacketUDP p = null;
		synchronized (this.udpSendLock) {
			while (!this.udpSendQueue.isEmpty() && p == null) {
				p = this.udpSendQueue.remove(0);
			}
			return p;
		}
	}
	
	/**
	 * Retrieves a {@code Packet} that needs to be processed
	 * 
	 * @return A {@code Packet} that needs to be processed
	 */
	public Packet getReadPacket() {
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
	 * Retrieves a {@code PacketUDP} that needs to be processed
	 * 
	 * @return A {@code PacketUDP} that needs to be processed
	 */
	public PacketUDP getReadUDPPacket() {
		try {
			return this.udpReadQueue.remove(0);
		} catch (Exception e) {
			return null;
		}
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
		
		if (this.udpReadThread != null) {
			this.udpReadThread.interrupt();
		}
		
		if (this.udpWriteThread != null) {
			this.udpWriteThread.interrupt();
		}
	}
	
	/**
	 * Attempts to read a {@code Packet} from the input stream
	 * 
	 * @return Whether a {@code Packet} was successfully read
	 */
	private boolean readPacket() {
//		synchronized (readQueueLock) {
		try {
			Packet p = Packet.readPacket(is);
			if (p != null) {
				this.readPackets.add(p);
				return true;
			}
		} catch (IOException e) {
			e.printStackTrace();
//			Engine.getClient().disconnect();
		}
		return false;
//		}
	}
	
	/**
	 * Attempts to read a {@code PacketUDP} from the {@code DatagramSocket}
	 * 
	 * @return Whether a {@code PacketUDP} was successfully read
	 */
	private boolean readUDPPacket() {
		try {
			PacketUDP p = PacketUDP.readPacket(this.udpSock);
			if (p != null) {
				this.udpReadQueue.add(p);
				return true;
			}
		} catch (IOException e) {
			e.printStackTrace();
//			Engine.getClient().disconnect();
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
				this.is.close();
				this.os.close();
				this.sock.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			this.is = null;
			this.os = null;
			this.sock = null;
			this.sadd = null;
			this.udpSock = null;
			
			this.wakeThreads();
		}
	}
	
	/**
	 * Gets the {@code Connection} to read a {@code Packet}
	 * 
	 * @param c
	 *            The {@code Connection} to use
	 * @return Whether a {@code Packet} was successfully read
	 */
	public static boolean readPacket(Connection c) {
		return c.readPacket();
	}
	
	/**
	 * Gets the {@code Connection} to write the first {@code Packet} available
	 * 
	 * @param c
	 *            The {@code Connection} to use
	 * @return Whether a {@code Packet} was successfully sent
	 */
	public static boolean sendPacket(Connection c) {
		return c.sendPacket();
	}
	
	/**
	 * Gets the {@code Connection} to read a {@code PacketUDP}
	 * 
	 * @param c
	 *            The {@code Connection} to use
	 * @return Whether a {@code PacketUDP} was successfully read
	 */
	public static boolean readUDPPacket(Connection c) {
		return c.readUDPPacket();
	}
	
	/**
	 * Gets the {@code Connection} to write the first {@code PacketUDP} available
	 * 
	 * @param c
	 *            The {@code Connection} to use
	 * @return Whether a {@code PacketUDP} was successfully sent
	 */
	public static boolean sendUDPPacket(Connection c) {
		return c.sendUDPPacket();
	}
	
	/**
	 * Whether the {@code Connection} is currently active
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
	 * Retrieves the {@code DataInputStream} used by this {@code Connection}
	 * 
	 * @return
	 */
	public DataInputStream getInputStream() {
		return this.is;
	}
	
	/**
	 * Retrieves the {@code DataOutputStream} used by this {@code Connection}
	 * 
	 * @return
	 */
	public DataOutputStream getOutputStream() {
		return this.os;
	}
	
	/**
	 * Retrieves the {@code Socket} used by this {@code Connection}
	 * 
	 * @return
	 */
	public Socket getSocket() {
		return this.sock;
	}
	
	/**
	 * Retrieves the {@code DatagramSocket} used by this {@code Connection}
	 * 
	 * @return
	 */
	public DatagramSocket getUDPSocket() {
		return this.udpSock;
	}
	
	/**
	 * Retrieves the {@code SocketAddress} used by this {@code Connection}
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
