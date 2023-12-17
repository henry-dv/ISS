import itb2.filter.AbstractFilter;
import itb2.filter.RequireImageType;
import itb2.image.*;
import itb2.image.Image;

import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

@RequireImageType(GrayscaleImage.class)
public class Formmerkmale_HL extends AbstractFilter {

    public Formmerkmale_HL() {
        properties.addStringProperty("Name", "");
        properties.addIntegerProperty("Schwellwert", 127);
    }

    @Override
    public Image[] filter(Image[] input) {
        BinaryImage binary = binarisieren(input[0], properties.getIntegerProperty("Schwellwert"));

        String name = properties.getStringProperty("Name");

        int flaeche = flaeche(binary);
        int rand_laenge = randLaenge(binary);
        double kompaktheit = (double)flaeche / (rand_laenge * rand_laenge);
        long[] traegheit = traegheit(binary);
        long traegheit_x = traegheit[0];
        long traegheit_y = traegheit[1];
        long traegheit_gemischt = traegheit[2];

        DrawableImage featureImage = ImageFactory.bytePrecision().drawable(350, 139);
        featureImage.setName(name);
        Graphics g = featureImage.getGraphics();
        g.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
        g.setColor(Color.WHITE);

        g.drawString("Bild: " + name, 14, 25);
        g.drawString("Fläche: " + flaeche, 14,  50);
        g.drawString("Randlänge: " + rand_laenge, 14, 65);
        g.drawString("Kompaktheit: " + kompaktheit, 14, 80);
        g.drawString("Trägheitsmoment in x-Richtung: " + traegheit_x, 14, 95);
        g.drawString("Trägheitsmoment in y-Richtung: " + traegheit_y, 14, 110);
        g.drawString("Gemischtes Trägheitsmoment: " + traegheit_gemischt, 14, 125);

        try {
            PrintWriter writer = new PrintWriter(new FileWriter("./Übungsblätter/Blatt09/" + name + ".feat"));

            writer.println(name);
            writer.println(flaeche);
            writer.println(rand_laenge);
            writer.println(kompaktheit);
            writer.println(traegheit_x);
            writer.println(traegheit_y);
            writer.println(traegheit_gemischt);

            writer.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return new Image[] {binary, featureImage};
    }

    private BinaryImage binarisieren(Image input, int schwellwert) {
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

                if (input.getValue(x, y, 0) == 1) {
                    continue;
                }

                boolean isEdgePixel = false;

                edgePixelCheck:
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dx = -1; dx <= 1; dx++) {
                        if (input.getValue(x + dx, y + dy, 0) == 1) {
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
                if (input.getValue(x, y, 0) == 1) {
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
