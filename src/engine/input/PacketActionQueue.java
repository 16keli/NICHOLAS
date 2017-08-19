package engine.input;

import java.io.IOException;
import java.nio.ByteBuffer;

import engine.client.Client;
import engine.networknio.packet.PacketNIO;
import engine.server.Server;

public class PacketActionQueue extends PacketNIO {
	
	public ActionQueue queue;
	
	public PacketActionQueue() {
		queue = new ActionQueue();
	}
	
	public PacketActionQueue(ActionQueue queue) {
		this.queue = queue;
	}
	
	@Override
	public void writePacketData(ByteBuffer buff) throws IOException {
		queue.write(buff);
	}
	
	@Override
	public void readPacketData(ByteBuffer buff) throws IOException {
		queue.read(buff);
	}
	
	@Override
	public void processClient(Client c) {
		// Nothing
	}
	
	@Override
	public void processServer(int i, Server s) {
		s.game.players.get(i).actionQueue.populate(queue);
	}
	
}
