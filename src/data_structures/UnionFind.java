package data_structures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class UnionFind {

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
