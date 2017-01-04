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
    public double[][] mixingOfCurveUsingBspline(double[][] s1,double[][] s2, int rate){
        double [][] coeffs_mirror1 = cubicCoeff2d(s1);
        double [][] coeffs_mirror2 = cubicCoeff2d(s2);
        int M1 = rate*s1.length - (rate-1);
        int N1 = rate*s1[0].length - (rate-1);
        int M2 = rate*s1.length - (rate-1);
        int N2 = rate*s1[0].length - (rate-1);
        double [][] s_interp1 = new double[M1][N1];
        double [][] s_interp2 = new double[M2][N2];
        for(int k=0; k<s_interp1.length; k++){
            for(int l=0; l<s_interp1[0].length; l++){
                s_interp1[k][l] = 
                    cubicInterp2d(coeffs_mirror1,coeffs_mirror2, k*(1.0/rate), l*(1.0/rate));
            }
        }                                                                                                       
        return s_interp1;                    
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
    	
    	
//    	BufferedImage bi3 = new BufferedImage(array.length,array[0].length, BufferedImage.TYPE_INT_RGB);
//		int[] rgbim3 = new int[array.length];
//
//		for (int row = 0; row < array[0].length; row++) {
//			//bi1.getRGB(0, row, array.length, 1, rgbim1, 0, array.length);
//
//			for (int col = 0; col < array.length; col++) {
//				int rgb1 =(int)array[row][col];
//				int r3 = (rgb1 >> 16) & 255;
//				int g3 = (rgb1 >> 8) & 255;
//				int b3 = rgb1 & 255;
//				rgbim3[col] = (r3 << 16) | (g3 << 8) | b3;
//			}
//			bi3.setRGB(0, row, array.length, 1, rgbim3, 0, array.length);
//		}
//		return bi3;
    	
//    	BufferedImage image = new BufferedImage(array.length, array[0].length, BufferedImage.TYPE_INT_RGB);
//        for (int x = 0; x < array.length; x++) { // b is my 2D array
//            for (int y = 0; y < array[x].length; y++) {
//                image.setRGB(x, y, (int)array[y][x]);
//            }
//        }
//        return image;
    }
    
    @SuppressWarnings("null")
	public static void main(String[] args){
        BufferedImage image = null;
        BufferedImage bufferImage=null;
        Blender1 blen=new Blender1();
        BufferedImage[] fuse=new BufferedImage[65];
        BufferedImage[] Hysi_Img=new BufferedImage[65];
        BufferedImage TMC_IMG=null;
        try {
        	TMC_IMG=ImageIO.read(new File("./data/tmc2561.png"));
//        	for(int j=1;j<=64;j++){
//        		//Path path = Paths.get(PATH);
//        		Hysi_Img[j]=ImageIO.read(new File("./data/h"+j+".png"));
//	        }
//        	Hysi_Img[0]=ImageIO.read(new File("./data/tmc2561.png"));
            Hysi_Img[1]=ImageIO.read(new File("./data/himg/h1.png"));
            Hysi_Img[2]=ImageIO.read(new File("./data/himg/h2.png"));
            Hysi_Img[3]=ImageIO.read(new File("./data/himg/h3.png"));
            Hysi_Img[4]=ImageIO.read(new File("./data/himg/h4.png"));
            Hysi_Img[5]=ImageIO.read(new File("./data/himg/h5.png"));
            Hysi_Img[6]=ImageIO.read(new File("./data/himg/h6.png"));
            Hysi_Img[7]=ImageIO.read(new File("./data/himg/h7.png"));
            Hysi_Img[8]=ImageIO.read(new File("./data/himg/h8.png"));
            Hysi_Img[9]=ImageIO.read(new File("./data/himg/h9.png"));
            Hysi_Img[10]=ImageIO.read(new File("./data/himg/h10.png"));
            Hysi_Img[11]=ImageIO.read(new File("./data/himg/h11.png"));
            Hysi_Img[12]=ImageIO.read(new File("./data/himg/h12.png"));
            Hysi_Img[13]=ImageIO.read(new File("./data/himg/h13.png"));
            Hysi_Img[14]=ImageIO.read(new File("./data/himg/h14.png"));
            Hysi_Img[15]=ImageIO.read(new File("./data/himg/h15.png"));
            Hysi_Img[16]=ImageIO.read(new File("./data/himg/h16.png"));
            Hysi_Img[17]=ImageIO.read(new File("./data/himg/h17.png"));
            Hysi_Img[18]=ImageIO.read(new File("./data/himg/h18.png"));
            Hysi_Img[19]=ImageIO.read(new File("./data/himg/h19.png"));
            Hysi_Img[20]=ImageIO.read(new File("./data/himg/h20.png"));
            Hysi_Img[21]=ImageIO.read(new File("./data/himg/h21.png"));
            Hysi_Img[22]=ImageIO.read(new File("./data/himg/h22.png"));
            Hysi_Img[23]=ImageIO.read(new File("./data/himg/h23.png"));
            Hysi_Img[24]=ImageIO.read(new File("./data/himg/h24.png"));
            Hysi_Img[25]=ImageIO.read(new File("./data/himg/h25.png"));
            Hysi_Img[26]=ImageIO.read(new File("./data/himg/h26.png"));
            Hysi_Img[27]=ImageIO.read(new File("./data/himg/h27.png"));
            Hysi_Img[28]=ImageIO.read(new File("./data/himg/h28.png"));
            Hysi_Img[29]=ImageIO.read(new File("./data/himg/h29.png"));
            Hysi_Img[30]=ImageIO.read(new File("./data/himg/h30.png"));
            Hysi_Img[31]=ImageIO.read(new File("./data/himg/h31.png"));
            Hysi_Img[32]=ImageIO.read(new File("./data/himg/h32.png"));
            Hysi_Img[33]=ImageIO.read(new File("./data/himg/h33.png"));
            Hysi_Img[34]=ImageIO.read(new File("./data/himg/h34.png"));
            Hysi_Img[35]=ImageIO.read(new File("./data/himg/h35.png"));
            Hysi_Img[36]=ImageIO.read(new File("./data/himg/h36.png"));
            Hysi_Img[37]=ImageIO.read(new File("./data/himg/h37.png"));
            Hysi_Img[38]=ImageIO.read(new File("./data/himg/h38.png"));
            Hysi_Img[39]=ImageIO.read(new File("./data/himg/h39.png"));
            Hysi_Img[40]=ImageIO.read(new File("./data/himg/h40.png"));
            Hysi_Img[41]=ImageIO.read(new File("./data/himg/h41.png"));
            Hysi_Img[42]=ImageIO.read(new File("./data/himg/h42.png"));
            Hysi_Img[43]=ImageIO.read(new File("./data/himg/h43.png"));
            Hysi_Img[44]=ImageIO.read(new File("./data/himg/h44.png"));
            Hysi_Img[45]=ImageIO.read(new File("./data/himg/h45.png"));
            Hysi_Img[46]=ImageIO.read(new File("./data/himg/h46.png"));
            Hysi_Img[47]=ImageIO.read(new File("./data/himg/h47.png"));
            Hysi_Img[48]=ImageIO.read(new File("./data/himg/h48.png"));
            Hysi_Img[49]=ImageIO.read(new File("./data/himg/h49.png"));
            Hysi_Img[50]=ImageIO.read(new File("./data/himg/h50.png"));
            Hysi_Img[51]=ImageIO.read(new File("./data/himg/h51.png"));
            Hysi_Img[52]=ImageIO.read(new File("./data/himg/h52.png"));
            Hysi_Img[53]=ImageIO.read(new File("./data/himg/h53.png"));
            Hysi_Img[54]=ImageIO.read(new File("./data/himg/h54.png"));
            Hysi_Img[55]=ImageIO.read(new File("./data/himg/h55.png"));
            Hysi_Img[56]=ImageIO.read(new File("./data/himg/h56.png"));
            Hysi_Img[57]=ImageIO.read(new File("./data/himg/h57.png"));
            Hysi_Img[58]=ImageIO.read(new File("./data/himg/h58.png"));
            Hysi_Img[59]=ImageIO.read(new File("./data/himg/h59.png"));
            Hysi_Img[60]=ImageIO.read(new File("./data/himg/h60.png"));
            Hysi_Img[61]=ImageIO.read(new File("./data/himg/h61.png"));
            Hysi_Img[62]=ImageIO.read(new File("./data/himg/h62.png"));
            Hysi_Img[63]=ImageIO.read(new File("./data/himg/h63.png"));
            Hysi_Img[64]=ImageIO.read(new File("./data/himg/h64.png"));
		} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}
        for(int i=1;i<=64;i++){
        	fuse[i]=blen.blend(Hysi_Img[i], TMC_IMG,0.3);
        }
		ImageView imageView3 = new ImageView();
		ImageView imageView1 = new ImageView();
		imageView3.drawImage(fuse[64]);
		imageView1.drawImage(fuse[25]);
        Blender1 blender3 = new Blender1();
        image=blender3.blendHysi(fuse,0.015);
		try {
//			Blender1 blender = new Blender1();
//			ImageView imageView = new ImageView();
//			ImageView imageView1 = new ImageView();
//			blender.bi1 = ImageIO.read(new File("./data/tmc2561.png"));
//			blender.bi2 = ImageIO.read(new File("./data/rgb123.png"));
//			
//			imageView.drawImage(blender.bi1);
//			imageView1.drawImage(blender.bi2);
//			image = blender.blend(blender.bi1, blender.bi2, 0.65);
			ImageIO.write(image, "PNG", new File("./data/saras11.png"));
			//image = ImageIO.read(new File("./data/take3.png"));
		} catch(IOException e) {
			e.printStackTrace();
		}
        if (image != null){
            ImageView imageView = new ImageView();
            imageView.drawImage(image);
            CubicInterpolation2d cubicInterpolation2d = new CubicInterpolation2d();
            double [][] img = cubicInterpolation2d.imageToDoubleArray(image);
            double [][] img_interp = cubicInterpolation2d.interpolate(img,2);
//            for(int i = 0; i<img_interp.length; i++)
//            {
//                for(int j = 0; j<img_interp[0].length; j++)
//                {
//                    System.out.print(img_interp[i][j]+"  ");
//                }
//                System.out.println();
//            }
            BufferedImage imageInterp = cubicInterpolation2d.doubleArrayToImage(img_interp);
            try {
				ImageIO.write(imageInterp, "PNG", new File("./data/interpolate2.png"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            ImageView imageView2 = new ImageView();
            imageView2.drawImage(imageInterp);  
        }
    }
}