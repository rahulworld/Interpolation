import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Fusion {
    BSplines bS;        
    public Fusion(){
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
    

    public double cubicInterp2d(double[][] coeffs_mirror, double row, double col){    
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
        	fuse[i]=blen.blend(Hysi_Img[i], TMC_IMG,0.3);
        }
        ImageView imageView41 = new ImageView();
		ImageView imageView31 = new ImageView();

		imageView41.drawImage(fuse[2]);
		imageView31.drawImage(fuse[3]);

        for(int i=1;i<=4;i++){
            Fusion cubicInterpolation2d = new Fusion();
            img = cubicInterpolation2d.imageToDoubleArray(fuse[i]);
            img_interp = cubicInterpolation2d.interpolate(img,1);
            System.out.println(img_interp.length+" "+img_interp[0].length);
            final_Img[i]= cubicInterpolation2d.doubleArrayToImage(img_interp);
            System.out.println("Complete Interpolated Image  "+i);
        }
		ImageView imageView7 = new ImageView();

		imageView7.drawImage(final_Img[2]);

        if (final_Img != null){
//            ImageView imageView = new ImageView();
//            imageView.drawImage(image);

            try {
				ImageIO.write(final_Img[1], "TIFF", new File("./data/new/output/w1.tif"));
				ImageIO.write(final_Img[2], "TIFF", new File("./data/new/output/w2.tif"));
				ImageIO.write(final_Img[3], "TIFF", new File("./data/new/output/w3.tif"));
				ImageIO.write(final_Img[4], "TIFF", new File("./data/new/output/w4.tif"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    }


public class BSplines {

    public double bspline(int degree, double x){
        double betta;
        double t;
        betta = 0;
        if(degree == 0){                
            if ((x > -0.5) && (x < 0.5)){
                betta = 1.0;
            }
            else if( Math.abs(x) == 0.5){
                betta = 0.5;
            }
            else if( Math.abs(x) > 0.5){
                betta = 0.0;
            }           
        }
        else if( degree == 1){
            if ((x<=-1) || (x>=1)){ 
                betta = 0.0;                        
            }
            else if ((x>-1) && (x<0)){
                betta = x+1;
            }
            else if ((x>0) && (x<1)){
                betta = -x+1;
            }
            else if( x==0){
                betta = 1.0;
            }                                   
        }       
        else if (degree == 2 ){     
            t = 1.5;
            if ((x <= (0-t)) || (x >= (3-t))){
                betta = 0.0;
            }
            else if ((x >= (0-t)) && (x< (1-t))) {
                betta = ((x+t)*(x+t))/2.0;
            }
            else if ((x >= (1-t)) && (x< (2-t))) {
                betta = ((x+t)*(x+t)-3.0*(x-1+t)*(x-1+t))/2.0;
            }
            else if ((x >= (2-t)) && (x< (3-t))) {
                betta = ((x+t)*(x+t) - 3.0*(x-1+t)*(x-1+t) + 
                        3.0*(x-2+t)*(x-2+t))/2.0;
            }
        }
        else if (degree == 3 ){ 
            if ((Math.abs(x)>=0) && (Math.abs(x)< 1)) {
                betta = 2.0/3.0 - Math.abs(x)*Math.abs(x) + 
                (Math.abs(x)*Math.abs(x)*Math.abs(x))/2.0;
            }
            else if ((Math.abs(x)>=1) && (Math.abs(x)< 2)) {
                betta = ((2-Math.abs(x))*(2-Math.abs(x))*
                        (2-Math.abs(x)))/6.0;
            }
            else if (Math.abs(x) >=2) {
                betta = 0.0;
            }
        }
        return betta;
    }
}
public class CubicInterpolation1d {

    private double[] mirrorW1d(double s[]){
        double [] s_mirror = new double[s.length+3];
        s_mirror[0] = s[1];
        for(int k=0; k<s.length; k++){
            s_mirror[k+1] = s[k];
        }
        s_mirror[s_mirror.length-2] = s[s.length-2];
        return s_mirror;
    }
    
    public double[] coeffs(double s[]){         
        DirectBsplFilter1d directFilter = 
                new DirectBsplFilter1d(s.length);
        double coeffs[] = directFilter.filter(s);
        double coeffs_mirror[] = mirrorW1d(coeffs);
        return coeffs_mirror;
    }   
    public double interp(double coeffs_mirror[], double x1){
        BSplines bS = new BSplines();
        int k = (int)Math.floor(x1);
        double y1 = coeffs_mirror[k+0]*bS.bspline(3,x1-k+1)+ 
                    coeffs_mirror[k+1]*bS.bspline(3,x1-k+0)+ 
                    coeffs_mirror[k+2]*bS.bspline(3,x1-k-1)+ 
                    coeffs_mirror[k+3]*bS.bspline(3,x1-k-2); 
        return y1;
    }
    
    public double[] interpolate(double s[], int rate){          
        double coeffs_mirror[] = coeffs(s);     
        double s_interp[] = new double[rate*s.length-(rate-1)];
        for(int k = 0; k < s_interp.length; k++){
            s_interp[k] = interp(coeffs_mirror, k*(1.0/rate));          
        }
        return s_interp;    
    }
}


}