package engine.client.menu;

import java.awt.Color;

import engine.client.Client;
import engine.client.InputHandler;
import engine.client.graphics.Screen;

/**
 * An Overlay-style {@code Menu} that only occupies a selected region of the {@code Screen}, while allowing
 * the rest of the {@code Screen} to continue updating as normal.
 * 
 * @author Kevin
 */
public abstract class MenuOverlay {
	
	/**
	 * The {@code Client} this menu is from
	 */
	protected Client client;
	
	/**
	 * The InputHander
	 */
	protected InputHandler input;
	
	/**
	 * The parent menu
	 */
	private Menu parent = null;
	
	/**
	 * The components of the menu
	 */
	protected MenuComponent[][] comps;
	
	/**
	 * The currently selected component
	 */
	protected MenuComponent selected;
	
	/**
	 * The X and Y of the currently selected components
	 */
	protected int x, y = 0;
	
	/**
	 * The offset of the Menu
	 */
	protected int offX, offY = 0;
	
	/**
	 * Whether to render the game underneath the menu
	 */
	protected boolean renderGameUnder = true;
	
	/**
	 * The RGB color code of the color to clear the {@code Screen} with
	 */
	public int clearRGB = Color.BLACK.getRGB();
	
	public MenuOverlay() {
	}
	
	public MenuOverlay(MenuComponent[][] c) {
		this.comps = c;
		this.selected = c[0][0];
	}
	
	public MenuOverlay(Menu p, MenuComponent[][] c) {
		this(c);
		this.parent = p;
	}
	
	/**
	 * Initializes the {@code Menu} by providing it with the appropriate {@code Client} and
	 * {@code InputHandler}
	 * 
	 * @param client
	 *            The {@code Client}
	 * @param input
	 *            The {@code InputHandler}
	 */
	public void init(Client client, InputHandler input) {
		this.input = input;
		this.client = client;
	}
	
	/**
	 * Handles selection of {@code MenuComponent}s as well as movement up the menu chain (Not down)
	 * 
	 * @see tickMenu()
	 */
	public void tick() {
//		if (input.enter.clicked) {
//			Sound.boop.play();
//		}
		if (this.parent != null) {
			if (this.input.escape.clicked) {
				this.client.setMenu(this.parent);
			}
		}
		if (this.comps.length != 0) {
			// Movement of cursor around the screen
			if (this.input.up.clicked) {
				if (this.componentExists(this.x, this.y - 1)) {
					this.y--;
					if (this.y < 0) {
						this.y = this.comps[this.x].length - 1;
					}
					this.selected = this.comps[this.x][this.y];
				}
			} else if (this.input.down.clicked) {
				if (this.componentExists(this.x, this.y + 1)) {
					this.y++;
					if (this.y >= this.comps[this.x].length) {
						this.y = 0;
					}
					this.selected = this.comps[this.x][this.y];
				}
			} else if (this.input.left.clicked) {
				if (this.componentExists(this.x - 1, this.y)) {
					this.x--;
					if (this.x < 0) {
						this.x = this.comps.length - 1;
					}
					this.selected = this.comps[this.x][this.y];
				}
			} else if (this.input.right.clicked) {
				if (this.componentExists(this.x + 1, this.y)) {
					this.x++;
					if (this.x >= this.comps.length) {
						this.x = 0;
					}
					this.selected = this.comps[this.x][this.y];
				}
			}
			// Movement of screen based on cursor position. The if statement helps!
			if (!this.componentEntirelyVisible(this.selected)) {
				if (this.selected.x + this.selected.textSize() - this.offX > this.x2 * .9) {
					this.offX += (this.x2 - this.x1) * .1;
				}
				if (this.selected.x - this.offX < this.x2 * .1) {
					this.offX -= (this.x2 - this.x1) * .1;
				}
				if (this.selected.y + 16 - this.offY > this.y2 * .9) {
					this.offY += (this.y2 - this.y1) * .1;
				}
				if (this.selected.y - this.offY < this.y2 * .1) {
					this.offY -= (this.y2 - this.y1) * .1;
				}
				System.out.println(this.selected.x + " " + this.selected.y);
				System.out
						.println(this.offX + " " + this.offY + this.componentEntirelyVisible(this.selected));
			}
		}
		this.tickMenu();
	}
	
