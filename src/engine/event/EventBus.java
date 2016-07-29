package engine.event;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.logging.Logger;

/**
 * A bus for communicating {@code Event}s between different classes, mainly to avoid seemingly unsafe casting.
 * <p>
 * To use an {@code EventBus}, first, a class must implement a method with the
 * {@link engine.event.SubscribeEvent} annotation like so:
 * 
 * <pre>
 * {@code @SubscribeEvent}
 * public void someEventHandler(EventClass varName) {
 * 	Code to handle varName goes here...
 * }
 * </pre>
 * 
 * Then, one must call {@link #register(Object)} with the argument being the instance that will be registered
 * to this {@code EventBus}. Then, whenever an event of {@code EventClass} is {@link #post(Event) posted} to
 * this {@code EventBus}, then {@code someEventHandler} will be invoked.
 * <p>
 * Objects of the following Classes, and all subclasses of these objects, are automatically registered to the
 * following {@code EventBus} instances.
 * <ul>
 * <li>{@link engine.client.Client Client}: {@link engine.client.Client#CLIENT_BUS}</li>
 * <li>{@link engine.level.Entity Entity}: {@link engine.Game#events}</li>
 * <li>{@link engine.physics.entity.EntityPhysics EntityPhysics}: {@link engine.physics.Physics#PHYSICS_BUS}
 * </li>
 * <li>{@link engine.Game Game}: {@link engine.Game#events};</li>
 * <li>{@link engine.level.Level Level}: {@link engine.Game#events}</li>
 * <li>{@link engine.Player Player}: {@link engine.Game#events}</li>
 * <li>{@link engine.physics.Physics Physics}: {@link engine.physics.Physics#PHYSICS_BUS}</li>
 * <li>{@link engine.server.Server Server}: {@link engine.server.Server#SERVER_BUS}</li>
 * </ul>
 * 
 * @author Kevin
 */
public class EventBus implements Serializable {
	
	public Logger eventLogger;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * The Next {@code EventBus} ID
	 */
	private static int nextID = 0;
	
//	private ConcurrentHashMap<Object, ArrayList<IEventListener>> listeners = new ConcurrentHashMap<Object, ArrayList<IEventListener>>();
	
	/**
	 * The {@code EventBus}' ID number
	 */
	public int id;
	
	/**
	 * The {@code ListenerList} of this {@code EventBus}
	 */
	public ListenerList listeners = new ListenerList();
	
	/**
	 * Creates a new {@code EventBus}
	 */
	public EventBus() {
		this.id = nextID++;
		this.eventLogger = Logger.getLogger("engine.event.bus" + this.id);
	}
	
	/**
	 * Registers the given listener instance
	 * <p>
	 * This method searches for any methods with the {@code @SubscribeEvent} annotation and a <b>single</b>
	 * {@code Event} subclass as a parameter
	 * 
	 * @param listener
	 *            The Instance to create as a listener
	 */
	public void register(Object listener) {
		eventLogger.info("Trying to register " + listener + " for any Event Messages");
		for (Method method : listener.getClass().getMethods()) {
			try {
				if (method.isAnnotationPresent(SubscribeEvent.class)) {
					Class<?>[] parameterTypes = method.getParameterTypes();
					if (parameterTypes.length != 1) {
						throw new IllegalArgumentException("Method " + method
								+ " has @SubscribeEvent annotation, but requires " + parameterTypes.length
								+ " arguments.  Event handler methods must require a single argument.");
					}
					
					@SuppressWarnings ("unchecked")
					Class<? extends Event> eventType = (Class<? extends Event>) parameterTypes[0];
					
					if (!Event.class.isAssignableFrom(eventType)) {
						throw new IllegalArgumentException("Method " + method
								+ " has @SubscribeEvent annotation, but takes a argument that is not a Event "
								+ eventType);
					}
					
					register(eventType, listener, method);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Registers the given listener instance
	 * 
	 * @param eventType
	 *            The {@code Event} class in question
	 * @param listener
	 *            The listener instance
	 * @param method
	 *            The method in question
	 */
	private void register(Class<? extends Event> eventType, Object listener, Method method) {
		try {
			eventLogger.info("Registering listener " + listener + " for Event Class " + eventType
					+ " and method " + method + " for Event Bus with id " + this.id);
			IEventListener impl = new EventListenerImpl(listener, method);
			this.listeners.register(eventType, impl);
			
//			ArrayList<IEventListener> llist = listeners.get(listener);
//			if (llist == null) {
//				llist = new ArrayList<IEventListener>();
//				listeners.put(listener, llist);
//			}
//			llist.add(impl);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Posts the give {@code Event} for all the {@code EventBus} listeners to receive
	 * 
	 * @param event
	 *            The {@code Event} instance
	 */
	public void post(Event event) {
		IEventListener[] listeners = this.listeners.getListeners(event.getClass());
		if (listeners != null) {
			for (IEventListener listener : listeners) {
				listener.invoke(event);
			}
		}
	}
	
}
