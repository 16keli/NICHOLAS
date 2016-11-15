package engine.physics;

import engine.geom2d.Point2;
import engine.geom2d.Vector2;
import engine.physics.entity.EntityPhysics;

/**
 * A standard collision handler whose behavior is based off of conservation of momentum and the coefficient of
 * restitution
 * <p>
 * This class can be used as-is. However, the priority of this handler is the lowest possible value in case
 * custom behaviour is desired.
 * 
 * @author Kevin
 */
public class StandardCollisionHandler extends CollisionHandler {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7873079179049462618L;

	/**
	 * The coefficient of restitution for this collision.
	 */
	public double restCoeff;
	
	/**
	 * The priority of this {@code StandardCollisionHandler};
	 */
	public int priority;
	
	protected StandardCollisionHandler(double restCoeff, int priority) {
		this.restCoeff = restCoeff;
		this.priority = priority;
	}
	
	@Override
	public void handleCollision(EntityPhysics ent1, EntityPhysics ent2) {
		// Initial vectors. Wow that's a lot.
		Vector2 vcm = ent1.getMomentum().plus(ent2.getMomentum()).scaleVector(1.0 / (ent1.mass + ent2.mass));
		Vector2 v1cm = ent1.vel.minus(vcm);
		Vector2 v2cm = ent2.vel.minus(vcm);
		Vector2 loa = Point2
				.vectorBetween(ent1.newp.addImmutable(ent1.hitbox.getCenterDisplacement()).toPoint(),
						ent2.newp.addImmutable(ent2.hitbox.getCenterDisplacement()).toPoint())
				.unit();
		Vector2 v1par = loa.scaleVector(ent1.vel.dot(loa));
		Vector2 v2par = loa.scaleVector(ent2.vel.dot(loa));
		Vector2 v1perp = v1cm.minus(v1par);
		Vector2 v2perp = v2cm.minus(v2par);
		// Now apply the coefficient of restitution
		v1par = v1par.scaleVector(-restCoeff);
		v2par = v2par.scaleVector(-restCoeff);
		// New vectors wow
		v1cm = v1par.plus(v1perp);
		v2cm = v2par.plus(v2perp);
		// Finally the new Entity Velocities
		ent1.vel = v1cm.plus(vcm);
		ent2.vel = v2cm.plus(vcm);
	}
	
	@Override
	public int handlePriority(EntityPhysics ent1, EntityPhysics ent2) {
		return priority;
	}
	
}
