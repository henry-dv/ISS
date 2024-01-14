import itb2.filter.AbstractFilter;
import itb2.filter.RequireImageType;
import itb2.image.BinaryImage;
import itb2.image.GrayscaleImage;
import itb2.image.Image;
import itb2.image.ImageFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;

@RequireImageType(GrayscaleImage.class)
public class Hu_Momente_HL extends AbstractFilter {

    private static final String PATH = "Speicherpfad";
    private static final String THRESHOLD = "Schwellwert";
    private static final String NAME = "Dateiname";

    public Hu_Momente_HL() {
        properties.addStringProperty(PATH, "Übungsblätter/Blatt11/hu_files/");
        properties.addIntegerProperty(THRESHOLD, 128);
        properties.addStringProperty(NAME, "output");
    }

    private long m(int p, int q, BinaryImage s) {
        long sum = 0;
        for (int j = 0; j < s.getHeight(); j++) {
            for (int i = 0; i < s.getWidth(); i++) {
                if (s.getValue(i, j, 0) == 1) {
                    sum += (long)Math.pow(i, p) * (long)Math.pow(j, q);
                }
            }
        }
        return sum;
    }

    private double[] schwerpunkt(BinaryImage s) {
        double F = m(0, 0, s);
        double i_mu = m(1, 0, s) / F;
        double j_mu = m(0, 1, s) / F;

        return new double[] {i_mu, j_mu};
    }

    private double mu(int p, int q, BinaryImage s) {
        double[] schwerpunkt = schwerpunkt(s);
        double i_mu = schwerpunkt[0];
        double j_mu = schwerpunkt[1];

        double sum = 0;
        for (int j = 0; j < s.getHeight(); j++) {
            for (int i = 0; i < s.getWidth(); i++) {
                if (s.getValue(i, j, 0) == 1) {
                    sum += Math.pow(i - i_mu, p) * Math.pow(j - j_mu, q);
                }
            }
        }
        return sum;
    }

    private double eta(int p, int q, BinaryImage s) {
        double gamma = ((p + q) / 2.0) + 1;

        return mu(p, q, s) / Math.pow(m(0, 0, s), gamma);
    }

    private double[] hu_momente(BinaryImage s) {
        double[] phi = new double[7];

        double eta_20 = eta(2, 0, s);
        double eta_02 = eta(0, 2, s);
        double eta_11 = eta(1, 1, s);
        double eta_30 = eta(3, 0, s);
        double eta_03 = eta(0, 3, s);
        double eta_12 = eta(1, 2, s);
        double eta_21 = eta(2, 1, s);

        phi[0] = eta_20 + eta_02;
        phi[1] = Math.pow(eta_20 - eta_02, 2) + 4 * eta_11 * eta_11;
        phi[2] = Math.pow(eta_30 - 3 * eta_12, 2) + Math.pow(3 * eta_21 - eta_03, 2);
        phi[3] = Math.pow(eta_30 + eta_12, 2) + Math.pow(eta_21 + eta_03, 2);
        phi[4] = (eta_30 - 3 * eta_12) * (eta_30 + eta_12) *
                (Math.pow(eta_30 + eta_12, 2) - 3 * Math.pow(eta_21 - 3 * eta_03, 2))
                + (3 * eta_30 + eta_12) * (eta_21 + eta_03) *
                (3 * Math.pow(eta_30 + eta_12, 2) - Math.pow(eta_21 + eta_03, 2));
        phi[5] = (eta_20 - eta_02) * (Math.pow(eta_30 + eta_12, 2) - Math.pow(eta_21 + eta_03, 2))
                + 4 * eta_11 * (eta_30 + eta_12) * (eta_21 + eta_03);
        phi[6] = (3 * eta_21 - eta_03) * (eta_30 + eta_12) *
                (Math.pow(eta_30 + eta_12, 2) - 3 * Math.pow(eta_21 + eta_03, 2))
                + (3 * eta_21 - eta_03) * (eta_03 + eta_21) *
                (3 * Math.pow(eta_30 + eta_12, 2) - Math.pow(eta_21 + eta_03, 2));

        return phi;
    }

    private double[] hu_normiert(BinaryImage s) {
        double[] phi = hu_momente(s);
        for (int i = 0; i < phi.length; i++) {
            phi[i] = -Math.log(Math.abs(phi[i]));
        }
        return phi;
    }

    private BinaryImage binarize(Image input, int schwellwert) {
        BinaryImage output = ImageFactory.bytePrecision().binary(input.getSize());

        for (int y = 0; y < input.getHeight(); y++) {
            for (int x = 0; x < input.getWidth(); x++) {
                output.setValue(x, y, (input.getValue(x, y, 0) >= schwellwert ? 1 : 0));
            }
        }

        return output;
    }

    private void writeHuFile(String dir, String name, double[] data) {

        Path path = Paths.get(dir, name + ".hu");

        PrintWriter writer;
        try {
            writer = new PrintWriter(new FileWriter(String.valueOf(path)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        writer.println(name);
        for (double datum : data) {
            writer.println(datum);
        }

        writer.close();
    }

    @Override
    public Image filter(Image input) {

        int schwellwert = properties.getIntegerProperty(THRESHOLD);
        String name = properties.getStringProperty(NAME);
        String path = properties.getStringProperty(PATH);

        BinaryImage binary = binarize(input, schwellwert);

        double[] phi = hu_normiert(binary);

        for (double p : phi) {
            System.out.println(p);
        }

        writeHuFile(path, name, phi);

        return binary;

    }

}
