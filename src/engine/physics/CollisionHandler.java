package engine.physics;

import java.io.Serializable;

import engine.physics.entity.EntityPhysics;

/**
 * A class whose instances handle collisions between two {@code EntityPhysics}
 * 
 * @author Kevin
 */
public abstract class CollisionHandler implements Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6706793176495044539L;

	/**
	 * Handles the collision between the two given {@code EntityPhysics}
	 * 
	 * @param ent1
	 *            The first {@code EntityPhysics}
	 * @param ent2
	 *            The second {@code EntityPhysics}
	 */
	public abstract void handleCollision(EntityPhysics ent1, EntityPhysics ent2);
	
	/**
	 * Retrieves the priority of this {@code CollisionHandler} for handling collisions between the two given
	 * {@code EntityPhysics}
	 * <p>
	 * The priority of this {@code CollisionHandler} compared to other {@code CollisionHandler}s is compared.
	 * <p>
	 * Multiple {@code CollisionHandler}s may return a non-zero value, but the one with the highest priority
	 * is the one whose method is called.
	 * 
	 * @param ent1
	 *            The first {@code EntityPhysics}
	 * @param ent2
	 *            The second {@code EntityPhysics}
	 * @return The integer priority of this {@code CollisionHandler}, or 0 if this {@code CollisionHandler}
	 *         should not handle the collision.
	 */
	public abstract int handlePriority(EntityPhysics ent1, EntityPhysics ent2);
	
	/**
	 * Checks whether this {@code CollisionHandler} should be used in the collision between two given
	 * {@code EntityPhysics}
	 * 
	 * @param ent1
	 *            The first {@code EntityPhysics}
	 * @param ent2
	 *            The second {@code EntityPhysics}
	 * @return Whether this {@code CollisionHandler} should be used to handle a collision
	 */
	public boolean shouldHandle(EntityPhysics ent1, EntityPhysics ent2) {
		return this.handlePriority(ent1, ent2) != 0;
	}
	
	/**
	 * Creates a new {@code StandardCollisionHandler} that handles collisions based on a coefficient of
	 * restitution and a given priority
	 * 
	 * @param coeff The coefficient of restitution
	 * @param priority The priority of this {@code CollisionHandler}
	 * @return
	 */
	public static CollisionHandler getStandard(double coeff, int priority) {
		return new StandardCollisionHandler(coeff, priority);
	}
}
