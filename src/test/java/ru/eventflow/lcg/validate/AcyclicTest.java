package ru.eventflow.lcg.validate;

import org.junit.Before;
import org.junit.Test;
import ru.eventflow.lcg.category.PrimitiveCategory;
import ru.eventflow.lcg.frame.Hyperedge;
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
        linkage.addRegularEdge(v3, v0, Hyperedge.Partition.LINKAGE);
        linkage.addRegularEdge(v0, v2, Hyperedge.Partition.LINKAGE);
        linkage.addRegularEdge(v2, v1, Hyperedge.Partition.LINKAGE);
        linkage.addRegularEdge(v1, v3, Hyperedge.Partition.LINKAGE);

        boolean acyclic = new LinkageValidator(linkage, false).isRegularAcyclic();
        assertFalse(acyclic);
    }

    @Test
    public void testAcyclic() {
        Linkage linkage = new Linkage();
        linkage.addVertex(v0);
        linkage.addVertex(v1);
        linkage.addVertex(v2);
        linkage.addVertex(v3);
        linkage.addRegularEdge(v3, v0, Hyperedge.Partition.LINKAGE);
        linkage.addRegularEdge(v0, v2, Hyperedge.Partition.LINKAGE);
        linkage.addRegularEdge(v2, v1, Hyperedge.Partition.LINKAGE);
        linkage.addRegularEdge(v3, v2, Hyperedge.Partition.LINKAGE);

        boolean acyclic = new LinkageValidator(linkage, false).isRegularAcyclic();
        assertTrue(acyclic);
    }

}
