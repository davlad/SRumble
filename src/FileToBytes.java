import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javazoom.jl.converter.*;


public class FileToBytes {

	private File f;
	private byte[] b;
	
	public byte[] getBytes() {
		return b;
	}
	
	public FileToBytes(File f) {
		this.f = f;
		Converter c = new Converter();
		b = WAVToBytes();
	}
	
	private byte[] WAVToBytes() {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		BufferedInputStream in = null;
		try {
			in = new BufferedInputStream(new FileInputStream(f));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		int read;
		byte[] buff = new byte[1024];
		try {
			while ((read = in.read(buff)) > 0)
			{
			    out.write(buff, 0, read);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			out.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		byte[] audioBytes = out.toByteArray();
		return audioBytes;
	}
}
