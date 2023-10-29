public class Mittelwertfilter_3x3_HL extends ConvolutionFilter_HL {

    @Override
    public double[][] getKernel() {
        return new double[][] {
                {1d/9, 1d/9, 1d/9},
                {1d/9, 1d/9, 1d/9},
                {1d/9, 1d/9, 1d/9}
        };
    }
}
