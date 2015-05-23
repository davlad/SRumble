import java.io.File;
import processing.core.*;
import ddf.minim.*;
import ddf.minim.analysis.*;

public class Sketch02 extends PApplet {
	Minim minim;
	AudioPlayer song;
	FFT fft;
	static final long serialVersionUID = 1;
	File clip;
	 
	public void setup()
	{
	  size(600, 400);
	  frameRate(24);
	  // always start Minim first!
	  minim = new Minim(this);
	 
	  // specify 512 for the length of the sample buffers
	  // the default buffer size is 1024
	  FileChooser c = new FileChooser();
	  clip = c.getFile();
	  song = minim.loadFile(clip.getAbsolutePath(), 512);
	  song.play();
	 
	  // an FFT needs to know how 
	  // long the audio buffers it will be analyzing are
	  // and also needs to know 
	  // the sample rate of the audio it is analyzing
	  fft = new FFT(song.bufferSize(), song.sampleRate());
	}
	 
	public void draw()
	{
	  background(0);
	  // first perform a forward fft on one of song's buffers
	  // I'm using the mix buffer
	  //  but you can use any one you like
	  fft.forward(song.mix);
	 
	  stroke(255, 0, 0, 128);
	  strokeWeight(5);
	  // draw the spectrum as a series of vertical lines
	  // I multiple the value of getBand by 4 
	  // so that we can see the lines better
	  for(int i = 0; i < fft.specSize(); i++) {
		  if (fft.getBand(i)*20 > height/4) {
			  line(i*6, height/2, i*6, height/4);
		  } else {
			  line(i*6, height/2, i*6, height/2 - fft.getBand(i)*20 - 20);
		  }
	    //line(i*6, height/2, i*6, height/2 - fft.getBand(i)*20 - 20);
	    //rect(i*5, height/3, 5, height - fft.getBand(i)*100);
	  }
	 
	  stroke(255);
	  strokeWeight(2);
	  // I draw the waveform by connecting 
	  // neighbor values with a line. I multiply 
	  // each of the values by 50 
	  // because the values in the buffers are normalized
	  // this means that they have values between -1 and 1. 
	  // If we don't scale them up our waveform 
	  // will look more or less like a straight line.
	  float step = width/(song.mix.size()-1);
	  for(int i = 0; i < song.mix.size() - 1; i++)
	  {
	    line(i*step, height*3/4 + song.mix.get(i)*50, (i+1)*step, height*3/4 + song.mix.get(i+1)*50);
	  }
	}

	//public Sketch01(File f) {
		//clip = f;
		//PApplet.main(new String[] {"--present", "Sketch01"});
	//}
	
	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "Sketch01" });
	}
}





