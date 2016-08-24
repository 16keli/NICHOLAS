package engine.level.tiled;

import engine.event.Event;


public class EntityTiledMoveEvent extends Event {
	
	public EntityTiled traveler;
	
	public Tile destination;
	
	public EntityTiledMoveEvent(EntityTiled traveler, Tile destination) {
		this.traveler = traveler;
		this.destination = destination;
	}

}
