import itb2.filter.AbstractFilter;
import itb2.filter.RequireImageType;
import itb2.image.GrayscaleImage;
import itb2.image.Image;
import itb2.image.ImageFactory;

@RequireImageType(GrayscaleImage.class)
public class SobelFilter_HL extends AbstractFilter {

    public static final int[][] Sx = {
            {-1, 0, 1},
            {-2, 0, 2},
            {-1, 0, 1}
    };
    public static final int[][] Sy = {
            {1, 2, 1},
            {0, 0, 0},
            {-1, -2, -1}
    };

    public SobelFilter_HL() {
        properties.addOptionProperty("Filterselektion", "Horizontales Sobel-Filter",
                "Horizontales Sobel-Filter", "Vertikales Sobel-Filter");
    }

    @Override
    public Image filter(Image input) {

        boolean horizontal = properties.getOptionProperty("Filterselektion").equals("Horizontales Sobel-Filter");

        int[][] kernel = horizontal ? Sx : Sy;

        Image output = ImageFactory.doublePrecision().gray(input.getSize());

        // filter anwenden
        for (int y = 0; y < input.getHeight(); y++) {
            for (int x = 1; x < input.getWidth() - 1; x++) {

                double newValue = 0;

                // über Kernel iterieren
                for (int kernelY = 0; kernelY < 3; kernelY++) {
                    for (int kernelX = 0; kernelX < 3; kernelX++) {
                        int offsetY = kernelY - 1;
                        int offsetX = kernelX - 1;

                        // sicherstellen, dass Sample-Punkt innerhalb des Bildes bleibt
                        int sampleY = clamp(y - offsetY, 0, input.getHeight() - 1);
                        int sampleX = clamp(x - offsetX, 0, input.getWidth() - 1);

                        newValue += kernel[kernelY][kernelX] * input.getValue(sampleX, sampleY, 0);
                    }
                }

                output.setValue(x, y, newValue);
            }
        }

        // lineare Grauwertspreizung
        double iMaxGiven = 0, iMinGiven = Double.POSITIVE_INFINITY;

        for (int y = 0; y < input.getHeight(); y++) {
            for (int x = 0; x < input.getWidth(); x++) {
                iMaxGiven = Math.max(iMaxGiven, output.getValue(x, y, 0));
                iMinGiven = Math.min(iMinGiven, output.getValue(x, y, 0));
            }
        }

        double c1 = -iMinGiven;
        double c2 = 255 / (iMaxGiven - iMinGiven);

        for (int y = 0; y < input.getHeight(); y++) {
            for (int x = 0; x < input.getWidth(); x++) {
                output.setValue(x, y, (output.getValue(x, y, 0) + c1) * c2);
            }
        }

        return output;

    }

    private int clamp(int val, int min, int max) {
        val = Math.max(val, min);
        val = Math.min(val, max);
        return val;
    }

}
