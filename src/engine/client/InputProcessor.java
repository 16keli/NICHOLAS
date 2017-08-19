package engine.client;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import engine.Engine;
import engine.input.Action;

/**
 * The wrapper around processing inputs
 * 
 * @author Kevin
 */
public abstract class InputProcessor {
	
	
	/**
	 * The Logger instance
	 */
	public static final Logger logger = Logger.getLogger("engine.client.input");
	
	/**
	 * The {@code Client} instance that controls this {@code InputProcessor}
	 */
	public Client client;
	
	/**
	 * The name of this {@code InputProcessor}
	 */
	protected String name;
	
	/**
	 * The file to save to
	 */
	protected File saveFile;
	
	/**
	 * A map of {@code KeyEvent}s to {@code Input}s
	 */
	protected Map<Integer, Input> keyInputs = new HashMap<Integer, Input>();
	
	/**
	 * The reversed map of the previous
	 */
	protected Map<Input, Integer> reverseKeyInputs = new HashMap<Input, Integer>();
	
	/**
	 * A map of key {@code Input}s to {@code Action}s
	 */
	protected Map<Input, Action> keyInputActionMap = new HashMap<Input, Action>();
	
	/**
	 * The reversed map of the previous
	 */
	protected Map<Action, Input> reverseInputActionMap = new HashMap<Action, Input>();
	
