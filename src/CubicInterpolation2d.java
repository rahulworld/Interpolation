import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class CubicInterpolation2d {

    BSplines bS;    
    
    public CubicInterpolation2d(){
        bS = new BSplines();
    }
    
    private double[][] mirrorW2d( double [][] s){

        double [][] s_mirror = new double[s.length+3][s[0].length+3];
        for(int i=0; i<s.length; i++){
            for(int j=0; j<s[0].length; j++){
                s_mirror[i+1][j+1] = s[i][j];
            }
        }
        
        /*########## mirror rows  1 and N-2 ################*/
        for(int j=0; j<s[0].length; j++){
            s_mirror[0][j+1] = s[1][j];
            s_mirror[s_mirror.length-2][j+1] = s[s.length-2][j];
        }
        
        /*########## mirror columns 1 and  N-2 ##############*/ 
        for(int i=0; i<s.length; i++){
            s_mirror[i+1][0] = s[i][1];
            s_mirror[i+1][s_mirror[0].length-2] = s[i][s[0].length-2];
        }   
        
        s_mirror[0][0] = s[1][1];
        s_mirror[0][s_mirror[0].length-2] = s[1][s[0].length-2];            
        s_mirror[s_mirror.length-2][0] = s[s.length-2][1];
        s_mirror[s_mirror.length-2 ][s_mirror[0].length-2] = 
                s[s.length-2][s[0].length-2];
                                        
        return s_mirror;
    }
    
    public double[][] cubicCoeff2d(double[][] s){
        DirectBsplFilter2d directFilter2d;
        directFilter2d = new DirectBsplFilter2d(s.length, s[0].length);
        double[][] coeffs = directFilter2d.filter(s);
        double[][] coeffs_mirror = mirrorW2d(coeffs);
        return coeffs_mirror;
    }
    

    public double cubicInterp2d(double[][] coeffs_mirror, 
                                double row, 
                                double col){    
        int k = (int)Math.floor(row);   
        int l = (int)Math.floor(col);       
        double interp_value =   
        coeffs_mirror[k+0][l+0]*bS.bspline(3,row-k+1)*bS.bspline(3,col-l+1)+ 
        coeffs_mirror[k+1][l+0]*bS.bspline(3,row-k+0)*bS.bspline(3,col-l+1)+
        coeffs_mirror[k+2][l+0]*bS.bspline(3,row-k-1)*bS.bspline(3,col-l+1)+
        coeffs_mirror[k+3][l+0]*bS.bspline(3,row-k-2)*bS.bspline(3,col-l+1)+
                                                                    
        coeffs_mirror[k+0][l+1]*bS.bspline(3,row-k+1)*bS.bspline(3,col-l+0)+ 
        coeffs_mirror[k+1][l+1]*bS.bspline(3,row-k+0)*bS.bspline(3,col-l+0)+
        coeffs_mirror[k+2][l+1]*bS.bspline(3,row-k-1)*bS.bspline(3,col-l+0)+
        coeffs_mirror[k+3][l+1]*bS.bspline(3,row-k-2)*bS.bspline(3,col-l+0)+
                                                                                                
        coeffs_mirror[k+0][l+2]*bS.bspline(3,row-k+1)*bS.bspline(3,col-l-1)+ 
        coeffs_mirror[k+1][l+2]*bS.bspline(3,row-k+0)*bS.bspline(3,col-l-1)+
        coeffs_mirror[k+2][l+2]*bS.bspline(3,row-k-1)*bS.bspline(3,col-l-1)+
        coeffs_mirror[k+3][l+2]*bS.bspline(3,row-k-2)*bS.bspline(3,col-l-1)+
                                                
        coeffs_mirror[k+0][l+3]*bS.bspline(3,row-k+1)*bS.bspline(3,col-l-2)+ 
        coeffs_mirror[k+1][l+3]*bS.bspline(3,row-k+0)*bS.bspline(3,col-l-2)+
        coeffs_mirror[k+2][l+3]*bS.bspline(3,row-k-1)*bS.bspline(3,col-l-2)+
        coeffs_mirror[k+3][l+3]*bS.bspline(3,row-k-2)*bS.bspline(3,col-l-2);                                        
        return interp_value;
    }
    
    
    public double[][] interpolate(double[][] s, int rate){
        double [][] coeffs_mirror = cubicCoeff2d(s);        
        int M = rate*s.length - (rate-1);
        int N = rate*s[0].length - (rate-1);
        double [][] s_interp = new double[M][N];        
        for(int k=0; k<s_interp.length; k++){
            for(int l=0; l<s_interp[0].length; l++){
                s_interp[k][l] = 
                    cubicInterp2d(coeffs_mirror, k*(1.0/rate), l*(1.0/rate));
            }
        }                                                                                                       
        return s_interp;                    
    }
    
    
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
    
    private BufferedImage doubleArrayToImage(double[][] array){     
        BufferedImage image = 
            new BufferedImage(array[0].length,array.length,
                              BufferedImage.TYPE_INT_RGB);  
        for( int y = 0; y < array.length; y++ ){
            for( int x = 0; x < array[0].length; x++ ){     
                 int value = 
                    (int)array[y][x] << 16 | 
                    (int)array[y][x] << 8 | 
                    (int)array[y][x];
                 image.setRGB(x, y, value);                                                   
            }
        }       
        return image;
    }
    
    public static void main(String[] args){
        BufferedImage image = null;
        try {
            image = ImageIO.read(new File("./data/img7.jpg"));
        } catch (IOException e) {
        	System.out.println("Error in Main Function");
        }
        if (image != null){
            ImageView imageView = new ImageView();
            imageView.drawImage(image);
            CubicInterpolation2d cubicInterpolation2d = 
                    new CubicInterpolation2d();
            double [][] img = 
                    cubicInterpolation2d.imageToDoubleArray(image);
            double [][] img_interp = 
                    cubicInterpolation2d.interpolate(img,2);
//            for(int i = 0; i<img_interp.length; i++)
//            {
//                for(int j = 0; j<img_interp[0].length; j++)
//                {
//                    System.out.print(img_interp[i][j]+"  ");
//                }
//                System.out.println();
//            }
            BufferedImage imageInterp = 
                    cubicInterpolation2d.doubleArrayToImage(img_interp);
            ImageView imageView2 = new ImageView();
            imageView2.drawImage(imageInterp);  
        }
    }
}