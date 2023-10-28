import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.List;

import itb2.engine.Controller;
import itb2.filter.AbstractFilter;
import itb2.image.DrawableImage;
import itb2.image.Image;
import itb2.image.ImageFactory;

/**
 * Example for pixel selection.
 * Draws a white line on black background using the selected corners.
 *
 * @author Micha Strauch
 */
public class SelectionExample extends AbstractFilter {
	
	/** Properties for the size of the output image */
	public static final String WIDTH = "Width", HEIGHT = "Height";
	
	/** Constructor for the selection example */
	public SelectionExample() {
		// Add filter properties
		properties.addIntegerProperty(WIDTH, 600);
		properties.addIntegerProperty(HEIGHT, 400);
	}
	
	@Override
	public Image[] filter(Image[] input) {
		
		// Read and check image width and height 
		int width = properties.getIntegerProperty(WIDTH);
		int height = properties.getIntegerProperty(HEIGHT);
		assertTrue("Width and height must be positive integers", width > 0 && height > 0);
		
		// Create output image
		DrawableImage output = ImageFactory.bytePrecision().drawable(width, height);
		Graphics graphics = output.getGraphics();
		
		// Draw background and set color of line
		graphics.setColor(Color.BLACK);
		graphics.fillRect(0, 0, width, height);
		graphics.setColor(Color.WHITE);
		
		// While the user selects points, continue drawing the line
		List<Point> selections;
		Point previous = null;
		do {
			// Ask user for selection
			selections = Controller.getCommunicationManager().getSelections("Please select point or close window.", 1, output);
			
			for(Point current : selections) {
				if(previous == null)
					graphics.fillOval(current.x - 3, current.y - 3, 7, 7);
				else
					graphics.drawLine(previous.x, previous.y, current.x, current.y);
				
				previous = current;
			}
			
		} while(selections.size() > 0);
		
		return new Image[] {output};
	}
	
}
