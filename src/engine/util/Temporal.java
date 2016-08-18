package engine.util;

import java.util.ArrayList;
import java.util.List;

import engine.Game;
import engine.event.Event;
import engine.event.SubscribeEvent;
import engine.event.game.TickEvent;

/**
 * A collection of Time-Based utilities, such as Timers and Stopwatches. TODO: Fix this entire thing
 * 
 * @author Kevin
 */
public abstract class Temporal {
	
	/**
	 * The number of ticks that this {@code Temporal} instance counts
	 */
	public long ticks;
	
	/**
	 * The time the {@code Temporal} was started
	 */
	protected long startTime;
	
	/**
	 * Whether to pause this {@code Temporal} instance or not
	 */
	protected boolean paused;
	
	/**
	 * The game instance
	 */
	protected Game game;
	
	/**
	 * Ticks this {@code Temporal} object
	 */
	@SubscribeEvent
	public void tick(TickEvent e) {
		if (!paused) {
			this.ticks++;
		}
		if (this.doneListening()) {
			this.game.temporaryEvents.unregister(this);
			this.game.events.post(new TemporalCompletedEvent(this));
		}
	}
	
	/**
	 * Sets the pause status of this {@code Temporal}
	 * 
	 * @param paused
	 */
	public void setPauseStatus(boolean paused) {
		this.paused = paused;
	}
	
	/**
	 * Resets the given {@code Temporal}
	 */
	public void reset() {
		this.startTime = this.game.gameTime;
		this.ticks = 0;
	}
	
	/**
	 * Gets the representation of this {@code Temporal} in terms of seconds.
	 * <p>
	 * The format is HH:MM:SS.MS. Hours and Minutes will only appear if necessary.
	 * 
	 * @return
	 */
	public String getTimeString() {
		return "";
	}
	
	protected Temporal(Game game) {
		this.startTime = game.gameTime;
		this.game = game;
	}
	
	/**
	 * Whether this {@code Temporal} instance is done listening
	 * @return
	 */
	public abstract boolean doneListening();
	
	/**
	 * Creates a new {@code Timer} instance from the given {@code Game} instance. The {@code Timer} is
	 * automatically registered.
	 * 
	 * @param game
	 *            The {@code Game} instance to use
	 * @param ticks
	 *            The number of ticks to time
	 * @return A new {@code Timer} instance
	 */
	public static Timer getTimer(Game game, long ticks) {
		Timer timer = new Timer(game, ticks);
		game.temporaryEvents.register(timer);
		return timer;
	}
	
	/**
	 * Creates a new {@code Stopwatch} instance from the given {@code Game} instance. The {@code Stopwatch} is
	 * automatically registered.
	 * 
	 * @param game
	 *            The {@code Game} instance to use
	 * @return A new {@code Stopwatch} instance
	 */
	public static Stopwatch getStopWatch(Game game) {
		Stopwatch watch = new Stopwatch(game);
		game.temporaryEvents.register(watch);
		return watch;
	}
	
	/**
	 * An implementation of a Timer, which counts down to 0 from a given starting point.
	 * 
	 * @author Kevin
	 */
	public static class Timer extends Temporal {
		
		/**
		 * The starting amount of ticks
		 */
		protected long startingTicks;
		
		/**
		 * Creates a new Timer that will count down from the given number of ticks
		 * 
		 * @param time
		 */
		protected Timer(Game game, long time) {
			super(game);
			this.startingTicks = time;
		}
		
		@Override
		public boolean doneListening() {
			return this.ticks >= this.startingTicks;
		}
		
	}
	
	/**
	 * An implementation of a Stopwatch, which counts up from 0
	 * 
	 * @author Kevin
	 */
	public static class Stopwatch extends Temporal {
		
		private boolean done = false;
		
		/**
		 * Contains the list of {@code long}s that are the difference between the starting time and the time
		 */
		public List<Long> lap = new ArrayList<Long>();
		
		protected Stopwatch(Game game) {
			super(game);
		}
		
		@Override
		public boolean doneListening() {
			return done;
		}
		
		/**
		 * Returns the difference between when the {@code Stopwatch} was started and when this function was
		 * called. Also saves the value.
		 * 
		 * @return What a stopwatch would return by pressing the lap button.
		 */
		public long lap() {
			long lapTime = this.ticks;
			lap.add(this.ticks);
			return lapTime;
		}
		
		@Override
		public void reset() {
			super.reset();
			this.lap.clear();
		}
		
		/**
		 * Stops this {@code StopWatch} instance for good
		 */
		public void stop() {
			this.done = true;
		}
	}
	
	/**
	 * An {@code Event} that fires on the main Game event bus when a {@code Temporal} instance is finished
	 * 
	 * @author Kevin
	 */
	public static class TemporalCompletedEvent extends Event {
		
		public Temporal temporalInstance;
		
		public TemporalCompletedEvent(Temporal temp) {
			this.temporalInstance = temp;
		}
	}
	
}
