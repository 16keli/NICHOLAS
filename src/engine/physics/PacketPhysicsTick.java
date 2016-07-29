package engine.physics;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import engine.client.Client;
import engine.network.packet.Packet;
import engine.server.Server;

public class PacketPhysicsTick extends Packet {
	
	public int stage;
	
	public PacketPhysicsTick() {
	}
	
	public PacketPhysicsTick(int stage) {
		this.stage = stage;
	}

	@Override
	protected void readPacketData(DataInputStream is) throws IOException {
		this.stage = is.readInt();
	}

	@Override
	protected void writePacketData(DataOutputStream os) throws IOException {
		os.writeInt(this.stage);
	}

	@Override
	public void processClient(Client c) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void processServer(short player, Server s) {
		Physics.PHYSICS_BUS.post(new EventPhysicsTick(this.stage));
	}

}
