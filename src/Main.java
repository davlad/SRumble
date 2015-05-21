
import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.File;
import java.nio.ByteBuffer;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Main{
	
	static FileChooser chooser = new FileChooser();
	static File clip;
	int i;
	static FileToBytes b;

	public static void main(String[] args) {
		clip = chooser.getFile();
		//b = new FileToBytes(clip);
		SoundClip tmp = new SoundClip(0, clip);
		b = new FileToBytes(tmp.getClip());
		for(int i = 0; i < b.getBytes().length; i++) {
			System.out.println(b.getBytes()[i]);
		}
		
		int times = Double.SIZE / Byte.SIZE;
	    double[] doubles = new double[b.getBytes().length / times];
	    for(int i=0;i<doubles.length;i++){
	        doubles[i] = ByteBuffer.wrap(b.getBytes(), i*times, times).getDouble();
	    }
	    
		System.out.println("length is: " + b.getBytes().length);
		PrimVisFrame f = new PrimVisFrame(doubles);
		//f.paint(f.getGraphics());
		
		//Sketch01.main(args);
	}
}
