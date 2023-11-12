import itb2.filter.AbstractFilter;
import itb2.filter.RequireImageType;
import itb2.image.GrayscaleImage;
import itb2.image.Image;
import itb2.image.ImageFactory;

import java.util.ArrayList;
import java.util.Collections;

@RequireImageType(GrayscaleImage.class)
public class Medianfilter_HL_YS extends AbstractFilter {

    public Medianfilter_HL_YS() {
        properties.addOptionProperty("Filtergröße", "3x3",
                "3x3", "5x5", "7x7", "9x9", "11x11", "13x13", "15x15");
    }

    @Override
    public Image filter(Image input) {

        Image output = ImageFactory.bytePrecision().gray(input.getSize());

        int filterSize = switch ((String) properties.getOptionProperty("Filtergröße")) {
            case "3x3" -> 3;
            case "5x5" -> 5;
            case "7x7" -> 7;
            case "9x9" -> 9;
            case "11x11" -> 11;
            case "13x13" -> 13;
            case "15x15" -> 15;
            default -> 0;
        };
        int filterRadius = filterSize / 2;
        for (int y = 0; y < input.getHeight(); y++) {
            for (int x = 0; x < input.getWidth(); x++) {
                ArrayList<java.lang.Integer> values = new ArrayList<>(filterSize * filterSize);

                // Werte einsammeln
                for (int filterY = Math.max(0, y - filterRadius);
                     filterY <= Math.min(input.getHeight() - 1, y + filterRadius);
                     filterY++) {
                    for (int filterX = Math.max(0, x - filterRadius);
                         filterX <= Math.min(input.getWidth() - 1, x + filterRadius);
                         filterX++) {
                        values.add((int) input.getValue(filterX, filterY, 0));
                    }
                }

                Collections.sort(values);

                output.setValue(x, y, median(values));

            }
        }

        return output;

    }

    private int median(ArrayList<java.lang.Integer> values) {
        if (values.size() % 2 == 1) {
            return values.get(values.size() / 2);
        }

        int upperMedian = values.get(values.size() / 2);
        int lowerMedian = values.get(values.size() / 2 - 1);
        return (upperMedian + lowerMedian) / 2;
    }

}
