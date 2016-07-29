package engine.client.graphics.sprite;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import engine.Engine;

/**
 * A collection of {@code Sprite}s organized in a single sheet for convenience.
 * <p>
 * Through {@code SpriteSheet}s, it is possible to get {@link #getSprite(int, int, int, int) Sprites},
 * {@link #getAnimated(int, int, int, int, int, int, int) SpriteAnimateds}, <strike>and more Sprites</strike>
 * (Not Supported Yet) by using any of the convenience methods included.
 * 
 * @author Kevin
 */
public class SpriteSheet {
	
	/**
	 * The width of the {@code SpriteSheet}
	 */
	public int width;
	
	/**
	 * The height of the {@code SpriteSheet}
	 */
	public int height;
	
	/**
	 * The width of this {@code SpriteSheet} in {@code Sprite}s
	 */
	protected int sWidth;
	
	/**
	 * The height of this {@code SpriteSheet} in {@code Sprite}s
	 */
	protected int sHeight;
	
	/**
	 * The width of an individual {@code Sprite} in pixels
	 */
	protected int sPixWidth;
	
	/**
	 * The height of an individual {@code Sprite} in pixels
	 */
	protected int sPixHeight;
	
	/**
	 * The {@code BufferedImage} that composes this {@code SpriteSheet}
	 */
	protected BufferedImage src;
	
	/**
	 * Creates a new {@code SpriteSheet} from the given {@code BufferedImage}
	 * 
	 * @param image
	 *            The {@code BufferedImage} to use as a source
	 * @param sPixWidth
	 *            The width of an individual {@code Sprite} in pixels
	 * @param sPixHeight
	 *            The height of an individual {@code Sprite} in pixels
	 */
	protected SpriteSheet(BufferedImage image, int sPixWidth, int sPixHeight) {
		this.src = image;
		this.width = image.getWidth();
		this.height = image.getHeight();
		this.sPixWidth = sPixWidth;
		this.sPixHeight = sPixHeight;
		this.sWidth = this.width / this.sPixWidth;
		this.sHeight = this.height / this.sPixHeight;
	}
	
	/**
	 * Creates a new {@code Sprite} from the given parameters on the {@code SpriteSheet}. The width and height
	 * are defaulted to 1.
	 * <p>
	 * Values for the parameters are <b>NOT</b> in pixels, rather they are in Sprites
	 * 
	 * @param x
	 *            The position of the {@code Sprite} in the x
	 * @param y
	 *            The position of the {@code Sprite} in the y
	 * @return A new {@code Sprite}
	 */
	public Sprite getSprite(int x, int y) {
		return getSprite(x, y, 1, 1);
	}
	
	/**
	 * Creates a new {@code Sprite} from the given parameters on the {@code SpriteSheet}
	 * <p>
	 * Values for the parameters are <b>NOT</b> in pixels, rather they are in Sprites
	 * 
	 * @param x
	 *            The position of the {@code Sprite} in the x
	 * @param y
	 *            The position of the {@code Sprite} in the y
	 * @param w
	 *            The width of the {@code Sprite}
	 * @param h
	 *            The height of the {@code Sprite}
	 * @return A new {@code Sprite}
	 */
	public Sprite getSprite(int x, int y, int w, int h) {
		return Sprite.of(getImage(x, y, w, h));
	}
	
	/**
	 * Creates a new {@code BufferedImage} from the given parameters on the {@code SpriteSheet}
	 * <p>
	 * Values for the parameters are <b>NOT</b> in pixels, rather they are in Sprites
	 * 
	 * @param x
	 *            The position of the {@code Sprite} in the x
	 * @param y
	 *            The position of the {@code Sprite} in the y
	 * @param w
	 *            The width of the {@code Sprite}
	 * @param h
	 *            The height of the {@code Sprite}
	 * @return A new {@code BufferedImage}
	 */
	public BufferedImage getImage(int x, int y, int w, int h) {
		return this.src.getSubimage(x * sPixWidth, y * sPixHeight, w * sPixWidth, h * sPixHeight);
	}
	
	/**
	 * Creates a new {@code SpriteAnimated} from the given parameters on the {@code SpriteSheet}. The width
	 * and height are defaulted to 1, and begins at the upper-left corner of 0, 0 and extends through the
	 * entire {@code SpriteSheet}.
	 * <p>
	 * The animation goes first from left to right, then goes down a row, if either are applicable.
	 * 
	 * @param period
	 *            The period of the {@code SpriteAnimated}
	 * @return A new {@code SpriteAnimated} based on the {@code SpriteSheet}
	 */
	public SpriteAnimated getAnimated(int period) {
		return getAnimated(period, this.sWidth, this.sHeight);
	}
	
	/**
	 * Creates a new {@code SpriteAnimated} from the given parameters on the {@code SpriteSheet}. The width
	 * and height are defaulted to 1, and begins at the upper-left corner of 0, 0.
	 * <p>
	 * Values for the parameters are <b>NOT</b> in pixels, rather they are in Sprites
	 * <p>
	 * The animation goes first from left to right, then goes down a row, if either are applicable.
	 * 
	 * @param period
	 *            The period of the {@code SpriteAnimated}
	 * @param aw
	 *            The width of the number of {@code Sprite}s
	 * @param ah
	 *            The height of the number of {@code Sprite}s
	 * @return A new {@code SpriteAnimated} with the given parameters
	 */
	public SpriteAnimated getAnimated(int period, int aw, int ah) {
		return getAnimated(period, 0, 0, aw, ah);
	}
	