	/**
	 * Checks whether a {@code MenuComponent} exists at the given {@code MenuComponent[]}
	 * 
	 * @param x
	 *            The X in the {@code MenuComponent[]}
	 * @param y
	 *            The Y in the {@code MenuComponent[]}
	 * @return Whether or not a component exists
	 */
	public boolean componentExists(int x, int y) {
		int ax = x;
		int ay = y;
		if (x >= this.comps.length) {
			ax = 0;
		}
		if (x < 0) {
			ax = this.comps.length - 1;
		}
		if (y >= this.comps[ax].length) {
			ay = 0;
		}
		if (y < 0) {
			ay = this.comps[ax].length - 1;
		}
		return (this.comps.length >= x && this.comps[ax].length >= y) && this.comps[ax][ay] != null;
	}
	
	/**
	 * It is recommended that menu component clicks are handled here
	 */
	public abstract void tickMenu();
	
	/**
	 * Any special non-{@code MenuComponent} stuff goes here
	 * 
	 * @param screen
	 */
	public abstract void renderMenu(Screen screen);
	
	/**
	 * The X and Y coordinates of the upper-left and lower-right corners of the region of jurisdiction of this
	 * {@code Menu}
	 */
	public int x1, y1, x2, y2;
	
	public void setRegion(int x1, int y1, int x2, int y2) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
	}
	
	/**
	 * Checks whether the given {@code MenuComponent} is entirely visible to the user
	 * 
	 * @param mc
	 *            The {@code MenuComponent} to check visibility of
	 * @return Whether the {@code MenuComponent} is entirely visible to the user
	 */
	public boolean componentEntirelyVisible(MenuComponent mc) {
		// Check each part individually
		return this.componentVisibleHorizontal(mc) && this.componentVisibleVertical(mc);
	}
	
	/**
	 * Checks whether the given {@code MenuComponent} is visible horizontally (Assuming the vertical size of
	 * the {@code MenuOverlay} is infinite
	 * 
	 * @param mc
	 *            The {@code MenuComponent} to check visibility of
	 * @return Whether the {@code MenuComponent} is visible horizontally
	 */
	public boolean componentVisibleHorizontal(MenuComponent mc) {
		boolean leftVisible = mc.x - this.offX >= this.x1;
		boolean rightVisible = mc.x - this.offX + mc.textSize <= this.x2;
		return leftVisible && rightVisible;
	}
	
	/**
	 * Checks whether the given {@code MenuComponent} is visible vertically (Assuming the horizontal size of
	 * the {@code MenuOverlay} is infinite
	 * 
	 * @param mc
	 *            The {@code MenuComponent} to check visibility of
	 * @return Whether the {@code MenuComponent is visible vertically}
	 */
	public boolean componentVisibleVertical(MenuComponent mc) {
		boolean topVisible = mc.y - this.offY + 16 >= this.y1;
		boolean bottomVisible = mc.y - this.offY <= this.y2;
		return topVisible && bottomVisible;
	}
	
	/**
	 * Renders the menu
	 * 
	 * @param screen
	 *            The {@code Screen}
	 */
	public void render(Screen screen) {
		screen.clearRegion(this.clearRGB, this.x1, this.y1, this.x2, this.y2);
		this.renderMenu(screen);
		for (int sx = 0; sx < this.comps.length; sx++) {
			for (int sy = 0; sy < this.comps[sx].length; sy++) {
				if (this.componentExists(sx, sy)) {
					MenuComponent m = this.comps[sx][sy];
//					if (m.update) {
//						if (m.center == true) {
//							m.x = (m.centerx * 2) - 4 * m.text.length();
//						} else if (m.centerScreen == true) {
//							m.x = (screen.width / 2) - 4 * m.text.length();
//						}
//						m.update = false;
//					}
					MenuComponent.draw(screen, m, m == this.selected, this.offX, this.offY);
				}
			}
		}
	}
	
	/**
	 * Whether or not to render the {@code Game} underneath the {@code Menu}
	 * 
	 * @return
	 */
	public boolean rendersGame() {
		return this.renderGameUnder;
	}
	
}
