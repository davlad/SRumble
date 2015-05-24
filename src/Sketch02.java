import java.io.File;

import processing.core.*;
import processing.event.MouseEvent;
import ddf.minim.*;
import ddf.minim.analysis.*;

public class Sketch02 extends PApplet {
	static final long serialVersionUID = 1;
	private Minim minim;
	private AudioPlayer song;
	private FFT fft;
	private File clip;
	
	private float cameraDist = 200;
	private float scrollingSpeed = 20;
	private float minAX = radians(90);
	private float maxAX = radians(-90);
	private float minAY = radians(0);
	private float maxAY = radians(-90);
	
	private float maxLevel = 0;
	 
	public void setup() {
		size(600, 600, P3D);
		frameRate(60);
		// always start Minim first!
		minim = new Minim(this);
/*		
		// specify 512 for the length of the sample buffers
		// the default buffer size is 1024
		FileChooser c = new FileChooser();
		clip = c.getFile();

		//song = minim.loadFile(clip.getAbsolutePath(), 1024/2);
		song = minim.loadFile(clip.getAbsolutePath(), 512);
*/
		song = minim.loadFile("/home/daniel/Music/tristam/Drumstep_-_Tristam_Braken_-_Flight_Monstercat_Release.mp3", 512);
		song.play();
		
		// an FFT needs to know how 
		// long the audio buffers it will be analyzing are
		// and also needs to know 
		// the sample rate of the audio it is analyzing
		fft = new FFT(song.bufferSize(), song.sampleRate());
	}
	
	public void mouseWheel(MouseEvent event) {
		  cameraDist += event.getCount()*scrollingSpeed;
		}
	 
	public void draw() {
		background(0);
		fft.forward(song.mix);
		setupCamera();
		level3d();
		
		//simpleHist();
		//simpleWave();
		//amplitude();
	}
	
	private void setupCamera() {
		PVector cameraPosition = cameraPos();
		camera(cameraPosition.x, cameraPosition.y, cameraPosition.z,
				0, 0, 0,
				0, 1, 0);
	}
	
	private PVector spheric2Rect(float radius, float alpha, float beta) {
		return new PVector(
				radius*cos(alpha)*sin(beta),
				radius*sin(alpha),
				radius*cos(alpha)*cos(beta));
	}
	
	private PVector cameraPos() {
		return spheric2Rect(
				cameraDist, 
				map(mouseY, 0, height, minAY, maxAY), 
				map(mouseX, 0, width, minAX, maxAX));
	}
	
	private void level3d() {
		float level = song.mix.level();
		if (level > maxLevel) {
			maxLevel = level;
		} else {
			maxLevel*=.99;
		}
		noStroke();
		lights();
		fill(153);
		float boxHeight = level*(float)levelScale();
		translate(0, -boxHeight/2, 0);
		box(90, boxHeight, 90);
		translate(0, boxHeight/2, 0);
		translate(0, -maxLevel*(float)levelScale(), 0);
		box(90, 5, 90);
		text("maxLevel= " + maxLevel, 100, 0, 0);
	}
	
	private double levelScale() {
		return (double)height * 0.45;
	}
	
	private void simpleHist() {
		stroke(255, 0, 0, 128);
		strokeWeight(5);
		for(int i = 0; i < fft.specSize(); i++) {
			line(i*6, height/2, i*6, height/2 - fft.getBand(i)*20 - 20);
		}
	}
	
	private void amplitude() {
		stroke(0, 255, 0, 128);
		strokeWeight(25);
		line(10, height*7/8, width*song.mix.level()+10, height*7/8);
	}
	
	private void simpleWave() {
		stroke(255);
		strokeWeight(2);
		float[] samples = song.mix.toArray();
		float step = (float)width/(float)(samples.length-1);
		for(int i = 0; i < samples.length - 1; i++) {
			line(i*step, height*3/4 + samples[i]*50, (i+1)*step, height*3/4 + samples[i+1]*50);
		}
	}

	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "Sketch01" });
	}
}





