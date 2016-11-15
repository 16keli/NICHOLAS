package engine.physics.level.platform;

import engine.client.graphics.Screen;
import engine.client.graphics.sprite.Sprite;
import engine.physics.entity.EntityPhysics;
import engine.physics.entity.Hitbox.HitboxRectangle;
import engine.physics.level.LevelPhysics;

/**
 * A platform that {@code EntityPhysicsPlatformer} can stand on
 * 
 * @author Kevin
 */
public class Platform extends EntityPhysics {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public Platform(LevelPhysics l, double x, double y, double w, double h, double m, Sprite sprite) {
		super(l, x, y, new HitboxRectangle(w, h), m, sprite);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void tickEntity1() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void tickEntity2() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void render(Screen s) {
		s.render(this);
	}
	
}