	/**
	 * Creates a new {@code SpriteAnimated} from the given parameters on the {@code SpriteSheet}. The width
	 * and height are defaulted to 1.
	 * <p>
	 * Values for the parameters are <b>NOT</b> in pixels, rather they are in Sprites
	 * <p>
	 * The animation goes first from left to right, then goes down a row, if either are applicable.
	 * 
	 * @param period
	 *            The period of the {@code SpriteAnimated}
	 * @param x
	 *            The x position to start in
	 * @param y
	 *            The y position to start in
	 * @param aw
	 *            The width of the number of {@code Sprite}s
	 * @param ah
	 *            The height of the number of {@code Sprite}s
	 * @return A new {@code SpriteAnimated} with the given parameters
	 */
	public SpriteAnimated getAnimated(int period, int x, int y, int aw, int ah) {
		return getAnimated(period, x, y, aw, ah, 1, 1);
	}
	
	/**
	 * Creates a new {@code SpriteAnimated} from the given parameters on the {@code SpriteSheet}.
	 * <p>
	 * Values for the parameters are <b>NOT</b> in pixels, rather they are in Sprites
	 * <p>
	 * The animation goes first from left to right, then goes down a row, if either are applicable.
	 * 
	 * @param period
	 *            The period of the {@code SpriteAnimated}
	 * @param x
	 *            The x position to start in
	 * @param y
	 *            The y position to start in
	 * @param aw
	 *            The width of the number of {@code Sprite}s
	 * @param ah
	 *            The height of the number of {@code Sprite}s
	 * @param w
	 *            The width of each {@code Sprite}
	 * @param h
	 *            The height of each {@code Sprite}
	 * @return A new {@code SpriteAnimated} with the given parameters
	 */
	public SpriteAnimated getAnimated(int period, int x, int y, int aw, int ah, int w, int h) {
		return SpriteAnimated.of(period, getImageArray(x, y, aw, ah, w, h));
	}
	
	/**
	 * Creates a new {@code BufferedImage[]} from the given parameters on the {@code SpriteSheet}.
	 * <p>
	 * Values for the parameters are <b>NOT</b> in pixels, rather they are in Sprites
	 * 
	 * @param x
	 *            The x position to start in
	 * @param y
	 *            The y position to start in
	 * @param aw
	 *            The width of the number of {@code Sprite}s
	 * @param ah
	 *            The height of the number of {@code Sprite}s
	 * @param w
	 *            The width of each {@code Sprite}
	 * @param h
	 *            The height of each {@code Sprite}
	 * @return A new {@code BufferedImage[]} based on the parameters
	 */
	public BufferedImage[] getImageArray(int x, int y, int aw, int ah, int w, int h) {
		BufferedImage[] imgs = new BufferedImage[aw * ah];
		for (int ax = 0; ax < aw; ax++) {
			for (int ay = 0; ay < ah; ay++) {
				imgs[ay * ah + ax] = this.src.getSubimage(ax * sPixWidth, ay * sPixHeight, w * sPixWidth,
						h * sPixHeight);
			}
		}
		return imgs;
	}
	
	/**
	 * Creates a new {@code SpriteSheet} from the given file
	 * 
	 * @param image
	 *            The {@code File} to read
	 * @param sPixWidth
	 *            The width of an individual {@code Sprite} in pixels
	 * @param sPixHeight
	 *            The height of an individual {@code Sprite} in pixels
	 * @return A new {@code SpriteSheet} instance if the file is found, {@code null} if not
	 */
	public static SpriteSheet of(File image, int sPixWidth, int sPixHeight) {
		try {
			return new SpriteSheet(ImageIO.read(image), sPixWidth, sPixHeight);
		} catch (Exception e) {
			System.err.println("Could not find file " + image.getAbsolutePath());
		}
		return null;
	}
	
	/**
	 * Creates a new {@code SpriteSheet} from the given {@code BufferedImage}
	 * 
	 * @param image
	 *            The {@code BufferedImage}
	 * @param sPixWidth
	 *            The width of an individual {@code Sprite} in pixels
	 * @param sPixHeight
	 *            The height of an individual {@code Sprite} in pixels
	 * @return A new {@code SpriteSheet}
	 */
	public static SpriteSheet of(BufferedImage image, int sPixWidth, int sPixHeight) {
		return new SpriteSheet(image, sPixWidth, sPixHeight);
	}
	
	/**
	 * Creates a new {@code SpriteSheet} from the given file name. The default file path
	 * {@link engine.Engine#getFilePath()} is automatically appended.
	 * 
	 * @param fileName
	 *            The name of the file
	 * @param sPixWidth
	 *            The width of an individual {@code Sprite} in pixels
	 * @param sPixHeight
	 *            The height of an individual {@code Sprite} in pixels
	 * @return A new {@code SpriteSheet} instance if the file is found, {@code null} if not
	 */
	public static SpriteSheet of(String fileName, int sPixWidth, int sPixHeight) {
		return of(new File(Engine.getFilePath() + fileName), sPixWidth, sPixHeight);
	}
}
