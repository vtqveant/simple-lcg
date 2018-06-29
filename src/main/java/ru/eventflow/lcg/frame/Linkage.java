package ru.eventflow.lcg.frame;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A (Partial) Term Graph is part of a proof net.
 * It represents a linkage between atoms and sibling nodes contained in a frame.
 */
public class Linkage {

    private Set<Vertex> vertices;
    private Set<Edge> edges;

    public Linkage() {
        vertices = new HashSet<>();
        edges = new HashSet<>();
    }

    public void addEdge(Vertex from, Vertex to, Edge.Partition partition, Edge.Type type) {
        if (findEdge(from, to, partition).size() == 0) {
            vertices.add(to);
            vertices.add(from);
            edges.add(new Edge(from, to, partition, type));
        }
    }

    public void addVertex(Vertex vertex) {
        vertices.add(vertex);
    }

    public Set<Edge> getRegularEdges() {
        return edges.stream().filter(Edge::isRegular).collect(Collectors.toSet());
    }

    public Set<Edge> getLambekEdges() {
        return edges.stream().filter(Edge::isLambek).collect(Collectors.toSet());
    }

    public boolean containsVertex(Vertex vertex) {
        return vertices.contains(vertex);
    }

    public void removeVertex(Vertex vertex) {
        vertices.remove(vertex);
    }

    public void removeEdge(Edge edge) {
        edges.remove(edge);
    }

    public Set<Edge> getInEdges(Vertex vertex) {
        return edges.stream().filter(e -> e.getTarget() == vertex).collect(Collectors.toSet());
    }

    public Set<Edge> getOutEdges(Vertex vertex) {
        return edges.stream().filter(e -> e.getSource() == vertex).collect(Collectors.toSet());
    }

    public Set<Vertex> getPredecessors(Vertex vertex, Edge.Type edgeType) {
        Set<Vertex> predecessors = new HashSet<>();
        for (Edge edge : getInEdges(vertex)) {
            if (edge.getType() == edgeType) {
                predecessors.add(edge.getSource());
            }
        }
        return predecessors;
    }

    public Set<Vertex> getPredecessors(Vertex vertex) {
        Set<Vertex> predecessors = new HashSet<>();
        for (Edge edge : getInEdges(vertex)) {
            predecessors.add(edge.getSource());
        }
        return predecessors;
    }

    public Collection<Vertex> getSuccessors(Vertex vertex) {
        Set<Vertex> successors = new HashSet<>();
        for (Edge edge : getOutEdges(vertex)) {
            successors.add(edge.getTarget());
        }
        return successors;
    }

    public Set<Vertex> getVertices() {
        return vertices;
    }

    public Set<Edge> getEdges() {
        return edges;
    }

    public Set<Edge> findEdge(Vertex from, Vertex to, Edge.Partition partition) {
        return edges.stream()
                .filter(e -> e.getPartition() == partition && e.getSource() == from && e.getTarget() == to)
                .collect(Collectors.toSet());
    }

    public Linkage copy() {
        Linkage copy = new Linkage();
        for (Vertex vertex : vertices) {
            copy.addVertex(vertex);
        }
        for (Edge edge : getEdges()) {
            copy.addEdge(edge.getSource(), edge.getTarget(), edge.getPartition(), edge.getType());
        }
        return copy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Linkage linkage = (Linkage) o;
        return Objects.equals(vertices, linkage.vertices) && Objects.equals(edges, linkage.edges);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vertices, edges);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Edge edge : edges) {
            sb.append(edge.toString());
            sb.append('\n');
        }
        return sb.toString();
    }
}
