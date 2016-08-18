package engine.event;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * The implementation of the {@code IEventListener}
 * <p>
 * Serves simply to invoke methods when called
 * 
 * @author Kevin
 */
public class EventListenerImpl implements IEventListener, Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * The listener instance
	 */
	private Object listener;
	
	/**
	 * The method
	 */
	private Method method;
	
	/**
	 * Creates a new implementation of an {@code IEventListener}
	 * 
	 * @param listener
	 *            The object in question
	 * @param method
	 *            The method in question
	 */
	public EventListenerImpl(Object listener, Method method) {
//		System.out.println("Created EventListenerImpl for listener " + listener + " and method " + method);
		this.listener = listener;
		this.method = method;
	}
	
	@Override
	public void invoke(Event event) {
		try {
			this.method.invoke(this.listener, event);
		} catch (Exception e) {
//			System.err.println("Trying to invoke " + method + " for object " + listener + " and args " + event);
			e.printStackTrace();
		}
	}
	
	@Override
	public Object getListeningObject() {
		return this.listener;
	}
	
}
