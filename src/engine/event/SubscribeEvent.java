package engine.event;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Subscribes to a certain {@code Event}. By subscribing, each time that specific class of {@code Event} is
 * posted to an {@code EventBus} that the listener object is registered to, the method that
 * {@code @SubscribeEvent} is annotated to will be invoked
 * <p>
 * Whenever that certain {@code Event} is posted (<b>NOT</b> any of its superclasses or subclasses), the
 * method this is annotated to will be invoked.
 * 
 * @author Kevin
 */
@Retention (value = RUNTIME)
@Target (value = METHOD)
public @interface SubscribeEvent {

}
