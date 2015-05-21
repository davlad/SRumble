import java.applet.*;
import java.awt.*;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class PrimVis extends JPanel {
	
	int n;
	double[] amp;
	JFrame f = new JFrame();
	JPanel p = new JPanel();
	
	public PrimVis(double[] d) {
		n = d.length;
		amp = d;
		this.setPreferredSize(new Dimension(800, 800));
	}
	
	public void paint(Graphics g) {
		super.paintComponents(g);
		for(int i  = 0; i < n; i ++) {
			g.setColor(Color.BLACK);
			g.fillRect(i, 100, 2, (int)(amp[i])/10);
		}
	}
}
