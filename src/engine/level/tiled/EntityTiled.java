package engine.level.tiled;

import java.util.LinkedList;
import java.util.List;

import engine.client.graphics.sprite.Sprite;
import engine.level.Entity;
import engine.level.tiled.Tile.TileCoords;
import engine.level.tiled.pathfind.PathfindNode;
import engine.level.tiled.pathfind.UnitPath;

public abstract class EntityTiled extends Entity {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public Tile currentTile;
	
	public List<Properties> props = new LinkedList<Properties>();
	
	public Properties actions;
	
	public EntityTiled(LevelTiled l, TileCoords t, Sprite sprite, int actions) {
		this(l, t.x, t.y, sprite, actions);
	}
	
	public EntityTiled(LevelTiled l, int x, int y, Sprite sprite, int actions) {
		super(l, l.getTileWidthPixel() * x, l.getTileHeightPixel() * y, sprite);
		this.currentTile = l.getTile(x, y);
		this.actions = Properties.of(actions);
		this.props.add(this.actions);
	}
	
	/**
	 * Resets the values for all the {@code Properties} that this {@code EntityTiled} has
	 */
	public void resetPropValues() {
		for (Properties p : this.props) {
			p.reset();
		}
	}
	
	/**
	 * Checks to see whether this {@code EntityTiled} can move to the given {@code Tile}
	 * 
	 * @param dest
	 * @return
	 */
	public boolean canMove(Tile dest) {
		return true;
	}
	
	/**
	 * Moves to the given {@code Tile}
	 * 
	 * @param destination
	 *            The {@code Tile} to move to
	 */
	public void move(Tile destination) {
		this.currentTile = destination;
		this.level.game.events.post(new EntityTiledMoveEvent(this, destination));
	}
	
	/**
	 * Moves along the given {@code UnitPath}
	 * @param path The {@code UnitPath} to take
	 */
	public void moveAlongPath(UnitPath path) {
		for (PathfindNode tile : path.nodes) {
			this.move((Tile) tile);
		}
	}
	
	/**
	 * A convenience class for things like movement range.
	 * 
	 * @author Kevin
	 */
	public static class Properties {
		
		private int current;
		
		private int max;
		
		public static Properties of(int value) {
			return new Properties(value);
		}
		
		private Properties(int value) {
			this.current = value;
			this.max = value;
		}
		
		public int getValue() {
			return this.current;
		}
		
		public void reset() {
			this.current = this.max;
		}
	}
	
}
