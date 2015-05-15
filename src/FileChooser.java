import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;


public class FileChooser {
	
	private File f;
	
	public FileChooser() {
		
		JFileChooser chooser = new JFileChooser();
	    FileNameExtensionFilter filter = new FileNameExtensionFilter(
	        "MP3 and WAV Sound", "mp3", "wav");
	    chooser.setFileFilter(filter);
	    
	    int returnVal = chooser.showOpenDialog(null);
	    if(returnVal == JFileChooser.APPROVE_OPTION) {
	       System.out.println("You chose to open this file: " +
	            chooser.getSelectedFile().getName());
	       f = chooser.getSelectedFile();
	    }
	}
	
	public File getFile() {
		return f;
	}
}
