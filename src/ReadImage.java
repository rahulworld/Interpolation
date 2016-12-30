import java.io.File;
import java.util.Scanner;


public class ReadImage {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int nDim=4,count=0;
		int nData=72;
		double[][] pix_spec=new double[4][74];
		double[][] pix_spec1=new double[4][74];
		File file_1=new File("./data/liberary.txt");
		File file_2=new File("./data/data1.txt");
        try {
          Scanner sc = new Scanner(file_1);
          sc.useDelimiter(",");
          
          for(int i=0;i<nDim;i++){
              for(int j=0;j<nData;j++){
                  pix_spec[i][j]=sc.nextDouble();
                 count++;
                  //System.out.println(count);
              }
              sc.nextLine();
          }
          sc.close();
          
      } catch (Exception e) {

          e.printStackTrace();
      }
        try {
            Scanner sc1 = new Scanner(file_2);
            sc1.useDelimiter(",");
            
            for(int i=0;i<nDim;i++){
                for(int j=0;j<nData;j++){
                    pix_spec1[i][j]=sc1.nextDouble();
                   count++;
                    System.out.println(pix_spec1[i][j]);
                }
                sc1.nextLine();
            }
            sc1.close();
            
        } catch (Exception e) {

            e.printStackTrace();
        }
        CubicInterpolation2d c1 = new CubicInterpolation2d();
        double[][] newCurv=c1.interpolate(pix_spec, 2);
        CubicInterpolation2d c2 = new CubicInterpolation2d();
//        BufferedImage imageInterp1 = c2.doubleArrayToImage(newCurv);
//        try {
//			ImageIO.write(imageInterp1, "PNG", new File("./data/interpolate3.png"));
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//        ImageView imageView2 = new ImageView();
//        imageView2.drawImage(imageInterp1);
//		Vector<Integer> vector = new Vector<Integer>();
//		Vector<Integer> vec1 = new Vector<Integer>(72);
//		Vector<Integer> vec2 = new Vector<Integer>(72);
//		int Total=0,mean1=0,mean2=0;1111111111111
//		if(vec1.size()==vec2.size()){
//			for(int i=0;i<vec1.size();i++){
//				Total+=vec1.elementAt(i);
//			}			
//			mean1=Total/vec1.size();
//			for(int i=0;i<vec2.size();i++){
//				Total+=vec1.elementAt(i);
//			}			
//			mean2=Total/vec1.size();
//		}
		
	}

}
