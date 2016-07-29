package engine.example;

import engine.event.Event;

public class EventPlayerScore extends Event {

	public short pnum;

	public int score;

	public EventPlayerScore() {
	}

	public EventPlayerScore(short pnum, int score) {
		this.pnum = pnum;
		this.score = score;
	}

}
