package engine.client.graphics.sprite;

import engine.level.Vector2;

/**
 * An Object that has a {@code Sprite} and must also have a {@code Vector2} position as well.
 * 
 * @author Kevin
 */
public interface ISpriteProvider {
	
	/**
	 * Gets the {@code Sprite} to render
	 * 
	 * @return The {@code Sprite} to render
	 */
	public Sprite getSprite();
	
	/**
	 * Gets the {@code Vector2} position to render the {@code Sprite} at
	 * 
	 * @return The {@code Vector2} position to render at
	 */
	public Vector2 getSpritePosition();
	
}
