import itb2.filter.AbstractFilter;
import itb2.filter.RequireImageType;
import itb2.image.GrayscaleImage;
import itb2.image.Image;
import itb2.image.ImageFactory;

import java.util.*;

@RequireImageType(GrayscaleImage.class)
public class CCL_HL_YS extends AbstractFilter {

    public CCL_HL_YS() { }

    @Override
    public Image filter(Image input) {

        int[][] labels = new int[input.getHeight()][input.getWidth()];
        int newLabel = 1;
        var labelEquivs = new UnionFind(0);

        for (int y = 0; y < input.getHeight(); y++) {
            for (int x = 0; x < input.getWidth(); x++) {
                if (input.getValue(x, y, 0) != 0) {
                    continue;
                }

                var neighbors = getNeighborLabels(x, y, labels);

                if (neighbors.isEmpty()) {
                    labels[y][x] = newLabel;
                    newLabel++;
                    labelEquivs.makeSet();
                    continue;
                }

                int min = Integer.MAX_VALUE;
                for (int neighbor : neighbors) {
                    if (min != Integer.MAX_VALUE)
                        labelEquivs.union(min, neighbor);
                    min = Math.min(neighbor, min);
                }
                labels[y][x] = min;
            }
        }

        var keys = new HashSet<Integer>();
        for (int i = 1; i < newLabel; i++) {
            keys.add(labelEquivs.find(i));
        }

        Image output = ImageFactory.bytePrecision().hsi(input.getSize());
        var colorMap = makeColorMap(keys);

        for (int y = 0; y < input.getHeight(); y++) {
            for (int x = 0; x < input.getWidth(); x++) {
                if (input.getValue(x, y, 0) == 0) {
                    output.setValue(x, y, colorMap.get(labelEquivs.find(labels[y][x])), 255, 255);
                }
            }
        }

        return output;
    }

    private HashMap<Integer, Integer> makeColorMap(HashSet<Integer> keys) {
        var colorMap = new HashMap<Integer, Integer>(); // maps from region label to chroma
        int i = 0;
        for (int label : keys) {
            int chroma = (i * (255 / keys.size()));
            i++;
            colorMap.put(label, chroma);
        }

        return colorMap;
    }

    private HashSet<Integer> getNeighborLabels(int x, int y, int[][] labels) {
        var neighbors = new HashSet<Integer>();
        if (y > 0) {
            for (int dx = -1; dx <= 1; dx++) {
                int nx = Math.clamp(x + dx, 0, labels[0].length - 1);
                if (labels[y - 1][nx] != 0) {
                    neighbors.add(labels[y - 1][nx]);
                }
            }
        }

        if (x > 0 && labels[y][x - 1] != 0) {
            neighbors.add(labels[y][x - 1]);
        }

        return neighbors;
    }

    private class UnionFind {

        private final ArrayList<Integer> representant;
        private final HashMap<Integer, LinkedList<Integer>> represented;
        private final HashMap<Integer, Integer> size;

        public UnionFind() {
            this(-1);
        }

        public UnionFind(int n) {
            representant = new ArrayList<>();
            represented = new HashMap<>();
            size = new HashMap<>();

            for (int i = 0; i <= n; i++) {
                makeSet();
            }
        }

        public void makeSet() {
            int newKey = representant.size();
            representant.add(newKey);
            represented.put(newKey, new LinkedList<Integer>());
            represented.get(newKey).add(newKey);
            size.put(newKey, 1);
        }

        public int find(int x) {
            if (x < representant.size())
                return representant.get(x);
            else
                return -1;
        }

        public void union(int x, int y) {
            int i = find(x);
            int j = find(y);

            if (i == j) return;

            // i soll Repräsentant der kleineren Menge sein
            if (size.get(i) > size.get(j)) {
                int temp = i;
                i = j;
                j = temp;
            }

            // j soll der neue Repräsentant für alle Elemente von i sein
            for (int z : represented.get(i)) {
                representant.set(z, j);
            }

            // Elemente von i an j anhängen
            var iList = represented.get(i);
            var jList = represented.get(j);
            jList.addAll(iList);
            size.put(j, size.get(j) + size.get(i));
            size.put(i, 0);
            represented.remove(i);
        }

        public int numSets() {
            return represented.size();
        }

    }

}
