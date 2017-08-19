package engine.networknio.packet;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Logger;

import engine.client.Client;
import engine.input.PacketActionQueue;
import engine.server.Server;

/**
 * The superclass for all Packets, or bits of information that are sent through the network
 * <p>
 * It is CRITICAL to provide a BLANK CONSTRUCTOR for any subclasses of {@code Packet} so that reflection
 * works! For example, {@link engine.example.PacketPlayerInput#PacketPlayerInput()}
 * <p>
 * Thanks to the Java NIO API, (and me deciding to not be super lazy), <strike>TCP and UDP protocols are now
 * united into a single Superclass for both. The subclasses for {@link PacketTCP TCP} and {@link PacketUDP
 * UDP} serve merely as identifiers, nothing more.</strike> These have been <b>REPLACED</b> by the new
 * {@code ConnectionNIO} methods that allow sending of packets through either protocol, depending on the need
 * or situation
 * <p>
 * Additionally, {@code PacketNIO} contains many useful static methods that can help with IO of certain things
 * to {@code ByteBuffer}s.
 * <ul>
 * <li>String: {@link #readString(ByteBuffer) read} and {@link #writeString(ByteBuffer, String) write}</li>
 * <li>Object: {@link #readObject(ByteBuffer) read} and {@link #writeObject(ByteBuffer, byte[]) write}. Note
 * that writing an Object requires the serialized version, which can be obtained with
 * {@link PacketObject#objectToBytes(Object)}</li>
 * <li>boolean[]: {@link #readBooleans(ByteBuffer) read} and {@link #writeBooleans(ByteBuffer, boolean[])
 * write}. This is due to the lack of inclusion of a native boolean write, for good reason. (Probably because
 * writing booleans one by one to a {@code ByteBuffer} would take up lots of space and cause quite a bit of
 * uncertainty)
 * </ul>
 * However, these methods do exist, should anyone need use of them.
 * <p>
 * Why use NIO? For the non-blocking feature, of course. That way, we can cut down on the number of active
 * threads, which is always nice.
 * 
 * @author Kevin
 */
public abstract class PacketNIO {
	
	
	/**
	 * The {@code PacketNIO} instance of {@code Logger}
	 */
	public static final Logger logger = Logger.getLogger("engine.packet");
	
	/**
	 * A {@code HashMap} with keys of Packet ID and values of class type
	 */
	public static HashMap<Integer, Class<? extends PacketNIO>> idtoclass = new HashMap<Integer, Class<? extends PacketNIO>>();
	
	/**
	 * A {@code HashMap} with keys of class type and values of Packet ID
	 */
	public static HashMap<Class<? extends PacketNIO>, Integer> classtoid = new HashMap<Class<? extends PacketNIO>, Integer>();
	
	public PacketNIO() {
	}
	
	// The next available packet ID
	private static int availableID = 0;
	
