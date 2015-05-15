import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.Timer;
import javax.swing.JPanel;


public class Main{
	
	FileChooser chooser = new FileChooser();
	Clip clip;
	int i;
	
	public Main(){
		
		AudioInputStream audioIn = null;
		try {
			audioIn = AudioSystem.getAudioInputStream(chooser.getFile());
		} catch (UnsupportedAudioFileException | IOException e) {
			e.printStackTrace();
		}
		clip = null;
		try {
			clip = AudioSystem.getClip();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
		try {
			clip.open(audioIn);
		} catch (LineUnavailableException | IOException e) {
			e.printStackTrace();
		}
		clip.start();
	}

	public static void main(String[] args) {
		try {
			Thread.sleep((long) 10000000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
