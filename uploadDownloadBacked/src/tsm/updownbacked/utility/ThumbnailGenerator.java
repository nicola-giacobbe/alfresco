package tsm.updownbacked.utility;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

public class ThumbnailGenerator {

    public ThumbnailGenerator() {}

    public static BufferedImage createThumbnail(InputStream inputStream) {
       
       BufferedImage img = null;          
       
       try {
    	    img = ImageIO.read(inputStream);
	   } catch (IOException e) {
		e.printStackTrace();
	   }
       BufferedImage thumb = createEmptyThumbnail();

       // BufferedImage has a Graphics2D
       Graphics2D g2d = (Graphics2D) thumb.getGraphics(); 
       g2d.drawImage(img, 0, 0, 
                      thumb.getWidth() - 1,
                      thumb.getHeight() - 1, 
                      0, 0, 
                      img.getWidth() - 1,
                      img.getHeight() - 1, 
                      null);
       g2d.dispose();	 
	   return thumb;
    }

    private static BufferedImage createEmptyThumbnail() {
        return new BufferedImage(100, 100, BufferedImage.SCALE_SMOOTH);
    }

}