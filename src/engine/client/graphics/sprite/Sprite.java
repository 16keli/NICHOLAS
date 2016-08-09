package engine.client.graphics.sprite;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import engine.Engine;
import engine.client.graphics.ColorWrapper;
import engine.client.graphics.Screen;
import engine.level.Vector2;

/**
 * Represents a sprite on the client side, that gets rendered on the {@code Screen}
 * <p>
 * Basically a utility wrapper around a {@code BufferedImage} that provides utility methods, as well as
 * possible animations
 * 
 * @see SpriteAnimated
 * @see ISpriteProvider
 * @author Kevin
 */
public class Sprite {
	
	/**
	 * The width of the {@code Sprite}
	 */
	public int width;
	
	/**
	 * The height of the {@code Sprite}
	 */
	public int height;
	
	/**
	 * The {@code BufferedImage}'s pixels
	 */
	protected int[] pixels;
	
	/**
	 * The {@code BufferedImage} source
	 */
	protected BufferedImage src;
	
	/**
	 * The cached image with all necessary transforms
	 */
	protected BufferedImage cache;
	
	protected int scale;
	
	protected boolean flipX;
	
	protected boolean flipY;
	
	protected int quads;
	
	/**
	 * Creates a new {@code Sprite} from a given {@code BufferedImage}
	 * <p>
	 * For convenience's sake, use the static methods {@link #of(BufferedImage)} and {@link #of(File)} to
	 * create {@code Sprite}s
	 * 
	 * @see Sprite.of
	 * @param image
	 *            The {@code BufferedImage} to create a {@code Sprite from}
	 */
	protected Sprite(BufferedImage image) {
		this.src = ColorWrapper.checkARGB(image);
		this.width = image.getWidth();
		this.height = image.getHeight();
		this.pixels = image.getRGB(0, 0, this.width, this.height, null, 0, this.width);
	}
	
	/**
	 * Retrieves an adjusted version of the internal {@code BufferedImage}
	 * <p>
	 * Makes use of a cached {@code BufferedImage} so as to not perform the same transformations every tick,
	 * if necessary. In case the parameters for the image are the same, then the cached image will be
	 * returned. If not, then the necessary transforms are performed, the image is cached, and the returned.
	 * 
	 * @param scale
	 *            The scale of the image
	 * @param flipX
	 *            Whether to flip the image in the X
	 * @param flipY
	 *            Whether to flip the image in the Y
	 * @param quads
	 *            The number of quadrants to rotate the image clockwise
	 * @return An adjusted {@code BufferedImage}
	 */
	public BufferedImage getAdjustedImage(int scale, boolean flipX, boolean flipY, int quads) {
		if ((scale == this.scale) && (flipX == this.flipX) && (flipY == this.flipY)
				&& (quads == this.quads)) {
			return this.cache;
		}
		this.cache = Screen.adjustImage(this.src, scale, flipX, flipY, quads);
		this.scale = scale;
		this.flipX = flipX;
		this.flipY = flipY;
		this.quads = quads;
		return this.cache;
	}
	
	/**
	 * Retrieves the {@code BufferedImage} of the {@code Sprite}
	 * 
	 * @return The {@code BufferedImage} of the {@code Sprite}
	 */
	public BufferedImage getImage() {
		return this.src;
	}
	
	/**
	 * Gets the size of this image as a {@code Vector2}
	 * 
	 * @return The size of the image as a {@code Vector2}
	 */
	public Vector2 getImageSize() {
		return Vector2.of(this.width, this.height);
	}
	
	/**
	 * Creates a new {@code Sprite} with the source {@code Sprite}'s image(s) being scaled by the given factor
	 * 
	 * @param scale
	 *            The scale
	 * @return
	 */
	public Sprite scale(int scale) {
		return this.transform(scale, false, false, 0);
	}
	
	/**
	 * Creates a new {@code Sprite} with the source {@code Sprite}'s image(s) being flipped in their
	 * respective directions
	 * 
	 * @param flipX
	 *            Whether to flip in the X
	 * @param flipY
	 *            Whether to flip in the Y
	 * @return
	 */
	public Sprite flip(boolean flipX, boolean flipY) {
		return this.transform(1, flipX, flipY, 0);
	}
	
