package engine.networknio.packet;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

import engine.networknio.ConnectionNIO;
import engine.networknio.ProtocolWrapper;

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
	public static class UDPChannelWrapper extends ProtocolWrapper {
		
		/**
		 * The {@code SocketChannel}
		 */
		private DatagramChannel udp;
		
		public UDPChannelWrapper(DatagramChannel channel, ByteBuffer in, ByteBuffer out, ConnectionNIO c) {
			super(in, out, c);
			this.udp = channel;
		}
		
		@Override
		public void sendData(SocketAddress remote) throws IOException {
			this.outputBuffer.flip();
			udp.send(this.outputBuffer, remote);
			this.outputBuffer.clear();
		}
		
		@Override
		public boolean readData() throws IOException {
			this.inputBuffer.clear();
			boolean flag = udp.receive(this.inputBuffer) != null;
			this.inputBuffer.flip();
			return flag;
		}
		
	}
	
}
