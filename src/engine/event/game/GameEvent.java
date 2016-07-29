package engine.event.game;

import engine.Game;
import engine.event.Event;

/**
 * An {@code Event} that corresponds to a Game event, such as initialization or resetting
 * @author Kevin
 *
 */
public class GameEvent extends Event {
	
	public Game game;
	
	public GameEvent(Game game) {
		this.game = game;
	}

}
