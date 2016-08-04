package engine.networknio.packet;

import java.io.IOException;
import java.nio.ByteBuffer;

import engine.client.Client;
import engine.physics.Physics;
import engine.physics.entity.EntityPhysics;
import engine.physics.entity.EventEntityPosition;
import engine.server.Server;


public class PacketEntityPosition extends PacketUDP {
	
	public int id;
	
	public double x, y;
	
	public PacketEntityPosition() {
		
	}
	
	public PacketEntityPosition(EntityPhysics e) {
		this.id = e.id;
		this.x = e.pos.x;
		this.y = e.pos.y;
	}
	
	@Override
	public void writePacketData(ByteBuffer buff) throws IOException {
		buff.putInt(this.id);
		buff.putDouble(this.x);
		buff.putDouble(this.y);
	}
	
	@Override
	public void readPacketData(ByteBuffer buff) throws IOException {
		this.id = buff.getInt();
		this.x = buff.getDouble();
		this.y = buff.getDouble();
	}
	
	@Override
	public void processClient(Client c) {
		Physics.PHYSICS_BUS.post(new EventEntityPosition(this.id, this.x, this.y));
	}
	
	@Override
	public void processServer(short player, Server s) {
		// TODO Auto-generated method stub
		
	}
	
}
