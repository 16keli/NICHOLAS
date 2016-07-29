package engine.network.packet;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * A {@code Packet} used to read and write {@code List}s of type {@code T} through the given
 * {@code ObjectInputStream} and {@code ObjectOutputStream}
 * 
 * @author Kevin
 * @param <T>
 *            The type of object the list of composed of
 */
public abstract class PacketList<T> extends PacketObject {
	
	public List<T> list;
	
	public PacketList() {
	}
	
	public PacketList(List<T> list) {
		this.list = list;
	}
	
	/**
	 * Reads a {@code List} from the given {@code ObjectInputStream}
	 * 
	 * @param is
	 *            The {@code ObjectInputStream}
	 * @return The {@code List}
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings ("unchecked")
	public static <T> List<T> readList(ObjectInputStream is) throws IOException, ClassNotFoundException {
		int size = is.readInt();
		List<T> list = new ArrayList<T>(size);
		for (int i = 0; i < size; i++) {
			list.add((T) is.readObject());
		}
		return list;
	}
	
	/**
	 * Writes a {@code List} to the given {@code ObjectOutputStream}
	 * 
	 * @param os
	 *            The {@code ObjectOutputStream}
	 * @param list
	 *            The List
	 * @throws IOException
	 */
	public static <T> void writeList(ObjectOutputStream os, List<T> list) throws IOException {
		os.writeInt(list.size());
		for (T type : list) {
			os.writeObject(type);
		}
	}
	
	@Override
	protected void readPacketData(ObjectInputStream is) throws IOException, ClassNotFoundException {
		this.list = readList(is);
	}
	
	@Override
	protected void writePacketData(ObjectOutputStream os) throws IOException {
		writeList(os, this.list);
	}
	
}
