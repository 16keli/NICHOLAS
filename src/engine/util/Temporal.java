package engine.util;

import java.util.ArrayList;
import java.util.List;

/**
 * A collection of Time-Based utilities, such as Timers and Stopwatches.
 * TODO: Fix this entire thing
 * @author Kevin
 *
 */
public abstract class Temporal {
	
	public static List<Temporal> active = new ArrayList<Temporal>();
	
	protected long ticks;
	
	/**
	 * The time the {@code Temporal} was started
	 */
	protected long startTime;
	
	/**
	 * Ticks this {@code Temporal} object
	 */
	public abstract void tick();
	
	/**
	 * Starts the {@code Temporal} instance
	 */
	public void start() {
		
	}
	
	/**
	 * Gets the representation of this {@code Temporal} in terms of seconds.
	 * <p>
	 * The format is HH:MM:SS.MS. Hours and Minutes will only appear if necessary.
	 * @return
	 */
	public String getTimeString() {
		return "";
	}
	
	/**
	 * An implementation of a Timer, which counts down to 0 from a given starting point.
	 * @author Kevin
	 *
	 */
	public static class Timer extends Temporal {
		
		protected Timer(long time) {
			this.ticks = time;
		}

		@Override
		public void tick() {
			// TODO Auto-generated method stub
			
		}
		
	}

}
