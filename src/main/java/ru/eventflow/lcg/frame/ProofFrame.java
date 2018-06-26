package ru.eventflow.lcg.frame;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The proof frame is a representation of the structure of the categories at the periphery of the derivation.
 * <p>
 * s. (Fowler 2016), p. 34
 *
 * In simple terms, it is a graph obtained from an initial sequent decomposition with a total order imposed on
 * the primitive syntactic categories (vertices).
 */
public class ProofFrame implements Frame {

    private Linkage linkage;

    /**
     * the total order O, i.e. leaves of the frame (the periphery)
     */
    private List<Vertex> axiomatic;

    private Map<Vertex, Integer> indices;

    ProofFrame(Linkage linkage, List<Vertex> axiomatic) {
        this.axiomatic = axiomatic;
        this.linkage = linkage;

        this.indices = new HashMap<>();
        for (int i = 0; i < axiomatic.size(); i++) {
            this.indices.put(axiomatic.get(i), i);
        }
    }

    public List<Vertex> getAxiomatic() {
        return axiomatic;
    }

    @Override
    public Vertex getAxiom(int index) {
        return axiomatic.get(index);
    }

    public Linkage getLinkage() {
        return linkage;
    }

    public int getIndex(Vertex vertex) {
        return indices.get(vertex);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Vertex entry : axiomatic) {
            sb.append(entry.toString());
            sb.append(", ");
        }
        return sb.toString();
    }

}
