package engine.networknio.packet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;

import engine.client.Client;
import engine.server.Server;

/**
 * A somewhat utility {@code PacketNIO} class that allows for the transfer of {@code Object}s. However, it is
 * not recommended that one uses this class as a base, rather use the static methods
 * {@link #objectToBytes(Object)} and {@link #bytesToObject(byte[])} in order to Serialize objects, and create
 * separate {@code PacketNIOTCP} subclasses in order to handle them.
 * <p>
 * A good practice to keep in mind when using {@code PacketObject} is to serialize the object
 * <b>IMMEDIATELY</b> upon creation. If serialization is delayed until {@link #writePacketData(ByteBuffer)
 * writing}, then there is a very high chance that the ID and Data will become separated, breaking the game
 * quite quickly. So keep that in mind.
 * <p>
 * Handy static methods are included with {@code PacketNIO} as well, namely
 * {@link PacketNIO#readObject(ByteBuffer)} and {@link PacketNIO#writeObject(ByteBuffer, byte[])} to simplify
 * the process.
 * 
 * @author Kevin
 * @param <T>
 */
public class PacketObject<T> extends PacketTCP {
	
	public T object;
	
	public byte[] objectAsBytes;
	
	public PacketObject() {
	
	}
	
	public PacketObject(T object) {
		this.object = object;
		this.objectAsBytes = objectToBytes(this.object);
	}
	
	@Override
	public void writePacketData(ByteBuffer buff) throws IOException {
		buff.put(this.objectAsBytes);
	}
	
	@SuppressWarnings ("unchecked")
	@Override
	public void readPacketData(ByteBuffer buff) throws IOException {
		this.objectAsBytes = buff.array();
		this.object = (T) bytesToObject(this.objectAsBytes);
	}
	
	@Override
	public void processClient(Client c) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void processServer(short player, Server s) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Transforms the given {@code Object} into a {@code byte[]}
	 * 
	 * @param object
	 * @return
	 */
	public static byte[] objectToBytes(Object object) {
//		System.out.println("Serializing " + object);
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ObjectOutputStream oos = new ObjectOutputStream(baos)) {
			oos.writeObject(object);
//			System.out.println("Serialized, length is " + baos.size());
//			PacketNIO.getPacketData(baos.toByteArray());
			return baos.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Transforms the given {@code byte[]} into an {@code Object}
	 * 
	 * @param bytes
	 * @return
	 */
	public static Object bytesToObject(byte[] bytes) {
//		System.out.println("Attempting deserialization of " + bytes.length);
//		PacketNIO.getPacketData(bytes);
		try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
				ObjectInputStream ois = new ObjectInputStream(bais)) {
			return ois.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
