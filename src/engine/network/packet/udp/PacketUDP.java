package engine.network.packet.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.HashMap;

import engine.client.Client;
import engine.server.Server;

/**
 * The superclass for all Packets, or bits of information that are sent through the network via UDP
 * <p>
 * It is CRITICAL to provide a BLANK CONSTRUCTOR for any subclasses of {@code PacketUDP} so that reflection
 * works! For example, {@link engine.network.packet.udp.PacketEntityPosition#PacketEntityPosition()}
 * <p>
 * Unlike the TCP protocols used for {@link engine.network.packet.Packet Packet}, the UDP protocols have less
 * overhead, but are not guaranteed to arrive at their destination. Thus, {@code PacketUDP} should only be
 * used if the Packet in question is to be sent and received many times and can afford to drop some, as is the
 * case with {@code PacketEntityPosition}.
 * <p>
 * The benefits of using UDP instead of TCP in those cases are the lower overhead, and the fact that it is
 * okay for some of the {@code PacketUDP}s to not make it through to their destination.
 * <p>
 * The only reason the two are not linked is because I'm lazy the masking tape holding this entire thing
 * together is still strong... enough
 * 
 * @see engine.network.packet.Packet Packet
 * @author Kevin
 */
public abstract class PacketUDP {
	
	/**
	 * A {@code HashMap} with keys of Packet ID and values of class type
	 */
	public static HashMap<Integer, Class<? extends PacketUDP>> idtoclass = new HashMap<Integer, Class<? extends PacketUDP>>();
	
	/**
	 * A {@code HashMap} with keys of class type and values of Packet ID
	 */
	public static HashMap<Class<? extends PacketUDP>, Integer> classtoid = new HashMap<Class<? extends PacketUDP>, Integer>();
	
	public PacketUDP() {
	}
	
	private static int availableID = 0;
	
	// Registers the default game engine packets
	static {
		PacketUDP.registerUDPPacket(PacketEntityPosition.class);
	}
	
	/**
	 * Retrieves the ID of the {@code Packet}
	 * 
	 * @return The ID that the {@code Packet} was registered with
	 */
	public final int getID() {
		return classtoid.get(this.getClass());
	}
	
	/**
	 * Read any data necessary from a {@code DatagramSocket}.
	 * <p>
	 * Should decode the data sent by {@code writePacketData}
	 * 
	 * @param sock
	 *            The socket
	 * @throws IOException
	 */
	protected abstract void readPacketData(DatagramSocket sock) throws IOException;
	
	/**
	 * Writes any data necessary to a {@code DatagramSocket}.
	 * <p>
	 * Should encode data for sending so that {@code readPacketData} can decode it
	 * 
	 * @param sock
	 *            The socket
	 * @param add
	 *            The {@code SocketAddress} of the destination
	 * @throws IOException
	 */
	protected abstract void writePacketData(DatagramSocket sock, SocketAddress add) throws IOException;
	
	/**
	 * Registers a {@code PacketUDP} class so that the system knows of its existence
	 * 
	 * @param c
	 *            The class of {@code PacketUDP} to register, for example {@code PacketUDP.class}
	 * @return Whether the {@code PacketUDP} registration was successful
	 */
	public static boolean registerUDPPacket(Class<? extends PacketUDP> c) {
		while (!registerUDPPacket(c, availableID++));
		return true;
	}
	
	/**
	 * Registers a {@code PacketUDP} class so that the system knows of its existence
	 * <p>
	 * Because the method {@code registerUDPPacket(Class)} already handles IDs, it's recommended to use that
	 * method instead
	 * 
	 * @param c
	 *            The class of {@code PacketUDP} to register, for example {@code PacketUDP.class}
	 * @param id
	 *            The integer ID to use
	 * @return Whether the {@code PacketUDP} registration was successful
	 */
	protected static boolean registerUDPPacket(Class<? extends PacketUDP> c, int id) {
		if (idtoclass.keySet().contains(id)) {
			return false;
		} else {
			idtoclass.put(id, c);
			classtoid.put(c, id);
		}
		return true;
	}
	
	/**
	 * Reads a {@code PacketUDP} from a {@code DataInputStream}
	 * 
	 * @param udpSock
	 * @param is
	 * @throws IOException
	 * @return A new instance of a {@code PacketUDP}
	 */
	public static PacketUDP readPacket(DatagramSocket udpSock) throws IOException {
		DatagramPacket idPack = new DatagramPacket(new byte[4], 4);
		udpSock.receive(idPack);
		int id = ByteBuffer.wrap(idPack.getData()).getInt();
		PacketUDP p = null;
		if (id < 0) {
			return null;
		}
		p = getNewPacket(id);
		p.readPacketData(udpSock);
		return p;
	}
	
	/**
	 * Writes a {@code PacketUDP} to a {@code DatagramSocket}
	 * 
	 * @param udpSock
	 * @param add
	 * @param p
	 * @throws IOException
	 */
	public static void writePacket(DatagramSocket udpSock, SocketAddress add, PacketUDP p)
			throws IOException {
		byte[] idArray = ByteBuffer.allocate(4).putInt(p.getID()).array();
		DatagramPacket idPack = new DatagramPacket(idArray, 4, add);
		udpSock.send(idPack);
		p.writePacketData(udpSock, add);
	}
	
	/**
	 * Creates a new instance of a {@code PacketUDP} from a given ID
	 * 
	 * @param id
	 *            The ID of the packet that it was registered with
	 * @return A new instance of a {@code PacketUDP}
	 */
	private static PacketUDP getNewPacket(int id) {
		try {
			Class<? extends PacketUDP> c = idtoclass.get(id);
			return (c == null ? null : c.newInstance());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Processes a {@code PacketUDP} on the client side
	 * 
	 * @param c
	 *            The {@code Client} instance
	 */
	public abstract void processClient(Client c);
	
	/**
	 * Processes a {@code PacketUDP} on the server side
	 * 
	 * @param player
	 *            The Connection ID the Packet was received from
	 * @param s
	 *            The {@code Server} instance
	 */
	public abstract void processServer(short player, Server s);
	
}
