package engine.physics.entity;

import java.util.LinkedList;
import java.util.List;

import engine.Game;
import engine.client.graphics.sprite.Sprite;
import engine.event.SubscribeEvent;
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
	public Vector2 newp;
	
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
		this.newp = Vector2.of(x, y);
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
			this.pos = Vector2.of(e.x, e.y);
		}
	}
	
	public Vector2 getMomentum() {
		return Vector2.of(this.mass * this.vel.getX(), this.mass * this.vel.getY());
	}
	
	public Vector2 getKineticEnergy() {
		return Vector2.of(.5 * this.mass * Math.pow(this.vel.getX(), 2),
				.5 * this.mass * Math.pow(this.vel.getY(), 2));
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
		Vector2 forceSum = Vector2.ZERO;
		for (Vector2 force : this.forces) {
			forceSum = forceSum.plus(force);
		}
		this.netForce = forceSum;
		this.acc = this.netForce.scale(1 / this.mass);
		this.newp = this.pos.plus(this.vel).plus(this.acc.scale(.5));
		this.vel = this.vel.plus(this.acc);
		this.tickEntity1();
	}
	
	/**
	 * Ticks a fraction of ticks
	 * 
	 * @param tickFractions
	 */
	public void tick1(double tickFractions) {
		this.newp = this.pos.plus(this.vel.scale(tickFractions))
				.plus(this.acc.scale(.5 * tickFractions * tickFractions));
		this.vel = this.vel.plus(this.acc.scale(tickFractions));
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
	
	@Override
	/**
	 * Does nothing for {@code EntityPhysics}!
	 */
	public void tick() {
	}
	
	/**
	 * Determines whether the two {@code EntityPhysics} are approaching each other
	 * 
	 * @param ent1
	 * @param ent2
	 * @return
	 */
	public static boolean approaching(EntityPhysics ent1, EntityPhysics ent2) {
		Vector2 posDiff = ent2.pos.plus(ent2.hitbox.getCenterDisplacement())
				.minus(ent1.pos.plus(ent1.hitbox.getCenterDisplacement()));
		Vector2 vcm = ent1.getMomentum().plus(ent2.getMomentum()).scale(1.0 / (ent1.mass + ent2.mass));
		Vector2 v1cm = ent1.vel.minus(vcm);
		return v1cm.dot(posDiff) > 0;
	}
	
	public String getPhysicsInformation() {
		String info = "Pos:\t" + this.pos + "\nVel:\t" + this.vel + "\nAcc:\t" + this.acc;
		
		return info;
	}
	
}
