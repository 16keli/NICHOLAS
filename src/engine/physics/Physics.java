package engine.physics;

import java.util.ArrayList;
import java.util.List;

import engine.Game;
import engine.event.EventBus;
import engine.event.SubscribeEvent;
import engine.level.Vector2;
import engine.networknio.Rebuildable;
import engine.physics.entity.EntityPhysics;
import engine.physics.entity.Hitbox;

/**
 * An implementation of a Physics engine into the game
 * <p>
 * Contains a debug mode though {@link #debug}.
 * <p>
 * Physics is really really hard
 * 
 * @author Kevin
 */
public class Physics implements Rebuildable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * The period, in game ticks, that {@code PacketEntityPosition}s are send
	 * <p>
	 * Deprecated. The only reason this existed was due to outdated network code (constantly interrupting
	 * threads) that caused extremely high CPU usage as well as unreliable {@code EntityPhysics} position
	 * updating. This was implemented as a workaround, but has since become unnecessary.
	 */
	@Deprecated
	public static final int TICK_DELAY = 1;
	
	/**
	 * The acceleration due to gravity, in distance/tick^2
	 */
	public static final double GRAVITY = -9.8 / 3600;
	
	/**
	 * Whether or not to run the Physics Engine in debug mode.
	 * <p>
	 * In debug mode, the Physics engine will not tick at the rate that the server and level ticks. Those will
	 * continue to tick at the normal rate. However, the Physics engine will tick only when it receives an
	 * {@code EventPhysicsTick}
	 */
	public boolean debug;
	
	/**
	 * The list of {@code EntityPhysics} that currently exist in this {@code Physics} instance
	 */
	public List<EntityPhysics> entities = new ArrayList<EntityPhysics>();
	
	/**
	 * The list of {@code Collision}s that have happened
	 */
	public List<Collision> collisions = new ArrayList<Collision>();
	
	/**
	 * The {@code EventBus} necessary for dispatching {@code EventEntityPosition}s and other Physics related
	 * {@code Event}s
	 */
	public static EventBus PHYSICS_BUS = new EventBus("Physics Bus");
	
	/**
	 * The previous stage in debug ticking
	 */
	private int prevStage = 0;
	
	public Physics() {
		this(false);
	}
	
	public Physics(boolean debug) {
		this.debug = debug;
		if (debug) {
			PHYSICS_BUS.register(this);
		}
	}
	
	@Override
	public void rebuild(Game g) {
		for (EntityPhysics e : this.entities) {
			e.rebuild(g);
		}
	}
	
	/**
	 * Ticks the Physics engine
	 * <p>
	 * Movement of {@code EntityPhysics} happens in two phases, and collisions are checked between the two
	 */
	public void tick() {
		this.tick1();
		this.check();
		this.tick2();
	}
	
	/**
	 * Ticks the first stage of Physics movement
	 */
	public void tick1() {
		for (EntityPhysics e : this.entities) {
			e.tick1();
		}
	}
	
	/**
	 * Does any collision checking necessary
	 */
	public void check() {
		for (int e1 = 0; e1 < this.entities.size() - 1; e1++) {
			for (int e2 = e1 + 1; e2 < this.entities.size(); e2++) {
				EntityPhysics ent1 = this.entities.get(e1);
				EntityPhysics ent2 = this.entities.get(e2);
				if (suspectedCollision(ent1, ent2)) {
					this.collisions.add(new Collision(ent1, ent2));
				}
			}
		}
	}
	
	/**
	 * Ticks the second stage of Physics movement
	 */
	public void tick2() {
		for (EntityPhysics e : this.entities) {
			e.tick2();
		}
	}
	
	/**
	 * Check to see if the {@code EntityPhysics}s pass by each other
	 * 
	 * @param ent1
	 *            The first {@code EntityPhysics}
	 * @param ent2
	 *            The second {@code EntityPhysics}
	 * @return Whether or not a collision is suspected
	 */
	public static boolean suspectedCollision(EntityPhysics e1, EntityPhysics e2) {
//		boolean passX = ((e1.posX + e1.sizeX < e2.posX) && (e1.newX + e1.sizeX > e2.newX)) || ((e2.posX + e2.sizeX < e1.posX) && (e2.newX + e2.sizeX > e1.newX)) || ((e1.posX + e1.sizeX > e2.posX) && (e1.newX + e1.sizeX < e2.newX)) || ((e2.posX + e2.sizeX > e1.posX) && (e2.newX + e2.sizeX < e1.newX));
//		boolean passY = ((e1.posY + e1.sizeY < e2.posY) && (e1.newY + e1.sizeY > e2.newY)) || ((e2.posY + e2.sizeY < e1.posY) && (e2.newY + e2.sizeY > e1.newY)) || ((e1.posY + e1.sizeY > e2.posY) && (e1.newY + e1.sizeY < e2.newY)) || ((e2.posY + e2.sizeY > e1.posY) && (e2.newY + e2.sizeY < e1.newY));
//		return passX && passY;
		return Hitbox.collides(e1, e2);
	}
	
	@SubscribeEvent
	public void debugTick(EventPhysicsTick e) {
		switch (e.stage) {
			case -1:
				this.tickNextStage();
			case 0:
				this.tick1();
				break;
			case 1:
				this.check();
				break;
			case 2:
				this.tick2();
				break;
//			default:
//				this.tick();
//				break;
		}
	}
	
	/**
	 * Ticks the next stage in the process
	 */
	private void tickNextStage() {
		switch (this.prevStage) {
			case 0:
				this.tick1();
				break;
			case 1:
				this.check();
				break;
			case 2:
				this.tick2();
				break;
		}
		if (++this.prevStage > 2) {
			this.prevStage = 0;
		}
	}
	
	/**
	 * Gets a {@code Vector2} representing the gravitational force acting on an {@code EntityPhysics}
	 * 
	 * @param ent
	 *            The {@code EntityPhysics} in question
	 * @return The {@code Vector2} of the gravitational force
	 */
	public static Vector2 gravitationalForce(EntityPhysics ent) {
		return Vector2.of(0, GRAVITY * ent.mass);
	}
	
}
