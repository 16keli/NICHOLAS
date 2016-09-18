package engine.util.turn;

/**
 * Provides the jumping point for turn-based games
 * 
 * @author Kevin
 */
public interface ITurnProvider {
	
	
	/**
	 * Adds an {@code ITurnUser}
	 * <p>
	 * Note that the effect of adding an {@code ITurnUser} while the {@code ITurnProvider} is already in use
	 * may have varying effects depending on the implementation. Some implementations may not care, others may
	 * fail and throw an exception.
	 * 
	 * @param user
	 */
	public void add(ITurnUser user);
	
	/**
	 * Gets the next {@code ITurnUser} in the queue
	 * <p>
	 * The process by which this is done is implementation-specific
	 * 
	 * @return
	 */
	public ITurnUser getNext();
}
