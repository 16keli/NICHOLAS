package engine.level;

import java.util.ArrayList;
import java.util.List;

import engine.Engine;
import engine.Game;
import engine.client.graphics.Screen;
import engine.networknio.Rebuildable;

/**
 * The most general implementation of a level possible, while still allowing for many possibilities.
 * <p>
 * For example, for levels with tiles, see {@link engine.level.tiled.LevelTiled}
 * <p>
 * For levels that require Physics, see {@link engine.physics.level.LevelPhysics}
 * 
 * @author Kevin
 */
public abstract class Level implements Rebuildable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * The {@code Game} instance
	 */
	public transient Game game;
	
	/**
	 * The width and height of the level in pixels
	 */
	public int width, height;
	
	/**
	 * The {@code Vector2} of the current visible upper-left corner of the {@code Level}
	 */
	public Vector2 offset;
	
	/**
	 * The next available entity ID
	 */
	private int nextEntityID = 0;
	
	/**
	 * The Master List of {@code Entity}s
	 */
	public List<Entity> entList = new ArrayList<Entity>();
	
	/**
	 * Creates a new level from the given {@code Game} with width and height equal to that of the
	 * {@code Client}
	 * 
	 * @param game
	 *            The {@code Game} instance
	 */
	public Level(Game game) {
		this(game, Engine.getClient().WIDTH, Engine.getClient().HEIGHT);
	}
	
	/**
	 * Creates a new level from the given parameters
	 * 
	 * @param game
	 *            The {@code Game} instance
	 * @param w
	 *            The width of the level
	 * @param h
	 *            The height of the level
	 */
	public Level(Game game, int w, int h) {
		this.game = game;
		this.game.events.register(this);
		this.width = w;
		this.height = h;
	}
	
	@Override
	public void rebuild(Game g) {
		this.game = g;
		this.game.events.register(this);
		for (Entity e : this.entList) {
			e.rebuild(g);
		}
	}
	
	/**
	 * Ticks the level
	 */
	public void tick() {
		this.tickLevel();
	}
	
	/**
	 * Gets the next available entity ID
	 * 
	 * @return The next available entity ID
	 */
	public int getNextAvailableID() {
		return this.nextEntityID++;
	}
	
	/**
	 * Any actions that the {@code Level} may want to do
	 */
	public abstract void tickLevel();
	
	/**
	 * Renders the {@code Level}
	 * 
	 * @param s
	 */
	public abstract void render(Screen s);
	
	/**
	 * Resets the level. Is <b>NOT</b> automatically called, so make sure to call it. But that may change
	 * soon.
	 */
	public abstract void reset();
	
	/**
	 * Adds an {@code Entity} to the {@code Level}
	 * 
	 * @param e
	 *            The {@code Entity} to add
	 * @return The {@code Entity}'s ID
	 */
	public int addEntity(Entity e) {
		this.entList.add(e);
		return e.id;
	}
	
	/**
	 * Retrieves the given {@code Entity} based on its ID
	 * 
	 * @param id
	 *            The {@code Entity}'s ID
	 * @return The {@code Entity}
	 */
	public Entity getEntity(int id) {
		return this.entList.get(id);
	}
	
}
