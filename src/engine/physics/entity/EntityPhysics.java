package engine.physics.entity;

import java.util.LinkedList;
import java.util.List;

import engine.Game;
import engine.client.graphics.sprite.Sprite;
import engine.event.SubscribeEvent;
import engine.geom2d.Point2;
import engine.geom2d.Tuple2Mutable;
import engine.geom2d.Vector2;
import engine.level.Entity;
import engine.physics.Physics;
import engine.physics.level.LevelPhysics;

/**
 * An Entity for use in the physics engine
 * <p>
 * Because of the limitations of this engine for game creating, Entities are only available in 2D
 * <p>
 * Entity movement happens in 2 stages:
 * <p>
 * <b>Stage 1</b> schedules any changes in the position
 * <p>
 * The Physics Engine then checks for any collisions between {@code EntityPhysics}
 * <p>
 * <b>Stage 2</b> sees the outcome of the movement
 * 
 * @author Kevin
 */
public abstract class EntityPhysics extends Entity {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * The new position of the entity
	 */
	public Point2 newp;
	
	/**
	 * Velocity
	 */
	public Vector2 vel;
	
	/**
	 * Acceleration
	 */
	public Vector2 acc;
	
	/**
	 * The Net Force being exerted on the {@code EntityPhysics} at the moment
	 */
	public Vector2 netForce;
	
	/**
	 * Mass of the object
	 */
	public double mass;
	
	/**
	 * The Hitbox used for collision checks
	 */
	public Hitbox hitbox;
	
	/**
	 * The list of forces being exerted on the {@code EntityPhysics} at the moment
	 */
	public List<Vector2> forces = new LinkedList<Vector2>();
	
	public EntityPhysics(LevelPhysics l, double x, double y, Hitbox hb, double m, Sprite sprite) {
		super(l, x, y, sprite);
		this.newp = Point2.of(x, y);
		this.vel = Vector2.ZERO;
		this.acc = Vector2.ZERO;
		this.mass = m;
		this.hitbox = hb;
		Physics.PHYSICS_BUS.register(this);
	}
	
	@Override
	public void rebuild(Game g) {
		super.rebuild(g);
		Physics.PHYSICS_BUS.register(this);
	}
	
	@SubscribeEvent
	public void entPos(EventEntityPosition e) {
		if (e.id == this.id) {
			this.pos = Point2.of(e.x, e.y);
		}
	}
	
	public Vector2 getMomentum() {
		return Vector2.of(this.mass * this.vel.getX(), this.mass * this.vel.getY());
	}
	
	public Vector2 getKineticEnergy() {
		return Vector2.of(.5 * this.mass * Math.pow(this.vel.getX(), 2), .5 * this.mass * Math.pow(this.vel.getY(), 2));
	}
	
	/**
	 * Exerts a force on the {@code EntityPhysics}
	 * 
	 * @param force
	 *            The force to exert
	 */
	public void exertForce(Vector2 force) {
		if (!this.forces.contains(force)) {
			this.forces.add(force);
		}
	}
	
	/**
	 * Stops exerting a force on the {@code EntityPhysics}
	 * 
	 * @param force
	 *            The force to stop exerting
	 */
	public void stopExertingForce(Vector2 force) {
		if (this.forces.contains(force)) {
			this.forces.remove(force);
		}
	}
	
	/**
	 * Can {@code Collision}s cause this {@code EntityPhysics} to move?
	 * 
	 * @return
	 */
	public boolean collisionMoveable() {
		return true;
	}
	
	/**
	 * Called in phase 1 of movement
	 */
	public void tick1() {
		Tuple2Mutable mutable = Tuple2Mutable.ZERO;
		for (Vector2 force : this.forces) {
			mutable.add(force);
		}
		this.netForce = mutable.toVector();
		this.acc = this.netForce.scaleVector(1 / this.mass);
		mutable = Tuple2Mutable.getMutable(this.pos);
		this.newp = mutable.add(this.vel).add(this.acc.scale(.5)).toPoint();
		this.vel = this.vel.plus(this.acc);
		this.tickEntity1();
	}
	
	/**
	 * Ticks a fraction of ticks
	 * 
	 * @param tickFractions
	 */
	public void tick1(double tickFractions) {
		this.newp = this.pos.add(this.vel.scale(tickFractions))
				.add(this.acc.scale(.5 * tickFractions * tickFractions)).toPoint();
		this.vel = this.vel.plus(this.acc.scaleVector(tickFractions));
	}
	
	/**
	 * Anything that this {@code EntityPhysics} may want to do in phase 1 of movement
	 */
	public abstract void tickEntity1();
	
	/**
	 * Called in phase 2 of movement
	 */
	public void tick2() {
		this.pos = this.newp.clone();
		this.tickEntity2();
	}
	
	/**
	 * Anything that this {@code EntityPhysics} may want to do in phase 2 of movement
	 */
	public abstract void tickEntity2();
	
	/**
	 * Called as soon as this {@code EntityPhysics} collides with another
	 * 
	 * @param collided
	 *            The {@code EntityPhysics} that this one collided with
	 */
	public abstract void onCollision(EntityPhysics collided);
	
	@Override
	/**
	 * Does nothing for {@code EntityPhysics}!
	 */
	public void tick() {
	}
	
	public String getPhysicsInformation() {
		String info = "Pos:\t" + this.pos + "\nVel:\t" + this.vel + "\nAcc:\t" + this.acc;
		
		return info;
	}
	
}
