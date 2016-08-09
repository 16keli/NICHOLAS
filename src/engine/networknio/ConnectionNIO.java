package engine.networknio;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SocketChannel;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import engine.networknio.packet.PacketNIO;
import engine.networknio.packet.PacketTCP;
import engine.networknio.packet.PacketTCP.TCPChannelWrapper;
import engine.networknio.packet.PacketUDP;
import engine.networknio.packet.PacketUDP.UDPChannelWrapper;

/**
 * The Connection between the Client and Server sides
 * <p>
 * Used to send {@link engine.engine.networknio.packet.PacketNIO Packets} back and forth between the two sides
 * <p>
 * Packets are sent at the end of every tick, but have a separate thread to read them.
 * 
 * @author Kevin
 */
public class ConnectionNIO {
	
	/**
	 * The Logger instance
	 */
	public Logger logger;
	
	/**
	 * The {@code Thread} that reads incoming {@code Packet} data
	 */
	private Thread readThread;
	
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
	 * The {@code ByteBuffer} that the TCP Channel sends
	 */
	private ByteBuffer tcpBuffer;
	
	/**
	 * The {@code ByteBuffer that the UDP Channel sends}
	 */
	private ByteBuffer udpBuffer;
	
	/**
	 * The {@code ByteBuffer} that TCP data is read into
	 */
	private ByteBuffer tcpIn;
	
	/**
	 * The {@code ByteBuffer} that UDP data is read into
	 */
	private ByteBuffer udpIn;
	
	/**
	 * The remote {@code SocketAddress}
	 */
	private SocketAddress remoteAddress;
	
	private boolean running = true;
	
	private boolean terminating = false;
	
	public String sourceName;
	
	public long ping;
	
	public boolean threadsActive;
	
	/**
	 * Packets read and awaiting processing
	 */
	private List<PacketNIO> readPackets = Collections.synchronizedList(new LinkedList<PacketNIO>());
	
	/**
	 * ChannelWrapper around the TCP Channel
	 */
	private ProtocolWrapper tcpWrapper;
	
	/**
	 * ChannelWrapper around the UDP Channel
	 */
	private ProtocolWrapper udpWrapper;
	
	public ConnectionNIO(SocketChannel s, String source, int tcpSize, int udpSize) throws IOException {
		this(s, source, tcpSize, udpSize, false);
	}
	
