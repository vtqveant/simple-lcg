package ru.eventflow.lcg.frame;

import ru.eventflow.lcg.category.Category;
import ru.eventflow.lcg.category.ComplexCategory;
import ru.eventflow.lcg.category.Connective;
import ru.eventflow.lcg.category.PrimitiveCategory;
import ru.eventflow.lcg.parser.Sequent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * According to (Fowler, 2016).
 * <p>
 * -->  regular edge
 * ~~>  Lambek edge
 * <p>
 * A/B (-)   =>  A- --> B+
 * A\B (-)   =>  B+ <-- A-
 * A/B (+)   =>  B- <~~ A+
 * A\B (+)   =>  A+ ~~> B-
 */
public class ProofFrameBuilder {

    private Sequent sequent;

    private ProofFrameBuilder() {
    }

    public static synchronized ProofFrameBuilder builder() {
        return new ProofFrameBuilder();
    }

    public ProofFrameBuilder setSequent(Sequent sequent) {
        this.sequent = sequent;
        return this;
    }

    public ProofFrame build() {
        if (sequent == null) {
            throw new IllegalStateException("Sequent not set.");
        }
        if (sequent.getSuccedent().size() != 1) {
            throw new IllegalStateException("Multiple categories in succedent are not supported.");
        }

        List<Vertex> list = new ArrayList<>();
        Linkage linkage = new Linkage();

        Category succedent = sequent.getSuccedent().get(0);
        Vertex s = new Vertex(succedent, Polarity.POSITIVE);
        process(s, list, linkage);
        int succedentSize = list.size();

        for (Category category : sequent.getAntecedent()) {
            Vertex a = new Vertex(category, Polarity.NEGATIVE);
            process(a, list, linkage);
        }

        // move the atoms from the succedent to the end of the list
        for (int i = 0; i < succedentSize; i++) {
            Collections.rotate(list, -1);
        }

        return new ProofFrame(linkage, list);
    }

    private void process(Vertex vertex, List<Vertex> list, Linkage linkage) {
        Category category = vertex.getCategory();
        if (category instanceof PrimitiveCategory) {
            list.add(vertex);
            if (!linkage.containsVertex(vertex)) {
                linkage.addVertex(vertex);
            }
        } else {
            ComplexCategory c = (ComplexCategory) category;
            if (vertex.getPolarity() == Polarity.NEGATIVE) {
                if (c.getConnective() == Connective.RIGHT_DIVISION) {           //   A/B (-)   =>  A- --> B+
                    Vertex a = new Vertex(c.getLeft(), Polarity.NEGATIVE);
                    Vertex b = new Vertex(c.getRight(), Polarity.POSITIVE);
                    processEdges(linkage, vertex, a, b, Edge.Type.REGULAR);
                    process(a, list, linkage);
                    process(b, list, linkage);
                } else if (c.getConnective() == Connective.LEFT_DIVISION) {    //    A\B (-)   =>  B+ <-- A-
                    Vertex a = new Vertex(c.getLeft(), Polarity.NEGATIVE);
                    Vertex b = new Vertex(c.getRight(), Polarity.POSITIVE);
                    processEdges(linkage, vertex, a, b, Edge.Type.REGULAR);
                    process(b, list, linkage);
                    process(a, list, linkage);
                } else {
                    throw new IllegalStateException("Multiplicative fragment of L is not supported");
                }
            } else {
                if (c.getConnective() == Connective.RIGHT_DIVISION) {          //    A/B (+)   =>  B- <~~ A+
                    Vertex a = new Vertex(c.getLeft(), Polarity.POSITIVE);
                    Vertex b = new Vertex(c.getRight(), Polarity.NEGATIVE);
                    processEdges(linkage, vertex, a, b, Edge.Type.LAMBEK);
                    process(b, list, linkage);
                    process(a, list, linkage);
                } else if (c.getConnective() == Connective.LEFT_DIVISION) {    //    A\B (+)   =>  A+ ~~> B-
                    Vertex a = new Vertex(c.getLeft(), Polarity.POSITIVE);
                    Vertex b = new Vertex(c.getRight(), Polarity.NEGATIVE);
                    processEdges(linkage, vertex, a, b, Edge.Type.LAMBEK);
                    process(a, list, linkage);
                    process(b, list, linkage);
                } else {
                    throw new IllegalStateException("Multiplicative fragment of L is not supported");
                }
            }

            linkage.removeVertex(vertex);
        }
    }

    private void processEdges(Linkage linkage, Vertex parent, Vertex a, Vertex b, Edge.Type type) {
        linkage.addVertex(a);
        linkage.addVertex(b);
        linkage.addEdge(a, b, Edge.Partition.FRAME, type);
        reattach(linkage, parent, a);
    }

    /**
     * The neighbourhood of the polarized category on the lhs of each rule is assigned to A
     */
    private void reattach(Linkage linkage, Vertex vertex, Vertex a) {
        List<Edge> removal = new ArrayList<>();

        for (Edge e : linkage.getInEdges(vertex)) {
            removal.add(e);
            linkage.addEdge(e.getSource(), a, Edge.Partition.FRAME, e.getType());
        }

        for (Edge e : linkage.getOutEdges(vertex)) {
            removal.add(e);
            linkage.addEdge(a, e.getTarget(), Edge.Partition.FRAME, e.getType());
        }

        for (Edge e : removal) {
            linkage.removeEdge(e);
        }
    }

}
