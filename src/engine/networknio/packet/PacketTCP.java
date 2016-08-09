package engine.networknio.packet;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import engine.networknio.ConnectionNIO;
import engine.networknio.ProtocolWrapper;

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
	public static class TCPChannelWrapper extends ProtocolWrapper {
		
		/**
		 * The {@code SocketChannel}
		 */
		private SocketChannel tcp;
		
		public TCPChannelWrapper(SocketChannel channel, ByteBuffer in, ByteBuffer out, ConnectionNIO c) {
			super(in, out, c);
			this.tcp = channel;
		}
		
		@Override
		public void sendData(SocketAddress remote) throws IOException {
			this.outputBuffer.flip();
			this.tcp.write(this.outputBuffer);
			this.outputBuffer.clear();
		}
		
		@Override
		public boolean readData() throws IOException {
			this.inputBuffer.clear();
			int tcpCount = this.tcp.read(this.inputBuffer);
			boolean flag = tcpCount > 0;
			this.inputBuffer.flip();
			return flag;
		}
	}
	
}
