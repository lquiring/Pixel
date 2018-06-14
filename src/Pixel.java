import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import acm.program.*;
import java.awt.Color;
import java.awt.event.*;
import acm.graphics.*;

public class Pixel extends GraphicsProgram {
	public static int APPLICATION_WIDTH;
	public static int APPLICATION_HEIGHT;
	
	public void run() {
		//read in file
		String str = "C:\\Users\\Austen\\eclipse-workspace\\Pixel\\pixelatorimage.jpg";
		Pixel pixel = new Pixel();
		BufferedImage i = pixel.readInImage(str);
		
		//set application size
		APPLICATION_WIDTH = i.getWidth();
		APPLICATION_HEIGHT = i.getHeight();
		setSize(APPLICATION_WIDTH, APPLICATION_HEIGHT);
		GCanvas c = this.getGCanvas();
		c.setBounds(0, 0, APPLICATION_WIDTH, APPLICATION_HEIGHT);
		
		//initial circle
		GOval circle = drawCircle(0, 0, c.getWidth(), findAvgPixelColor(i));
		setCircleProperties(circle, findAvgPixelColor(i));
		c.add(circle);
		
		//mouse motion listener to activate split
		c.addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				GPoint g = new GPoint(e.getPoint());
				if(c.getElementAt(g) != null) {
					GObject obj = c.getElementAt(g);
					
					if(obj.getWidth() >= 6) {
						splitCircle(obj, c, i);
					}
				}
			}
		});
	}
	
	/**
	 * Read in image file
	 * @param str | string file path
	 * @return BufferedImage
	 */
	private BufferedImage readInImage(String str) {
		//Read in file string
		File f = new File(str);
		BufferedImage img = null;
		try {
			img = ImageIO.read(f);
		} catch (IOException e) {
			System.out.println("Error reading image - make sure your path is correct and try again");
			e.printStackTrace();
		}
		return img;
	}
	
	/**
	 * Split a circle into 4 smaller in the same area
	 * @param obj | GObject | whatever the mouse moved over
	 * @param c | GCanvas | the application window
	 * @param image | BufferedImage | original image
	 * @return void
	 */
	private void splitCircle(GObject obj, GCanvas c, BufferedImage image) {
		BufferedImage temp = image;
		GRectangle r = obj.getBounds();
		c.remove(obj);
		
		int halfWidth = (int) Math.floor(r.getWidth() / 2);
		//upper left corner and lower right corner circles
		for(int i = 0; i < 2; i++) {
			temp = cropImage(image, (int) (r.getX() + (i % 2) * halfWidth), (int) (r.getY() + (i % 2) * halfWidth), halfWidth);
			GOval circle = new GOval(r.getX() + (i % 2) * halfWidth, r.getY() + (i % 2) * halfWidth, halfWidth, halfWidth);
			setCircleProperties(circle, findAvgPixelColor(temp));
			c.add(circle);
		}
		
		//upper right corner
		temp = cropImage(image, (int) (r.getX() + halfWidth), (int) r.getY(), halfWidth);
		GOval circle = new GOval(r.getX() + halfWidth, r.getY(), r.getWidth() / 2, halfWidth);
		setCircleProperties(circle, findAvgPixelColor(temp));
		c.add(circle);
		
		//lower left corner
		temp = cropImage(image, (int) r.getX(), (int) (r.getY() + halfWidth), halfWidth);
		circle = new GOval(r.getX(), r.getY() + halfWidth, halfWidth, halfWidth);
		setCircleProperties(circle, findAvgPixelColor(temp));
		c.add(circle);
	}
	
	/**
	 * Draw the new circle
	 * @param startX | int 
	 * @param startY | int
	 * @param width | int | half the width of the original image
	 * @param c | Color | average color of the subset of the image
	 * @return GOVal
	 */
	private GOval drawCircle(int startX, int startY, int width, Color c) {
		GOval circle = new GOval(startX, startY, width, width);
		setCircleProperties(circle, c);
		return circle;
	}
	
	/**
	 * Set circle colors
	 * @param circle | GOval
	 * @param c | Color
	 * @return void
	 */
	private void setCircleProperties(GOval circle, Color c) {
		circle.setFillColor(c);
		circle.setColor(c);
		circle.setFilled(true);
	}
	
	/**
	 * Get a subset of an image
	 * @param image | BufferedImage | original image
	 * @param x | int
	 * @param y | int
	 * @param width | int
	 * @return BufferedImage | subimage of original image
	 */
	private BufferedImage cropImage(BufferedImage image, int x, int y, int width) {
		return image.getSubimage(x, y, width, width);
	}
	
	/**
	 * Get average pixel color of an image
	 * @param image | BufferedImage
	 * @return Color
	 */
	private Color findAvgPixelColor(BufferedImage image) {
		int rSum = 0;
		int gSum = 0;
		int bSum = 0;
		//Loop through all the pixels in the image
		for(int i = 0; i < image.getWidth(); i++) {
			for(int j = 0; j < image.getHeight(); j++) {
				//Figure out rgb components of each pixel
				int clr = image.getRGB(i, j);
				int red = (clr & 0x00ff0000) >> 16;
				int green = (clr & 0x0000ff00) >> 8;
				int blue = clr & 0x000000ff;
				//Sum to find average later
				rSum += red;
				gSum += green;
				bSum += blue;
			}
		}
		rSum = rSum / (image.getHeight()*image.getWidth());
		gSum = gSum / (image.getHeight()*image.getWidth());
		bSum = bSum / (image.getHeight()*image.getWidth());
		return new Color(rSum, gSum, bSum);
	}
}
