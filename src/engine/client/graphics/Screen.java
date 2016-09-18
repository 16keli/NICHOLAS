package engine.client.graphics;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import engine.client.Client;
import engine.client.graphics.sprite.ISpriteProvider;
import engine.client.graphics.sprite.Sprite;
import engine.geom2d.Point2;
import engine.geom2d.Tuple2;

/**
 * Represents the {@code Screen} where all the drawing happens
 * <p>
 * As of 22 June 2016, this is essentially just here for legacy support and static methods. It is basically
 * just a wrapper around a {@code VolatileImage} for performance.
 * <p>
 * Contains many useful {@code static} methods that adjust images as necessary, as well as many, many methods
 * that will render something on the {@code Screen}.
 * <p>
 * Somewhat of a legacy holdover from the Minicraft code base, though it has been adapted to better fit my own
 * belief of how to most easily utilize the Engine
 * 
 * @author Kevin
 */
public class Screen {
	
	
	/**
	 * The offset of the screen
	 * <p>
	 * Should be the {@code Tuple2} of the upper-left corner of the area visible to the client
	 */
	public Tuple2 offset = Point2.ORIGIN;
	
	/**
	 * The width of the {@code Screen}
	 */
	public int width;
	
	/**
	 * The height of the {@code Screen}
	 */
	public int height;
	
	/**
	 * The {@code Client} instance
	 */
	public Client client;
	
	/**
	 * Creates a new {@code Screen}
	 * 
	 * @param w
	 *            Screen width in pixels
	 * @param h
	 *            Screen height in pixels
	 */
	public Screen(Client c, int w, int h) {
		this.width = w;
		this.height = h;
		this.client = c;
	}
	
	/**
	 * Sets every pixel of the {@code BufferedImage} to the given color
	 * 
	 * @param color
	 *            The ARGB color code to set every pixel to
	 */
	public void clear(int color) {
		this.clearRegion(color, 0, 0, this.client.vImg.getWidth(), this.client.vImg.getHeight());
	}
	
	/**
	 * Sets every pixel in the area of the {@code BufferedImage} bounded by the upper-left coordinate (x, y)
	 * and width and height w and h to the given color
	 * 
	 * @param color
	 *            The ARGB color code to set the pixels to
	 * @param x
	 *            The X Coordinate of the upper-left corner
	 * @param y
	 *            The Y Coordinate of the upper-left corner
	 * @param w
	 *            The width of the region
	 * @param h
	 *            The height of the region
	 */
	public void clearArea(int color, int x, int y, int w, int h) {
		this.clearRegion(color, x, y, x + w, y + h);
	}
	
	/**
	 * Sets every pixel in the region of the {@code BufferedImage} bounded by (x1, y1) and (x2, y2) to the
	 * given color
	 * 
	 * @param color
	 *            The ARGB color code to set the pixels to
	 * @param x1
	 *            The X Coordinate of the upper-left corner
	 * @param y1
	 *            The Y Coordinate of the upper-left corner
	 * @param x2
	 *            The X Coordinate of the lower-right corner
	 * @param y2
	 *            The Y Coordinate of the lower-right corner
	 */
	public void clearRegion(int color, int x1, int y1, int x2, int y2) {
		Graphics2D g = this.client.vImg.createGraphics();
		g.fillRect(x1, y1, x2 - x1 + 1, y2 - y1 + 1);
	}
	
	/**
	 * A convenience method equivalent to calling
	 * 
	 * <pre>
	 * this.client.vImg.createGraphics();
	 * </pre>
	 * 
	 * This method simply exists to cut down on the amount of code
	 * 
	 * @return A {@code Graphics2D} instance created by the {@code Client}'s {@code VolatileImage}
	 */
	public Graphics2D getGraphics() {
		return this.client.vImg.createGraphics();
	}
	
	/**
	 * Renders the give {@code ISpriteProvider}
	 * 
	 * @param sp
	 *            The {@code ISpriteProvider} to render
	 */
	public void render(ISpriteProvider sp) {
		this.render(sp, false, false, 0);
	}
	
