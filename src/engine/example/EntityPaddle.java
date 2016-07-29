package engine.example;

import java.awt.Color;
import java.awt.Graphics2D;

import engine.client.graphics.Screen;
import engine.level.Vector2;
import engine.physics.entity.EntityPhysics;
import engine.physics.entity.Hitbox.HitboxRectangle;
import engine.physics.level.LevelPhysics;

public class EntityPaddle extends EntityPhysics {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public EntityPaddle(LevelPhysics l, int pnum) {
		super(l, (pnum == 0 ? 20 : l.width - 20 - 8), (l.height - 24) / 2, new HitboxRectangle(8, 24), 50,
				null);
				
	}
	
	@Override
	public void render(Screen s) {
		Graphics2D g = s.client.vImg.createGraphics();
		g.setColor(Color.WHITE);
		g.fillRect((int) this.pos.x, (int) this.pos.y, (int) ((HitboxRectangle) this.hitbox).sizeX,
				(int) ((HitboxRectangle) this.hitbox).sizeY);
//		this.hitbox.renderHitbox(g, this.pos);
		g.dispose();
//		System.out.println(this.hitbox.getFurthestAtAngle(0).plus(this.pos));
	}
	
	@Override
	public void tickEntity1() {
		if (this.newp.y < 0) {
			this.newp = Vector2.of(this.newp.x, 0);
		}
		if (this.newp.y > this.level.height - ((HitboxRectangle) this.hitbox).sizeY) {
			this.newp = Vector2.of(this.newp.x, this.level.height - ((HitboxRectangle) this.hitbox).sizeY);
		}
	}
	
	@Override
	public void tickEntity2() {
	}
	
	@Override
	public boolean collisionMoveable() {
		return false;
	}
	
	@Override
	public void onCollision(EntityPhysics collided) {
		// TODO Auto-generated method stub
		
	}
	
}
