import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.*;

import javax.imageio.ImageIO;
public class correlation {
	
	private static double[][] imageToDoubleArray(BufferedImage image){   
        double[][] array = new double[image.getHeight()][image.getWidth()];
        Raster raster = image.getData();
        for( int y = 0; y < image.getHeight(); y++ ){
            for( int x = 0; x < image.getWidth(); x++ ){
                array[y][x] = raster.getSampleDouble(x, y, 0);                      
            }            
        }           
        return array;
    }
	
	
//	private static double[][] pan_imageToDoubleArray(BufferedImage image){   
//        double[][] array = new double[image.getHeight()/4][image.getWidth()/4];
//        Raster raster = image.getData();
//        int p=0,q=0;
//        for( int y = 0; y < image.getHeight(); y+=4 ){
//        	q=0;
//            for( int x = 0; x < image.getWidth(); x+=4 ){
//                array[p][q] = (raster.getSampleDouble(x, y, 0)
//                		+raster.getSampleDouble(x, y+1, 0)
//                		+raster.getSampleDouble(x, y+2, 0)
//                		+raster.getSampleDouble(x, y+3, 0)
//                		+raster.getSampleDouble(x+1, y, 0)
//                		+raster.getSampleDouble(x+1, y+1, 0)
//                		+raster.getSampleDouble(x+1, y+2, 0)
//                		+raster.getSampleDouble(x+1, y+3, 0)
//                		+raster.getSampleDouble(x+2, y, 0)
//                		+raster.getSampleDouble(x+2, y+1, 0)
//                		+raster.getSampleDouble(x+2, y+2, 0)
//                		+raster.getSampleDouble(x+2, y+3, 0)
//                		+raster.getSampleDouble(x+3, y, 0)
//                		+raster.getSampleDouble(x+3, y+1, 0)
//                		+raster.getSampleDouble(x+3, y+2, 0)
//                		+raster.getSampleDouble(x+3, y+3, 0))/16;
//                		System.out.println("Pan Chromatic Image"+p+" "+q);
//                q++;
//            }
//            p++;
//        }           
//        return array;
//    }
	
	
	public static double coeffecient(double arr1[][],double arr2[][]) {
		int      i, j;
		double   corr=0, mean1=0, mean2=0, mag1=0, mag2=0;
//		System.out.println("Original image dimension  "+arr1.length+"  "+arr1[0].length);
//		System.out.println("Fused image dimension  "+arr2.length+"  "+arr2[0].length);
		
		
      for (i=0; i<arr1.length; i++) {  
        for (j=0; j<arr1[0].length; j++) {
          mean1 += arr1[i][j];
          mean2 += arr2[i][j];
        }
      }
      
      double size=arr1.length*arr1[0].length;
      
      mean1 /= size;
      mean2 /= size;

      for (i=0; i<arr1.length; i++) {
        for (j=0; j<arr1[0].length; j++) {
          arr1[i][j] -= mean1;
          arr2[i][j] -= mean2;
          mag1 += arr1[i][j] * arr1[i][j];
          mag2 += arr2[i][j] * arr2[i][j];
          corr += arr1[i][j] * arr2[i][j];
          }
      }
      corr /= Math.sqrt(mag1*mag2);
    
 return corr;  
 	}
	public static void main(String[] args){

        BufferedImage[] Multi_Img=new BufferedImage[5];
        BufferedImage[] final_Img=new BufferedImage[5];
        double[][] Mul_Img1=new double[1025][1025];
        double[][] Mul_Img2=new double[1025][1025];
        double[][] Mul_Img3=new double[1025][1025];
        double[][] Mul_Img4=new double[1025][1025];
        double[][] Array1=new double[1025][1025];
        double[][] Array2=new double[1025][1025];
        double[][] Array3=new double[1025][1025];
        double[][] Array4=new double[1025][1025];
        
        
        try {
        	Multi_Img[1]=ImageIO.read(new File("./data/new/h1.tif"));
        	Multi_Img[2]=ImageIO.read(new File("./data/new/h2.tif"));
        	Multi_Img[3]=ImageIO.read(new File("./data/new/h3.tif"));
        	Multi_Img[4]=ImageIO.read(new File("./data/new/h4.tif"));
        	
        	final_Img[1]=ImageIO.read(new File("./data/new/waverage1.tif"));
        	final_Img[2]=ImageIO.read(new File("./data/new/waverage2.tif"));
        	final_Img[3]=ImageIO.read(new File("./data/new/waverage3.tif"));
        	final_Img[4]=ImageIO.read(new File("./data/new/waverage4.tif"));
        	
        	//panImg=correlation.pan_imageToDoubleArray(pan[1]);
        	
        	System.out.println("######################################### Weighted Method ###############################################");
        	System.out.println("Complete MultiSenser Band 1 ");
        	Mul_Img1=correlation.imageToDoubleArray(Multi_Img[1]);
        	
        	System.out.println("Complete MultiSenser Band 2 ");
        	Mul_Img2=correlation.imageToDoubleArray(Multi_Img[2]);
        	
        	System.out.println("Complete MultiSenser Band 3 " );
        	Mul_Img3=correlation.imageToDoubleArray(Multi_Img[3]);
        	
        	System.out.println("Complete MultiSenser Band 4 ");
        	Mul_Img4=correlation.imageToDoubleArray(Multi_Img[4]);
        	
        	System.out.println("Complete Fused 1 image  ");
        	Array1=correlation.imageToDoubleArray(final_Img[1]);
        	
        	System.out.println("Complete Fused 2 image  ");
        	Array2=correlation.imageToDoubleArray(final_Img[2]);
        	
        	System.out.println("Complete Fused 3 image  ");
        	Array3=correlation.imageToDoubleArray(final_Img[3]);
        	
        	System.out.println("Complete Fused 4 image  ");
        	Array4=correlation.imageToDoubleArray(final_Img[4]);
        	
        	System.out.println("Correlation iamge 1 "+correlation.coeffecient(Mul_Img1,Array1));
        	
        	System.out.println("Correlation iamge 2 "+correlation.coeffecient(Mul_Img2,Array2));
        	
        	System.out.println("Correlation iamge 3 "+correlation.coeffecient(Mul_Img3,Array3));
        	
        	System.out.println("Correlation iamge 4 "+correlation.coeffecient(Mul_Img4,Array4));
        	
		}catch(IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}
	}
	
 }