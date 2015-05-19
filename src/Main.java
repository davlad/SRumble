
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;



public class Main{
	
	static FileChooser chooser = new FileChooser();
	static File clip;
	int i;
	static FileToBytes b;
	
	
	public void printArray() {
		
	}

	public static void main(String[] args) {
		clip = chooser.getFile();
		b = new FileToBytes(clip);
		for(int i = 0; i < b.getBytes().length; i++) {
			System.out.println(b.getBytes()[i]);
		}
		System.out.println("length is: " + b.getBytes().length);
	}
}
