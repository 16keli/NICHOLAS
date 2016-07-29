package engine.event.game;

import engine.Game;

/**
 * Called whenever a {@code Client}, {@code Server}, or {@code Game} is initialized
 * @author Kevin
 *
 */
public class InitializationEvent extends GameEvent {

	public InitializationEvent(Game game) {
		super(game);
	}

}
