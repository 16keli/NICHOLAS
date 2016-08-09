package engine.event.game;

import engine.Game;
import engine.Player;
import engine.networknio.ConnectionNIO;

/**
 * Posted whenever a {@code Connection} is established
 * 
 * @author Kevin
 */
public class ConnectionEstablishedEvent extends GameEvent {
	
	public ConnectionNIO connect;
	
	public Player player;
	
	public ConnectionEstablishedEvent(Game game, ConnectionNIO c, Player p) {
		super(game);
		this.connect = c;
		this.player = p;
	}
}
