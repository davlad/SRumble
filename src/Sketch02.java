import java.io.File;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileFilter;

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
	private AudioMetaData meta;
	//3D Camera
	private float cameraDist = 1000;
	private float scrollingSpeed = 50;
	private float minAX = radians(150);
	private float maxAX = radians(-150);
	private float minAY = radians(150);
	private float maxAY = radians(-150);
	//LevelVis
	private float maxLevelBoxHeight = 5;
	private float boxWidth = 20;
	private float boxDepth = 20;
	private float boxSpacing = 5;
	private float histHeightPos = 200;
	//LogarithmicFFT
	private FFT fft;
	private int minFreq = 55/1;
	private int octaveSubs = 6*1;
	private int numOctaves = 7;
	private int histLength() {
		return octaveSubs*numOctaves;
	}
	private float totalLength() {
		return histLength()*(boxWidth+boxSpacing);
	}
	private float histScale() {
		return ((float)height-20.0f)/(128.0f*5f);
	}
	private float[] fftAvgs;
	private float[] fftMax;
	private float fftMaxVal = 0;
	private boolean createList = true;
	//key bindings
	private boolean addOctSubs = false;
	private boolean subOctSubs = false;
	private boolean addHistHeight = false;
	private boolean subHistHeight = false;
	private boolean darker = false;
	private boolean lighter = false;
	private boolean redder = false;
	private boolean greener = false;
	private boolean bluer = false;
	private int maxSat = 200;
	private int cSpeed = 5;
	private int bgR = 0;
	private int bgG = 0;
	private int bgB = 0;
	private float scrubScale = 10;
	//particles
	private float originDist = 1000;
	private PVector origin1 = new PVector(-originDist, -originDist, 0);
	private PVector origin2 = new PVector(originDist, -originDist, 0);
	private PVector origin3 = new PVector(-originDist, originDist, 0);
	private PVector origin4 = new PVector(originDist, originDist, 0);
	private Particle[] p = new Particle[600];
	private boolean createPs = true;
	private float maxSpeed = 0.1f;
	private float minSize = 1;
	private float maxSize = 10;
	
	public void keyPressed() {
		if(key == CODED) {
			if (keyCode == RIGHT) {addOctSubs = true;}
			if (keyCode == LEFT) {subOctSubs = true;}
			if (keyCode == UP) {addHistHeight = true;}
			if (keyCode == DOWN) {subHistHeight = true;}
		}
		if(key == 'd') {darker = true;}
		if(key == 'l') {lighter = true;}
		if(key == 'r') {redder = true;}
		if(key == 'g') {greener = true;}
		if(key == 'b') {bluer = true;}
	}
	
	public void keyReleased() {
		addOctSubs = false;
		subOctSubs = false;
		addHistHeight = false;
		subHistHeight = false;
		darker = false;
		lighter = false;
		redder = false;
		greener = false;
		bluer = false;
	}
	
	public void setup() {
		size(1440, 800, P3D);
		frameRate(60);
		// always start Minim first!
		minim = new Minim(this);
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		selectInput("Select a file to process:", "fileSelected");
	}
	
	public void fileSelected(File selection) {
		if (selection == null) {
			println("Window was closed or the user hit cancel.");
		} else {
		    println("User selected " + selection.getAbsolutePath());
		    song = minim.loadFile(selection.getAbsolutePath(), 1<<11);
		    meta = song.getMetaData();
		    song.play();
		}
	}
	
	private void particles() {
		if(createPs) {makeParticles();}
		pushMatrix();
		noStroke();
		fill(255, 200);
		drawParticles();
		translate(0, 0, 50);
		popMatrix();
	}
	
	private void rotToCamera() {
		PVector c = cameraPos();
		float x = asin(c.y/cameraDist);
		float y = acos(c.x/(cameraDist*cos(x)));
		rotateY(y);
		rotateX(x);
	}
	
	private void makeParticles() {
		for(int i = 0; i < p.length; i++) {
			if (i%4 == 0)
				p[i] = new Particle(origin1.x, origin1.y, origin1.z, 0, 0, 0, random(minSize, maxSize));
			if (i%4 == 1)
				p[i] = new Particle(origin2.x, origin2.y, origin2.z, 0, 0, 0, random(minSize, maxSize));
			if (i%4 == 2)
				p[i] = new Particle(origin3.x, origin3.y, origin3.z, 0, 0, 0, random(minSize, maxSize));
			if (i%4 == 3)
				p[i] = new Particle(origin4.x, origin4.y, origin4.z, 0, 0, 0, random(minSize, maxSize));
		}
		createPs = false;
	}
	
	private void drawParticles() {
		for(int i = 0; i < p.length; i++) {
			pushMatrix();
			translate(p[i].x(), p[i].y(), p[i].z());
			rotToCamera();
			ellipse(0, 0, p[i].r(), p[i].r());
			popMatrix();
			p[i].updateVel(random(-1, 1) * maxSpeed, random(-1, 1) * maxSpeed, random(-1, 1) * maxSpeed);
			p[i].update();
		}
	}
	
	public void mouseWheel(MouseEvent event) {
		cameraDist += event.getCount()*scrollingSpeed;
	}
	 
	public void draw() {
		if (song == null) {return;}
		respondToKeys();
		background(color(bgR, bgG, bgB));
		setupCamera();
		lightSetUp();
		pushMatrix();
		hist3d();
		specs();
		popMatrix();
		particles();
	}
	
	private void specs() {
		textSize(24);
		material(0, 255, 0, 255);
		translate(-((histLength())*(boxWidth+boxSpacing)), 30, 0);
		text(	"Octave Subdivisions: " + octaveSubs +
				"; Background RGB: " +bgR +", "+bgG+", "+bgB + ";",
				0, 0, 0);
		scrubDisp();
		text(meta.title(), -totalLength(), 60, 0);
		translate(-totalLength(), 100, 0);
		simpleWave();
		noStroke();
	}
	
	private void scrubDisp() {
		float songPos = song.position()*totalLength()/song.length();
		translate((songPos)/2, 30, 0);
		box(songPos, scrubScale, scrubScale);
		int minutes = song.position()/60000;
		int seconds = song.position()/1000 - minutes*60;
		if (seconds > 9) {
			text(minutes+":"+seconds, songPos/2, 30, 0);
		} else {
			text(minutes+":0"+seconds, songPos/2, 30, 0);
		}
		translate(totalLength() - songPos/2 +10, 0, 0);
		box(scrubScale);
		int tMins = song.length()/60000;
		int tSecs = song.length()/1000 - tMins*60;
		if (tSecs > 9) {
			text(tMins+":"+tSecs, 20, 0, 0);
		} else {
			text(tMins+":0"+tSecs, 20, 0, 0);
		}
	}
	
	private void respondToKeys() {
		if(addOctSubs == true && octaveSubs < 12) {
			octaveSubs++;
			createList = true;
		}
		if(subOctSubs == true && octaveSubs > 1) {
			octaveSubs--;
			createList = true;
		}
		if(addHistHeight == true && histHeightPos > 0) {
			histHeightPos-=10;
		}
		if(subHistHeight == true && histHeightPos < 250) {
			histHeightPos+=10;
		}
		if(darker == true) {decR(); decG(); decB();}
		if(lighter == true) {incR(); incG(); incB();}
		if(redder == true) {incR(); decG(); decB();}
		if(greener == true) {decR(); incG(); decB();}
		if(bluer == true) {decR(); decG(); incB();}	
	}
	
	private void incR() {if(bgR < maxSat){bgR+=cSpeed;}}
	private void decR() {if(bgR > 0){bgR-=cSpeed;}}
	private void incG() {if(bgG < maxSat){bgG+=cSpeed;}}
	private void decG() {if(bgG > 0){bgG-=cSpeed;}}
	private void incB() {if(bgB < maxSat){bgB+=cSpeed;}}
	private void decB() {if(bgB > 0){bgB-=cSpeed;}}

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
		if (createList) {
			fftMax = new float[histLength()];
			fftAvgs = new float[histLength()];
			createList = false;
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
		translate(-totalLength()/2, histHeightPos, 0);
		for(int i = 0; i < histLength(); i++) {
			translate(boxWidth+boxSpacing, 0, 0);
			levelVis(i);
		}
	}
	
	private void simpleWave() {
		stroke(255);
		strokeWeight(2);
		float[] samples = song.mix.toArray();
		float step = (float)totalLength()/(float)(samples.length-1);
		for(int i = 0; i < samples.length - 1; i++) {
			line(i*step, samples[i]*50, (i+1)*step, samples[i+1]*50);
		}
	}

	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "Sketch01" });
	}
}
