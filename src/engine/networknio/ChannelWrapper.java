package engine.networknio;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;

import engine.networknio.packet.PacketNIO;

/**
 * A wrapper around a given Channel, be it {@code SocketChannel} or {@code DatagramChannel}, that simplifies
 * sending/receiving data
 * 
 * @author Kevin
 */
public abstract class ChannelWrapper {
	
	/**
	 * Sends the data from the given {@code ByteBuffer} to the given {@code SocketAddress}
	 * @param buffer
	 * @param remote
	 * @throws IOException
	 */
	public abstract void writeData(ByteBuffer buffer, SocketAddress remote) throws IOException;
	
	/**
	 * Reads the data into the given {@code ByteBuffer}
	 * @param buffer
	 * @throws IOException
	 * @return Whether the read was successful
	 */
	public abstract boolean readData(ByteBuffer buffer) throws IOException;

	/**
	 * Writes a {@code PacketNIO} to the given remote address
	 * @param remote
	 * @param p
	 * @throws IOException
	 */
	public abstract void writePacket(SocketAddress remote, PacketNIO p) throws IOException;

	/**
	 * Reads a {@code PacketNIO} from the respective {@code Channel}
	 * @throws IOException
	 * @return A read PacketNIO
	 */
	public abstract PacketNIO readPacket() throws IOException;
	
}
