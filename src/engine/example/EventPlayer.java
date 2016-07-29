package engine.example;

import engine.event.Event;

public class EventPlayer extends Event {
	
	public short pnum;
	
	public EventPlayer(){}
	
	public EventPlayer(short pnum) {
		this.pnum = pnum;
	}

}
