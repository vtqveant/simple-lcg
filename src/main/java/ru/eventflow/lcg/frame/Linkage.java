package ru.eventflow.lcg.frame;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A (Partial) Term Graph is part of a proof net.
 * It represents a linkage between atoms and sibling nodes contained in a frame.
 */
public class Linkage {

    private Set<Vertex> vertices;
    private Set<Hyperedge> edges;

    public Linkage() {
        vertices = new HashSet<>();
        edges = new HashSet<>();
    }

    public void addLambekEdge(Set<Vertex> from, Vertex to, Hyperedge.Partition partition) {
        if (findEdge(from, to, partition).size() == 0) {
            vertices.add(to);
            vertices.addAll(from);
            edges.add(new Hyperedge(new HashSet<>(from), to, partition));
        }
    }

    public void addRegularEdge(Vertex from, Vertex to, Hyperedge.Partition partition) {
        if (findEdge(from, to, partition).size() == 0) {
            vertices.add(to);
            vertices.add(from);
            edges.add(new Hyperedge(from, to, partition));
        }
    }

    public void addVertex(Vertex vertex) {
        vertices.add(vertex);
    }

    public Set<Hyperedge> getRegularEdges() {
        return edges.stream().filter(Hyperedge::isRegular).collect(Collectors.toSet());
    }

    public Set<Hyperedge> getLambekEdges() {
        return edges.stream().filter(Hyperedge::isLambek).collect(Collectors.toSet());
    }

    public boolean containsVertex(Vertex vertex) {
        return vertices.contains(vertex);
    }

    public void removeVertex(Vertex vertex) {
        vertices.remove(vertex);
        removeFromLambekSources(vertex);
    }

    public void removeFromLambekSources(Vertex vertex) {
        for (Hyperedge edge : edges) {
            if (edge.isLambek()) {
                edge.getSourceSet().remove(vertex);
            }
        }

        // simply prune edges with empty sources
        edges.removeIf(e -> e.getSourceSet().size() == 0);
    }

    public void removeRegularEdge(Vertex from, Vertex to) {
        edges.removeIf(e -> e.isRegular() && e.getSource().equals(from) && e.getTarget().equals(to));
    }

    public void removeEdge(Hyperedge edge) {
        edges.remove(edge);
    }

    public Set<Hyperedge> getInEdges(Vertex vertex) {
        return edges.stream().filter(e -> e.getTarget().equals(vertex)).collect(Collectors.toSet());
    }

    /**
     * Given a PTG G and an atom a ∈ V(G), a is open iff a is positive and has no regular out-edge or a is negative and has no regular in-edge.
     */
    public boolean isOpen(Vertex vertex) {
        if (vertex.getType() != Vertex.Type.ATOM) {
            return false;
        }
        if (vertex.getPolarity() == Polarity.POSITIVE) {
            return getOutEdges(vertex).stream().noneMatch(Hyperedge::isRegular);
        } else {
            return getInEdges(vertex).stream().noneMatch(Hyperedge::isRegular);
        }
    }

    public Set<Hyperedge> getOutEdges(Vertex vertex) {
        return edges.stream().filter(e -> e.getSourceSet().contains(vertex)).collect(Collectors.toSet());
    }

    public Set<Vertex> getPredecessors(Vertex vertex, Hyperedge.Type edgeType) {
        Set<Vertex> predecessors = new HashSet<>();
        for (Hyperedge edge : getInEdges(vertex)) {
            if (edge.getType() == edgeType) {
                predecessors.addAll(edge.getSourceSet());
            }
        }
        return predecessors;
    }

    public Set<Vertex> getPredecessors(Vertex vertex) {
        Set<Vertex> predecessors = new HashSet<>();
        for (Hyperedge edge : getInEdges(vertex)) {
            predecessors.addAll(edge.getSourceSet());
        }
        return predecessors;
    }

    public Collection<Vertex> getSuccessors(Vertex vertex) {
        Set<Vertex> successors = new HashSet<>();
        for (Hyperedge edge : getOutEdges(vertex)) {
            successors.add(edge.getTarget());
        }
        return successors;
    }

    public int getVertexCount() {
        return vertices.size();
    }

    public Set<Vertex> getVertices() {
        return vertices;
    }

    public Set<Hyperedge> getEdges() {
        return edges;
    }

    public Set<Hyperedge> findEdge(Vertex from, Vertex to, Hyperedge.Partition partition) {
        return edges.stream().filter(e ->
                e.getPartition() == partition &&
                        e.getSourceSet().contains(from) &&
                        e.getTarget().equals(to)
        ).collect(Collectors.toSet());
    }

    public Set<Hyperedge> findEdge(Set<Vertex> from, Vertex to, Hyperedge.Partition partition) {
        return edges.stream().filter(e ->
                e.getPartition() == partition &&
                        e.getSourceSet().containsAll(from) &&
                        e.getSourceSet().size() == from.size() &&
                        e.getTarget().equals(to)
        ).collect(Collectors.toSet());
    }

    public boolean containsEdge(Hyperedge edge) {
        return edges.contains(edge);
    }

    public Linkage copy() {
        Linkage copy = new Linkage();
        for (Vertex vertex : vertices) {
            copy.addVertex(vertex);
        }
        for (Hyperedge edge : getLambekEdges()) {
            copy.addLambekEdge(edge.getSourceSet(), edge.getTarget(), edge.getPartition());
        }
        for (Hyperedge edge : getRegularEdges()) {
            copy.addRegularEdge(edge.getSource(), edge.getTarget(), edge.getPartition());
        }
        return copy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Linkage linkage = (Linkage) o;
        return Objects.equals(vertices, linkage.vertices) &&
                Objects.equals(edges, linkage.edges);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vertices, edges);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Hyperedge hyperedge : edges) {
            sb.append(hyperedge.toString());
            sb.append('\n');
        }
        return sb.toString();
    }
}
