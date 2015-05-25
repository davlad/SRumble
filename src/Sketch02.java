import java.io.File;

import processing.core.*;
import processing.event.MouseEvent;
import ddf.minim.*;
import ddf.minim.analysis.*;
import processing.opengl.*;

public class Sketch02 extends PApplet {
	static final long serialVersionUID = 1;
	private Minim minim;
	private AudioPlayer song;
	private FFT fftAct;
	private File clip;
	
	private float cameraDist = 200;
	private float scrollingSpeed = 20;
	private float minAX = radians(90);
	private float maxAX = radians(-90);
	private float minAY = radians(0);
	private float maxAY = radians(-90);
	
	private float maxLevel = 0;
	
	private FFT fftLog;
	private float[] fftVals;
	
	 
	public void setup() {
		size(600, 600, P3D);
		frameRate(60);
		// always start Minim first!
		minim = new Minim(this);
		
		song = minim.loadFile("/home/daniel/Music/tristam/Drumstep_-_Tristam_Braken_-_Flight_Monstercat_Release.mp3", 512);
		//song = minim.loadFile("/data/music/Classical/Britten. Works for Oboe/01 - Six Metamorphoses after Ovid. - I. Pan.mp3", 512);
		song.play();
		
		//fftAct = new FFT(song.bufferSize(), song.sampleRate());
		//fftLog = new FFT(song.bufferSize(), song.sampleRate());
		//fftLog.logAverages( 22, 12 );
	}
	
	public void mouseWheel(MouseEvent event) {
		  cameraDist += event.getCount()*scrollingSpeed;
		}
	 
	public void draw() {
		background(0);
		//fftAct.forward(song.mix);
		//fftLog.forward(song.mix);
		
		
		setupCamera();
		level3d();
		
		//simpleHist();
		//logHist();
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
		translate(0, maxLevel*(float)levelScale()+10, 0);
		box(600, 20, 100);
	}
	
	private double levelScale() {
		return (double)height * 0.45;
	}
	
	private void simpleHist() {
		stroke(255, 0, 0, 256);
		strokeWeight(7);
		line(10, (float)((float)height*0.95), (fftLog.specSize()-1)*6 +10, (float)((float)height*0.95));
		strokeWeight(5);
		for(int i = 0; i < fftLog.specSize(); i++) {
			line((float)(i*6 +10), (float)((float)height*0.95), (float)(i*6 +10),
					(float)((float)height*0.95 - fftLog.getBand(i)*20 - 20));
		}
	}
	
	private void logHist() {
		float centerFreq = 0;
		float spectrumScale = (float) 0.1;
		
	    for(int i = 0; i < fftLog.avgSize(); i++) {
	    	centerFreq = fftLog.getAverageCenterFrequency(i);
	      // how wide is this average in Hz?
	    	float averageWidth = fftLog.getAverageBandWidth(i);   
	      
	      // we calculate the lowest and highest frequencies
	      // contained in this average using the center frequency
	      // and bandwidth of this average.
	    	float lowFreq  = centerFreq - averageWidth/2;
	    	float highFreq = centerFreq + averageWidth/2;
	      
	      // freqToIndex converts a frequency in Hz to a spectrum band index
	      // that can be passed to getBand. in this case, we simply use the 
	      // index as coordinates for the rectangle we draw to represent
	      // the average.
	    	int xl = (int)fftLog.freqToIndex(lowFreq);
	    	int xr = (int)fftLog.freqToIndex(highFreq);
	      
	      // draw a rectangle for each average, multiply the value by spectrumScale so we can see it better
	    	//rect( xl, height, xr, height - fftLog.getAvg(i)*spectrumScale );
	    	stroke(255, 0, 0, 256);
	    	strokeWeight(5);
	    	line (i*6+10, height, i*6+10, height-10 - exp(fftLog.getAvg(i)*spectrumScale));
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





