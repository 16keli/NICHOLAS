package engine.event.game;

import engine.event.Event;

public class ChatEvent extends Event {
	
	public short pnum;
	
	public String msg;

	public ChatEvent(short pnum, String msg) {
		this.pnum = pnum;
		this.msg = msg;
	}

}
