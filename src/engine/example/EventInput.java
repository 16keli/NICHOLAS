package engine.example;

import engine.event.Event;

public class EventInput extends Event {
	
	public short pnum;
	
	public byte dir;
	
	public EventInput(){}
	
	public EventInput(short pnum, byte dir) {
		this.pnum = pnum;
		this.dir = dir;
	}

}
