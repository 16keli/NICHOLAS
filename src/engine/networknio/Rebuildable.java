package engine.networknio;

import java.io.Serializable;

import engine.Game;

/**
 * An interface for any items that can be rebuilt using the {@code Client}'s instance of {@code Game}
 * <p>
 * The reason this exists is because when sending synchronization data, there is only so much that Object
 * streams and serialization can accomplish. {@code Client}s also have their own instances of {@code EventBus}
 * es, Loggers, etc. which are not serialized, thus this interface exists.
 * <p>
 * For an example of how to properly make use of the {@link #rebuild(Game)} method, see
 * {@link engine.level.Level#rebuild(Game)}
 * 
 * @author Kevin
 */
public interface Rebuildable extends Serializable {
	
	/**
	 * Rebuilds the given {@code Rebuildable} using {@code Client} instances of things, such as
	 * {@code EventBus}es
	 * 
	 * @param g
	 */
	public void rebuild(Game g);
	
}
