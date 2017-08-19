package engine.input;

import java.awt.event.KeyEvent;
import java.nio.ByteBuffer;

import engine.server.Server;

/**
 * A movement on the menu. This is Client-side only, so we do not need to implement the methods required by
 * {@code Action}, but doing so is good practice so we will
 * 
 * @author Kevin
 */
public class ActionMenuInput extends Action {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2124720962120044032L;
	
	/**
	 * The "Direction" that the action is desired to be
	 */
	public int direction;
	
	/**
	 * The up direction
	 */
	public static final ActionMenuInput UP = new ActionMenuInput(KeyEvent.VK_UP);
	
	/**
	 * The down direction
	 */
	public static final ActionMenuInput DOWN = new ActionMenuInput(KeyEvent.VK_DOWN);
	
	/**
	 * The left direction
	 */
	public static final ActionMenuInput LEFT = new ActionMenuInput(KeyEvent.VK_LEFT);
	
	/**
	 * The right direction
	 */
	public static final ActionMenuInput RIGHT = new ActionMenuInput(KeyEvent.VK_RIGHT);
	
	/**
	 * The "escape" direction (up a layer)
	 */
	public static final ActionMenuInput ESCAPE = new ActionMenuInput(KeyEvent.VK_ESCAPE);
	
	/**
	 * The "select" direction (down a layer)
	 */
	public static final ActionMenuInput SELECT = new ActionMenuInput(KeyEvent.VK_ENTER);
	
	public ActionMenuInput() {
		// Default needed
	}
	
	protected ActionMenuInput(int direction) {
		this.direction = direction;
	}
	
	@Override
	public void writeData(ByteBuffer buffer) {
		buffer.putInt(direction);
	}
	
	@Override
	public void readData(ByteBuffer buffer) {
		this.direction = buffer.getInt();
	}
	
	@Override
	public void processActionOnServer(int i, Server server) {
		// Actually nothing to do here though
	}
	
	@Override
	public int hashCode() {
		return this.getHashcodeBase() + this.direction;
	}
	
}
