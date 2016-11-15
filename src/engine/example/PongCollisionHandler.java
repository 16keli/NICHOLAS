package engine.example;

import engine.geom2d.Vector2;
import engine.physics.CollisionHandler;
import engine.physics.entity.EntityPhysics;


public class PongCollisionHandler extends CollisionHandler {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 815334978180025838L;
	
	@Override
	public void handleCollision(EntityPhysics ent1, EntityPhysics ent2) {
		if (ent1 instanceof EntityBall) {
			ent1.vel = Vector2.of(ent1.vel.getX() * -1.05, ent1.vel.getY() * 1.05);
		}
		if (ent2 instanceof EntityBall) {
			ent2.vel = Vector2.of(ent2.vel.getX() * -1.05, ent2.vel.getY() * 1.05);
		}
	}
	
	@Override
	public int handlePriority(EntityPhysics ent1, EntityPhysics ent2) {
		return Integer.MAX_VALUE;
	}
	
}
