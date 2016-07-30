package engine.networknio.packet;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import engine.networknio.ChannelWrapper;

/**
 * A {@code PacketNIO} that makes use of TCP to communicate
 * 
 * @author Kevin
 */
public abstract class PacketTCP extends PacketNIO {
	
	/**
	 * The TCP Channel Wrapper
	 * 
	 * @author Kevin
	 */
	public static class TCPChannelWrapper extends ChannelWrapper {
		
		/**
		 * The {@code SocketChannel}
		 */
		private SocketChannel tcp;
		
		public TCPChannelWrapper(SocketChannel channel) {
			this.tcp = channel;
		}
		
		@Override
		public void sendData(ByteBuffer buffer, SocketAddress remote) throws IOException {
			tcp.write(buffer);
		}
		
		@Override
		public void readData(ByteBuffer buffer) throws IOException {
			tcp.read(buffer);
		}
		
	}
	
}
