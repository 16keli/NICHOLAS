package engine.event;

/**
 * A simple wrapper interface for {@code Event} listeners
 * 
 * @author Kevin
 */
public interface IEventListener {
	
	/**
	 * Invokes the given method on this listener instance
	 * 
	 * @param event
	 *            The {@code Event} class
	 */
	public void invoke(Event event);
	
	/**
	 * Retrieves the object that is listening for events
	 * 
	 * @return
	 */
	public Object getListeningObject();
}
