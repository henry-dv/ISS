import itb2.filter.AbstractFilter;
import itb2.filter.RequireImageType;
import itb2.image.BinaryImage;
import itb2.image.Image;
import itb2.image.ImageFactory;

// @RequireImageType(BinaryImage.class)
public class HT_HL extends AbstractFilter {
    public HT_HL() {}

    @Override
    public Image filter(Image input) {
        int rho_max = 64;
        int rho_min = - rho_max;

        // Image hough_space = ImageFactory.bytePrecision().gray(rho_max - rho_min, 360);
        int[][] hough_space = new int[rho_max - rho_min][360];

        System.out.println(input.getValue(0, 0, 0));
        System.out.println(input.getValue(1, 1, 0));
        System.out.println(input.getValue(2, 2, 0));

        for (int y = 1; y < input.getHeight() - 1; y++) {
            for (int x = 1; x < input.getWidth() - 1; x++) {

                if (input.getValue(x, y, 0) != 0) {
                    continue;
                }

                boolean isEdgePixel = false;

                edgePixelCheck:
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dx = -1; dx <= 1; dx++) {
                        if (input.getValue(x + dx, y + dy, 0) != 0) {
                            isEdgePixel = true;
                            break edgePixelCheck;
                        }
                    }
                }
                if (!isEdgePixel) {
                    continue;
                }

                for (int theta = 0; theta < 360; theta++) {
                    int rho = (int)(x * Math.cos(Math.PI / theta) + y * Math.sin(Math.PI / theta));

                    if (rho_min <= rho && rho <= rho_max) {
                        hough_space[rho - rho_min][theta]++;
                    }
                }
            }
        }

        return grauwertSpreizung(hough_space);
    }

    private Image grauwertSpreizung(int[][] values) {

        Image output = ImageFactory.bytePrecision().gray(values[0].length, values.length);

        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;

        for (int y = 0; y < output.getHeight(); y++) {
            for (int x = 0; x < output.getWidth(); x++) {
                min = Math.min(min, values[y][x]);
                max = Math.max(max, values[y][x]);
            }
        }

        System.out.println("min = " + min);
        System.out.println("max = " + max);

        for (int y = 0; y < output.getHeight(); y++) {
            for (int x = 0; x < output.getWidth(); x++) {
                output.setValue(x, y, (int) ((255.0 / (max - min)) * (values[y][x] - min)));
            }
        }

        return output;
    }

}
