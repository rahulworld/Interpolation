import java.awt. * ;
import java.awt.image. * ;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing. * ;
import javax.swing.event. * ;

public class Blender1 extends JFrame {
/**
  * Construct Blender1 GUI.
  */
	BufferedImage bi1 = null;
	BufferedImage bi2 = null;
	public void ImageFusion(){
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		jSliderMixture();
	}
	
	public void jSliderMixture(){
		// Create an image panel capable of displaying entire image. The widths
		// of both images and the heights of both images must be identical.
		final ImagePanel ip = new ImagePanel();
		ip.setPreferredSize(new Dimension(bi1.getWidth(), bi1.getHeight()));
		getContentPane().add(ip, BorderLayout.NORTH);

		// Create a slider for selecting the blending percentage: 100% means
		// show all of first image; 0% means show all of second image.
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
				// Each time the user adjusts the slider, obtain the new
				// blend percentage value and use it to blend the images.
				int value = slider.getValue();
				ip.setImage(blend(bi1, bi2, value / 100.0));
			}
		};
		slider.addChangeListener(cl);
		getContentPane().add(slider, BorderLayout.SOUTH);

		// Display the first image, which corresponds to a 100% blend percentage.
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

	public static void main(String[] args) {
		Blender1 blender = new Blender1();
		try {
			// Load first image from JAR file and draw image into a buffered image.
			//ImageIcon ii1 = new ImageIcon (getClass ().getResource ("/data2/saras51.jpg"));
			blender.bi1 = ImageIO.read(new File("./data/take1.png"));
			//final BufferedImage bi1;
			//bi1 = new BufferedImage (ii1.getIconWidth (), ii1.getIconHeight (),BufferedImage.TYPE_INT_RGB);
			//Graphics2D g2d = bi1.createGraphics ();
			//g2d.drawImage (ii1.getImage (), 0, 0, null);
			//g2d.dispose ();
			// Load second image from JAR file and draw image into a buffered image.
			blender.bi2 = ImageIO.read(new File("./data/take2.png"));
			//   ImageIcon ii2 = new ImageIcon (getClass ().getResource ("/data2/saras52.jpg"));
			//   final BufferedImage bi2;
			//   bi2 = new BufferedImage (ii2.getIconWidth (), ii2.getIconHeight (),
			//	                BufferedImage.TYPE_INT_RGB);
			//   g2d = bi2.createGraphics ();
			//   g2d.drawImage (ii2.getImage (), 0, 0, null);
			//   g2d.dispose ();
		} catch(IOException e) {
			e.printStackTrace();
		}
		//blender.ImageFusion();
		BufferedImage bufferImage = blender.blend(blender.bi1, blender.bi2, 0.50);
		ImageView imageShow=new ImageView();
		imageShow.drawImage(bufferImage);
		try {
			ImageIO.write(bufferImage, "PNG", new File("./data/saras5732.png"));
		} catch(IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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