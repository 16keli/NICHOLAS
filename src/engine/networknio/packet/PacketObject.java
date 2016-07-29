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
 * 
 * @author Kevin
 * @param <T>
 */
public class PacketObject<T> extends PacketNIOTCP {
	
	public T object;
	
	public byte[] objectAsBytes;
	
	public PacketObject() {
	
	}
	
	public PacketObject(T object) {
		this.object = object;
		this.objectAsBytes = objectToBytes(this.object);
	}
	
	@Override
	protected void writePacketData(ByteBuffer buff) throws IOException {
		buff.put(objectAsBytes);
	}
	
	@SuppressWarnings ("unchecked")
	@Override
	protected void readPacketData(ByteBuffer buff) throws IOException {
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
	
	@Override
	public int getDataSize() {
		return this.objectAsBytes.length;
	}
	
	/**
	 * Transforms the given {@code Object} into a {@code byte[]}
	 * 
	 * @param object
	 * @return
	 */
	public static byte[] objectToBytes(Object object) {
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ObjectOutputStream oos = new ObjectOutputStream(baos)) {
			oos.writeObject(object);
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
		try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
				ObjectInputStream ois = new ObjectInputStream(bais)) {
			return ois.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