	/**
	 * Creates a new {@code Connection} ready to send and receive data
	 * 
	 * @param s
	 *            The {@code SocketChannel} to connect to
	 * @param source
	 *            The name for the source
	 * @param tcpSize
	 *            The size of the TCP Write buffer
	 * @param udpSize
	 *            The size of the UDP Write buffer
	 * @param threads
	 *            Whether to start the own threads
	 * @throws IOException
	 *             If an I/O stream cannot be opened
	 */
	public ConnectionNIO(SocketChannel s, String source, int tcpSize, int udpSize, boolean threads)
			throws IOException {
		this.sourceName = source;
		this.legacySocket = s.socket();
		this.remoteAddress = this.legacySocket.getRemoteSocketAddress();
		
		this.tcpChannel = s;
		this.udpChannel = DatagramChannel.open();
		this.udpChannel.bind(this.legacySocket.getLocalSocketAddress());
		this.udpChannel.connect(this.remoteAddress);
		
		this.tcpChannel.configureBlocking(false);
		// The Nagle algorithm is not necessary as we are doing it manually
		this.tcpChannel.setOption(StandardSocketOptions.TCP_NODELAY, true);
		this.udpChannel.configureBlocking(false);
		
		this.tcpBuffer = ByteBuffer.allocate(tcpSize);
		this.udpBuffer = ByteBuffer.allocate(udpSize);
		this.tcpIn = ByteBuffer.allocate(tcpSize);
		this.udpIn = ByteBuffer.allocate(udpSize);
		
		System.out.println(source + " UDP is bound to " + this.udpChannel.getLocalAddress()
				+ " and connected to " + this.udpChannel.getRemoteAddress());
				
		this.tcpWrapper = new TCPChannelWrapper(this.tcpChannel, this.tcpIn, this.tcpBuffer, this);
		this.udpWrapper = new UDPChannelWrapper(this.udpChannel, this.udpIn, this.udpBuffer, this);
		
		this.logger = Logger.getLogger("engine.connection." + this.sourceName);
		this.logger.info("Local Address:\t" + this.legacySocket.getLocalSocketAddress());
		this.logger.info("Remote Address:\t" + this.remoteAddress);
		
		if (threads) {
			this.readThread = new ThreadConnectionNIORead(this);
			this.logger.info("Packet Read Thread ID:\t" + this.readThread.getId());
			this.readThread.start();
		}
		
		this.threadsActive = threads;
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
	 * Sends all the {@code PacketNIO} data in the sending queue
	 */
	public void sendPackets() {
		try {
			// Send only if there is actually data contained
			if (this.tcpBuffer.position() > 0) {
				// End delimiter
				this.tcpBuffer.putInt(Integer.MIN_VALUE);
				this.tcpWrapper.sendData(this.remoteAddress);
			}
			if (this.udpBuffer.position() > 0) {
				// End delimiter
				this.udpBuffer.putInt(Integer.MIN_VALUE);
				this.udpWrapper.sendData(this.remoteAddress);
			}
		} catch (Exception e) {
			// Swallow because it's gonna happen a lot
		}
	}
	
	/**
	 * Adds a {@code Packet} to the send queue of the connection
	 * 
	 * @param p
	 *            The {@code Packet} to send in the future
	 */
	public void addToSendQueue(PacketNIO p) {
		if (!this.terminating) {
			try {
				this.getAppropriateWrapper(p).writePacket(p);
				if (!PacketNIO.idtoclass.containsKey(p.getID())) {
					System.err.println("An unregistered type of PacketNIO was added to " + this.sourceName
							+ "'s send queue! ID is " + p.getID() + ", class is " + p.getClass().getName());
				}
			} catch (Exception e) {
				// Swallow the exception because it's gonna happen a lot
			}
		}
	}
	
	/**
	 * Retrieves a {@code Packet} that needs to be processed
	 * 
	 * @return A {@code Packet} that needs to be processed
	 */
	public PacketNIO getReadPacket() {
		try {
			return this.readPackets.remove(0);
		} catch (Exception e) {
//			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Interrupts the reading <strike>and writing</strike> {@code Threads}
	 */
	public void wakeThreads() {
		if (this.readThread != null) {
			this.readThread.interrupt();
		}
	}
	
	/**
	 * Attempts to read a {@code PacketNIO} from each source. This method will return if a {@code PacketNIO}
	 * was read successfully from either source, first reading from the TCP channel, then the UDP one.
	 * 
	 * @return Whether a {@code PacketNIO} was successfully read
	 */
	private boolean readPackets() {
		try {
			if (this.tcpWrapper.readData()) {
				this.readPackets.addAll(this.tcpWrapper.readFully());
				return true;
			}
			if (this.udpWrapper.readData()) {
				this.readPackets.addAll(this.udpWrapper.readFully());
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
			
			this.remoteAddress = null;
			
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
	public static boolean readPackets(ConnectionNIO c) {
		return c.readPackets();
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
	 * Gets the appropriate {@code ChannelWrapper} for the given {@code PacketNIO}
	 * 
	 * @param p
	 * @return
	 */
	public ProtocolWrapper getAppropriateWrapper(PacketNIO p) {
		if (p instanceof PacketTCP) {
			return this.tcpWrapper;
		} else if (p instanceof PacketUDP) {
			return this.udpWrapper;
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
		return this.remoteAddress;
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
	
	/**
	 * Disconnects the connection by stopping the threads and closing the channels
	 * 
	 * @throws IOException
	 */
	public void disconnect() throws IOException {
		this.wakeThreads();
		this.tcpChannel.close();
		this.udpChannel.close();
		
	}
	
}
