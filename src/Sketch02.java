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
	private FFT fftAct;
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
	private float maxLevel = 0;
	private float maxLevelBoxHeight = 20;
	//LogarithmicFFT
	private FFT fftLog;
	private float[] fftMaxVals;
	private int minFreq = 22*4;
	private int octaveSubs = 6*1;
	private float histScale() {
		return ((float)height-20.0f)/(128.0f*0.75f);
	}
	private float[] fftLogAvgs;
	private float[] histMaxLevel;
	private float histSuperMaxLevel = 0;
	private int i = 0;
	
	//color c;
	
	public void setup() {
		size(1440, 800, P3D);
		frameRate(60);
		// always start Minim first!
		minim = new Minim(this);
		
		//song = minim.loadFile("/home/daniel/Music/tristam/Drumstep_-_Tristam_Braken_-_Flight_Monstercat_Release.mp3", 512*2);
		//song = minim.loadFile("/home/daniel/Downloads/02 My Songs Know What You Did in the Dark (Light Em Up).mp3", 512*2);
		//song = minim.loadFile("/home/daniel/Music/Dubstep/Ben_Moon_-_New_Beginning.mp3", 512*2);
		song = minim.loadFile("/data/music/Classical/Britten. Works for Oboe/01 - Six Metamorphoses after Ovid. - I. Pan.mp3", 512);
		song.play();
	}
	
	public void mouseWheel(MouseEvent event) {
		  cameraDist += event.getCount()*scrollingSpeed;
		}
	 
	public void draw() {
		background(0);
		//fftAct.forward(song.mix);
		scene3d();
		//scene2d();
	}
	private void scene3d() {
		setupCamera();
		lightSetUp();
		//level3d();
		hist3d();
	}
	
	private void scene2d() {
		//simpleHist();
		histLine();
		//simpleWave();
		//level2d();
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
	
	private float maxAmplitude() {
		float level = song.mix.level();
		if (level > maxLevel) {
			maxLevel = level;
		} else {
			maxLevel*=.99;
		}
		return level;
	}
	
	private void level3d() {
		noStroke();
		//lights();
		material(0, 52, 102, 255);
		float boxHeight = maxAmplitude()*(float)levelScale();
		levelVis(maxLevel, boxHeight);
		text("maxLevel= " + maxLevel, 100, 0, 0);
		//translate(0, 10, 0);
		//box(600, 20, 100);
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
	
	private double levelScale() {
		return (double)height * 0.45;
	}
	
	private void simpleHist() {
		fftAct = new FFT(song.bufferSize(), song.sampleRate());
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
		fftLog = new FFT(song.bufferSize(), song.sampleRate());
		fftLog.logAverages(minFreq, octaveSubs);
		fftLog.forward(song.mix);
		if (i == 0) {
			histMaxLevel = new float[fftLog.avgSize()];
			fftLogAvgs = new float[fftLog.avgSize()];
			i++;
		}
		for(int i = 0; i < fftLog.avgSize(); i++) {
			//float scale = map(exp(i*histScale), 0, fftLog.avgSize(), (float).02, (float).1); 
			fftLogAvgs[i] = fftLog.getAvg(i)*histScale();//exp(fftLog.getAvg(i)*scale);
			if (fftLogAvgs[i] > histMaxLevel[i]) {
				histMaxLevel[i] = fftLogAvgs[i];
			} else {
				histMaxLevel[i]*=(float)0.99;
			}
			if (histMaxLevel[i] > histSuperMaxLevel) {
				histSuperMaxLevel = histMaxLevel[i];
			} else {
				histSuperMaxLevel*=.99;
			}
		}
	}
	
	private void histLine() {
		logHist();
		stroke(255, 0, 0, 256);
    	strokeWeight(width/(fftLog.avgSize()+10));
    	//System.out.println("Highest level: " + histSuperMaxLevel/histScale());
    	for(int i = 0; i < fftLog.avgSize(); i++) {
    		stroke(255, 0, 0, 256);
    		line(i*width/fftLog.avgSize() +10, height, i*width/fftLog.avgSize() +10, height-10 - fftLogAvgs[i]);
    		stroke(0, 255, 255, 256);
    		line(i*width/fftLog.avgSize() +10, (float)height-10 - histMaxLevel[i], i*width/fftLog.avgSize() +10, (float)height-10 -histMaxLevel[i]-2);
    	}
	}
	
	private void hist3d() {
		logHist();
		translate((-95*(fftLog.avgSize()+2))/2, 600, 0);
		for(int i = 0; i < fftLog.avgSize(); i++) {
			translate(100, 0, 0);
			levelVis(fftLogAvgs[i], histMaxLevel[i]);
		}
	}
	
	private void level2d() {
		stroke(0, 255, 0, 255/2);
		strokeWeight(50);
		line((float)width/2, (float)height*15/16, (float)width/2,
			(float)height*15/16 - song.mix.level()*(float)levelScale()*2);
		
		float maxHeight = maxAmplitude()*(float)levelScale();
		line((float)width/2, (float)height*15/16 - maxLevel*(float)levelScale()*2,
			(float)width/2, (float)height*15/16 - maxLevel*(float)levelScale()*2);
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
