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
		public void writeData(ByteBuffer buffer, SocketAddress remote) throws IOException {
			tcp.write(buffer);
		}
		
		@Override
		public boolean readData(ByteBuffer buffer) throws IOException {
			return tcp.read(buffer) > 0;
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
			ByteBuffer idAndSize = ByteBuffer.allocate(8);
			if (!this.readData(idAndSize)) {
				return null;
			}
			idAndSize.flip();
			int id = idAndSize.getInt();
			int size = idAndSize.getInt();
			if (id < 0) {
				return null;
			}
			p = PacketNIO.getNewPacket(id);
			ByteBuffer data = ByteBuffer.allocate(size);
			if (!this.readData(data)) {
				System.out.println("Failed to read Data from " + this + ", id was " + id + " and size was " + size);
				return null;
			}
			data.flip();
			p.readPacketData(data);
			
//			System.out.println("Read data:");
//			getPacketDataFromBuffer(idAndSize, data);
			return p;
		}
		
	}
	
}
