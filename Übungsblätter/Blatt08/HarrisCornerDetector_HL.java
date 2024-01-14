import itb2.filter.AbstractFilter;
import itb2.filter.RequireImageType;
import itb2.image.GrayscaleImage;
import itb2.image.Image;
import itb2.image.ImageFactory;

@RequireImageType(GrayscaleImage.class)
public class HarrisCornerDetector_HL extends AbstractFilter {
    public HarrisCornerDetector_HL() {
        properties.addDoubleProperty("kappa", 0.4);
        properties.addDoubleProperty("sigma", 2.0);

    }

    private double[][] gaussWeights(double sigma) {
        int m = 2 * (int)Math.ceil(3 * sigma) + 1;

        // generate 2-dimensional gaussian filter
        double[][] gaussFilter = new double[m][m];
        double sum = 0;
        for (int i = 0; i < m; i++) {
            int u = i - (m / 2);
            for (int j = 0; j < m; j++) {
                int v = j - (m/2);
                gaussFilter[i][j] = Math.pow(Math.E, -(u*u + v*v)/(2 * sigma * sigma));
            }
        }

        return gaussFilter;
    }

    @Override
    public Image[] filter(Image[] input) {
        double sigma = properties.getDoubleProperty("sigma");
        double kappa = properties.getDoubleProperty("kappa");

        double[][] w = gaussWeights(sigma);
        double[][] R = new double[input[0].getHeight()][input[0].getWidth()];

        for (int y = w.length/2 + 1; y < input[0].getHeight() - w.length/2 - 1; y++) {
            for (int x = w.length/2 + 1; x < input[0].getWidth() - w.length/2 - 1; x++) {
                double sum_I_x = 0, sum_I_y = 0, sum_I_x_I_y = 0;

                for (int i = 0; i < w.length; i++) {
                    int u = i - (w.length / 2);
                    for (int j = 0; j < w.length; j++) {
                        int v = j - (w.length/2);

                        // System.out.println("x = " + x + ", y = " + y + ", u = " + u + ", v = " + v);
                        double I_x = input[0].getValue(x + u - 1, y + v, 0) -
                                     input[0].getValue(x + u + 1, y + v, 0);
                        double I_y = input[0].getValue(x + u, y + v - 1, 0) -
                                     input[0].getValue(x + u, y + v + 1, 0);

                        sum_I_x += w[i][j] * I_x * I_x;
                        sum_I_y += w[i][j] * I_y * I_y;
                        sum_I_x_I_y += w[i][j] * I_x * I_y;
                    }
                }

                double det = sum_I_x * sum_I_y - sum_I_x_I_y * sum_I_x_I_y;
                double trace = sum_I_x + sum_I_y;
                R[y][x] =  det - kappa * trace * trace;
            }
        }

        Image response = ImageFactory.doublePrecision().gray(input[0].getSize());
        for (int y = 0; y < input[0].getHeight(); y++) {
            for (int x = 0; x < input[0].getWidth(); x++) {
                response.setValue(x, y, R[y][x]);
            }
        }

        return new Image[] { response };
    }
}
