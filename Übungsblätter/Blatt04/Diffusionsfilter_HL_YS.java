import itb2.filter.AbstractFilter;
import itb2.filter.RequireImageType;
import itb2.image.GrayscaleImage;
import itb2.image.Image;
import itb2.image.ImageFactory;

@RequireImageType(GrayscaleImage.class)
public class Diffusionsfilter_HL_YS extends AbstractFilter {

    public Diffusionsfilter_HL_YS() {
        properties.addIntegerProperty("Iterationen", 500);
        properties.addDoubleProperty("e0", 1);
        properties.addDoubleProperty("lambda", 1.75);
    }

    @Override
    public Image filter(Image input) {

        int iterations = properties.getIntegerProperty("Iterationen");
        double[][] oldIteration = imageToArray(input);

        for (int i = 0; i < iterations; i++) {

            double[][] newIteration = new double[input.getHeight()][input.getWidth()];
            double[][][] flow = calculateFlow(oldIteration);

            for (int y = 0; y < input.getHeight(); y++) {
                for (int x = 0; x < input.getWidth(); x++) {
                    double flowGradX = flow[0][y][Math.min(x+1, input.getWidth()-1)] - flow[0][y][Math.max(x-1, 0)];
                    double flowGradY = flow[1][Math.min(y+1, input.getHeight()-1)][x] - flow[1][Math.max(y-1, 0)][x];

                    newIteration[y][x] = oldIteration[y][x] - flowGradX - flowGradY;
                }
            }

            oldIteration = newIteration;
        }

        return arrayToImage(oldIteration);
    }

    private double[][][] calculateFlow(double[][] input) {
        double e0 = properties.getDoubleProperty("e0");
        double lambda = properties.getDoubleProperty("lambda");

        int sizeY = input.length;
        int sizeX = input[0].length;
        double[][][] flow = new double[2][sizeY][sizeX];

        for (int y = 0; y < sizeY; y++) {
            for (int x = 0; x < sizeX; x++) {
                double gradX = input[y][Math.min(x+1, sizeX-1)] - input[y][Math.max(x-1, 0)];
                double gradY = input[Math.min(y+1, sizeY-1)][x] - input[Math.max(y-1, 0)][x];

                double gradNormSquared = gradX * gradX + gradY * gradY;
                double diffusion = e0 * lambda * lambda / (gradNormSquared + lambda * lambda);

                flow[0][y][x] = -diffusion * gradX;
                flow[1][y][x] = -diffusion * gradY;
            }
        }

        return flow;
    }

    private double[][] imageToArray(Image image) {
        double[][] array = new double[image.getHeight()][image.getWidth()];
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                array[y][x] = image.getValue(x, y, 0);
            }
        }
        return array;
    }

    private Image arrayToImage(double[][] array) {
        Image image = ImageFactory.doublePrecision().gray(array[0].length, array.length);
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                image.setValue(x, y, array[y][x]);
            }
        }
        return image;
    }

}
