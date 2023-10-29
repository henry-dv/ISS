import itb2.filter.AbstractFilter;
import itb2.filter.RequireImageType;
import itb2.image.GrayscaleImage;
import itb2.image.Image;
import itb2.image.ImageFactory;

/**
 * Einfache Gamma-Korrektur für ein Grauwertbild
 *
 * @author Henning Lehmann
 */
@RequireImageType(GrayscaleImage.class)
public class Gammakorrektur_HL extends AbstractFilter {

    public Gammakorrektur_HL() {
        properties.addDoubleProperty("gamma", 1.0);
    }

    @Override
    public Image filter(Image input) {

        GrayscaleImage output = ImageFactory.bytePrecision().gray(input.getSize());

        int iMax = 255;
        int iMin = 0;
        int nG = iMax + 1;

        double gamma = properties.getDoubleProperty("gamma");

        // erstelle Gamma-Mapping für jeden Intensitätswert
        int[] gammaMapping = new int[256];
        for (int i = 0; i < 256; i++) {
            double relative_intensity = ((double)(i - iMin))/(iMax - iMin);
            double new_intensity = nG * Math.pow(relative_intensity, gamma) + iMin;
            gammaMapping[i] = (int)Math.round(new_intensity); // math.round für kaufmännische Rundung
        }

        for (int y = 0; y < input.getHeight(); y++) {
            for (int x = 0; x < input.getWidth(); x++) {
                int intensity = (int)input.getValue(x, y, 0);
                output.setValue(x, y, gammaMapping[intensity]);
            }
        }

        return output;
    }
}
