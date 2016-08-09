package engine.level;

import engine.Game;
import engine.client.graphics.Screen;
import engine.client.graphics.sprite.ISpriteProvider;
import engine.client.graphics.sprite.Sprite;
import engine.networknio.Rebuildable;
import engine.physics.entity.EntityPhysics;

/**
 * Any object that can be contained in a level.
 * <p>
 * For use of Physics, see {@link engine.physics.entity.EntityPhysics}
 * 
 * @see EntityPhysics
 * @author Kevin
 */
public abstract class Entity implements ISpriteProvider, Rebuildable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * The {@code Level}
	 */
	public Level level;
	
	/**
	 * The upper-left Position of this {@code Entity}
	 */
	public Vector2 pos;
	
	/**
	 * The {@code Sprite}
	 */
	public Sprite sprite;
	
	/**
	 * The {@code Entity} ID
	 */
	public int id;
	
	public Entity(Level l, double x, double y, Sprite sprite) {
		this.level = l;
		this.id = l.getNextAvailableID();
		this.pos = Vector2.of(x, y);
		this.sprite = sprite;
		this.level.game.events.register(this);
		this.level.addEntity(this);
	}
	
	@Override
	public void rebuild(Game g) {
		g.events.register(this);
	}
	
	@Override
	public Sprite getSprite() {
		return this.sprite;
	}
	
	@Override
	public Vector2 getSpritePosition() {
		return this.pos;
	}
	
	/**
	 * Any actions that may want to be executed every tick, such as AI decisions
	 */
	public abstract void tick();
	
	/**
	 * Renders the given {@code Entity}
	 * 
	 * @param s
	 *            The {@code Entity} to render
	 */
	public abstract void render(Screen s);
	
	public boolean equals(Entity e) {
		return this.id == e.id;
	}
	
//	@Override
//	public boolean equals(Object o) {
//		if (o == null) {
//			return false;
//		}
//		if (this == o) {
//			return true;
//		}
//		if (o instanceof Entity) {
//			return this.id == ((Entity) o).id;
//		}
//		return false;
//	}

}
