package engine.event.game;

import engine.event.Event;


public class TickEvent extends Event {
	
	/**
	 * The tick
	 */
	public long tick;
	
	public TickEvent(long tickNumber) {
		this.tick = tickNumber;
	}
	
}
