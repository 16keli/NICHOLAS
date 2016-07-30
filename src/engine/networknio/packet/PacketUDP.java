package engine.networknio.packet;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

import engine.networknio.ChannelWrapper;

/**
 * A {@code PacketNIO} that makes use of UDP to communicate
 * 
 * @author Kevin
 */
public abstract class PacketUDP extends PacketNIO {
	
	/**
	 * The UDP Channel Wrapper
	 * 
	 * @author Kevin
	 */
	public static class UDPChannelWrapper extends ChannelWrapper {
		
		/**
		 * The {@code SocketChannel}
		 */
		private DatagramChannel udp;
		
		public UDPChannelWrapper(DatagramChannel channel) {
			this.udp = channel;
		}
		
		@Override
		public void sendData(ByteBuffer buffer, SocketAddress remote) throws IOException {
			udp.send(buffer, remote);
		}
		
		@Override
		public void readData(ByteBuffer buffer) throws IOException {
			udp.receive(buffer);
		}
		
	}
	
}
