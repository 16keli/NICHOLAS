package engine.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import engine.Player;
import engine.client.Client;
import engine.event.game.ChatEvent;
import engine.server.Server;

/**
 * A {@code Packet} that does double duty, helping with Chat as well as Usernames!
 * 
 * @author Kevin
 */
public class PacketChat extends Packet {
	
	public short pnum;
	
	public String msg;
	
	/**
	 * {@code true} if for chat, {@code false} if for naming
	 */
	public boolean chat = true;
	
	public PacketChat() {
	}
	
	public PacketChat(Player player) {
		this(player.number, player.name);
		this.chat = false;
	}
	
	public PacketChat(short number, String msg) {
		this.pnum = number;
		this.msg = msg;
	}
	
	@Override
	protected void readPacketData(DataInputStream is) throws IOException {
		this.chat = is.readBoolean();
		this.pnum = is.readShort();
		this.msg = Packet.readString(is);
//		this.msg = is.readUTF();
	}
	
	@Override
	protected void writePacketData(DataOutputStream os) throws IOException {
		os.writeBoolean(this.chat);
		os.writeShort(this.pnum);
		Packet.writeString(os, this.msg);
//		os.writeUTF(this.msg);
	}
	
	@Override
	public void processClient(Client c) {
		if (!this.chat && !c.game.players.get(this.pnum).hasName()) {
			c.game.players.get(this.pnum).setName(this.msg);
		} else {
			c.game.events.post(new ChatEvent(this.pnum, this.msg));
		}
	}
	
	@Override
	public void processServer(short player, Server s) {
		if (!this.chat && !s.game.players.get(this.pnum).hasName()) {
			s.game.players.get(this.pnum).setName(this.msg);
			System.out.println("Player " + this.pnum + "'s desired name is " + this.msg);
			s.sendPacketAll(new PacketChat(s.game.players.get(pnum)));
		} else {
			s.game.events.post(new ChatEvent(this.pnum, this.msg));
		}
	}
	
}
