import itb2.filter.AbstractFilter;
import itb2.filter.RequireImageType;
import itb2.image.GrayscaleImage;
import itb2.image.Image;
import itb2.image.ImageFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

@RequireImageType(GrayscaleImage.class)
public class Interaktive_Binarisierung_HL_YS extends AbstractFilter {

    public Interaktive_Binarisierung_HL_YS() {
        properties.addOptionProperty("Anzahl Schwellwerte", "1",
                "1", "2", "3", "4", "5", "6", "7", "8", "9");
        properties.addRangeProperty("Schwellwert 1", 120, 0, 1, 255);
        properties.addRangeProperty("Schwellwert 2", 127, 0, 1, 255);
        properties.addRangeProperty("Schwellwert 3", 127, 0, 1, 255);
        properties.addRangeProperty("Schwellwert 4", 127, 0, 1, 255);
        properties.addRangeProperty("Schwellwert 5", 127, 0, 1, 255);
        properties.addRangeProperty("Schwellwert 6", 127, 0, 1, 255);
        properties.addRangeProperty("Schwellwert 7", 127, 0, 1, 255);
        properties.addRangeProperty("Schwellwert 8", 127, 0, 1, 255);
        properties.addRangeProperty("Schwellwert 9", 127, 0, 1, 255);
    }

    @Override
    public Image filter(Image input) {
        ArrayList<Integer> thresholds = getThresholdValues();

        Image output = ImageFactory.bytePrecision().gray(input.getSize());

        for (int y = 0; y < input.getHeight(); y++) {
            for (int x = 0; x < input.getWidth(); x++) {
                output.setValue(x, y, applyThresholds((int)input.getValue(x, y, 0), thresholds));
            }
        }

        return output;
    }

    private int applyThresholds(int value, ArrayList<Integer> thresholds) {

        double numThresholds = thresholds.size();

        // Thresholds sollen Bild in gleich große Intensitätsklassen einteilen
        for (int i = 0; i < thresholds.size(); i++) {
            if (value < thresholds.get(i)) {
                return (int)((i / numThresholds) * 255);
            }
        }

        // Wert größer als alle Thresholds -> maximale Helligkeit
        return 255;
    }

    private ArrayList<Integer> getThresholdValues() {
        var thresholdSet = new HashSet<Integer>(); // hashset, um Duplikate zu vermeiden

        String numThresholds = properties.getOptionProperty("Anzahl Schwellwerte");

        // Schwellwerte werden rückwärts abgearbeitet, damit per fall-through der Rest eingesammelt wird
        switch (numThresholds) {
            case "9":
                thresholdSet.add(properties.getRangeProperty("Schwellwert 9"));
            case "8":
                thresholdSet.add(properties.getRangeProperty("Schwellwert 8"));
            case "7":
                thresholdSet.add(properties.getRangeProperty("Schwellwert 7"));
            case "6":
                thresholdSet.add(properties.getRangeProperty("Schwellwert 6"));
            case "5":
                thresholdSet.add(properties.getRangeProperty("Schwellwert 5"));
            case "4":
                thresholdSet.add(properties.getRangeProperty("Schwellwert 4"));
            case "3":
                thresholdSet.add(properties.getRangeProperty("Schwellwert 3"));
            case "2":
                thresholdSet.add(properties.getRangeProperty("Schwellwert 2"));
            case "1":
                thresholdSet.add(properties.getRangeProperty("Schwellwert 1"));
                break;
        }

        var thresholdList = new ArrayList<Integer>(thresholdSet);
        Collections.sort(thresholdList);

        return thresholdList;
    }

}
