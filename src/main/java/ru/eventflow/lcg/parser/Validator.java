package ru.eventflow.lcg.parser;

import ru.eventflow.lcg.frame.Edge;
import ru.eventflow.lcg.frame.Linkage;
import ru.eventflow.lcg.frame.Polarity;
import ru.eventflow.lcg.frame.Vertex;

import java.util.*;
import java.util.stream.Collectors;

/**
 * L* is Lambek calculus with empty premises
 * L is Lambek calculus without empty premises
 * <p>
 * L is harder, so validity checks for L include checks for L*
 * <p>
 * Definition 3.2.4, p. 60
 */
public class Validator {

    private Linkage linkage;
    private RegularReachabilityDetector reachability;
    private boolean acyclic;
    private boolean verbose;

    public Validator(Linkage linkage, boolean verbose, boolean buildIndex) {
        this.verbose = verbose;
        this.linkage = linkage;
        this.acyclic = isRegularAcyclic();
        if (this.acyclic && buildIndex) {
            this.reachability = new RegularReachabilityDetector(linkage, verbose);
        }
    }

    public boolean isLIntegral() {
        if (!isLStarIntegral()) {
            return false;
        }

        // T(CT)
        // For each Lambek edge (s, t), there exists a negative vertex x such that
        // there is a regular path from s to x and there is no Lambek edge (s2, x) such that
        // there is a regular path from s to s2.
        // IMPORTANT: I had to add to that rule an additional requirement that t is regular reachable from x, which seems
        // compatible with the original LC-graphs definition from (Penn 2004)
        for (Edge s_t : linkage.getLambekEdges()) {
            Vertex s = s_t.getSource();
            Vertex t = s_t.getTarget();

            // check all negative vertices
            for (Vertex x : linkage.getVertices()) {
                if (x.getPolarity() == Polarity.NEGATIVE && x != t && reachability.reachable(s, x) && reachability.reachable(x, t)) {
                    Set<Edge> edges = linkage.getInEdges(x).stream().filter(Edge::isLambek).collect(Collectors.toSet());

                    // here, s can be s2
                    boolean pass = edges.stream().map(Edge::getSource).noneMatch(s2 -> reachability.reachable(s, s2));
                    if (!pass) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    /**
     * p. 60
     */
    public boolean isLStarIntegral() {
        // T(1) G is regular acyclic.
        if (!acyclic) {
            if (verbose) {
                System.out.println("DEBUG: T(1) fail");
            }
            return false;
        }

        // T(2) For all Lambek edges (s, t) in G, there is a regular path from s to t.
        // note that sources of lambek edges are singletons in PTG
        for (Edge l : linkage.getLambekEdges()) {
            Vertex s = l.getSource();
            Vertex t = l.getTarget();

            if (!reachability.reachable(s, t)) {
                if (verbose) {
                    System.out.println("DEBUG: T(2) fail");
                }
                return false;
            }

            // IMPORTANT: this is to rule out regular links within the same complex category, which corresponds
            // to a non-empty antecedent side-condition in the rules of introduction to the succedent of L.
            // This check diverges from the Fowler's definition.
            if (linkage.findEdge(s, t, Edge.Partition.LINKAGE).size() != 0) {
                boolean violation = linkage.getInEdges(s).stream().anyMatch(Edge::isRegular);
                if (violation) {
                    if (verbose) {
                        System.out.println("DEBUG: T(2)-addition fail");
                    }
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Regular acyclic means only regular edges are considered. Acyclicity is checked by means of a topological sort.
     */
    public boolean isRegularAcyclic() {
        Deque<Vertex> stack = new ArrayDeque<>();
        List<Edge> order = new ArrayList<>();
        Set<Edge> visited = new HashSet<>();

        for (Vertex v : linkage.getVertices()) {
            if (linkage.getPredecessors(v, Edge.Type.REGULAR).size() == 0) {
                stack.add(v);
            }
        }

        while (stack.size() > 0) {
            Vertex v = stack.pop();
            for (Edge edge : linkage.getOutEdges(v)) {
                if (edge.getType() == Edge.Type.REGULAR && !visited.contains(edge)) {
                    visited.add(edge);
                    order.add(edge);

                    boolean flag = true;
                    Vertex dest = edge.getTarget();
                    for (Edge in : linkage.getInEdges(dest)) {
                        if (in.getType() == Edge.Type.REGULAR && !visited.contains(in)) {
                            flag = false;
                            break;
                        }
                    }
                    if (flag) {
                        stack.push(dest);
                    }
                }
            }
        }

        return linkage.getRegularEdges().size() == order.size();
    }

}
