package engine.client;

import java.io.Serializable;

/**
 * A client side only way to read the inputs from the user
 * 
 * @author Kevin
 */
public class Input implements Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5503091190910397374L;
	
	/**
	 * Creates a new {@code Input} that is bound to the given {@code KeyEvent} or {@code MouseEvent}
	 * 
	 * @param bind
	 */
	public Input(int bind) {
		this.bind = bind;
	}
	
	/**
	 * The number of times this key has been toggled into "pressed" state
	 */
	private int presses = 0;
	
	/**
	 * The number of times this key has absorbed a press. Essentially just here to act as a buffer and allow
	 * for input click detection
	 */
	private int absorbs = 0;
	
	/**
	 * Whether the input is currently being held down
	 */
	private boolean down = false;
	
	/**
	 * Whether the input was just clicked in the past tick
	 */
	private boolean clicked = false;
	
	/**
	 * The {@code KeyEvent} or {@code MouseEvent} that this key is bound to by default
	 */
	private int bind;
	
	/**
	 * Toggles the {@code Input}
	 * 
	 * @param pressed
	 */
	public void toggle(boolean pressed) {
		if (pressed != this.down) {
			this.down = pressed;
		}
		if (pressed) {
			this.presses++;
		}
	}
	
	/**
	 * Ticks the input's clicked status
	 */
	public void tick() {
		if (this.absorbs < this.presses) {
			this.absorbs++;
			this.clicked = true;
		} else {
			this.clicked = false;
		}
	}
	
	/**
	 * Returns whether the input is currently being held down
	 * 
	 * @return
	 */
	public boolean isDown() {
		return this.down;
	}
	
	/**
	 * Returns whether the input was clicked in the past tick
	 * 
	 * @return
	 */
	public boolean isClicked() {
		return this.clicked;
	}
	
	/**
	 * Gets the {@code KeyEvent} or {@code MouseEvent} this {@code Input} is bound to
	 * 
	 * @return
	 */
	public int getBind() {
		return this.bind;
	}
	
	/**
	 * Releases this Input
	 */
	protected void release() {
		this.down = false;
		this.clicked = false;
	}
}
