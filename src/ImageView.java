import java.awt.*;
import javax.swing.*;
import java.awt.image.*;
import javax.swing.JFrame;

@SuppressWarnings("serial")
public class ImageView extends JFrame{

    private SignalPanel signalPanel;
    
    public ImageView(){                     
        setTitle("Rahul");      
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(200, 1000);       
        signalPanel = new SignalPanel();                 
        getContentPane().add(signalPanel);          
        setVisible(true);        
    }
    
    public void drawImage(BufferedImage image){
        setSize(image.getWidth()+25, image.getHeight()+50);         
        signalPanel.setImage( image);
    }   
}

@SuppressWarnings("serial")
class SignalPanel extends JPanel {
    
   private BufferedImage image;
   
   @Override
   protected void paintComponent(Graphics graphics) {
       super.paintComponent(graphics);   
       if (image != null) {
           graphics.drawImage(image, 5, 5, this);
       }
   }
   
   public void setImage(BufferedImage image){
       this.image = image;
   }  
}