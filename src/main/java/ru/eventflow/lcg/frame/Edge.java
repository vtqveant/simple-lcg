package ru.eventflow.lcg.frame;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A hyperedge in term graphs always has a single target vertex and one or more source vertices.
 */
public class Edge {

    private static AtomicInteger counter = new AtomicInteger();

    private final int id = counter.incrementAndGet();
    private final Vertex source;
    private final Vertex target;
    private final Type type;

    /**
     * Designates the part of the proof net where this edge belongs,
     * either the frame (i.e. below the primitive categories, built during the sequent decomposition phase)
     * or the axiomatic linkage (i.e. above the primitive categories, built during the parsing phase)
     */
    private final Partition partition;

    public Edge(Vertex source, Vertex target, Partition partition, Edge.Type type) {
        this.source = source;
        this.target = target;
        this.type = type;
        this.partition = partition;
    }

    public Vertex getSource() {
        return source;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Edge edge = (Edge) o;
        return id == edge.id &&
                Objects.equals(target, edge.target) &&
                type == edge.type &&
                partition == edge.partition;
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
        return "[" + source + ", " + target + "] " + type;
    }

    public enum Partition {FRAME, LINKAGE}

    public enum Type {REGULAR, LAMBEK}
}
