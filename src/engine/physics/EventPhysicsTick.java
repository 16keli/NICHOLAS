package engine.physics;

import engine.event.Event;

public class EventPhysicsTick extends Event {
	
	public int stage;
	
	public EventPhysicsTick(int stage) {
		this.stage = stage;
	}
	
}
