package engine.physics.level;

import engine.Engine;
import engine.Game;
import engine.level.Entity;
import engine.level.Level;
import engine.networknio.packet.PacketEntityPosition;
import engine.physics.Physics;
import engine.physics.entity.EntityPhysics;

/**
 * An implementation of a {@code Level} that requires an implementation of {@link engine.physics.Physics
 * Physics} into the level itself
 * 
 * @author Kevin
 */
public abstract class LevelPhysics extends Level {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * The {@code Physics} instance
	 */
	public Physics physics;
	
	public LevelPhysics(Game game) {
		super(game);
		this.physics = new Physics();
	}
	
	public LevelPhysics(Game game, int w, int h) {
		super(game, w, h);
		this.physics = new Physics();
	}
	
	@Override
	public void rebuild(Game g) {
		super.rebuild(g);
		this.physics.rebuild(g);
	}
	
	public void tick() {
		this.physics.tick();
		this.tickLevel();
		for (EntityPhysics e : this.physics.entities) {
			Engine.getServer().connections.sendPacketAll(new PacketEntityPosition(e));
		}
	}
	
	@Override
	public int addEntity(Entity e) {
		this.physics.entities.add((EntityPhysics) e);
		return e.id;
	}
	
	@Override
	public Entity getEntity(int i) {
		return this.physics.entities.get(i);
	}
	
}