	/**
	 * Creates a new {@code InputProcessor} with the given Client instance
	 * 
	 * @param client
	 */
	public InputProcessor(Client client, String name) {
		this.client = client;
		this.name = name;
		this.saveFile = new File(Engine.getFilePath() + "cfg/" + name.toLowerCase() + ".cfg");
		try {
			if (!saveFile.exists()) {
				logger.info("Config file for input processor " + name + " does not exist; creating");
				File directory = saveFile.getParentFile();
				if (!directory.exists()) {
					directory.mkdirs();
				}
				saveFile.createNewFile();
				client.registerDefaultInputs(this);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Initializes this {@code InputProcessor} by reading binds if possible, or replacing with defaults if not
	 */
	public void init() {
		// TODO finish the file stuff so we don't have to call this
		client.registerDefaultInputs(this);
	}
	
	// Queries
	
	/**
	 * Retrieves the input currently associated with the given {@code Action}, or {@code null} if none is
	 * associated
	 * 
	 * @param action
	 *            The action
	 * @return The {@code Input} associated, or {@code null} if none exists
	 */
	public Input getInputFromAction(Action action) {
		return reverseInputActionMap.get(action);
	}
	
	/**
	 * Retrieves the action currently associated with the given {@code Input}, or {@code null} if none is
	 * associated
	 * 
	 * @param in
	 *            The input in question
	 * @return The {@code Action} associated, or {@code null} if none exists
	 */
	public Action getActionFromInput(Input in) {
		return keyInputActionMap.get(in);
	}
	
	/**
	 * Retrieves the action currently associated with the given int {@code keyCode}, or {@code null} if either
	 * the key code is not registered or there is no {@code Action} associated with it
	 * 
	 * @param keyCode
	 *            The {@code KeyEvent} int keycode
	 * @return The action class currently associated with the given int {@code keyCode}, or {@code null} if
	 *         either the key code is not registered or there is no {@code Action} associated with it
	 */
	public Action getActionFromKeycode(int keyCode) {
		if (!isKeyBound(keyCode)) {
			return null;
		}
		return getActionFromInput(keyInputs.get(keyCode));
	}
	
	/**
	 * Checks whether the given keycode is registered
	 * 
	 * @param keyCode
	 *            The {@code KeyEvent} int keycode
	 * @return Whether or not the given keycode is registered
	 */
	public boolean isKeyBound(int keyCode) {
		return keyInputs.containsKey(keyCode);
	}
	
	// Registration specific stuff
	
	/**
	 * Creates a binding between the appropriate {@code Action} class and int keycode
	 * <p>
	 * This method also registers the keycode if necessary
	 * 
	 * @param keyCode
	 *            The {@code KeyEvent} int keycode
	 * @param action
	 *            The action to bind
	 * @return {@code true} if successfully registered without collision, {@code false} if not
	 */
	public boolean bindAction(int keyCode, Action action) {
		registerKey(keyCode);
		return bindAction(keyInputs.get(keyCode), action);
	}
	
	/**
	 * Creates a binding between the appropriate {@code Action} class and {@code Input}
	 * <p>
	 * In case of a conflict (attempting to bind two different {@code Action}s to the same {@code Input}, or
	 * vice-versa), the engine prefers to preserve the status quo, simply rejecting the new attempt to bind
	 * the {@code Action}
	 * 
	 * @param input
	 *            The {@code Input}
	 * @param action
	 *            The action to bind
	 * @return {@code true} if successfully registered without collision, {@code false} if not
	 */
	public boolean bindAction(Input input, Action action) {
		logger.finer("Attempting Binding input " + input.getBind() + " to action " + action.getID());
		if (keyInputActionMap.containsKey(input) || reverseInputActionMap.containsKey(action)) {
			return false;
		}
		
		keyInputActionMap.put(input, action);
		reverseInputActionMap.put(action, input);
		return true;
	}
	
	/**
	 * Changes the binding between the appropriate {@code Action} class and int keycode
	 * <p>
	 * This method also registers the keycode if necessary
	 * 
	 * @param keyCode
	 *            The {@code KeyEvent} int keycode
	 * @param action
	 *            The action to bind
	 * @return {@code true} if successfully rebound without collision, {@code false} if not
	 */
	public boolean rebindAction(int keyCode, Action action) {
		registerKey(keyCode);
		return rebindAction(keyInputs.get(keyCode), action);
	}
	
	/**
	 * Changes the binding between the appropriate {@code Action} class and {@code Input}
	 * <p>
	 * This method is first checks whether the attempted rebinding of both action and input is legal, then
	 * performs the rebinding if it is
	 * 
	 * @param input
	 *            The {@code Input}
	 * @param action
	 *            The action to bind
	 * @return {@code true} if successfully rebound without collision, {@code false} if not
	 */
	public boolean rebindAction(Input input, Action action) {
		// Checks whether the attempted input to bind is already bound
		if (keyInputActionMap.containsKey(input) && keyInputActionMap.get(input) != action) {
			return false;
		}
		// And now whether the attempted action is already bound
		if (reverseInputActionMap.containsKey(action) && reverseInputActionMap.get(action) != input) {
			return false;
		}
		unbindAction(action);
		return bindAction(input, action);
	}
	
	/**
	 * Undoes any binding that currently exists for the given {@code Action} class, if any
	 * 
	 * @param action
	 *            // * The action to unbind
	 */
	public void unbindAction(Action action) {
		Input in = reverseInputActionMap.get(action);
		if (in != null) {
			reverseInputActionMap.remove(action);
			keyInputActionMap.remove(in);
		}
	}
	
	/**
	 * Undoes all bindings that currently exist
	 */
	public void unbindAllActions() {
		reverseInputActionMap.clear();
		keyInputActionMap.clear();
	}
	
	/**
	 * Registers the given key to the {@code KeyInputHandler} to make sure the hander records events relevant
	 * to the key
	 * 
	 * @param keyCode
	 *            The {@code KeyEvent} int keycode
	 * @return {@code true} if successfully registered, {@code false} if not (already registered, etc.)
	 */
	public boolean registerKey(int keyCode) {
		if (keyInputs.containsKey(keyCode)) {
			return false;
		}
		Input in = new Input(keyCode);
		keyInputs.put(keyCode, in);
		reverseKeyInputs.put(in, keyCode);
		return true;
	}
	
	/**
	 * Unregisters the given key to the {@code KeyInputHandler} to stop the handler from recording events
	 * relevant to the key
	 * 
	 * @param keyCode
	 *            The {@code KeyEvent} int keycode
	 * @return {@code true} if successfully unregistered, {@code false} if not (not registered already, etc.)
	 */
	public boolean unregisterKey(int keyCode) {
		if (!keyInputs.containsKey(keyCode)) {
			return false;
		}
		// So we can chain these eh...
		reverseKeyInputs.remove(keyInputs.remove(keyCode));
		return true;
	}
	
	/**
	 * Releases all inputs currently bound
	 */
	public void releaseAll() {
		for (Input i : this.reverseKeyInputs.keySet()) {
			i.release();
		}
	}
	
	/**
	 * Causes all bound inputs to tick once
	 */
	public void tick() {
		for (Input i : this.reverseKeyInputs.keySet()) {
			i.tick();
		}
	}
	
}
