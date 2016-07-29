package engine.client.graphics;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

/**
 * A wrapper class helps render text and other useful things
 * 
 * @author Kevin
 */
public class FontWrapper {
	
	/**
	 * The default font: Monospaced Size 6 (8 pixels)
	 */
	public static final Font defFont = new Font(Font.MONOSPACED, Font.PLAIN, 18);
	
	/**
	 * The current font
	 */
	public static Font current = defFont;
	
	/**
	 * Draws the specified message
	 * 
	 * @param msg
	 *            The message to draw
	 * @param screen
	 *            The {@code Screen} to draw on
	 * @param x
	 *            The X position of the upper-left of the message
	 * @param y
	 *            The Y position of the upper-left of the message
	 * @param col
	 *            The RGB color code of the color to render the message in
	 */
	public static void draw(String msg, Screen screen, int x, int y, int col) {
		draw(msg, screen, defFont, x, y, col);
	}
	
	/**
	 * Draws the specified message
	 * 
	 * @param msg
	 *            The message to draw
	 * @param screen
	 *            The {@code Screen} to draw on
	 * @param f
	 *            The {@code Font} to use
	 * @param x
	 *            The X position of the upper-left of the message
	 * @param y
	 *            The Y position of the upper-left of the message
	 * @param col
	 *            The RGB color code of the color to render the message in
	 */
	public static void draw(String msg, Screen screen, Font f, int x, int y, int col) {
		Graphics2D font = screen.client.vImg.createGraphics();
		font.setColor(new Color(col));
		font.setFont(current);
		font.drawString(msg, x, y);
		font.dispose();
	}
	
	/**
	 * Draws a rectangular frame
	 * 
	 * @param screen
	 *            The {@code Screen} to draw on
	 * @param title
	 *            The title, or words to render on top, of the frame
	 * @param x
	 *            The X position to start rendering in
	 * @param y
	 *            The Y position to start rendering in
	 * @param w
	 *            The width of the frame
	 * @param h
	 *            The height of the frame
	 * @param colFrame
	 *            The RGB color code of the frame
	 * @param colText
	 *            The RGB color code of the text
	 */
	public static void renderFrame(Screen screen, String title, int x, int y, int w, int h, int colFrame, int colText) {
		Graphics2D g = screen.client.vImg.createGraphics();
		g.setColor(new Color(colFrame));
		g.setStroke(new BasicStroke(1));
		g.drawRect(x, y, w, h);
		g.dispose();
		draw(title, screen, x, y, colText);
	}
	
	/**
	 * Gets the X Coordinate such that the given {@code String} will be centered at the center of the screen
	 * 
	 * @param screen
	 *            The {@code Screen} to use
	 * @param string
	 *            The Text to render
	 * @param x
	 *            The X Coordinate to center the text on
	 * @return The X Coordinate such that the given {@code String} will be centered at the center of the
	 *         screen
	 */
	public static int getXCoord(Screen screen, String string) {
		return getXCoord(screen, string, screen.width / 2);
	}
	
	/**
	 * Gets the X Coordinate such that the given {@code String} will be centered at that X Coordinate
	 * 
	 * @param screen
	 *            The {@code Screen} to use
	 * @param string
	 *            The Text to render
	 * @param x
	 *            The X Coordinate to center the text on
	 * @return The X Coordinate such that the given {@code String} will be centered at that X Coordinate
	 */
	public static int getXCoord(Screen screen, String string, int x) {
		return x - getTextSize(screen, string) / 2;
	}
	
	/**
	 * Gets the size of the given String in pixels
	 * @param screen The {@code Screen} to use
	 * @param string The Text to get the size of
	 * @return
	 */
	public static int getTextSize(Screen screen, String string) {
		Graphics2D g = screen.client.vImg.createGraphics();
		g.setFont(current);
		int disp = g.getFontMetrics().stringWidth(string);
		g.dispose();
		return disp;
	}
}