	/**
	 * Renders the give {@code ISpriteProvider}
	 * 
	 * @param sp
	 *            The {@code ISpriteProvider} to render
	 * @param mirrorX
	 *            Whether to flip the image in the x
	 * @param mirrorY
	 *            Whether to flip the image in the y
	 * @param quads
	 *            How many quadrants to rotate the image clockwise
	 */
	public void render(ISpriteProvider sp, boolean mirrorX, boolean mirrorY, int quads) {
		this.render(sp.getSprite(), sp.getSpritePosition(), mirrorX, mirrorY, quads);
	}
	
	/**
	 * Renders the given {@code Sprite} given its position relative to the offset
	 * 
	 * @param src
	 *            The {@code Sprite} to render
	 * @param pos
	 *            The {@code Tuple2} of this image's relative upper-left position
	 */
	public void render(Sprite src, Tuple2 pos) {
		this.render(src, pos, false, false, 0);
	}
	
	/**
	 * Renders the given {@code Sprite} given its position relative to the offset
	 * 
	 * @param src
	 *            The {@code Sprite} to render
	 * @param pos
	 *            The {@code Tuple2} of this image's relative upper-left position
	 * @param mirrorX
	 *            Whether to flip the image in the x
	 * @param mirrorY
	 *            Whether to flip the image in the y
	 * @param quads
	 *            How many quadrants to rotate the image clockwise
	 */
	public void render(Sprite src, Tuple2 pos, boolean mirrorX, boolean mirrorY, int quads) {
		this.renderAbsolute(src, pos.subtract(this.offset), mirrorX, mirrorY, quads);
	}
	
	/**
	 * Renders the given {@code Sprite} given its absolute position on the {@code Screen}
	 * 
	 * @param src
	 *            The {@code Sprite} to render
	 * @param pos
	 *            The {@code Tuple2} of this image's absolute upper-left position
	 */
	public void renderAbsolute(Sprite src, Tuple2 pos) {
		this.renderAbsolute(src, pos, false, false, 0);
	}
	
	/**
	 * Renders the given {@code Sprite} given its absolute position on the {@code Screen}
	 * 
	 * @param src
	 *            The {@code Sprite} to render
	 * @param pos
	 *            The {@code Tuple2} of this image's absolute upper-left position
	 * @param mirrorX
	 *            Whether to flip the image in the x
	 * @param mirrorY
	 *            Whether to flip the image in the y
	 * @param quads
	 *            How many quadrants to rotate the image clockwise
	 */
	public void renderAbsolute(Sprite src, Tuple2 pos, boolean mirrorX, boolean mirrorY, int quads) {
//		src = adjustImage(src, 1, mirrorX, mirrorY);
		Graphics2D g = this.client.vImg.createGraphics();
		g.drawImage(src.getAdjustedImage(1, mirrorX, mirrorY, quads), (int) pos.getX(), (int) pos.getY(),
				null);
		g.dispose();
	}
	
	/**
	 * Sets the offset of the screen
	 * 
	 * @param x
	 *            The x component of the offset
	 * @param y
	 *            The y component of the offset
	 */
	public void setOffset(double x, double y) {
		this.setOffset(Point2.of(x, y));
	}
	
	/**
	 * Sets the offset of the screen
	 * 
	 * @param off
	 *            The {@code Tuple2} representing the offset
	 */
	public void setOffset(Tuple2 off) {
		this.offset = off;
	}
	
	/**
	 * Adjusts a given {@code BufferedImage} for use with rendering images
	 * 
	 * @param src
	 *            The {@code BufferedImage} to transform
	 * @param scale
	 *            The scale to transform at
	 * @return A transformed {@code BufferedImage}
	 */
	public static BufferedImage adjustImage(BufferedImage src, int scale) {
		return adjustImage(src, scale, false, false, 0);
	}
	
