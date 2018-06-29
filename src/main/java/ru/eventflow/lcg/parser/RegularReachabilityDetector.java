package ru.eventflow.lcg.parser;

import ru.eventflow.lcg.frame.Edge;
import ru.eventflow.lcg.frame.Linkage;
import ru.eventflow.lcg.frame.Vertex;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Only works for DAGs.
 */
public class RegularReachabilityDetector {

    private Linkage linkage;
    private Map<Vertex, Set<Vertex>> index;
    private Set<Vertex> marked;

    public RegularReachabilityDetector(Linkage linkage, boolean verbose) {
        this.linkage = linkage;
        this.index = new HashMap<>();
        this.marked = new HashSet<>();

        buildIndex();

        if (verbose) {
            dumpIndex();
        }
    }

    private void buildIndex() {
        for (Vertex v : linkage.getVertices()) {
            processVertex(v, new HashSet<>());
        }
    }

    public boolean reachable(Vertex v1, Vertex v2) {
        return linkage.containsVertex(v1) && linkage.containsVertex(v2) && index.get(v1).contains(v2);
    }

    private void processVertex(Vertex v, Set<Vertex> visited) {
        Set<Vertex> copy = new HashSet<>(visited);
        copy.add(v);

        // more vertices to check?
        if (!marked.contains(v)) {
            marked.add(v);
            index.putIfAbsent(v, new HashSet<>());
            index.get(v).add(v);

            for (Edge outEdge : linkage.getOutEdges(v).stream().filter(Edge::isRegular).collect(Collectors.toSet())) {
                Vertex successor = outEdge.getTarget();

                processVertex(successor, copy);
                index.get(v).add(successor);
                if (index.containsKey(successor)) {
                    index.get(v).addAll(index.get(successor));
                }
            }
        }
    }

    public void dumpIndex() {
        StringBuilder sb = new StringBuilder();

        List<Vertex> keys = new ArrayList<>(index.keySet());
        keys.sort(Comparator.comparingInt(Vertex::getId));

        for (Vertex key : keys) {
            sb.append(key);
            sb.append(": ");

            List<Vertex> vs = new ArrayList<>(index.get(key));
            vs.sort(Comparator.comparingInt(Vertex::getId));
            for (Vertex v : vs) {
                sb.append(v);
                sb.append(", ");
            }
            sb.append("\n");
        }
        System.out.println(sb.toString());
    }

}
