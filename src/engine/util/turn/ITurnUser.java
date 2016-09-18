package engine.util.turn;

import engine.event.SubscribeEvent;

/**
 * Something that can make use of turns
 * @author Kevin
 *
 */
public interface ITurnUser {
	/**
	 * Checks whether it is this {@code ITurnUser}'s turn right now
	 * @return
	 */
	public boolean isTurn();
	
	/**
	 * Sets this {@code ITurnUser}'s turn status
	 * @param e The {@code TurnEvent}
	 */
	@SubscribeEvent
	public void setTurnStatus(TurnEvent e);
}
