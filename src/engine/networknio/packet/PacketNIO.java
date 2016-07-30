package engine.networknio.packet;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;

import engine.client.Client;
import engine.networknio.ChannelWrapper;
import engine.server.Server;

/**
 * The superclass for all Packets, or bits of information that are sent through the network
 * <p>
 * It is CRITICAL to provide a BLANK CONSTRUCTOR for any subclasses of {@code Packet} so that reflection
 * works! For example, {@link engine.example.PacketPlayerInput#PacketPlayerInput()}
 * <p>
 * Thanks to the Java NIO API, (and me deciding to not be super lazy), TCP and UDP protocols are now united
 * into a single Superclass for both. The subclasses for {@link PacketTCP TCP} and {@link PacketUDP UDP} serve
 * merely as identifiers, nothing more.
 * 
 * @author Kevin
 */
public abstract class PacketNIO {
	
	/**
	 * A {@code HashMap} with keys of Packet ID and values of class type
	 */
	public static HashMap<Integer, Class<? extends PacketNIO>> idtoclass = new HashMap<Integer, Class<? extends PacketNIO>>();
	
	/**
	 * A {@code HashMap} with keys of class type and values of Packet ID
	 */
	public static HashMap<Class<? extends PacketNIO>, Integer> classtoid = new HashMap<Class<? extends PacketNIO>, Integer>();
	
	/**
	 * A {@code HashMap} with keys of Packet ID and values of the sizes of the Packet
	 */
	public static HashMap<Class<? extends PacketNIO>, Integer> sizes = new HashMap<Class<? extends PacketNIO>, Integer>();
	
	/**
	 * The largest data size of a UDP packet
	 */
	public static int maxUDPSize;
	
	public PacketNIO() {
	}
	
	private static int availableID = 0;
	