	/**
	 * Creates a new {@code Sprite} with the source {@code Sprite}'s image(s) being rotated clockwise the
	 * given number of quadrants
	 * 
	 * @param quads
	 *            The number of quadrants to rotate
	 * @return
	 */
	public Sprite rotate(int quads) {
		return this.transform(1, false, false, quads);
	}
	
	/**
	 * Creates a new {@code Sprite} with the source {@code Sprite}'s image(s) transformed by the given
	 * parameters
	 * 
	 * @param scale
	 *            The scale
	 * @param flipX
	 *            Whether to flip in the X
	 * @param flipY
	 *            Whether to flip in the Y
	 * @param quads
	 *            The number of quadrants to rotate
	 * @return
	 */
	public Sprite transform(int scale, boolean flipX, boolean flipY, int quads) {
		return Sprite.transform(this, scale, flipX, flipY, quads);
	}
	
	/**
	 * Creates a new {@code Sprite} with the source {@code Sprite}'s image(s) being scaled by the given factor
	 * 
	 * @param src
	 *            The source {@code Sprite}
	 * @param scale
	 *            The scale
	 * @return
	 */
	public static Sprite scale(Sprite src, int scale) {
		return transform(src, scale, false, false, 0);
	}
	
	/**
	 * Creates a new {@code Sprite} with the source {@code Sprite}'s image(s) being flipped in their
	 * respective directions
	 * 
	 * @param src
	 *            The source {@code Sprite}
	 * @param flipX
	 *            Whether to flip in the X
	 * @param flipY
	 *            Whether to flip in the Y
	 * @return
	 */
	public static Sprite flip(Sprite src, boolean flipX, boolean flipY) {
		return transform(src, 1, flipX, flipY, 0);
	}
	
	/**
	 * Creates a new {@code Sprite} with the source {@code Sprite}'s image(s) being rotated clockwise the
	 * given number of quadrants
	 * 
	 * @param src
	 *            The source {@code Sprite}
	 * @param quads
	 *            The number of quadrants to rotate
	 * @return
	 */
	public static Sprite rotate(Sprite src, int quads) {
		return transform(src, 1, false, false, quads);
	}
	
	/**
	 * Creates a new {@code Sprite} with the source {@code Sprite}'s image(s) transformed by the given
	 * parameters
	 * 
	 * @param src
	 *            The source {@code Sprite}
	 * @param scale
	 *            The scale
	 * @param flipX
	 *            Whether to flip in the X
	 * @param flipY
	 *            Whether to flip in the Y
	 * @param quads
	 *            The number of quadrants to rotate
	 * @return
	 */
	public static Sprite transform(Sprite src, int scale, boolean flipX, boolean flipY, int quads) {
		return of(src.getAdjustedImage(scale, flipX, flipY, quads));
	}
	
	/**
	 * Creates a new {@code Sprite} from the given file name. The default file path
	 * {@link engine.Engine#getFilePath()} is automatically appended.
	 * 
	 * @param fileName
	 *            The name of the file
	 * @return A new {@code Sprite} instance if the file is found, {@code null} if not
	 */
	public static Sprite of(String fileName) {
		return of(new File(Engine.getFilePath() + fileName));
	}
	
	/**
	 * Creates a new {@code Sprite} from the given file
	 * 
	 * @param image
	 *            The {@code File} to read
	 * @return A new {@code Sprite} instance if the file is found, {@code null} if not
	 */
	public static Sprite of(File image) {
		try {
			return new Sprite(ImageIO.read(image));
		} catch (Exception e) {
			System.err.println("Could not find file " + image.getName());
		}
		return null;
	}
	
	/**
	 * Creates a new {@code Sprite} from the given {@code BufferedImage}
	 * 
	 * @param image
	 *            The {@code BufferedImage}
	 * @return A new {@code Sprite}
	 */
	public static Sprite of(BufferedImage image) {
		return new Sprite(image);
	}
}
