import java.awt. * ;
import java.awt.image. * ;
import javax.swing. * ;
import javax.swing.event. * ;

public class Mixer extends JFrame {
	BufferedImage bi1 = null;
	BufferedImage bi2 = null;
	public void ImageFusion(){
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		jSliderMixture();
	}
	public void jSliderMixture(){
		final ImagePanel ip = new ImagePanel();
		ip.setPreferredSize(new Dimension(bi1.getWidth(), bi1.getHeight()));
		getContentPane().add(ip, BorderLayout.NORTH);
		final JSlider slider = new JSlider(JSlider.HORIZONTAL, 0, 100, 100);
		slider.setMinorTickSpacing(5);
		slider.setMajorTickSpacing(10);
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
		slider.setLabelTable(slider.createStandardLabels(10));
		slider.setInverted(true);
		ChangeListener cl;
		cl = new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				int value = slider.getValue();
				ip.setImage(blend(bi1, bi2, value / 100.0));
			}
		};
		slider.addChangeListener(cl);
		getContentPane().add(slider, BorderLayout.SOUTH);
		ip.setImage(bi1);
		pack();
		setVisible(true);
	}
	public BufferedImage blend(BufferedImage bi1, BufferedImage bi2, double weight) {
		if (bi1 == null) throw new NullPointerException("bi1 is null");

		if (bi2 == null) throw new NullPointerException("bi2 is null");

		int width = bi1.getWidth();
		if (width != bi2.getWidth()) throw new IllegalArgumentException("widths not equal");

		int height = bi1.getHeight();
		if (height != bi2.getHeight()) throw new IllegalArgumentException("heights not equal");

		BufferedImage bi3 = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		int[] rgbim1 = new int[width];
		int[] rgbim2 = new int[width];
		int[] rgbim3 = new int[width];

		for (int row = 0; row < height; row++) {
			bi1.getRGB(0, row, width, 1, rgbim1, 0, width);
			bi2.getRGB(0, row, width, 1, rgbim2, 0, width);

			for (int col = 0; col < width; col++) {
				int rgb1 = rgbim1[col];
				int r1 = (rgb1 >> 16) & 255;
				int g1 = (rgb1 >> 8) & 255;
				int b1 = rgb1 & 255;

				int rgb2 = rgbim2[col];
				int r2 = (rgb2 >> 16) & 255;
				int g2 = (rgb2 >> 8) & 255;
				int b2 = rgb2 & 255;

				int r3 = (int)(r1 * weight + r2 * (1.0 - weight));
				int g3 = (int)(g1 * weight + g2 * (1.0 - weight));
				int b3 = (int)(b1 * weight + b2 * (1.0 - weight));
				rgbim3[col] = (r3 << 16) | (g3 << 8) | b3;
			}

			bi3.setRGB(0, row, width, 1, rgbim3, 0, width);
		}

		return bi3;
	}
	public BufferedImage blendHysi(BufferedImage[] bi, double weight) {
//		if (bi == null) throw new NullPointerException("bi1 is null");

//		if (bi2 == null) throw new NullPointerException("bi2 is null");

		int width = bi[1].getWidth();
//		if (width != bi2.getWidth()) throw new IllegalArgumentException("widths not equal");

		int height = bi[1].getHeight();
//		if (height != bi2.getHeight()) throw new IllegalArgumentException("heights not equal");

		BufferedImage bi3 = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		int rgbim[][] = new int[65][width];
		int rgbim65[] = new int[width];
		for (int row = 0; row < height; row++) {
			for(int l=1;l<=64;l++){
				bi[l].getRGB(0, row, width, 1, rgbim[l], 0, width);				
			}
			int[] r=new int[65];
			int[] g=new int[65];
			int[] b=new int[65];
			for (int col = 0; col < width; col++) {
				int rgb[]=new int[65];
				for(int k=1;k<=64;k++){					
					rgb[k] =rgbim[k][col];
					r[k] = (rgb[k] >> 16) & 255;
					g[k] = (rgb[k] >> 8) & 255;
					b[k] = rgb[k] & 255;
				}
				int r65=0,g65=0,b65=0;
				for(int m=1;m<=64;m++){					
					r65 += (int)(r[m] * weight);
					g65 += (int)(g[m] * weight);
					b65 += (int)(b[m] * weight);
				}
				rgbim65[col] = (r65 << 16) | (g65 << 8) | b65;
			}
			bi3.setRGB(0, row, width, 1, rgbim65, 0, width);
		}
		return bi3;
	}
}
class ImagePanel extends JPanel {
	private BufferedImage bi;
	void setImage(BufferedImage bi) {
		this.bi = bi;
		repaint();
	}

	public void paintComponent(Graphics g) {
		if (bi != null) {
			Graphics2D g2d = (Graphics2D) g;
			g2d.drawImage(bi, null, 0, 0);
		}
	}
}