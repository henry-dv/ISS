package filters;

import itb2.image.GrayscaleImage;
import itb2.image.Image;
import itb2.image.ImageFactory;

public abstract class LinearFilter {

    public static double[][] sobel_x = {
        {-1, 0, 1},
        {-2, 0, 2},
        {-1, 0, 1}
    };
    public static double[][] sobel_y = {
            {1, 2, 1},
            {0, 0, 0},
            {-1, -2, -1}
    };
    public static double[][] l_4 = {
            {0, -1, 0},
            {-1, 4, -1},
            {0, -1, 0}
    };
    public static double[][] l_8 = {
            {-1, -1, -1},
            {-1, 8, -1},
            {-1, -1, -1}
    };
    public static double[][] box_blur_3x3 = {
            {1d/9, 1d/9, 1d/9},
            {1d/9, 1d/9, 1d/9},
            {1d/9, 1d/9, 1d/9}
    };
    public static double[][] box_blur_5x5 = {
            {1d/25, 1d/25, 1d/25, 1d/25, 1d/25},
            {1d/25, 1d/25, 1d/25, 1d/25, 1d/25},
            {1d/25, 1d/25, 1d/25, 1d/25, 1d/25},
            {1d/25, 1d/25, 1d/25, 1d/25, 1d/25},
            {1d/25, 1d/25, 1d/25, 1d/25, 1d/25}
    };

    public static Image convolution(GrayscaleImage input, double[][] kernel) {
        final int kernelSizeX = kernel[0].length;
        final int kernelSizeY = kernel.length;

        if (kernelSizeX % 2 == 0 || kernelSizeY % 2 == 0) {
            throw new IllegalArgumentException("Kernel must have odd side lengths");
        }

        Image output = ImageFactory.doublePrecision().gray(input.getSize());
        int off_x = kernelSizeX / 2;
        int off_y = kernelSizeY / 2;

        for (int y = off_y; y < input.getHeight() - off_y; y++) {
            for (int x = off_x; x < input.getWidth() - x; x++) {

                double newVal = 0;

                for (int kx = 0; kx < kernelSizeX; kx++) {
                    for (int ky = 0; ky < kernelSizeY; ky++) {
                        newVal += input.getValue(x + off_x - kx, y + off_y - ky, 0) * kernel[ky][kx];
                    }
                }

                output.setValue(x, y, newVal);
            }
        }

        return output;
    }

}
