package ru.eventflow.lcg.parser;

import ru.eventflow.lcg.dto.ParseDTO;
import ru.eventflow.lcg.frame.*;

import java.util.*;

/**
 * A simplistic adaptation of Fowler's LCG parser which uses dynamic programming with bracketing and adjoining subroutines
 * to build all possible planar linkages for a given frame. No graph minimizations are attempted.
 * <p>
 * Planarity is maintained by construction, so no additional checks are required. But we need to deduplicate the result set.
 */
public class ChartLCGParser implements LCGParser {

    private Map<Key, Set<Item>> chart;
    private Frame frame;
    private OutputBuilder outputBuilder = new OutputBuilder();
    private boolean verbose;

    public ChartLCGParser(boolean verbose) {
        this.verbose = verbose;
    }

    public ParseDTO parse(Sequent sequent) {
        this.frame = ProofFrameBuilder.builder().setSequent(sequent).build();
        this.chart = new HashMap<>();

        int size = frame.getAxiomatic().size();

        // prepare length one entries
        for (int i = 0; i < size - 1; i++) {
            Edge edge = attemptLink(i, i + 1);
            if (edge != null) {
                put(new Item(i, i + 1, edge, frame.getLinkage()));
            }
        }

        // going along a diagonal up and to the left
        for (int r = 1; r < size; r++) {
            for (int l = r - 1; l >= 0; l--) {
                Set<Item> items = get(l, r);

                // successful bracketing will fill in a cell (l - 1, r + 1), which is above the diagonal
                Set<Item> addition = new HashSet<>();
                for (Item item : items) {
                    if (item.i > 0 && item.j < size - 1) {
                        Edge edge = attemptLink(item.i - 1, item.j + 1);
                        if (edge != null) {
                            addition.add(new Item(item.i - 1, item.j + 1, edge, Arrays.asList(item)));
                        }
                    }
                }

                for (Item item : addition) {
                    put(item);
                }

                // only attempting to adjoin with the left cell, successful adjoin fills in a cell in the same
                // diagonal two cells higher, so if we go upwards the cell will be complete when we get there

                // things that can be adjoined on the left all lie on a diagonal below the current one
                Set<Item> ls = new HashSet<>();
                for (int m = 0; m < l - 1; m++) { // left index
                    ls.addAll(get(m, l - 1));
                }

                addition = new HashSet<>();
                for (Item item : items) {
                    for (Item left : ls) {
                        addition.add(new Item(left.i, item.j, null, Arrays.asList(left, item)));
                    }
                }

                for (Item item : addition) {
                    put(item);
                }
            }
        }

        Set<Item> results = get(0, size - 1);

        if (verbose) {
            System.out.println("DEBUG: results size = " + results.size());
        }

        // we have spurious ambiguity in the result set, so now we'll need to dedup. I do stupid things here. TODO do not.
        Set<Item> deduped = new TreeSet<>(new Comparator<Item>() {
            // for the purpose of deduplication I build a string of indices of successor vertices in the underlying frame according to the linkage, then compare those strings
            @Override
            public int compare(Item o1, Item o2) {
                String c1 = "";
                String c2 = "";
                for (Vertex vertex : frame.getAxiomatic()) {
                    c1 += o1.linkage.getSuccessors(vertex).stream().map(Vertex::getId).sorted().map(String::valueOf).reduce("|", (s, s2) -> s + " " + s2);
                    c2 += o2.linkage.getSuccessors(vertex).stream().map(Vertex::getId).sorted().map(String::valueOf).reduce("|", (s, s2) -> s + " " + s2);
                }
                return c1.compareTo(c2);
            }
        });
        deduped.addAll(results);
        if (verbose) {
            System.out.println("DEBUG: deduped size = " + deduped.size());
        }

        Set<Linkage> integral = new HashSet<>();
        for (Item item : deduped) {
            Linkage linkage = item.linkage;
            Validator validator = new Validator(linkage, verbose, true);
            if (validator.isLIntegral()) {
                integral.add(linkage);
                if (verbose) {
                    System.out.println("DEBUG: integral");
                }
            }
        }

        return outputBuilder.build(sequent, integral, frame.getAxiomatic());
    }

    private Set<Item> get(int leftIndex, int rightIndex) {
        return chart.getOrDefault(new Key(leftIndex, rightIndex), Collections.emptySet());
    }

    private void put(Item item) {
        Validator validator = new Validator(item.linkage, verbose, false);
        if (validator.isRegularAcyclic()) {
            chart.putIfAbsent(item.getKey(), new HashSet<>());
            chart.get(item.getKey()).add(item);
        }
    }

    private Edge attemptLink(int l, int r) {
        Vertex left = frame.getAxiom(l);
        Vertex right = frame.getAxiom(r);

        if (left.getCategory().equals(right.getCategory()) && left.getPolarity() != right.getPolarity()) {
            // a regular edge is from positive to negative
            if (left.getPolarity() == Polarity.POSITIVE) {
                return new Edge(left, right, Edge.Partition.LINKAGE, Edge.Type.REGULAR);
            } else {
                return new Edge(right, left, Edge.Partition.LINKAGE, Edge.Type.REGULAR);
            }
        } else {
            return null;
        }
    }

    private static class Key {
        private int i;
        private int j;

        Key(int i, int j) {
            this.i = i;
            this.j = j;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Key key = (Key) o;

            if (i != key.i) return false;
            return j == key.j;
        }

        @Override
        public int hashCode() {
            int result = i;
            result = 31 * result + j;
            return result;
        }
    }

    private static class Item {
        int i;
        int j;
        Linkage linkage;

        Item(int i, int j, Edge edge, Linkage linkage) {
            this.i = i;
            this.j = j;
            this.linkage = linkage.copy();
            this.linkage.addEdge(edge.getSource(), edge.getTarget(), Edge.Partition.LINKAGE, Edge.Type.REGULAR);
        }

        Item(int i, int j, Edge edge, List<Item> parents) {
            this.i = i;
            this.j = j;

            linkage = new Linkage();
            for (Item parent : parents) {
                Linkage l = parent.linkage;
                for (Edge e : l.getEdges()) {
                    linkage.addEdge(e.getSource(), e.getTarget(), e.getPartition(), e.getType());
                }
            }

            if (edge != null) {
                this.linkage.addEdge(edge.getSource(), edge.getTarget(), edge.getPartition(), edge.getType());
            }
        }

        Key getKey() {
            return new Key(i, j);
        }

        @Override
        public String toString() {
            return "[" + i + ", " + j + "]";
        }
    }

}
