package engine.physics;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import engine.Engine;
import engine.Game;
import engine.event.EventBus;
import engine.event.SubscribeEvent;
import engine.geom2d.Point2;
import engine.geom2d.Vector2;
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
	 * The acceleration due to gravity, in pixels/tick^2
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
	 * The default amount of subticks
	 * 
	 * @see subticks
	 */
	public static final int DEFAULT_SUBTICKS = 1;
	
	/**
	 * The amount of "subticks", or physics evaluations within a single tick, to perform.
	 */
	public static int subticks;
	
	/**
	 * The list of {@code EntityPhysics} that currently exist in this {@code Physics} instance
	 */
	public List<EntityPhysics> entities = new ArrayList<EntityPhysics>();
	
	/**
	 * List of {@code CollisionHandler}s that this instance of {@code Physics} should use when evaluating
	 * collisions
	 */
	public List<CollisionHandler> collisionHandlers = new ArrayList<CollisionHandler>();
	
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
		this.collisionHandlers.add(CollisionHandler.getStandard(1, Integer.MIN_VALUE));
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
	 * <p>
	 * Unfortunately, this method is O(n^2). <strike>I'm not quite sure how to cut down on that,</strike>
	 * Every {@code Hitbox} now has the field {@link engine.physics.entity.Hitbox#circleRadius circleRadius}
	 * which specifies a radius outside which it would be impossible to collide with it. A simple check is all
	 * it takes to drastically reduce the number of comparisons required each tick.
	 * <p>
	 * but I highly doubt there will be enough active entities in order cause enough lag...
	 */
	public void check() {
		for (int e1 = 0; e1 < this.entities.size() - 1; e1++) {
			for (int e2 = e1 + 1; e2 < this.entities.size(); e2++) {
				EntityPhysics ent1 = this.entities.get(e1);
				EntityPhysics ent2 = this.entities.get(e2);
				if (!ent1.dead && !ent2.dead) {
					if (Point2.displacement(ent1.newp.add(ent1.hitbox.getCenterDisplacement()).toPoint(),
							ent2.newp.add(ent2.hitbox.getCenterDisplacement())
									.toPoint()) <= (ent1.hitbox.circleRadius + ent2.hitbox.circleRadius)) {
						if (suspectedCollision(ent1, ent2) && EntityPhysics.approaching(ent1, ent2)) {
							this.handleCollision(ent1, ent2);
						}
					}
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
	
	/**
	 * Handles the collision between the two {@code EntityPhysics}
	 * 
	 * @param ent1
	 * @param ent2
	 */
	private void handleCollision(EntityPhysics ent1, EntityPhysics ent2) {
		this.collisionHandlers.sort(new Comparator<CollisionHandler>() {
			
			
			@Override
			public int compare(CollisionHandler o1, CollisionHandler o2) {
				if (!o1.shouldHandle(ent1, ent2) && !o2.shouldHandle(ent1, ent2)) {
					return 0;
				}
				if (!o2.shouldHandle(ent1, ent2)
						|| o1.handlePriority(ent1, ent2) > o2.handlePriority(ent1, ent2)) {
					return -1;
				} else if (!o1.shouldHandle(ent1, ent2)
						|| o2.handlePriority(ent1, ent2) > o1.handlePriority(ent1, ent2)) {
					return 1;
				}
				return 0;
			}
		});
		if (this.collisionHandlers.get(0).handlePriority(ent1, ent2) == 0) {
			throw new RuntimeException(
					"Expected collision handler to handle this collision, but none available!");
		}
		this.collisionHandlers.get(0).handleCollision(ent1, ent2);
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
	 * Returns the number of physics evaluations performed in a single game tick
	 * 
	 * @return
	 */
	public int physicsTicks() {
		return subticks * Engine.getTickRate();
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
