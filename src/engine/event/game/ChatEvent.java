package engine.event.game;

import engine.event.Event;

public class ChatEvent extends Event {
	
	public int pnum;
	
	public String msg;
	
	public ChatEvent(int pnum, String msg) {
		this.pnum = pnum;
		this.msg = msg;
	}
	
}
