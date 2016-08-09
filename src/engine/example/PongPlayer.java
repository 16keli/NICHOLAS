package engine.example;

import engine.Game;
import engine.Player;
import engine.event.SubscribeEvent;

public class PongPlayer extends Player {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public PongPlayer(Game g, short number, String name) {
		super(g, number, name);
		// TODO Auto-generated constructor stub
	}
	
	public PongPlayer(Game g, short number) {
		super(g, number);
		// TODO Auto-generated constructor stub
	}
	
	public int score;
	
	@SubscribeEvent
	public void playerScore(EventPlayerScore e) {
		if (e.pnum == this.number) {
			this.score = e.score;
		}
	}
	
}
