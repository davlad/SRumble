import java.applet.*;
import java.awt.*;

public class PrimVis {
	
	int n;
	double[] amp;
	
	public PrimVis(int l, double[] d) {
		n = l;
		amp = d;
	}
	
	public void paint(Graphics g) {
		for(int i  = 0; i < n; i ++) {
			g.setColor(Color.BLACK);
			g.fillRect(70, i * 50 + 10, (int)(amp[i]*10), 40);
		}
	}
}
