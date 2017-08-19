package engine.client.graphics;

import engine.client.Client;
import engine.client.KeyInputProcessor;
import engine.client.MouseInputProcessor;

/**
 * An implementation of a Heads-up Display. Any information that might need to be rendered direct to the
 * {@code Client} without rendering it as part of a {@code Level} should probably go in here
 * <p>
 * {@code HUD} is similar to {@code Menu}, the only difference being that a {@code Client}'s {@code HUD} will
 * render during gameplay, i.e. {@code menu == null}
 * 
 * @author Kevin
 */
public abstract class HUD {
	
	
	/**
	 * The {@code Client} this hud is on
	 */
	protected Client client;
	
	/**
	 * The KeyInputProcessor
	 */
	protected KeyInputProcessor keyInput;
	
	/**
	 * The Menu input processor
	 */
	protected KeyInputProcessor menuInput;
	
	/**
	 * The MouseInputProcessor
	 */
	protected MouseInputProcessor mouseInput;
	
	/**
	 * Renders the HUD
	 * 
	 * @param screen
	 *            The {@code Screen}
	 */
	public abstract void render(Screen s);
	
	/**
	 * Initializes the {@code HUD} by providing it with the appropriate {@code Client} and
	 * {@code InputHandler}
	 * 
	 * @param client
	 *            The {@code Client}
	 * @param keyInput
	 *            The {@code KeyInputProcessor}
	 * @param menuInput
	 *            The Menu input processor
	 * @param mouseInput
	 *            The {@code MouseInputProcessor}
	 */
	public void init(Client client, KeyInputProcessor keyInput, KeyInputProcessor menuInput,
			MouseInputProcessor mouseInput) {
		this.keyInput = keyInput;
		this.mouseInput = mouseInput;
		this.menuInput = menuInput;
		this.client = client;
	}
	
}
