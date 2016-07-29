package engine.example;

import java.awt.Color;
import java.awt.Graphics2D;

import engine.Engine;
import engine.client.graphics.Screen;
import engine.level.Vector2;
import engine.physics.entity.EntityPhysics;
import engine.physics.entity.Hitbox.HitboxCircle;
import engine.physics.level.LevelPhysics;

public class EntityBall extends EntityPhysics {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final double VELOCITY = 3;
	
	public EntityBall(LevelPhysics l) {
		super(l, (l.width - 8) / 2, (l.height - 8) / 2, new HitboxCircle(5), 5, null);
		this.reset();
	}
	
	@Override
	public void tickEntity1() {
//		System.out.println(this.hitbox.getFurthestAtAngle(Math.PI).plus(this.pos));
//		System.out.println(this.getPhysicsInformation());
	}
	
	@Override
	public void tickEntity2() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void render(Screen s) {
		Graphics2D g = s.client.vImg.createGraphics();
		g.setColor(Color.WHITE);
		g.drawOval((int) pos.x, (int) pos.y, 10, 10);
//		this.hitbox.renderHitbox(g, pos);
		g.dispose();
	}
	
	public void reset() {
		double x = VELOCITY;
		double y = VELOCITY;
		if (Engine.rand.nextBoolean()) {
			x *= (Engine.rand.nextDouble() + .5);
		} else {
			x *= -(Engine.rand.nextDouble() + .5);
		}
		if (Engine.rand.nextBoolean()) {
			y *= (Engine.rand.nextDouble() + .5);
		} else {
			y *= -(Engine.rand.nextDouble() + .5);
		}
		this.vel = Vector2.of(x, y);
	}
	
	@Override
	public void onCollision(EntityPhysics collided) {
		this.vel = Vector2.of(this.vel.x * 1.05, this.vel.y * -1.05);
	}
	
}
