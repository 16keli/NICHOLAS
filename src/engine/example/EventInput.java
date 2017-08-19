package engine.example;

import engine.event.Event;

public class EventInput extends Event {
	
	public int pnum;
	
	public int dir;
	
	public EventInput() {
	}
	
	public EventInput(int pnum, int dir) {
		this.pnum = pnum;
		this.dir = dir;
	}
	
}
