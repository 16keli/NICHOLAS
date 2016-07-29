package engine.client.graphics.sprite;

import java.awt.image.BufferedImage;

import engine.Engine;
import engine.client.graphics.ColorWrapper;
import engine.client.graphics.Screen;

/**
 * An animated {@code Sprite} that will flip through multiple images in succession.
 * <p>
 * The time each image is displayed is roughly equal, and is dependent on {@link #period}, which is based on
 * Client-side game ticks and not fps, as ticks are on a regular schedule whereas fps can vary.
 * 
 * @author Kevin
 */
public class SpriteAnimated extends Sprite {
	
	/**
	 * The array of images to flip through
	 */
	protected BufferedImage[] imgs;
	
	/**
	 * Cached images
	 */
	protected BufferedImage[] caches;
	
	/**
	 * How long, in game ticks, each image is shown
	 */
	public int period;
	
	/**
	 * The current image the animated sprite is on
	 */
	private int current;
	
	/**
	 * The total period
	 */
	public int pertotal;
	
	public int[] scales;
	
	public boolean[] flipXs;
	
	public boolean[] flipYs;
	
	public int[] quadss;
	
	/**
	 * The time the last {@link #setCurrent(int)} was called.
	 */
	public int offset = 0;
	
	/**
	 * Creates a new {@code SpriteAnimated} with the given images and period
	 * 
	 * @param period
	 *            The time each image is shown
	 * @param imgs
	 *            The {@code BufferedImage[]} to cycle through
	 */
	protected SpriteAnimated(int period, BufferedImage... imgs) {
		super(imgs[0]);
		this.period = period;
		this.pertotal = period * imgs.length;
		this.imgs = new BufferedImage[imgs.length];
		this.caches = new BufferedImage[imgs.length];
		this.scales = new int[imgs.length];
		this.flipXs = new boolean[imgs.length];
		this.flipYs = new boolean[imgs.length];
		this.quadss = new int[imgs.length];
		for (int i = 0; i < imgs.length; i++) {
			this.imgs[i] = ColorWrapper.checkARGB(imgs[i]);
		}
	}
	
	/**
	 * Sets the current image of the {@code Sprite}. Doing so will reset the image tho lel
	 * 
	 * @param current
	 */
	public void setCurrent(int current) {
		this.offset = Engine.getGameTimeClient();
		this.current = current;
	}
	
	@Override
	public BufferedImage getImage() {
		current = ((Engine.getGameTimeClient() - offset) % pertotal) / period;
		return imgs[current];
	}
	
	@Override
	public BufferedImage getAdjustedImage(int scale, boolean flipX, boolean flipY, int quads) {
		current = ((Engine.getGameTimeClient() - offset) % pertotal) / period;
		if ((scale == this.scales[current]) && (flipX == this.flipXs[current])
				&& (flipY == this.flipYs[current]) && (quads == this.quadss[current])) {
			return caches[current];
		}
		caches[current] = Screen.adjustImage(this.imgs[current], scale, flipX, flipY, quads);
		this.scales[current] = scale;
		this.flipXs[current] = flipX;
		this.flipYs[current] = flipY;
		this.quadss[current] = quads;
		return caches[current];
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
	@Override
	public Sprite transform(int scale, boolean flipX, boolean flipY, int quads) {
		return SpriteAnimated.transform(this, scale, flipX, flipY, quads);
	}
	
	/**
	 * Creates a new {@code SpriteAnimated} with the source {@code SpriteAnimated}'s images transformed by the given
	 * parameters
	 * 
	 * @param src
	 *            The source {@code SpriteAnimated}
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
	public static SpriteAnimated transform(SpriteAnimated src, int scale, boolean flipX, boolean flipY, int quads) {
		BufferedImage[] imgs = new BufferedImage[src.imgs.length];
		for (int i = 0; i < imgs.length; i++) {
			imgs[i] = Screen.adjustImage(src.imgs[i], scale, flipX, flipY, quads);
		}
		return of(src.period, imgs);
	}
	
	/**
	 * Creates a new {@code SpriteAnimated} from the given parameters
	 * 
	 * @param period
	 *            The period, in game ticks, that each image is shown
	 * @param imgs
	 *            The array of images to display
	 * @return
	 */
	public static SpriteAnimated of(int period, BufferedImage... imgs) {
		return new SpriteAnimated(period, imgs);
	}
	
}
