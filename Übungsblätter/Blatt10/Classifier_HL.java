import itb2.filter.AbstractFilter;
import itb2.image.BinaryImage;
import itb2.image.Image;
import itb2.image.ImageFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Classifier_HL extends AbstractFilter {

    static final String PATH = "Directory Path";
    static final HashMap<String, Double[]> colorMap = new HashMap<String, Double[]>() {{
        put("Allen_Key", new Double[] {255., 255., 0.});
        put("Bit", new Double[] {255., 0., 0.});
        put("Floppy", new Double[] {0., 255., 0.});
        put("Hammer", new Double[] {0., 0., 255.});
        put("Sliding_Caliper", new Double[] {0., 0., 0.});
    }};

    public Classifier_HL() {
        properties.addStringProperty(PATH, "Übungsblätter/Blatt10/feat_files/");
    }

    @Override
    public Image[] filter(Image[] input) {

        String path = properties.getStringProperty(PATH);
        var output = new ArrayList<Image>();

        var featureMap = readFeatFiles(path);

        for (Image image : input) {

            var binary =  toBinary(image, 126);

            Double[] imageFeatures = features(binary);

            String bestMatch = "";
            double bestSim = 0;

            for (String featureKey : featureMap.keySet()) {
                System.out.println("\n--- " + featureKey);
                double sim = similarity(featureMap.get(featureKey), imageFeatures);
                System.out.println("Similarity: " + sim);
                if (sim >= bestSim) {
                    bestSim = sim;
                    bestMatch = featureKey;
                }
            }

            Image coloredImage = ImageFactory.bytePrecision().rgb(image.getSize());
            System.out.println("\nBest match: " + bestMatch);
            System.out.println("sim = " + bestSim);
            Double[] color = colorMap.get(bestMatch);
            for (int y = 0; y < image.getHeight(); y++) {
                for (int x = 0; x < image.getWidth(); x++) {
                    if (binary.getValue(x, y, 0) == 0) {
                        coloredImage.setValue(x, y, color[0], color[1], color[2]);
                    }
                    else {
                        coloredImage.setValue(x, y, 255., 255., 255.);
                    }
                }
            }

            output.add(coloredImage);

        }

        return output.toArray(new Image[0]);
    }

    private BinaryImage toBinary(Image input, int schwellwert) {
        BinaryImage output = ImageFactory.bytePrecision().binary(input.getSize());

        for (int y = 0; y < input.getHeight(); y++) {
            for (int x = 0; x < input.getWidth(); x++) {
                if (input.getValue(x, y, 0) >= schwellwert) {
                    output.setValue(x, y, 1);
                }
            }
        }

        return output;
    }

    private double similarity(Double[] modelFeatures, Double[] imageFeatures) {
       double sum = 0;
       for (int i = 0; i < modelFeatures.length; i++) {
           System.out.println(modelFeatures[i] + " vs. " + imageFeatures[i]);
           sum += Math.abs(modelFeatures[i] - imageFeatures[i]) / Math.abs(modelFeatures[i]);
       }

       sum /= modelFeatures.length;

       return Math.max(0, 1 - sum);
    }

    private Double[] features(BinaryImage image) {
        var features = new Double[6];
        features[0] = (double)flaeche(image);
        features[1] = (double)randLaenge(image);
        features[2] = features[0] / (features[1] * features[1]);
        long[] traegheit = traegheit(image);
        features[3] = (double)traegheit[0];
        features[4] = (double)traegheit[1];
        features[5] = (double)traegheit[2];

        return features;
    }

    private HashMap<String, Double[]> readFeatFiles(String folderPath) {

        var map = new HashMap<String, Double[]>();
        final File folder = new File(folderPath);

        for (final File fileEntry : folder.listFiles()) {
            if (!fileEntry.isFile() || !fileEntry.getName().contains(".feat")) {
                continue;
            }

            try (var reader = new BufferedReader(new FileReader(fileEntry))) {

                String key = reader.readLine();

                var featureList = new ArrayList<Double>();
                String feature = reader.readLine();
                while (feature != null) {
                    featureList.add(Double.parseDouble(feature));
                    feature = reader.readLine();
                }

                map.put(key, featureList.toArray(new Double[0]));

            }
            catch(IOException ioe) {
                System.err.println("on no");
                ioe.printStackTrace();
                System.exit(-1);
            }
        }

        return map;

    }

    private int flaeche(BinaryImage input) {

        int area = 0;

        for (int y = 0; y < input.getHeight(); y++) {
            for (int x = 0; x < input.getWidth(); x++) {
                if (input.getValue(x, y, 0) == 0) {
                    area++;
                }
            }
        }

        return area;
    }

    private int randLaenge(BinaryImage input) {

        int length = 0;

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
                if (isEdgePixel) {
                    length++;
                }
            }
        }

        return length;
    }

    private long[] traegheit(BinaryImage input) {
        int[] mu = schwerpunkt(input);
        int x_mu = mu[0];
        int y_mu = mu[1];

        long m_x = 0, m_y = 0, m_xy = 0;

        for (int y = 1; y < input.getHeight() - 1; y++) {
            for (int x = 1; x < input.getWidth() - 1; x++) {
                if (input.getValue(x, y, 0) != 0) {
                    continue;
                }

                long delta_x = x - x_mu;
                long delta_y = y - y_mu;

                m_x += delta_x * delta_x;
                m_y += delta_y * delta_y;
                m_xy += delta_x * delta_y;
            }
        }

        return new long[] {m_x, m_y, m_xy};
    }

    private int[] schwerpunkt(BinaryImage input) {

        long sum_x = 0, sum_y = 0;

        for (int y = 1; y < input.getHeight() - 1; y++) {
            for (int x = 1; x < input.getWidth() - 1; x++) {
                if (input.getValue(x, y, 0) == 0) {
                    sum_x += x;
                    sum_y += y;
                }
            }
        }

        int area = flaeche(input);
        int x_mu = (int)((1.0 / area) * sum_x);
        int y_mu = (int)((1.0 / area) * sum_y);

        return new int[] {x_mu, y_mu};
    }

}
