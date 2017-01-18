import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Fusion {

    private double[][] imageToDoubleArray(BufferedImage image){   
        double[][] array = new double[image.getHeight()][image.getWidth()];
        Raster raster = image.getData();
        for( int y = 0; y < image.getHeight(); y++ ){
            for( int x = 0; x < image.getWidth(); x++ ){
                array[y][x] = raster.getSampleDouble(x, y, 0);                      
            }            
        }           
        return array;
    }
    
    public BufferedImage doubleArrayToImage(double[][] array){     
        BufferedImage image = new BufferedImage(array[0].length,array.length,BufferedImage.TYPE_INT_RGB);  
        for( int y = 0; y < array.length; y++ ){
            for( int x = 0; x < array[0].length; x++ ){ 
                 int value = (int)array[y][x] << 16|(int)array[y][x] << 8|(int)array[y][x];
                 image.setRGB(x, y, value);                                                   
            }
        }       
        return image;
    }
	public static void main(String[] args){
        BufferedImage image = null;
        double [][] img=null;
        double [][] img_interp=null;
        Mixer blen=new Mixer();
        BufferedImage[] fuse=new BufferedImage[5];
        BufferedImage[] final_Img=new BufferedImage[5];
        BufferedImage[] Hysi_Img=new BufferedImage[5];
        BufferedImage TMC_IMG=null;
        try {
        	TMC_IMG=ImageIO.read(new File("./data/new/p1.tif"));
        	for(int j=1;j<=4;j++){
        		Hysi_Img[j]=ImageIO.read(new File("./data/new/h"+j+".tif"));
        		System.out.println("taking Multispectral image  "+j);
	        }
		} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}
        for(int i=1;i<=4;i++){
        	fuse[i]=blen.blend(Hysi_Img[i], TMC_IMG,0.1);
        }
        ImageView imageView41 = new ImageView();
		ImageView imageView31 = new ImageView();

		imageView41.drawImage(fuse[2]);
		imageView31.drawImage(fuse[3]);

        if (fuse!= null){
            try {
				ImageIO.write(fuse[1], "TIFF", new File("./data/new/waverage1.tif"));
				ImageIO.write(fuse[2], "TIFF", new File("./data/new/waverage2.tif"));
				ImageIO.write(fuse[3], "TIFF", new File("./data/new/waverage3.tif"));
				ImageIO.write(fuse[4], "TIFF", new File("./data/new/waverage4.tif"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    }
}