import java.io.File;

import processing.core.*;
import processing.event.MouseEvent;
import ddf.minim.*;
import ddf.minim.analysis.*;
//import processing.opengl.*;

public class Sketch02 extends PApplet {
	//system
	static final long serialVersionUID = 1;
	private Minim minim;
	private AudioPlayer song;
	//3D Camera
	private float cameraDist = 1000;
	private float scrollingSpeed = 50;
	private float minAX = radians(150);
	private float maxAX = radians(-150);
	private float minAY = radians(150);
	private float maxAY = radians(-150);
	//LevelVis
	private float maxLevelBoxHeight = 3;
	private float boxWidth = 17;
	private float boxDepth = 15;
	private float boxSpacing = 2;
	private float histHeightPos = 200;
	//LogarithmicFFT
	private FFT fft;
	private int minFreq = 55/1;
	private int octaveSubs = 6*1;
	private int numOctaves = 7;
	private int histLength() {
		return octaveSubs*numOctaves;
	}
	private float histScale() {
		return ((float)height-20.0f)/(128.0f*5f);
	}
	private float[] fftAvgs;
	private float[] fftMax;
	private float fftMaxVal = 0;
	private int i = 0;
	//key bindings
	boolean addOctSubs = false;
	boolean subOctSubs = false;
	boolean addHistHeight = false;
	boolean subHistHeight = false;
	boolean darker = false;
	boolean lighter = false;
	boolean redder = false;
	boolean bluer = false;
	boolean greener = false;

	//color c;
	
	public void keyPressed() {
		if (key == CODED) {
			if (keyCode == RIGHT) {
				addOctSubs = true;
			}
			if (keyCode == LEFT) {
				subOctSubs = true;
			}
			if (keyCode == UP) {
				addHistHeight = true;
			}
			if (keyCode == DOWN) {
				subHistHeight = true;
			}
		}
		
	}
	
	public void keyReleased() {
		addOctSubs = false;
		subOctSubs = false;
		addHistHeight = false;
		subHistHeight = false;
	}
	
	public void setup() {
		size(1440, 800, P3D);
		frameRate(60);
		// always start Minim first!
		minim = new Minim(this);
		selectInput("Select a file to process:", "fileSelected");
	}
	
	public void fileSelected(File selection) {
		if (selection == null) {
			println("Window was closed or the user hit cancel.");
		} else {
		    println("User selected " + selection.getAbsolutePath());
		    song = minim.loadFile(selection.getAbsolutePath(), 1<<11);
		    song.play();
		}
	}
	
	public void mouseWheel(MouseEvent event) {
		cameraDist += event.getCount()*scrollingSpeed;
	}
	 
	public void draw() {
		if (song == null) { 
			return; 
		}
		background(0);
		setupCamera();
		lightSetUp();
		respondToKeys();
		hist3d();
	}
	
	private void respondToKeys() {
		if (addOctSubs == true && octaveSubs < 13) {
			octaveSubs++;
			i=0;
		}
		if (subOctSubs == true && octaveSubs > 1) {
			octaveSubs--;
			i=0;
		}
		if (addHistHeight == true && histHeightPos > 0) {
			histHeightPos-=10;
		}
		if (subHistHeight == true && histHeightPos < 500) {
			histHeightPos+=10;
		}
		
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

	private void levelVis(int index) {
		translate(0, -fftAvgs[index]/2, 0);
		material(0, 0, 255, 255);
		box(boxWidth, fftAvgs[index], boxDepth);
		translate(0, -fftMax[index] - maxLevelBoxHeight/2 + fftAvgs[index]/2, 0);
		material(	map(fftMax[index], 0f, fftMaxVal, 0f, 255f), 
					0, 
					map(fftMax[index], fftMaxVal, 0, 0, 255), 
					255);
		box(boxWidth, maxLevelBoxHeight, boxDepth);
		translate(0, fftMax[index] + maxLevelBoxHeight/2, 0);
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
			fftMax = new float[histLength()];
			fftAvgs = new float[histLength()];
			i++;
		}
		for(int i = 0; i < histLength(); i++) {
			fftAvgs[i] = fft.getAvg(i)*histScale();
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
	
	private void hist3d() {
		logHist();
		translate(-((histLength()+1)*(boxWidth+boxSpacing))/2, histHeightPos, 0);
		//translate(0, histHeightPos, 0);
		for(int i = 0; i < histLength(); i++) {
			//rotateY(radians(360)/histLength);
			translate(boxWidth+boxSpacing, 0, 0);
			levelVis(i);
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