	// Registers the default game engine packets
	static {
		// Static-sized packets
		registerPacket(PacketPing.class, 8);
		registerPacket(PacketConnection.class, 4);
		registerPacket(PacketEntityPosition.class, 20);
		
		// Dynamic-sized packets
		registerDynamicSizePacket(PacketChat.class);
		registerDynamicSizePacket(PacketGame.class);
		registerDynamicSizePacket(PacketObject.class);
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
	 * Writes the {@code PacketNIO}'s data to the given {@code ByteBuffer}.
	 * <p>
	 * This {@code ByteBuffer} includes the ID and Size for TCP, but not for UDP
	 * 
	 * @param buff
	 *            The {@code ByteBuffer} to write to
	 * @throws IOException
	 */
	protected abstract void writePacketData(ByteBuffer buff) throws IOException;
	
	/**
	 * Reads the {@code PacketNIO}'s data from the given {@code ByteBuffer}.
	 * <p>
	 * This {@code ByteBuffer} includes only the data of the {@code PacketNIO}, not the ID or Size.
	 * 
	 * @param buff
	 *            The {@code ByteBuffer} to read from
	 * @throws IOException
	 */
	protected abstract void readPacketData(ByteBuffer buff) throws IOException;
	
	/**
	 * Registers a {@code PacketNIO} class so that the system knows of its existence.
	 * <p>
	 * This method is reserved for {@code PacketNIO}s with dynamic data sizes. All it does is register the
	 * size as -1 bytes.
	 * 
	 * @param c
	 *            The class of {@code Packet} to register, for example {@code Packet.class}
	 * @return Whether the {@code Packet} registration was successful
	 */
	public static boolean registerDynamicSizePacket(Class<? extends PacketNIO> c) {
		return registerPacket(c, -1);
	}
	
	/**
	 * Registers a {@code PacketNIO} class so that the system knows of its existence.
	 * 
	 * @param c
	 *            The class of {@code PacketNIO} to register, for example {@code PacketNIO.class}
	 * @param size
	 *            The size of the Packet's data in bytes. For packets with dynamic sizes, be sure to override
	 *            {@link #getDataSize()} with the appropriate algorithms to determine the size. To simplify
	 *            the process of registering, use {@link #registerDynamicSizePacket(Class)}
	 * @return Whether the {@code Packet} registration was successful
	 */
	public static boolean registerPacket(Class<? extends PacketNIO> c, int size) {
		while (!registerPacket(c, size, availableID++));
		return true;
	}
	
	/**
	 * Registers a {@code PacketNIO} class so that the system knows of its existence.
	 * <p>
	 * Because the method {@code registerPacket(Class)} already handles IDs, it's recommended to use that
	 * method instead
	 * 
	 * @param c
	 *            The class of {@code PacketNIO} to register, for example {@code PacketNIO.class}
	 * @param size
	 *            The size of the Packet's data in bytes. For dynamic packet sizes, be sure to override
	 *            {@link #getDataSize()} with the appropriate algorithms to determine the size. This
	 *            parameter, then, does not matter.
	 * @param id
	 *            The integer ID to use
	 * @return Whether the {@code Packet} registration was successful
	 */
	protected static boolean registerPacket(Class<? extends PacketNIO> c, int size, int id) {
		if (idtoclass.keySet().contains(id)) {
			return false;
		} else {
			idtoclass.put(id, c);
			classtoid.put(c, id);
			sizes.put(c, size);
			if (PacketUDP.class.isAssignableFrom(c)) {
				if (size > maxUDPSize) {
					maxUDPSize = size;
				}
			}
			System.out.println("Registered Packet " + c.getName() + " with id " + id + " and size " + size);
		}
		return true;
	}
	
	/**
	 * Reads a {@code Packet} from a {@code DataInputStream}
	 * 
	 * @param is
	 * @throws IOException
	 * @return A new instance of a {@code Packet}
	 */
	public static PacketNIO readPacket(ChannelWrapper channel) throws IOException {
		return channel.readPacket();
	}
	
	/**
	 * Writes a {@code Packet} to the given {@code ChannelWrapper}
	 * 
	 * @param channel
	 * @param remote
	 * @param p
	 * @throws IOException
	 */
	public static void writePacket(ChannelWrapper channel, SocketAddress remote, PacketNIO p)
			throws IOException {
		channel.writePacket(remote, p);
	}
	
	/**
	 * Creates a new instance of a {@code Packet} from a given ID
	 * 
	 * @param id
	 *            The ID of the packet that it was registered with
	 * @return A new instance of a {@code Packet}
	 */
	protected static PacketNIO getNewPacket(int id) {
		try {
			Class<? extends PacketNIO> c = idtoclass.get(id);
			return (c == null ? null : c.newInstance());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Gets the size of this Packet's data in Bytes
	 * <p>
	 * {@code PacketNIO}s with dynamic Data Sizes, such as Strings, should override this method
	 */
	public int getDataSize() {
		return sizes.get(this.getClass());
	}
	
	/**
	 * Processes a {@code Packet} on the client side
	 * 
	 * @param c
	 *            The {@code Client} instance
	 */
	public abstract void processClient(Client c);
	
	/**
	 * Processes a {@code Packet} on the server side
	 * 
	 * @param player
	 *            The Connection ID the Packet was received from
	 * @param s
	 *            The {@code Server} instance
	 */
	public abstract void processServer(short player, Server s);
	
	/**
	 * Reads a {@code String} from a {@code ByteBuffer}
	 * 
	 * @param is
	 *            The {@code ByteBuffer}
	 * @return The String
	 * @throws IOException
	 */
	protected static String readString(ByteBuffer buff) throws IOException {
		int l = buff.getInt();
		StringBuilder s = new StringBuilder(l);
		for (int i = 0; i < l; i++) {
			s.append(buff.getChar());
		}
		return s.toString();
	}
	
	/**
	 * Writes a {@code String} to a {@code ByteBuffer}
	 * 
	 * @param buff
	 *            The {@code ByteBuffer}
	 * @param s
	 *            The String
	 * @throws IOException
	 */
	protected static void writeString(ByteBuffer buff, String s) throws IOException {
		buff.putInt(s.length());
		for (char c : s.toCharArray()) {
			buff.putChar(c);
		}
	}
	
	public static void getPacketDataFromBuffer(ByteBuffer buffer) {
		byte[] allData = buffer.array();
		getPacketDataFromArrays(Arrays.copyOfRange(allData, 0, 8),
				Arrays.copyOfRange(allData, 8, allData.length));
	}
	
	public static void getPacketDataFromBuffer(ByteBuffer idAndSizeBuff, ByteBuffer dataBuff) {
		byte[] isd = idAndSizeBuff.array();
		byte[] data = dataBuff.array();
		getPacketDataFromArrays(isd, data);
		
	}
	
	public static void getPacketDataFromArrays(byte[] isd, byte[] data) {
		int id = ((isd[0] << 24) + (isd[1] << 16) + (isd[2] << 8) + (isd[3] << 0));
		int size = ((isd[4] << 24) + (isd[5] << 16) + (isd[6] << 8) + (isd[7] << 0));
		System.out.println("Packet Data from Buffer:");
		System.out.print("ID:\t" + id + "\tSize:\t" + size + "\tData:\t");
		for (byte b : data) {
			System.out.print(String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0') + " ");
		}
		System.out.println();
	}
	
}
