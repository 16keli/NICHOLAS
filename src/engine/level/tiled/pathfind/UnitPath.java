package engine.level.tiled.pathfind;

import java.io.Serializable;
import java.util.List;

import engine.level.tiled.EntityTiled;
import engine.level.tiled.Tile;

/**
 * Essentially a wrapper class that represents a valid path that an {@code EntityTiled} can take on its way to
 * its destination
 * 
 * @author Kevin
 */
public class UnitPath implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The {@code EntityTiled} that is moving
	 */
	public EntityTiled traveler;
	
	/**
	 * The {@code Tile} that the {@code EntityTiled} starts on
	 */
	public Tile start;
	
	/**
	 * The {@code Tile} that the {@code EntityTiled} ends on
	 */
	public Tile end;
	
	/**
	 * A List of {@code Tile}s between the start and end points
	 */
	public List<Tile> tiles;
	
	/**
	 * The total cost of moving
	 */
	public int cost;
	
	protected UnitPath(EntityTiled traveler, Tile start, Tile end, List<Tile> tiles, int cost) {
		this.traveler = traveler;
		this.start = start;
		this.end = end;
		this.tiles = tiles;
		this.cost = cost;
	}
	
	/**
	 * Checks whether this Path is, in fact, valid.
	 * 
	 * @return
	 */
	public boolean isValidPath() {
		return this.traveler.actions.getValue() <= this.cost;
	}
	
	/**
	 * Checks whether this {@code UnitPath} is the one with the given parameters
	 * 
	 * @param traveler
	 *            The {@code EntityTiled}
	 * @param goal
	 *            The {@code Tile} destination
	 * @return
	 */
	boolean isPathWith(EntityTiled traveler, Tile goal) {
		return traveler.equals(this.traveler) && goal.equals(this.end);
	}
}
