package engine.level.tiled;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import engine.client.graphics.Screen;
import engine.client.graphics.sprite.ISpriteProvider;
import engine.client.graphics.sprite.Sprite;
import engine.level.Vector2;

public abstract class Tile implements ISpriteProvider, Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public Sprite sprite;
	
	public TileCoords tileCoords;
	
	public LevelTiled level;
	
	public List<EntityTiled> ents = new LinkedList<EntityTiled>();
	
	public Vector2 position;
	
	private int moveCost;
	
	/**
	 * HashMap that contains the specific movement costs of this tile based on the subclass of
	 * {@code EntityTiled}
	 * <p>
	 * A value of -1 means that movement to this tile is invalid
	 */
	public HashMap<Class<? extends EntityTiled>, Integer> moveCosts = new HashMap<Class<? extends EntityTiled>, Integer>();
	
	public Tile(LevelTiled level, TileCoords tile, Sprite sprite) {
		this(level, tile, sprite, 1);
	}
	
	public Tile(LevelTiled level, TileCoords tile, Sprite sprite, int moveCost) {
		this.level = level;
		this.position = Vector2.of(tile.x * level.width / level.getTileWidth(),
				tile.y * level.height / level.getTileHeight());
		this.tileCoords = tile;
		this.sprite = sprite;
		this.moveCost = moveCost;
	}
	
	@Override
	public Sprite getSprite() {
		return this.sprite;
	}
	
	@Override
	public Vector2 getSpritePosition() {
		return this.position;
	}
	
	public void render(Screen s) {
		s.render(this);
	}
	
	public int getDistanceFrom(Tile t) {
		return Math.abs(t.tileCoords.x - this.tileCoords.x) + Math.abs(t.tileCoords.y - this.tileCoords.y);
	}
	
	/**
	 * Retrieves the specific movement cost for the given subclass of {@code EntityTiled}
	 * 
	 * @param c
	 *            The class
	 * @return The movement cost of the class
	 */
	public int getMovementCost(Class<?> c) {
		while (c != EntityTiled.class) {
			if (c != null && this.moveCosts.containsKey(c)) {
				return this.moveCosts.get(c);
			}
			c = c.getSuperclass();
		}
		return this.moveCost;
	}
	
	/**
	 * Checks whether the given subclass of {@code EntityTiled} can move to this {@code Tile}
	 * 
	 * @param c
	 *            The class
	 * @return Whether it can move to this {@code Tile}
	 */
	public boolean canMoveToHere(Class<?> c) {
		return this.getMovementCost(c) != -1;
	}
	
	public boolean equals(Tile t) {
		return this.tileCoords.equals(t.tileCoords);
	}
	
//	@Override
//	public boolean equals (Object o) {
//		if (o == null) {
//			return false;
//		}
//		if (this == o) {
//			return true;
//		}
//		if (o instanceof Tile) {
//			return this.tileCoords.equals(((Tile) o).tileCoords);
//		}
//		return false;
//	}
	
	/**
	 * Checks whether the given {@code Tile} is adjacent to this one
	 * 
	 * @param tile
	 * @return
	 */
	public boolean isAdjacentTo(Tile tile) {
		return this.level.adjacentTiles(this).contains(tile);
	}
	
	/**
	 * Returns the direction of the given {@code Tile} compared to this one
	 * <p>
	 * For example, if {@code tile} is above {@code this}, this method will return {@code Direction.NORTH}
	 * 
	 * @param tile
	 * @return
	 */
	public Direction getDirectionOf(Tile tile) {
		if (!this.isAdjacentTo(tile)) {
			return Direction.NONE;
		}
		for (Direction d : Direction.values()) {
			if (d.of().equals(TileCoords.diff(this.tileCoords, tile.tileCoords))) {
				return d;
			}
		}
		return Direction.NONE;
	}
	
	public static class TileCoords {
		
		public int x, y;
		
		private TileCoords(int x, int y) {
			this.x = x;
			this.y = y;
		}
		
		public static TileCoords of(int x, int y) {
			return new TileCoords(x, y);
		}
		
		public static TileCoords diff(TileCoords minuend, TileCoords subtrahend) {
			return of(minuend.x - subtrahend.x, minuend.y - subtrahend.y);
		}
		
		public boolean equals(TileCoords t) {
			return this.x == t.x && this.y == t.y;
		}
	}
	
	public static enum Direction {
		NORTH (0, -1), EAST (1, 0), SOUTH (0, 1), WEST (-1, 0), NONE (0, 0);
		
		Direction(int x, int y) {
			this.x = x;
			this.y = y;
		}
		
		public int x;
		
		public int y;
		
		public TileCoords of() {
			return TileCoords.of(this.x, this.y);
		}
	}
	
}