	/**
	 * Adjusts a given {@code BufferedImage} for use with rendering images
	 * <p>
	 * Applies any scaling, flipping, and rotating necessary to the given image
	 * <p>
	 * This method does not change {@code src} at all, rather it places the results of the transforms into an
	 * entirely new {@code BufferedImage}
	 * 
	 * @param src
	 *            The {@code BufferedImage} to transform
	 * @param scale
	 *            The scale to transform at
	 * @param flipX
	 *            Whether or not to flip the image in the x
	 * @param flipY
	 *            Whether or not to flip the image in the y
	 * @param quads
	 *            How many quadrants to rotate the image clockwise
	 * @return A transformed {@code BufferedImage}
	 */
	public static BufferedImage adjustImage(BufferedImage src, int scale, boolean flipX, boolean flipY,
			int quads) {
		BufferedImage adj = new BufferedImage(src.getWidth() * scale, src.getHeight() * scale,
				BufferedImage.TYPE_INT_ARGB);
		AffineTransform tf = new AffineTransform();
		// First scale/flip
		if (scale != 1 || flipX || flipY) {
			tf.scale(scale * (flipX ? -1 : 1), scale * (flipY ? -1 : 1));
			tf.translate((flipX ? -src.getWidth() : 0), (flipY ? -src.getHeight() : 0));
		}
		// Then rotate
		if (quads % 4 != 0) {
			tf.quadrantRotate(quads, src.getWidth() / 2, src.getHeight() / 2);
		}
		AffineTransformOp op = new AffineTransformOp(tf, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		op.filter(src, adj);
		return adj;
	}
	
	/**
	 * Adjusts the colors of an image such that all colors that should be replaced are indeed replaced
	 * <p>
	 * This should be called by <b>ALL</b> Sprite-Possessing objects that need their colors adjusted, but only
	 * once! That will severely reduce stress on the machine!
	 * 
	 * @param src
	 *            The {@code BufferedImage} to operate on
	 * @param replace
	 *            The {@code int[]} of RGB values to replace them with
	 * @return The adjusted {@code BufferedImage}
	 */
	public static BufferedImage colorAdjust(BufferedImage src, int[] replace) {
		return colorAdjust(src, ColorWrapper.DEFAULT, replace);
	}
	
	/**
	 * Adjusts the colors of an image such that all colors that should be replaced are indeed replaced
	 * <p>
	 * This should be called by <b>ALL</b> Sprite-Possessing objects that need their colors adjusted, but only
	 * once! Store the result of this method! This will severely reduce stress on the machine!
	 * 
	 * @param src
	 *            The {@code BufferedImage} to operate on
	 * @param source
	 *            The {@code int[]} of RGB values to search for
	 * @param replace
	 *            The {@code int[]} of RGB values to replace them with
	 * @return The adjusted {@code BufferedImage}
	 */
	public static BufferedImage colorAdjust(BufferedImage src, int[] source, int[] replace) {
		for (int i = 0; i < source.length; i++) {
			System.out.println(Integer.toHexString(source[i]) + " -> " + Integer.toHexString(replace[i]));
		}
		for (int x = 0; x < src.getWidth(); x++) {
			for (int y = 0; y < src.getHeight(); y++) {
				for (int i = 0; i < source.length; i++) {
					if (src.getRGB(x, y) == source[i]) {
						src.setRGB(x, y, replace[i]);
//						System.out.println("Replacing " + Integer.toHexString(source[i]) + " with " + Integer.toHexString(replace[i]));
					}
				}
			}
		}
		return src;
	}
	
	/**
	 * Replaces all instances of one ARGB color code with another ARGB color code
	 * <p>
	 * This is more a convenience method, given that it only replaces one color with another.
	 * 
	 * @param src
	 *            The {@code BufferedImage} to operate on
	 * @param source
	 *            The ARGB Color code to search for
	 * @param replace
	 *            The ARGB Color code to replace it with
	 */
	public static BufferedImage colorAdjust(BufferedImage src, int source, int replace) {
		System.out.println(Integer.toHexString(source) + " -> " + Integer.toHexString(replace));
		for (int x = 0; x < src.getWidth(); x++) {
			for (int y = 0; y < src.getHeight(); y++) {
				if (src.getRGB(x, y) == source) {
					src.setRGB(x, y, replace);
				}
			}
		}
		return src;
	}
}
