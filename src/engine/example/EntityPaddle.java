package engine.example;

import java.awt.Color;
import java.awt.Graphics2D;

import engine.client.graphics.Screen;
import engine.geom2d.Point2;
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
		g.fillRect((int) this.pos.getX(), (int) this.pos.getY(), (int) ((HitboxRectangle) this.hitbox).sizeX,
				(int) ((HitboxRectangle) this.hitbox).sizeY);
//		this.hitbox.renderHitbox(g, this.pos);
		g.dispose();
//		System.out.println(this.hitbox.getFurthestAtAngle(0).plus(this.pos));
	}
	
	@Override
	public void tickEntity1() {
		if (this.newp.getY() < 0) {
			this.newp = Point2.of(this.newp.getX(), 0);
		}
		if (this.newp.getY() > this.level.height - ((HitboxRectangle) this.hitbox).sizeY) {
			this.newp = Point2.of(this.newp.getX(), this.level.height - ((HitboxRectangle) this.hitbox).sizeY);
		}
	}
	
	@Override
	public void tickEntity2() {
	}
	
	@Override
	public boolean collisionMoveable() {
		return false;
	}
	
}