	/**
	 * Registers the default game engine packets
	 */
	static {
		// lol these comments are so old tbh
		// Static-sized packets
		registerPacket(PacketPing.class);
		registerPacket(PacketConnection.class);
		registerPacket(PacketEntityPosition.class);
		
		// Dynamic-sized packets
		registerPacket(PacketChat.class);
		registerPacket(PacketGame.class);
		registerPacket(PacketObject.class);
		registerPacket(PacketActionQueue.class);
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
	 * 
	 * @param buff
	 *            The {@code ByteBuffer} to write to
	 * @throws IOException
	 */
	public abstract void writePacketData(ByteBuffer buff) throws IOException;
	
	/**
	 * Reads the {@code PacketNIO}'s data from the given {@code ByteBuffer}.
	 * 
	 * @param buff
	 *            The {@code ByteBuffer} to read from
	 * @throws IOException
	 */
	public abstract void readPacketData(ByteBuffer buff) throws IOException;
	
	/**
	 * Registers a {@code PacketNIO} class so that the system knows of its existence.
	 * 
	 * @param c
	 *            The class of {@code PacketNIO} to register, for example {@code PacketNIO.class}
	 * @return Whether the {@code Packet} registration was successful
	 */
	public static boolean registerPacket(Class<? extends PacketNIO> c) {
		while (!registerPacket(c, availableID++)) {
			;
		}
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
	 * @param id
	 *            The integer ID to use
	 * @return Whether the {@code PacketNIO} registration was successful
	 */
	protected static boolean registerPacket(Class<? extends PacketNIO> c, int id) {
		if (idtoclass.keySet().contains(id)) {
			return false;
		} else {
			idtoclass.put(id, c);
			classtoid.put(c, id);
			logger.info("Registered Packet " + c.getName() + " with id " + id);
		}
		return true;
	}
	
	/**
	 * Creates a new instance of a {@code PacketNIO} from a given ID
	 * 
	 * @param id
	 *            The ID of the packet that it was registered with
	 * @return A new instance of a {@code PacketNIO}
	 */
	public static PacketNIO getNewPacket(int id) {
		try {
			Class<? extends PacketNIO> c = idtoclass.get(id);
			if (!idtoclass.containsKey(id)) {
				logger.warning("Tried to initialize Packet with ID " + id + ", but it doesn't exist!");
			}
			return (c == null ? null : c.newInstance());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Processes a {@code PacketNIO} on the client side
	 * 
	 * @param c
	 *            The {@code Client} instance
	 */
	public abstract void processClient(Client c);
	
	/**
	 * Processes a {@code PacketNIO} on the server side
	 * 
	 * @param i
	 *            The Connection ID the Packet was received from
	 * @param s
	 *            The {@code Server} instance
	 */
	public abstract void processServer(int i, Server s);
	
	/**
	 * Reads a {@code String} from a {@code ByteBuffer}
	 * 
	 * @param buff
	 *            The {@code ByteBuffer}
	 * @return The String
	 * @throws IOException
	 */
	public static String readString(ByteBuffer buff) {
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
	public static void writeString(ByteBuffer buff, String s) {
		buff.putInt(s.length());
		for (char c : s.toCharArray()) {
			buff.putChar(c);
		}
	}
	
	/**
	 * Reads an {@code Object} from a {@code ByteBuffer}
	 * 
	 * @param buff
	 *            The {@code ByteBuffer}
	 * @return
	 */
	public static Object readObject(ByteBuffer buff) {
		int size = buff.getInt();
		byte[] bytes = new byte[size];
		buff.get(bytes);
		return PacketObject.bytesToObject(bytes);
	}
	
	/**
	 * Writes an {@code Object} to a {@code ByteBuffer}. Be sure to have serialized the object beforehand with
	 * {@link PacketObject#objectToBytes(Object)}, otherwise the Packet ID and Data may become separated! That
	 * causes lots of problems!
	 * 
	 * @param buff
	 *            The {@code ByteBuffer}
	 * @param bytes
	 *            The Object to write, in bytes
	 */
	public static void writeObject(ByteBuffer buff, byte[] bytes) {
		buff.putInt(bytes.length);
		buff.put(bytes);
	}
	
	/**
	 * Reads a boolean array from the given {@code ByteBuffer}
	 * 
	 * @param buff
	 * @return
	 */
	public static boolean[] readBooleans(ByteBuffer buff) {
		int size = buff.getInt();
		boolean[] array = new boolean[size];
		byte[] bytes = new byte[((array.length + 7) / 8)];
		buff.get(bytes);
		for (int i = 0; i < array.length; i++) {
			array[i] = ((bytes[i / 8] >> (i % 8)) & 0b00000001) == 1;
		}
		return array;
	}
	
	/**
	 * Writes a boolean array to the given {@code ByteBuffer}
	 * 
	 * @param buff
	 * @param array
	 */
	public static void writeBooleans(ByteBuffer buff, boolean[] array) {
		buff.putInt(array.length);
		// The number of bytes required
		byte[] bytes = new byte[((array.length + 7) / 8)];
		for (int i = 0; i < array.length; i++) {
			bytes[i / 8] += ((array[i] ? 1 : 0) << (i % 8));
		}
		buff.put(bytes);
	}
	
	public static void printPacketDataFromBuffer(ByteBuffer buffer) {
		byte[] allData = buffer.array();
		printPacketDataFromArrays(Arrays.copyOfRange(allData, 0, 4),
				Arrays.copyOfRange(allData, 4, allData.length));
	}
	
	public static void printPacketDataFromBuffer(ByteBuffer idBuff, ByteBuffer dataBuff) {
		byte[] isd = idBuff.array();
		byte[] data = dataBuff.array();
		printPacketDataFromArrays(isd, data);
	}
	
	public static void printPacketDataFromArrays(byte[] isd, byte[] data) {
		System.out.println("Packet Data from Buffer:");
		printIDData(isd);
		printPacketData(data);
	}
	
	public static void printIDData(byte[] isd) {
		int id = ((isd[0] << 24) + (isd[1] << 16) + (isd[2] << 8) + (isd[3] << 0));
		System.out.println("ID:\t" + id);
	}
	
	public static void printPacketData(byte[] data) {
		printPacketDataToLimit(data, data.length);
	}
	
	public static void printPacketDataToLimit(ByteBuffer buffer) {
		printPacketDataToLimit(buffer.array(), buffer.limit());
	}
	
	public static void printPacketDataToLimit(byte[] data, int limit) {
		printPacketDataToLimit(data, limit, System.out);
	}
	
	/**
	 * Prints data to a certain limit to the given {@code PrintStream}
	 * 
	 * @param data
	 *            The data extracted from the {@code Packet}
	 * @param limit
	 *            The limit to print data to
	 * @param p
	 *            The {@code PrintStream} to output to
	 */
	public static void printPacketDataToLimit(byte[] data, int limit, PrintStream p) {
		p.print("Limit:\t" + limit + "\tData:\t");
		for (int i = 0; i < limit; i++) {
			p.print(String.format("%2s", Integer.toHexString(data[i] & 0xFF)).replace(' ', '0') + " ");
		}
		p.println();
	}
	
}
