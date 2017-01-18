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
		if (width != bi2.getWidth()/4) throw new IllegalArgumentException("widths not equal");

		int height = bi1.getHeight();
		if (height != bi2.getHeight()/4) throw new IllegalArgumentException("heights not equal");

		BufferedImage bi3 = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		BufferedImage bi4 = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		int[] rgbim1 = new int[width];
		int[] rgbim2 = new int[width];
		int[] rgbim21 = new int[bi2.getWidth()];
		int[] rgbim22 = new int[bi2.getWidth()];
		int[] rgbim23 = new int[bi2.getWidth()];
		int[] rgbim24 = new int[bi2.getWidth()];
		int[] rgbim3 = new int[width];
		int[] rgbim4 = new int[width];
		int p=0,q=0;
		for (int row = 0; row < bi2.getHeight(); row+=4) {
			bi2.getRGB(0, row, bi2.getWidth(), 1, rgbim21, 0, bi2.getWidth());
			bi2.getRGB(0, row+1, bi2.getWidth(), 1, rgbim22, 0, bi2.getWidth());
			bi2.getRGB(0, row+2, bi2.getWidth(), 1, rgbim23, 0, bi2.getWidth());
			bi2.getRGB(0, row+3, bi2.getWidth(), 1, rgbim24, 0, bi2.getWidth());
			q=0;
			for (int col = 0; col < bi2.getWidth(); col+=4) {

				int rgb2 = (rgbim21[col]+rgbim21[col+1]+rgbim21[col+2]+rgbim21[col+3]+rgbim22[col]+rgbim22[col+1]+rgbim22[col+2]+rgbim22[col+3]+
						rgbim23[col]+rgbim23[col+1]+rgbim23[col+2]+rgbim23[col+3]+rgbim24[col]+rgbim24[col+1]+rgbim24[col+2]+rgbim24[col+3])/16;
				int r2 = (rgb2 >> 16) & 255;
				int g2 = (rgb2 >> 8) & 255;
				int b2 = rgb2 & 255;
				rgbim3[q] = (r2 << 16) | (g2 << 8) | b2;
				System.out.println("Pan Chromatic "+p+" "+q);
				q++;
			}
			bi3.setRGB(0, p, width, 1, rgbim3, 0, width);
			p++;
		}

		for (int row = 0; row < height; row++) {
			bi1.getRGB(0, row, width, 1, rgbim1, 0, width);
			bi3.getRGB(0, row, width, 1, rgbim2, 0, width);

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
				rgbim4[col] = (r3 << 16) | (g3 << 8) | b3;
				System.out.println("Fuse Image "+row+" "+col);
			}

			bi4.setRGB(0, row, width, 1, rgbim4, 0, width);
		}

		return bi4;
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