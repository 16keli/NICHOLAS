package engine.event.game;

import engine.Game;
import engine.Player;
import engine.network.Connection;

/**
 * Posted whenever a {@code Connection} is established
 * 
 * @author Kevin
 */
public class ConnectionEstablishedEvent extends GameEvent {
	
	public Connection connect;
	
	public Player player;
	
	public ConnectionEstablishedEvent(Game game, Connection c) {
		super(game);
		this.connect = c;
	}
}
