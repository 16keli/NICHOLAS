package engine.example;

import engine.event.Event;

public class EventPlayer extends Event {
	
	
	public int pnum;
	
	public EventPlayer() {
	}
	
	public EventPlayer(int pnum) {
		this.pnum = pnum;
	}
	
}
