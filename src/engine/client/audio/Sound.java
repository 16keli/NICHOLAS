package engine.client.audio;

import java.applet.Applet;
import java.applet.AudioClip;
import java.io.File;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import engine.Engine;

/**
 * Any sounds that might need to be played should be of this class.
 * 
 * @author Kevin
 */
public class Sound implements Runnable, PlayableSound {
	
	public static final Sound boop = new Sound(new File("res/engine/sound/boop.wav"));
	
	private AudioClip clip;
	
	private String path;
	
	public String name;
	
	/**
	 * Creates a new Sound from the given File name
	 * <p>
	 * Searches in the default directory, i.e. {@code Engine.getFilePath()}/sounds/
	 * 
	 * @param name
	 *            The File Name (including extensions)
	 */
	public Sound(String name) {
		this("", name);
	}
	
	/**
	 * Creates a new Sound from the given File name
	 * <p>
	 * Searches in the default directory, i.e. {@code Engine.getFilePath()}/sounds/directory/
	 * 
	 * @param directory
	 *            The Directory Name
	 * @param name
	 *            The File Name (including extensions)
	 */
	public Sound(String directory, String name) {
		this.name = name;// .split(".")[0];
		this.name = this.name.replace(".wav", "");
		if (!directory.equals("")) {
			this.path = Engine.getFilePath() + "/sounds/" + directory + "/" + name;
		} else {
			this.path = Engine.getFilePath() + "/sounds/" + name;
		}
		try {
			clip = Applet.newAudioClip(new File(path).toURI().toURL());
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Creates a new Sound directly from the given {@code File}
	 * 
	 * @param f
	 *            The Sound File
	 */
	public Sound(File f) {
		this.name = f.getName();// .split(".")[0];
		this.name = this.name.replace(".wav", "");
		this.path = f.getPath();
		try {
			clip = Applet.newAudioClip(f.toURI().toURL());
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Plays the sound
	 */
	public void run() {
		clip.play();
	}
	
	public long getLength() {// TODO make this method better
		AudioInputStream stream = null;
		try {
			stream = AudioSystem.getAudioInputStream(new File(path));
			
			AudioFormat format = stream.getFormat();
			
			return (long) (1000 * (new File(path)).length() / format.getSampleRate()
					/ (format.getSampleSizeInBits() / 8.0) / format.getChannels());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	public void play() {
		new Thread(this, "Sound: " + this.name + " Thread").start();
	}
}
