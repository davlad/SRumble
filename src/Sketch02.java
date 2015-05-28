import java.io.File;

import processing.core.*;
import processing.event.MouseEvent;
import ddf.minim.*;
import ddf.minim.analysis.*;
import processing.opengl.*;

public class Sketch02 extends PApplet {
	//system
	static final long serialVersionUID = 1;
	private Minim minim;
	private AudioPlayer song;
	private File clip;
	//translation
	private PVector t = new PVector(0, 0, 0);
	//3D Camera
	private float cameraDist = 2500*1;
	private float scrollingSpeed = 50;
	private float minAX = radians(150);
	private float maxAX = radians(-150);
	private float minAY = radians(150);
	private float maxAY = radians(-150);
	//LevelVis
	private float maxLevelBoxHeight = 20;
	//LogarithmicFFT
	private FFT fft;
	private int minFreq = 55;
	private int octaveSubs = 6*2;
	private int numOctaves = 7;
	private int histLength = octaveSubs*numOctaves;
	private float histScale() {
		return ((float)height-20.0f)/(128.0f*0.75f);
	}
	private float[] fftAvgs;
	private float[] fftMax;
	private float fftMaxVal = 0;
	private int i = 0;
	
	//color c;
	
	public void setup() {
		size(1440, 800, P3D);
		frameRate(60);
		// always start Minim first!
		minim = new Minim(this);
		
		//song = minim.loadFile("/home/daniel/Music/tristam/Drumstep_-_Tristam_Braken_-_Flight_Monstercat_Release.mp3", 512*2);
		song = minim.loadFile("/home/daniel/Downloads/02 My Songs Know What You Did in the Dark (Light Em Up).mp3", 512*2);
		//song = minim.loadFile("/home/daniel/Music/Dubstep/Ben_Moon_-_New_Beginning.mp3", 512*2);
		//song = minim.loadFile("/home/daniel/Downloads/Braken - To The Stars.mp3", 512);
		song.play();
	}
	
	public void mouseWheel(MouseEvent event) {
		  cameraDist += event.getCount()*scrollingSpeed;
		}
	 
	public void draw() {
		background(0);
		setupCamera();
		lightSetUp();
		hist3d();
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
		return spheric2Rect(cameraDist, 
				map(mouseY, 0, height, minAY, maxAY), 
				map(mouseX, 0, width, minAX, maxAX));
	}

	private void levelVis(float boxHeight, float maxAmp) {
		translate(0, -boxHeight/2, 0);
		material(0, 52, 102, 255);
		box(90, boxHeight, 90);
		translate(0, -maxAmp + boxHeight/2, 0);
		material(255, 0, 0, 255);
		box(90, maxLevelBoxHeight, 90);
		translate(0, maxAmp, 0);
	}
	
	public void material(float r, float g, float b, int s) {
		fill(r, g, b);
		specular(s);
		shininess(9);
	}

	private void lightSetUp() {
		ambientLight(102, 102, 102);
		lightSpecular(255, 255, 255);
		pointLight(255, 255, 255, (float)width*(float).75, (float)height*(float).25, (float)height*(float).25);
	}
	
	private void logHist() {
		fft = new FFT(song.bufferSize(), song.sampleRate());
		fft.logAverages(minFreq, octaveSubs);
		fft.forward(song.mix);
		if (i == 0) {
			fftMax = new float[histLength];
			fftAvgs = new float[histLength];
			i++;
		}
		for(int i = 0; i < histLength; i++) {
			fftAvgs[i] = fft.getAvg(i)*histScale();
			//histArtsy();
			if (fftAvgs[i] > fftMax[i]) {
				fftMax[i] = fftAvgs[i];
			} else {
				fftMax[i]*=(float)0.99;
			}
			if (fftMax[i] > fftMaxVal) {
				fftMaxVal = fftMax[i];
			} else {
				fftMaxVal*=.99;
			}
		}
	}
	
	private void histArtsy() {
		
	}
	
	private void hist3d() {
		
		logHist();
		translate((-95*(histLength))/2, 600, 0);
		for(int i = 0; i < histLength; i++) {
			translate(100, 0, 0);
			levelVis(fftAvgs[i], fftMax[i]);
		}
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
