package engine.client.menu;

import java.awt.Color;

import engine.client.graphics.FontWrapper;
import engine.client.graphics.Screen;

/**
 * A component of a {@code Menu}
 * <p>
 * Can be selected via use of keyboard keys, as well as the arrow keys to navigate between them
 * 
 * @author Kevin
 */
public class MenuComponent {
	
	/**
	 * The text of the component
	 */
	private String text;
	
	/**
	 * Text that goes before the component when selected
	 */
	private String before;
	
	/**
	 * Text that goes after the component when selected
	 */
	private String after;
	
	/**
	 * The X and Y coordinates of the component
	 */
	protected int x, y;
	
	/**
	 * Whether or not to update the component's position
	 */
	public boolean update = true;
	
	/**
	 * The size of the Text
	 */
	public int textSize = 0;
	
	/**
	 * Whether or not to center the component at a certain X position
	 */
	private boolean center = false;
	
	/**
	 * Whether or not to center the component in the middle of the screen
	 */
	private boolean centerScreen = false;
	
	public MenuComponent(String t, int y) {
		this(t, "> ", " <", y);
	}
	
	/**
	 * Creates a new menu component in the middle of the screen
	 * 
	 * @param t
	 *            The text of the component
	 * @param b
	 *            The text before the component when selected
	 * @param a
	 *            The text after the component when selected
	 * @param y
	 *            The Y position of the component
	 */
	public MenuComponent(String t, String b, String a, int y) {
		this.text = t;
		this.before = b;
		this.after = a;
		this.y = y;
		this.centerScreen = true;
	}
	
	/**
	 * Creates a new menu component in the middle of the screen and default before and after selected strings
	 * 
	 * @param t
	 *            The text of the component
	 * @param y
	 *            The Y position of the component
	 */
	public MenuComponent(String t, int x, int y) {
		this(t, "> ", " <", x, y, true);
	}
	
	/**
	 * Creates a new menu component at the given coordinates with default before and after selected strings
	 * 
	 * @param t
	 *            The text of the component
	 * @param b
	 *            The text before the component when selected
	 * @param a
	 *            The text after the component when selected
	 * @param x
	 *            The X position of the component
	 * @param y
	 *            The Y position of the component
	 * @param center
	 *            Whether or not to center the component at the given X or not
	 */
	public MenuComponent(String t, int x, int y, boolean center) {
		this(t, "> ", " <", x, y, center);
	}
	
	/**
	 * Creates a new menu component at the given coordinates
	 * 
	 * @param t
	 *            The text of the component
	 * @param b
	 *            The text before the component when selected
	 * @param a
	 *            The text after the component when selected
	 * @param x
	 *            The X position of the component
	 * @param y
	 *            The Y position of the component
	 * @param center
	 *            Whether or not to center the component at the given X or not
	 */
	public MenuComponent(String t, String b, String a, int x, int y, boolean center) {
		this.text = t;
		this.before = b;
		this.after = a;
		this.x = x;
		this.y = y;
		this.center = center;
	}
	
	/**
	 * Updates the text of the component
	 * 
	 * @param s
	 *            The new text of the component
	 */
	public void update(String s) {
		this.text = s;
		update = true;
	}
	
	/**
	 * Retrieves the combined text of the component
	 * 
	 * @param selected
	 *            Whether the component is selected
	 * @return The aggregate text of the component
	 */
	public String getText(boolean selected) {
		return (selected ? this.before + this.text + this.after : this.text);
	}
	
	/**
	 * Draws a {@code MenuComponent} on the screen
	 * 
	 * @param screen
	 *            The {@code Screen} to draw on
	 * @param m
	 *            The {@code MenuComponent} in question
	 * @param selected
	 *            Whether or not the component is selected
	 * @param offX
	 *            The offset in the X
	 * @param offY
	 *            The offset in the Y
	 */
	public static void draw(Screen screen, MenuComponent m, boolean selected, int offX, int offY) {
		if (m.centerScreen) {
			m.x = FontWrapper.getXCoord(screen, m.getText(selected));
			FontWrapper.draw(m.getText(selected), screen, m.x - offX, m.y - offY,
					(selected ? Color.WHITE.getRGB() : Color.GRAY.getRGB()));
		} else if (m.center) {
			FontWrapper.draw(m.getText(selected), screen,
					FontWrapper.getXCoord(screen, m.getText(selected), m.x) - offX, m.y - offY,
					(selected ? Color.WHITE.getRGB() : Color.GRAY.getRGB()));
		} else {
			FontWrapper.draw(m.getText(selected), screen, m.x - offX, m.y - offY,
					(selected ? Color.WHITE.getRGB() : Color.GRAY.getRGB()));
		}
		if (m.textSize == 0) {
			m.textSize = FontWrapper.getTextSize(screen, m.getText(selected));
		}
	}
	
	public int textSize() {
		return this.textSize;
	}
	
}
