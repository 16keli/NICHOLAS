package engine.util.turn;

import engine.event.Event;

/**
 * An {@code Event} related to turns
 * @author Kevin
 *
 */
public class TurnEvent extends Event {
	public ITurnUser user;
	public TurnStatus status;
	
	public TurnEvent(ITurnUser user, TurnStatus status) {
		this.user = user;
		this.status = status;
	}
}
