package engine.networknio;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;

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
	public abstract void sendData(ByteBuffer buffer, SocketAddress remote) throws IOException;
	
	/**
	 * Reads the data into the given {@code ByteBuffer}
	 * @param buffer
	 * @throws IOException
	 */
	public abstract void readData(ByteBuffer buffer) throws IOException;
	
}
