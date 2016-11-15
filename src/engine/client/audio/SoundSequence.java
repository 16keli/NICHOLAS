package engine.client.audio;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import engine.Game;

/**
 * A sequence of sounds that needs to be played in order
 * 
 * @author Kevin
 */
public class SoundSequence implements Runnable, PlayableSound {
	
	
	/**
	 * The delay between audio clips being played
	 */
	public static final int DELAY = 0;
	
	private int delay;
	
	public List<PlayableSound> sounds = new ArrayList<PlayableSound>();
	
	public Game g;
	
	/**
	 * Creates a new SoundSequence from the given {@code PlayableSound}s with the default delay
	 * 
	 * @param s
	 *            The necessary {@code PlayableSound}s
	 */
	public SoundSequence(PlayableSound... s) {
		this(DELAY, s);
		
	}
	
	/**
	 * Creates a new SoundSequence from the given {@code PlayableSound}s and delay
	 * 
	 * @param delay
	 *            The delay between each sound sequence playing
	 * @param s
	 *            The necessary {@code {PlayableSound}s
	 */
	public SoundSequence(int delay, PlayableSound... s) {
		this.delay = delay;
		this.add(s);
	}
	
	/**
	 * Adds the given {@code PlayableSound}s to the {@code SoundSequence}
	 * 
	 * @param s
	 */
	public void add(PlayableSound... s) {
		this.sounds.addAll(Arrays.asList(s));
	}
	
	@Override
	public void run() {
		for (PlayableSound s : this.sounds) {
			s.play();
			try {
				Thread.sleep(s.getLength() + delay);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public long getLength() {
		int l = -delay;
		for (PlayableSound s : this.sounds) {
			l += s.getLength() + delay;
		}
		return l;
	}
	
	@Override
	public void play() {
		new Thread(this).start();
	}
	
}
