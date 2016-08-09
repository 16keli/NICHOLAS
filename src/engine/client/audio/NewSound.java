package engine.client.audio;

import java.io.File;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;

public class NewSound implements PlayableSound {
	
	private Clip clip;
	
	public NewSound(File f) {
		try {
			AudioInputStream strm = AudioSystem.getAudioInputStream(f);
			DataLine.Info info = new DataLine.Info(Clip.class, strm.getFormat());
			this.clip = (Clip) AudioSystem.getLine(info);
			this.clip.open(strm);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void play() {
		this.clip.start();
	}
	
	@Override
	public long getLength() {
		return this.clip.getMicrosecondLength() / 1000;
	}
	
}
