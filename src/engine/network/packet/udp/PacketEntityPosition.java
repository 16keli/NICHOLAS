package engine.network.packet.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;

import engine.client.Client;
import engine.level.Entity;
import engine.physics.Physics;
import engine.physics.entity.EventEntityPosition;
import engine.server.Server;

public class PacketEntityPosition extends PacketUDP {
	
	public double x, y;
	public int id;
//	public long timeSent, timeRead, timeProc;
	
	public PacketEntityPosition() {
	}
	
	public PacketEntityPosition(int id, double x, double y) {
		this.id = id;
		this.x = x;
		this.y = y;
	}

	public PacketEntityPosition(int i, Entity entity) {
		this(i, entity.pos.x, entity.pos.y);
	}

	@Override
	protected void readPacketData(DatagramSocket sock) throws IOException {
		DatagramPacket info = new DatagramPacket(new byte[20], 20);
		sock.receive(info);
		ByteBuffer buff = ByteBuffer.wrap(info.getData());
		this.id = buff.getInt();
		this.x = buff.getDouble();
		this.y = buff.getDouble();
//		System.out.println(this.id + " " + this.x + " " + this.y);
	}

	@Override
	protected void writePacketData(DatagramSocket sock, SocketAddress add) throws IOException {
		byte[] data = ByteBuffer.allocate(20).putInt(this.id).putDouble(this.x).putDouble(this.y).array();
		DatagramPacket info = new DatagramPacket(data, 20, add);
		sock.send(info);
	}
	
	@Override
	public void processClient(Client c) {
		Physics.PHYSICS_BUS.post(new EventEntityPosition(this.id, this.x, this.y));
//		System.out.println("EntityPosition! id " + id + "\n" + Vector2.of(x, y).getCartesian());
	}
	
	@Override
	public void processServer(short player, Server s) {
	}

}
