package ru.eventflow.lcg.parser;

import org.junit.Before;
import org.junit.Test;
import ru.eventflow.lcg.category.PrimitiveCategory;
import ru.eventflow.lcg.frame.Edge;
import ru.eventflow.lcg.frame.Linkage;
import ru.eventflow.lcg.frame.Polarity;
import ru.eventflow.lcg.frame.Vertex;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AcyclicTest {

    private Vertex v0;
    private Vertex v1;
    private Vertex v2;
    private Vertex v3;

    private static Vertex v(String symbol) {
        return new Vertex(new PrimitiveCategory(symbol), Polarity.NEGATIVE);
    }

    @Before
    public void setUp() {
        v0 = v("v0");
        v1 = v("v1");
        v2 = v("v2");
        v3 = v("v3");
    }

    @Test
    public void testCyclic() {
        Linkage linkage = new Linkage();
        linkage.addVertex(v0);
        linkage.addVertex(v1);
        linkage.addVertex(v2);
        linkage.addVertex(v3);
        linkage.addEdge(v3, v0, Edge.Partition.LINKAGE, Edge.Type.REGULAR);
        linkage.addEdge(v0, v2, Edge.Partition.LINKAGE, Edge.Type.REGULAR);
        linkage.addEdge(v2, v1, Edge.Partition.LINKAGE, Edge.Type.REGULAR);
        linkage.addEdge(v1, v3, Edge.Partition.LINKAGE, Edge.Type.REGULAR);

        boolean acyclic = new Validator(linkage, true, false).isRegularAcyclic();
        assertFalse(acyclic);
    }

    @Test
    public void testAcyclic() {
        Linkage linkage = new Linkage();
        linkage.addVertex(v0);
        linkage.addVertex(v1);
        linkage.addVertex(v2);
        linkage.addVertex(v3);
        linkage.addEdge(v3, v0, Edge.Partition.LINKAGE, Edge.Type.REGULAR);
        linkage.addEdge(v0, v2, Edge.Partition.LINKAGE, Edge.Type.REGULAR);
        linkage.addEdge(v2, v1, Edge.Partition.LINKAGE, Edge.Type.REGULAR);
        linkage.addEdge(v3, v2, Edge.Partition.LINKAGE, Edge.Type.REGULAR);

        boolean acyclic = new Validator(linkage, true, false).isRegularAcyclic();
        assertTrue(acyclic);
    }

}
