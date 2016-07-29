package engine.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;

import engine.client.Client;
import engine.network.synchro.PacketGame;
import engine.network.synchro.PacketTile;
import engine.server.Server;

/**
 * The superclass for all Packets, or bits of information that are sent through the network via TCP
 * <p>
 * It is CRITICAL to provide a BLANK CONSTRUCTOR for any subclasses of {@code Packet} so that reflection
 * works! For example, {@link engine.example.PacketPlayerInput#PacketPlayerInput()}
 * <p>
 * For performance reasons, Packet I/O uses {@code DataInputStream} and {@code DataOutputStream}. However, if
 * it is necessary for Object I/O, use {@link engine.network.packet.PacketObject}
 * <p>
 * Unlike the UDP protocols used in {@link engine.network.packet.udp.PacketUDP PacketUDP}, the TCP protocols
 * used have higher overhead but ensure that the data reaches its destination. Thus, use this in cases where
 * the data is important and cannot afford to be lost, such as {@link engine.example.PacketPlayerScore keeping
 * score}
 * 
 * @see PacketObject
 * @see engine.network.packet.udp.PacketUDP PacketUDP
 * @author Kevin
 */
public abstract class Packet {
	
	/**
	 * A {@code HashMap} with keys of Packet ID and values of class type
	 */
	public static HashMap<Integer, Class<? extends Packet>> idtoclass = new HashMap<Integer, Class<? extends Packet>>();
	
	/**
	 * A {@code HashMap} with keys of class type and values of Packet ID
	 */
	public static HashMap<Class<? extends Packet>, Integer> classtoid = new HashMap<Class<? extends Packet>, Integer>();
	
	public Packet() {
	}
	
	private static int availableID = 0;
	
	// Registers the default game engine packets
	static {
		Packet.registerPacket(PacketChat.class);
		Packet.registerPacket(PacketConnection.class);
		Packet.registerPacket(PacketDisconnect.class);
		Packet.registerPacket(PacketPing.class);
		Packet.registerPacket(PacketGame.class);
		Packet.registerPacket(PacketTile.class);
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
	 * Read any data necessary from a {@code DataInputStream}.
	 * <p>
	 * Should decode the data sent by {@code writePacketData}
	 * <p>
	 * Note the accompanying {@link #readString(DataInputStream) String reading} and
	 * {@link #readByteArray(DataInputStream) byte[] reading} methods, if necessary
	 * 
	 * @param is
	 *            The stream to read from
	 * @throws IOException
	 */
	protected abstract void readPacketData(DataInputStream is) throws IOException;
	
	/**
	 * Writes any data necessary to a {@code DataOutputStream}.
	 * <p>
	 * Should encode data for sending so that {@code readPacketData} can decode it
	 * <p>
	 * Note the accompanying {@link #writeString(DataOutputStream, String) String writing} and
	 * {@link #writeString(DataOutputStream, String) byte array writing} methods
	 * 
	 * @param os
	 * @throws IOException
	 */
	protected abstract void writePacketData(DataOutputStream os) throws IOException;
	
	/**
	 * Registers a {@code Packet} class so that the system knows of its existence
	 * 
	 * @param c
	 *            The class of {@code Packet} to register, for example {@code Packet.class}
	 * @return Whether the {@code Packet} registration was successful
	 */
	public static boolean registerPacket(Class<? extends Packet> c) {
		while (!registerPacket(c, availableID++));
		return true;
	}
	
	/**
	 * Registers a {@code Packet} class so that the system knows of its existence
	 * <p>
	 * Because the method {@code registerPacket(Class)} already handles IDs, it's recommended to use that
	 * method instead
	 * 
	 * @param c
	 *            The class of {@code Packet} to register, for example {@code Packet.class}
	 * @param id
	 *            The integer ID to use
	 * @return Whether the {@code Packet} registration was successful
	 */
	protected static boolean registerPacket(Class<? extends Packet> c, int id) {
		if (idtoclass.keySet().contains(id)) {
			return false;
		} else {
			idtoclass.put(id, c);
			classtoid.put(c, id);
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
	public static Packet readPacket(DataInputStream is) throws IOException {
		Packet p = null;
		int id = is.readInt();
		if (id < 0) {
			return null;
		}
		p = getNewPacket(id);
//		System.out.println("Trying to read Packet with ID " + id + " (" + p.getClass().getName() + ")");
		p.readPacketData(is);
		return p;
	}
	
	/**
	 * Writes a {@code Packet} to a {@code DataOutputStream}
	 * 
	 * @param os
	 * @param p
	 * @throws IOException
	 */
	public static void writePacket(DataOutputStream os, Packet p) throws IOException {
		os.writeInt(p.getID());
		p.writePacketData(os);
	}
	
	/**
	 * Reads a {@code String} from a {@code DataInputStream}
	 * 
	 * @param is
	 *            The {@code DataInputStream}
	 * @return The String
	 * @throws IOException
	 */
	protected static String readString(DataInputStream is) throws IOException {
		short l = is.readShort();
		StringBuilder s = new StringBuilder(l);
		for (int i = 0; i < l; i++) {
			s.append(is.readChar());
		}
		return s.toString();
	}
	
	/**
	 * Writes a {@code String} to a {@code DataOutputStream}
	 * 
	 * @param os
	 *            The {@code DataOutputStream}
	 * @param s
	 *            The String
	 * @throws IOException
	 */
	protected static void writeString(DataOutputStream os, String s) throws IOException {
		os.writeShort(s.length());
		os.writeChars(s);
	}
	
	/**
	 * Reads a byte array from a {@code DataOutputStream}
	 * 
	 * @param is
	 *            The {@code DataInputStream}
	 * @return The byte array
	 * @throws IOException
	 */
	protected static byte[] readByteArray(DataInputStream is) throws IOException {
		short l = is.readShort();
		if (l < 0) {
			throw new IOException("Key is smaller than nothing! WutFace");
		} else {
			byte[] bytes = new byte[l];
			is.readFully(bytes);
			return bytes;
		}
	}
	
	/**
	 * Writes a byte array to a {@code DataOutputStream}
	 * 
	 * @param os
	 *            The {@code DataOutputStream}
	 * @param bytes
	 *            The byte array
	 * @throws IOException
	 */
	protected static void writeByteArray(DataOutputStream os, byte[] bytes) throws IOException {
		os.writeShort(bytes.length);
		os.write(bytes);
	}
	
	/**
	 * Creates a new instance of a {@code Packet} from a given ID
	 * 
	 * @param id
	 *            The ID of the packet that it was registered with
	 * @return A new instance of a {@code Packet}
	 */
	private static Packet getNewPacket(int id) {
		try {
			Class<? extends Packet> c = idtoclass.get(id);
			return (c == null ? null : c.newInstance());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
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
	
}
