package engine.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * An implementation of a {@code Packet} that needs an {@code ObjectInputStream} and
 * {@code ObjectOutputStream} to write data.
 * <p>
 * By default, {@code Packet}s use {@code DataInputStream} and {@code DataOutputStream} due to performance
 * concerns. However, if Object I/O is necessary, use {@code PacketObject}
 * @see Packet
 * 
 * @author Kevin
 */
public abstract class PacketObject extends Packet {

	@Override
	protected void readPacketData(DataInputStream is) throws IOException {
		try {
			readPacketData(new ObjectInputStream(is));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Read any data necessary from an {@code ObjectInputStream}
	 * 
	 * @param is The stream to read from
	 * @throws IOException
	 */
	protected abstract void readPacketData(ObjectInputStream is) throws IOException, ClassNotFoundException;

	@Override
	protected void writePacketData(DataOutputStream os) throws IOException {
		writePacketData(new ObjectOutputStream(os));
	}

	/**
	 * Writes any data necessary to the {@code ObjectOutputStream}
	 * @param os The stream to write to
	 * @throws IOException
	 */
	protected abstract void writePacketData(ObjectOutputStream os) throws IOException;

}
