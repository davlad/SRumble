import javax.swing.JFrame;

public class PrimVisFrame extends JFrame{
	
	public PrimVisFrame(double[] d) {
		super("PimitiveVisualization");
		this.add(new PrimVis(d));
		this.pack();
		this.validate();
		this.setResizable(false);
		this.setVisible(true);
	}
}
