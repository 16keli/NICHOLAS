package engine.event;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A list of {@code IEventListener}s
 * 
 * @author Kevin
 */
public class ListenerList implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * A {@code HashMap} of {@code ListenerListImpl} based on {@code Event} class
	 */
	public ConcurrentHashMap<Class<? extends Event>, ListenerListImpl> lists = new ConcurrentHashMap<Class<? extends Event>, ListenerListImpl>();
	
	/**
	 * Retrieves a {@code IEventListener[]} for the given {@code Event} class
	 * 
	 * @param eventClass
	 *            The class of {@code Event} to get
	 * @return An array of {@code IEventListener}s that subscribe to that {@code Event}
	 */
	public IEventListener[] getListeners(Class<? extends Event> eventClass) {
		try {
			IEventListener[] llist = lists.get(eventClass).getListeners();
			return llist;
		} catch (NullPointerException e) {
			System.err.println("Event " + eventClass.getName()
					+ " was posted, but there are no listeners! This may or may not be a big problem! I don't know because I didn't write your code!");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
		
	}
	
	/**
	 * Registers the given {@code IEventListener} with the given {@code Event} class
	 * 
	 * @param eventClass
	 *            The class of {@code Event}
	 * @param listener
	 *            The {@code IEventListener} to register
	 */
	public void register(Class<? extends Event> eventClass, IEventListener listener) {
		ListenerListImpl list = lists.get(eventClass);
		if (list == null) {
			list = new ListenerListImpl();
			lists.put(eventClass, list);
		}
		list.register(listener);
	}
	
	/**
	 * An implementation of a ListenerList for a specific class of {@code Event}
	 * <p>
	 * Basically just a wrapper around a List
	 * 
	 * @author Kevin
	 */
	private static class ListenerListImpl implements Serializable {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		/**
		 * The list of {@code IEventListener}s
		 */
		private List<IEventListener> listeners = new ArrayList<IEventListener>();
		
		/**
		 * Gets all the {@code IEventListener}s
		 * 
		 * @return Every {@code IEventListener} of this instance
		 */
		public IEventListener[] getListeners() {
			return listeners.toArray(new IEventListener[listeners.size()]);
		}
		
		/**
		 * Registers a new {@code IEventListener}
		 * 
		 * @param listener
		 *            The instance to register
		 */
		public void register(IEventListener listener) {
			listeners.add(listener);
		}
		
	}
	
}
