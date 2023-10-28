import itb2.filter.AbstractFilter;
import itb2.filter.RequireImageType;
import itb2.image.*;

/**
 * Für den Fall, dass ein Grauwertbild fälschlicherweise als RGB-Bild erkannt wurde (und alle Grauwerte daher im
 * roten Kanal sind)
 */
@RequireImageType(RgbImage.class)
public class VerkorkstesGrauwertbild extends AbstractFilter {

    public VerkorkstesGrauwertbild() {
        // Als Konverter registrieren
        ImageConverter.register(RgbImage.class, ImageFactory.bytePrecision().gray(), this);
    }

    @Override
    public Image filter(Image input) {
        GrayscaleImage output = ImageFactory.bytePrecision().gray(input.getSize());

        for (int y = 0; y < input.getHeight(); y++) {
            for (int x = 0; x < input.getWidth(); x++) {
                output.setValue(x, y, input.getValue(x, y, 0));
            }
        }

        return output;
    }

}
