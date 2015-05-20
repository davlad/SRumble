
import java.io.File;

public class Main{
	
	static FileChooser chooser = new FileChooser();
	static File clip;
	int i;
	static FileToBytes b;
	
	
	public void printArray() {
		
	}

	public static void main(String[] args) {
		clip = chooser.getFile();
		//b = new FileToBytes(clip);
		SoundClip tmp = new SoundClip(0, clip);
		b = new FileToBytes(tmp.getClip());
		for(int i = 0; i < b.getBytes().length; i++) {
			System.out.println(b.getBytes()[i]);
		}
		System.out.println("length is: " + b.getBytes().length);
	}
}
