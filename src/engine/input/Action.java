package engine.input;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.logging.Logger;

import engine.server.Server;

/**
 * Represents an action that can be performed that is input by the player
 * <p>
 * Though the specific inputs for {@code Action}s can differ from player to player, we simply read the fact
 * that they have sent the input and pass along that fact
 * <p>
 * Though this may open the door to easy cheating or TAS or whatnot, it's probably much more expensive and
 * complicated to pass along each of the inputs that a player performs, as well as decode those on the server
 * side and translate those into actions
 * <p>
 * Basically, I'm lazy
 * <p>
 * The basis for {@code Action}s is identical to that of {@code PacketNIO}, and uses much of the same code.
 * But they're different because they serve slightly different purposes. It is definitely possible to use the
 * utilities that work with {@link engine.networknio.packet.PacketNIO PacketNIO} to work the {@code Action}.
 * Save yourself pain
 * <p>
 * {@code Action}s are now highly dependent on having proper {@link #hashCode() hashes}. This is because there
 * are some cases where actions of the same class must be treated differently, such as
 * {@link engine.input.ActionMenuInput ActionMenuInput}, which has 6 different "modes" of action that are all
 * tied to different inputs. There are also other cases, however, such as a mouse input that would have to
 * record the position of the click. There are many millions of different X and Y combinations of positions,
 * but each of those need not be mapped to a different input.
 * 
 * @author Kevin
 */
public abstract class Action implements Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4020765285682828965L;
	
	/**
	 * The {@code Action} instance of {@code Logger}
	 */
	public static final Logger logger = Logger.getLogger("engine.action");
	
	/**
	 * A {@code HashMap} with keys of Packet ID and values of class type
	 */
	public static HashMap<Integer, Class<? extends Action>> idtoclass = new HashMap<Integer, Class<? extends Action>>();
	
	/**
	 * A {@code HashMap} with keys of class type and values of Packet ID
	 */
	public static HashMap<Class<? extends Action>, Integer> classtoid = new HashMap<Class<? extends Action>, Integer>();
	
	/**
	 * The next available action ID
	 */
	private static int availableID = 0;
	
	/**
	 * The space currently reserved for each {@code Action}'s hashes
	 */
	private static int hashSpace;
	
	// Get them registered boss
	static {
		registerAction(ActionMenuInput.class);
	}
	
	/**
	 * Retrieves the ID of the {@code Action}
	 * 
	 * @return The ID that the {@code Action} was registered with
	 */
	public final int getID() {
		return classtoid.get(this.getClass());
	}
	
	/**
	 * Registers a {@code Action} class so that the system knows of its existence.
	 * 
	 * @param c
	 *            The class of {@code Action} to register, for example {@code Action.class}
	 * @return Whether the {@code Action} registration was successful
	 */
	public static boolean registerAction(Class<? extends Action> c) {
		while (!registerAction(c, availableID++)) {
			;
		}
		return true;
	}
	
	/**
	 * Registers a {@code Action} class so that the system knows of its existence.
	 * <p>
	 * Because the method {@code registerAction(Class)} already handles IDs, it's recommended to use that
	 * method instead
	 * 
	 * @param c
	 *            The class of {@code Action} to register, for example {@code Action.class}
	 * @param id
	 *            The integer ID to use
	 * @return Whether the {@code Action} registration was successful
	 */
	protected static boolean registerAction(Class<? extends Action> c, int id) {
		if (idtoclass.keySet().contains(id)) {
			return false;
		} else {
			idtoclass.put(id, c);
			classtoid.put(c, id);
			logger.info("Registered Action " + c.getName() + " with id " + id);
			// Ok... this is how we determine the hashSpace
			// This is the base 2 logarithm of the maximum number of potential hashes per action
			double log2 = (Math.log(Integer.MAX_VALUE / idtoclass.size()) / Math.log(2));
			// Floor the result for a nice number and a bit of breathing room, then raise it again
			hashSpace = (int) Math.pow(2, Math.floor(log2));
			System.out.println("Action hashSpace is " + hashSpace + " (2^" + Math.floor(log2) + ")");
		}
		return true;
	}
	
	/**
	 * Creates a new instance of a {@code Action} from a given ID
	 * 
	 * @param id
	 *            The ID of the action that it was registered with
	 * @return A new instance of a {@code Action}
	 */
	public static Action getNewAction(int id) {
		try {
			Class<? extends Action> c = idtoclass.get(id);
			if (!idtoclass.containsKey(id)) {
				logger.warning("Tried to initialize Action with ID " + id + ", but it doesn't exist!");
			}
			return (c == null ? null : c.newInstance());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Writes any data necessary for this action to the given {@code ByteBuffer}
	 * 
	 * @param buffer
	 *            The {@code ByteBuffer} to write data to
	 */
	public abstract void writeData(ByteBuffer buffer);
	
	/**
	 * Reads any data necessary for this action from the given {@code ByteBuffer}, in the reverse order that
	 * they were written
	 * 
	 * @param buffer
	 *            The {@code ByteBuffer} to read data from
	 */
	public abstract void readData(ByteBuffer buffer);
	
	/**
	 * Processes this {@code Action} on the server side, applying what it should do appropriately
	 * 
	 * @param player
	 *            The connection ID this {@code Action} was received from
	 * @param server
	 *            The server instance
	 */
	public abstract void processActionOnServer(int player, Server server);
	
	/**
	 * Retrieves the "base" of the {@code Action}'s hashcode, or the hash that a vanilla {@code Action}
	 * instance of this class would have
	 * 
	 * @return
	 */
	public int getHashcodeBase() {
		return this.getID() * hashSpace;
	}
	
}
