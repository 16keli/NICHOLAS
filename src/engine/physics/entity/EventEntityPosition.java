package engine.physics.entity;

import engine.event.Event;

public class EventEntityPosition extends Event {
	
	public double x, y;
	public int id;
	
	public EventEntityPosition(){}
	
	public EventEntityPosition(int id, double x, double y) {
		this.id = id;
		this.x = x;
		this.y = y;
	}

}
