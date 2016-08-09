package engine.physics;

import java.io.IOException;
import java.nio.ByteBuffer;

import engine.client.Client;
import engine.networknio.packet.PacketTCP;
import engine.server.Server;

public class PacketPhysicsTick extends PacketTCP {
	
	public int stage;
	
	public PacketPhysicsTick() {
	}
	
	public PacketPhysicsTick(int stage) {
		this.stage = stage;
	}
	
	@Override
	public void processClient(Client c) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void processServer(short player, Server s) {
		Physics.PHYSICS_BUS.post(new EventPhysicsTick(this.stage));
	}
	
	@Override
	public void writePacketData(ByteBuffer buff) throws IOException {
		buff.putInt(this.stage);
	}
	
	@Override
	public void readPacketData(ByteBuffer buff) throws IOException {
		this.stage = buff.getInt();
	}
	
}
