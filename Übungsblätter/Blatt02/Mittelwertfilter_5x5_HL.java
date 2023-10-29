public class Mittelwertfilter_5x5_HL extends ConvolutionFilter_HL {

    @Override
    public double[][] getKernel() {
        return new double[][]{
                {1d/25, 1d/25, 1d/25, 1d/25, 1d/25},
                {1d/25, 1d/25, 1d/25, 1d/25, 1d/25},
                {1d/25, 1d/25, 1d/25, 1d/25, 1d/25},
                {1d/25, 1d/25, 1d/25, 1d/25, 1d/25},
                {1d/25, 1d/25, 1d/25, 1d/25, 1d/25}
        };
    }

}
