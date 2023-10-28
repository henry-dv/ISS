import itb2.filter.AbstractFilter;
import itb2.filter.RequireImageType;
import itb2.image.GrayscaleImage;
import itb2.image.Image;
import itb2.image.ImageConverter;
import itb2.image.ImageFactory;
import itb2.image.RgbImage;

/**
 * Wandelt ein RGB-Bild in ein Grauwertbild um.
 *
 * @author Micha Strauch
 */
@RequireImageType(RgbImage.class)
public class Grayfilter extends AbstractFilter {
	
	public Grayfilter() {
		// Als Konverter registrieren
		ImageConverter.register(RgbImage.class, ImageFactory.bytePrecision().gray(), this);
	}
	
	@Override
	public Image filter(Image input) {
		// Ausgabebild erstellen
		GrayscaleImage output = ImageFactory.bytePrecision().gray(input.getSize());
		
		// Über Pixel iterieren
		for(int col = 0; col < input.getWidth(); col++) {
			for(int row = 0; row < input.getHeight(); row++) {
				double sum = 0;
				
				// Kanäle aufsummieren
				for(int chan = 0; chan < input.getChannelCount(); chan++)
					sum += input.getValue(col, row, chan);
				
				// Mittelwert abspeichern
				output.setValue(col, row, sum / input.getChannelCount());
			}
		}
		
		return output;
	}
}
