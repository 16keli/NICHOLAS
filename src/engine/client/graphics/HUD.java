package engine.client.graphics;

import engine.client.Client;
import engine.client.InputHandler;

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
	 * The InputHander
	 */
	protected InputHandler input;

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
	 * @param input
	 *            The {@code InputHandler}
	 */
	public void init(Client client, InputHandler input) {
		this.input = input;
		this.client = client;
	}

}
