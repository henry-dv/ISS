import itb2.filter.AbstractFilter;
import itb2.image.GrayscaleImage;
import itb2.image.Image;
import itb2.image.ImageFactory;

/**
 * Abstrakte Klasse zur Anwendung eines Convolution Filters
 * @author Henning Lehmann
 */

public abstract class ConvolutionFilter_HL extends AbstractFilter {
    public abstract double[][] getKernel();

    @Override
    public Image filter(Image input) {

        GrayscaleImage output = ImageFactory.doublePrecision().gray(input.getSize());

        double[][] kernel = this.getKernel();

        int kernelHeight = kernel.length;
        int kernelWidth = kernel[0].length;

        // über Bildpixel iterieren
        for (int y = 0; y < input.getHeight(); y++) {
            for (int x = 0; x < input.getWidth(); x++) {
                double newValue = 0;

                // über Kernel iterieren
                for (int kernelY = 0; kernelY < kernelHeight; kernelY++) {
                    for (int kernelX = 0; kernelX < kernelWidth; kernelX++) {
                        int offsetY = kernelY - (kernelHeight / 2);
                        int offsetX = kernelX - (kernelWidth / 2);

                        // sicherstellen, dass Sample-Punkt innerhalb des Bildes bleibt
                        int sampleY = clamp(y + offsetY, 0, input.getHeight() - 1);
                        int sampleX = clamp(x + offsetX, 0, input.getWidth() - 1);

                        newValue += kernel[kernelY][kernelX] * input.getValue(sampleX, sampleY, 0);
                    }
                }

                output.setValue(x, y, newValue);
            }
        }

        return output;
    }

    /**
     * Hilfsfunktion, um val auf ein geschlossenes Intervall einzuschränken
     */
    private int clamp(int val, int min, int max) {
        val = Math.max(val, min);
        val = Math.min(val, max);
        return val;
    }
}
