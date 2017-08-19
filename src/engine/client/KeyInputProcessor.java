package engine.client;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * An input processor for keys
 * @author Kevin
 *
 */
public class KeyInputProcessor extends InputProcessor implements KeyListener {
	
	
	/**
	 * Creates the {@code KeyInputHandler} and registers it with the appropriate {@code Client}
	 * 
	 * @param client The Client
	 * @param name The name of the {@code KeyInputProcessor}, either "Key" or "Menu"
	 */
	public KeyInputProcessor(Client client, String name) {
		super(client, name);
		client.addKeyListener(this);
	}
	
	// Events that will be used in Inputs and stuff
	
	@Override
	public void keyPressed(KeyEvent e) {
		if (keyInputs.containsKey(e.getKeyCode())) {
			keyInputs.get(e.getKeyCode()).toggle(true);
		}
	}
	
	@Override
	public void keyReleased(KeyEvent e) {
		if (keyInputs.containsKey(e.getKeyCode())) {
			keyInputs.get(e.getKeyCode()).toggle(false);
		}
	}
	
	// Don't need this poor guy
	
	@Override
	public void keyTyped(KeyEvent e) {
	}
}
