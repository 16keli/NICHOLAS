package engine.example;

import engine.event.Event;

public class EventPlayerScore extends Event {
	
	
	public int pnum;
	
	public int score;
	
	public EventPlayerScore() {
	}
	
	public EventPlayerScore(int pnum, int score) {
		this.pnum = pnum;
		this.score = score;
	}
	
}
