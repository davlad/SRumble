import java.io.*;
import javax.sound.sampled.*;

public class SoundClip {
	
	String src;
	String dest;
	
	public SoundClip(int n, File f) {
		src = f.getAbsolutePath();
		dest = "/tmp/SRumble/s_" + n + ".wav";
		copyAudio(src, dest, n);
	  }

	public File getClip() {
		File tmp = new File(dest);
		return tmp;
	}
	
	private void copyAudio(String source, String destination, int start) {
		AudioInputStream inputStream = null;
		AudioInputStream shortenedStream = null;
		try {
			File file = new File(source);
			AudioFileFormat fileFormat = AudioSystem.getAudioFileFormat(file);
			AudioFormat format = fileFormat.getFormat();
			inputStream = AudioSystem.getAudioInputStream(file);
			int bytesPerSecond = format.getFrameSize() * (int)format.getFrameRate();
			inputStream.skip(start * bytesPerSecond / 24);
			long framesOfAudioToCopy = (int)format.getFrameRate() / 24;
			shortenedStream = new AudioInputStream(inputStream, format, framesOfAudioToCopy);
			File destinationFile = new File(destination);
			AudioSystem.write(shortenedStream, fileFormat.getType(), destinationFile);
		}
		catch (Exception e) {System.out.println(e); }
		finally {
			if (inputStream != null) try {
				inputStream.close(); 
			}
			catch (Exception e) { System.out.println(e); }
			if (shortenedStream != null) try {
				shortenedStream.close(); 
			} catch (Exception e) { System.out.println(e); }
		}
	}
	
}
