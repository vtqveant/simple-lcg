package ru.eventflow.lcg.parser;

import ru.eventflow.lcg.category.PrimitiveCategory;
import ru.eventflow.lcg.dto.ParseDTO;
import ru.eventflow.lcg.frame.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A brute force (exponential time) parser for LCG.
 * <p>
 * Produces a set of complete proof frames.
 * This implementation was used to validate the results of the chart-based parser.
 */
public class ExponentialLCGParser implements LCGParser {

    private OutputBuilder outputBuilder = new OutputBuilder();

    @Override
    public ParseDTO parse(Sequent sequent) {
        Set<Linkage> complete = new HashSet<>();
        Frame frame = ProofFrameBuilder.builder().setSequent(sequent).build();

        int size = frame.getAxiomatic().size();

        // heuristic 1: if the number of primitive categories in the frame is odd or zero, there is no linkage
        if (size == 0 || size % 2 == 1) {
            return outputBuilder.build(sequent, Collections.emptySet(), frame.getAxiomatic());
        }

        // heuristic 2: if there's a single primitive category in the succedent (e.g. S), and the antecedent does not
        // contain the same with an opposite polarity, there is no linkage
        if (sequent.getSuccedent().size() == 1 && sequent.getSuccedent().get(0) instanceof PrimitiveCategory) {
            List<Vertex> order = frame.getAxiomatic();

            Vertex succedent = order.get(size - 1);

            boolean match = false;
            for (int i = 0; i < size - 1; i++) {
                Vertex vertex = order.get(i);
                if (vertex.getCategory().getSymbol().equals(succedent.getCategory().getSymbol()) && vertex.getPolarity() != succedent.getPolarity()) {
                    match = true;
                    break;
                }
            }
            if (!match) {
                System.out.println("DEBUG: H2");
                return outputBuilder.build(sequent, Collections.emptySet(), frame.getAxiomatic());
            }
        }

        Set<Linkage> incomplete = new HashSet<>();
        incomplete.add(new Linkage());

        for (int l = 0; l < size; l++) {
            Set<Linkage> attempts = new HashSet<>(incomplete);

            for (Linkage linkage : incomplete) {
                for (int r = l + 1; r < size; r++) {
                    Linkage attempt = attemptLink(frame, linkage, l, r);
                    if (attempt != null) {
                        if (complete(frame, attempt)) {
                            complete.add(attempt);
                        } else {
                            attempts.add(attempt);
                        }
                    }
                }
            }

            incomplete = attempts;

            // heuristic 3: if the number of atoms without a link to the right of l is less than
            // the number of non-connected atoms to the left of l, there is no way this linkage can be
            // completed, so we can throw it away immediately
            int i = l + 1;
            incomplete.removeIf(linkage -> dead(frame, linkage, i, size));

//            System.out.println("DEBUG: incomplete size = " + incomplete.size());
        }

        // add edges from the frame and validate
        for (Linkage linkage : complete) {
            for (Edge edge : frame.getLinkage().getEdges()) {
                linkage.addEdge(edge.getSource(), edge.getTarget(), edge.getPartition(), edge.getType());
            }
        }

        System.out.println("DEBUG: complete size = " + complete.size());

        Set<Linkage> integral = new HashSet<>();

        for (Linkage linkage : complete) {
            Validator validator = new Validator(linkage, true, true);
            if (validator.isLIntegral()) {
                System.out.println("DEBUG: integral");
                integral.add(linkage);
            }
        }

        return outputBuilder.build(sequent, integral, frame.getAxiomatic());
    }

    private boolean dead(Frame frame, Linkage linkage, int l, int size) {
        int left = 0;
        int right = 0;
        for (int i = 0; i < size; i++) {
            Vertex v = frame.getAxiom(i);
            if (linkage.getInEdges(v).size() + linkage.getOutEdges(v).size() == 0) {
                if (i < l) {
                    left++;
                } else {
                    right++;
                }
            }
        }
        return left > right;
    }

    private boolean complete(Frame frame, Linkage linkage) {
        return linkage.getEdges().size() * 2 == frame.getAxiomatic().size();
    }

    private Linkage attemptLink(Frame frame, Linkage linkage, int l, int r) {
        Linkage copy = linkage.copy();
        Vertex left = frame.getAxiom(l);
        Vertex right = frame.getAxiom(r);

        // already attempted
        if (linkage.getInEdges(left).size() + linkage.getOutEdges(left).size() > 0 || linkage.getInEdges(right).size() + linkage.getOutEdges(right).size() > 0) {
            return null;
        }

        if (left.getCategory().equals(right.getCategory()) && left.getPolarity() != right.getPolarity()) {
            // check planarity
            for (int i = l + 1; i < r; i++) {
                Vertex internal = frame.getAxiom(i);
                Set<Vertex> incident = new HashSet<>();
                incident.addAll(linkage.getPredecessors(internal));
                incident.addAll(linkage.getSuccessors(internal));
                for (Vertex v : incident) {
                    int index = frame.getIndex(v);
                    if (index < l || index > r) {
                        return null;
                    }
                }
            }

            // a regular edge is from positive to negative
            if (left.getPolarity() == Polarity.POSITIVE) {
                copy.addEdge(left, right, Edge.Partition.LINKAGE, Edge.Type.REGULAR);
            } else {
                copy.addEdge(right, left, Edge.Partition.LINKAGE, Edge.Type.REGULAR);
            }
            return copy;
        } else {
            return null;
        }
    }

}
