package ru.eventflow.lcg.frame;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A hyperedge in term graphs always has a single target vertex and one or more source vertices.
 */
public class Hyperedge {

    private static AtomicInteger counter = new AtomicInteger();

    private final int id = counter.incrementAndGet();
    private final Set<Vertex> sources;
    private final Vertex target;
    private final Type type;

    /**
     * Designates the part of the proof net where this edge belongs,
     * either the frame (i.e. below the primitive categories, built during the sequent decomposition phase)
     * or the axiomatic linkage (i.e. above the primitive categories, built during the parsing phase)
     */
    private final Partition partition;

    public Hyperedge(Vertex source, Vertex target, Partition partition) {
        this.sources = Collections.singleton(source);
        this.target = target;
        this.type = Type.REGULAR;
        this.partition = partition;
    }

    public Hyperedge(Set<Vertex> sources, Vertex target, Partition partition) {
        this.sources = new HashSet<>(sources);
        this.target = target;
        this.type = Type.LAMBEK;
        this.partition = partition;
    }

    public Vertex getSource() {
        if (isLambek() && sources.size() != 1) {
            throw new IllegalStateException("The source set of a Lambek edge is not a singleton.");
        } else {
            return sources.iterator().next();
        }
    }

    public Set<Vertex> getSourceSet() {
        return sources;
    }

    public boolean isRegular() {
        return type == Type.REGULAR;
    }

    public boolean isLambek() {
        return type == Type.LAMBEK;
    }

    public Vertex getTarget() {
        return target;
    }

    public Partition getPartition() {
        return partition;
    }

    public int getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Hyperedge hyperedge = (Hyperedge) o;
        return id == hyperedge.id &&
                Objects.equals(target, hyperedge.target) &&
                type == hyperedge.type &&
                partition == hyperedge.partition;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, target, type, partition);
    }

    public Type getType() {
        return type;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[{");
        List<Vertex> vs = new ArrayList<>(sources);
        for (int i = 0; i < sources.size(); i++) {
            sb.append(vs.get(i).toString());
            if (i < sources.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append("}, ");
        sb.append(target.toString());
        sb.append("] ");
        sb.append(type);
        return sb.toString();
    }

    public enum Partition {FRAME, LINKAGE}

    public enum Type {REGULAR, LAMBEK}
}
