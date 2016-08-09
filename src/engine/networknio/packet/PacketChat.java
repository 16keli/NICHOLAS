package engine.networknio.packet;

import java.io.IOException;
import java.nio.ByteBuffer;

import engine.Player;
import engine.client.Client;
import engine.event.game.ChatEvent;
import engine.server.Server;

public class PacketChat extends PacketTCP {
	
	public short pnum;
	
	public String msg;
	
	/**
	 * {@code true} if for chat, {@code false} if for naming
	 */
	public boolean chat = true;
	
	public PacketChat(Player player) {
		this(player.number, player.name);
		this.chat = false;
	}
	
	public PacketChat(short number, String msg) {
		this.pnum = number;
		this.msg = msg;
	}
	
	public PacketChat() {
	}
	
	@Override
	public void writePacketData(ByteBuffer buff) throws IOException {
		buff.put((this.chat ? Byte.MAX_VALUE : Byte.MIN_VALUE));
		buff.putShort(this.pnum);
		PacketNIO.writeString(buff, this.msg);
	}
	
	@Override
	public void readPacketData(ByteBuffer buff) throws IOException {
		this.chat = buff.get() == Byte.MAX_VALUE;
		this.pnum = buff.getShort();
		this.msg = PacketNIO.readString(buff);
	}
	
	@Override
	public void processClient(Client c) {
		if (this.chat) {
			c.game.events.post(new ChatEvent(this.pnum, this.msg));
		}
	}
	
	@Override
	public void processServer(short player, Server s) {
		if (!this.chat && !s.game.players.get(this.pnum).hasName()) {
			s.game.players.get(this.pnum).setName(this.msg);
			System.out.println("Player " + this.pnum + "'s desired name is " + this.msg);
			s.connections.sendPacketAll(new PacketChat(s.game.players.get(this.pnum)));
		} else {
			s.game.events.post(new ChatEvent(this.pnum, this.msg));
		}
	}
	
}
