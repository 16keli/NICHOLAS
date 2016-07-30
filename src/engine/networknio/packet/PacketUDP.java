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
		public void writeData(ByteBuffer buffer, SocketAddress remote) throws IOException {
			udp.send(buffer, remote);
		}
		
		@Override
		public boolean readData(ByteBuffer buffer) throws IOException {
			return udp.receive(buffer) != null;
		}
		
		@Override
		public void writePacket(SocketAddress remote, PacketNIO p) throws IOException {
			ByteBuffer buffer = ByteBuffer.allocate(8 + p.getDataSize());
			buffer.putInt(p.getID());
			buffer.putInt(p.getDataSize());
			p.writePacketData(buffer);
			buffer.flip();
			this.writeData(buffer, remote);
			
//			System.out.println("Written data:");
//			PacketNIO.getPacketDataFromBuffer(buffer);
		}

		@Override
		public PacketNIO readPacket() throws IOException {
			PacketNIO p = null;
			ByteBuffer buffer = ByteBuffer.allocate(8 + PacketNIO.maxUDPSize);
			if (!this.readData(buffer)) {
				return null;
			}
			buffer.flip();
			int id = buffer.getInt();
			buffer.getInt();
			if (id < 0) {
				return null;
			}
			p = PacketNIO.getNewPacket(id);
			p.readPacketData(buffer);
			
//			System.out.println("Read data:");
//			getPacketDataFromBuffer(idAndSize, data);
			return p;
		}
		
	}
	
}
