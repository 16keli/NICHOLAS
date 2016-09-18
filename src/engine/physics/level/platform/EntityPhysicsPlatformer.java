package engine.physics.level.platform;

import engine.client.graphics.Screen;
import engine.client.graphics.sprite.Sprite;
import engine.geom2d.Vector2;
import engine.physics.Physics;
import engine.physics.entity.EntityPhysics;
import engine.physics.entity.Hitbox;
import engine.physics.level.LevelPhysics;

public class EntityPhysicsPlatformer extends EntityPhysics {
	
	public Vector2 forceGravity;
	
	public Vector2 forceNormal;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public EntityPhysicsPlatformer(LevelPhysics l, double x, double y, Hitbox hb, double m, Sprite sprite) {
		super(l, x, y, hb, m, sprite);
		this.forceGravity = Physics.gravitationalForce(this);
		this.forceNormal = Physics.gravitationalForce(this).scaleVector(-1);
	}
	
	@Override
	public void tickEntity1() {
		if (this.onGround()) {
			this.exertForce(this.forceNormal);
		} else {
			this.stopExertingForce(this.forceNormal);
		}
	}
	
	@Override
	public void tickEntity2() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onCollision(EntityPhysics collided) {
		if (!(collided instanceof Platform)) {
			// Do stuff here
		}
	}
	
	public boolean onGround() {
		for (EntityPhysics e : ((LevelPhysics) this.level).physics.entities) {
			if (e instanceof Platform) {
				if (e.hitbox.pointLiesInsideHitbox(
						this.hitbox.getFurthestAtAngle(3 * Math.PI / 2).add(this.pos).toPoint())) {
					return true;
				}
			}
		}
		return false;
	}
	
	@Override
	public void render(Screen s) {
		s.render(this);
	}
	
}
