import itb2.filter.AbstractFilter;
import itb2.image.Image;
import itb2.image.ImageFactory;

public class GaussianPyramid_HL extends AbstractFilter {

    public GaussianPyramid_HL() {
        properties.addDoubleProperty("Standardabweichung", 2.0);
        properties.addOptionProperty("Modus", "expand", "expand", "reduce");
        properties.addRangeProperty("Levels", 1, 1, 1, 5);
    }

    private Image toGrayscale(Image colorImage) {
        Image grayImage = ImageFactory.doublePrecision().gray(colorImage.getSize());

        for (int y = 0; y < colorImage.getHeight(); y++) {
            for (int x = 0; x < colorImage.getWidth(); x++) {
                double value = 0;
                for (int i = 0; i < colorImage.getChannelCount(); i++) {
                    value += colorImage.getValue(x, y, i);
                }
                value /= colorImage.getChannelCount();
                grayImage.setValue(x, y, value);
            }
        }

        return grayImage;
    }

    private double[] gaussFilter(double sigma) {
        int m = 2 * (int)Math.ceil(3 * sigma) + 1;

        // generate 1-dimensional gaussian filter
        double[] gaussFilter = new double[m];
        double sum = 0;
        for (int i = 0; i < m; i++) {
            int u = i - (m / 2);
            gaussFilter[i] = Math.pow(Math.E, -(u * u)/(2 * sigma * sigma)) / Math.sqrt(2 * Math.PI * sigma * sigma);
            sum += gaussFilter[i];
        }
        for (int i = 0; i < m; i++) {
            gaussFilter[i] /= sum;
        }

        return gaussFilter;
    }

    private Image gaussianBlur(Image input) {
        double sigma = properties.getDoubleProperty("Standardabweichung");
        double[] gaussFilter = gaussFilter(sigma);
        int m = gaussFilter.length;

        // apply filter along x-axis
        Image output = ImageFactory.doublePrecision().gray(input.getSize());
        for (int y = 0; y < output.getHeight(); y++) {
            for (int x = 0; x < output.getWidth(); x++) {
                double value = 0;
                for (int i = 0; i < m; i++) {
                    int dx = i - (m / 2);
                    value += input.getValue(Math.clamp(x + dx, 0, output.getWidth() - 1), y, 0) * gaussFilter[i];
                }
                output.setValue(x, y, value);
            }
        }

        // apply filter along y-axis
        input = output;
        for (int x = 0; x < output.getWidth(); x++) {
            for (int y = 0; y < output.getHeight(); y++) {
                double value = 0;
                for (int i = 0; i < m; i++) {
                    int dy = i - (m / 2);
                    value += input.getValue(x, Math.clamp(y + dy, 0, output.getHeight() - 1), 0) * gaussFilter[i];
                }
                output.setValue(x, y, value);
            }
        }

        return output;
    }

    private Image reduce(Image input) {
        input = gaussianBlur(input);
        Image reduced = ImageFactory.doublePrecision().gray(input.getWidth() / 2, input.getHeight() / 2);
        for (int y = 0; y < reduced.getHeight(); y++) {
            for (int x = 0; x < reduced.getWidth(); x++) {
                reduced.setValue(x, y, input.getValue(x * 2, y * 2, 0));
            }
        }
        return reduced;
    }

    private Image expand(Image input) {
        Image expanded = ImageFactory.doublePrecision().gray(input.getWidth() * 2, input.getHeight() * 2);
        double sigma = properties.getDoubleProperty("Standardabweichung");
        double[] gaussFilter = gaussFilter(sigma);
        int m = gaussFilter.length;

        for (int y = 0; y < expanded.getHeight(); y++) {
            for (int x = 0; x < expanded.getWidth(); x++) {
                double value = 0;
                for (int i = 0; i < m; i++) {
                    int dx = i - (m / 2);
                    for (int j = 0; j < m; j++) {
                        int dy = j - (m / 2);
                        value += input.getValue(Math.clamp((x + dx) / 2, 0, input.getWidth() - 1),
                                                Math.clamp((y + dy) / 2, 0, input.getHeight() - 1), 0)
                                * gaussFilter[i] * gaussFilter[j];
                    }
                }
                expanded.setValue(x, y, value);
            }
        }

        return expanded;
    }

    @Override
    public Image[] filter(Image[] input) {
        Image root = input[0].getChannelCount() > 1 ? toGrayscale(input[0]) : input[0];

        Image[] pyramid = new Image[properties.getRangeProperty("Levels") + 1];
        pyramid[0] = root;

        if (properties.getOptionProperty("Modus").equals("expand")) {
            for (int i = 1; i < pyramid.length; i++) {
                pyramid[i] = expand(pyramid[i - 1]);
            }
        }
        else {
            for (int i = 1; i < pyramid.length; i++) {
                pyramid[i] = reduce(pyramid[i - 1]);
            }
        }

        return pyramid;
    }

}
