package engine.client.audio;

/**
 * A simple sound interface for playing sounds
 * @author Kevin
 *
 */
public interface PlayableSound {
	/**
	 * Plays the sound
	 */
	public void play();

	/**
	 * Gets the length of the sound file in milliseconds
	 * @return
	 */
	public long getLength();
}
