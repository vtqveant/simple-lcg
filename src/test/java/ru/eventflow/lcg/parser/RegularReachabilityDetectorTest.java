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

public class RegularReachabilityDetectorTest {

    private RegularReachabilityDetector regularReachabilityDetector;

    private Vertex v1;
    private Vertex v2;
    private Vertex v3;
    private Vertex v4;
    private Vertex isolated;

    private static Vertex v(String symbol) {
        return new Vertex(new PrimitiveCategory(symbol), Polarity.NEGATIVE);
    }

    @Before
    public void setUp() {
        v1 = v("v1");
        v2 = v("v2");
        v3 = v("v3");
        v4 = v("v4");
        isolated = v("isolated");

        Linkage linkage = new Linkage();
        linkage.addVertex(v1);
        linkage.addVertex(v2);
        linkage.addVertex(v3);
        linkage.addVertex(v4);
        linkage.addEdge(v1, v2, Edge.Partition.LINKAGE, Edge.Type.REGULAR);
        linkage.addEdge(v1, v3, Edge.Partition.LINKAGE, Edge.Type.REGULAR);
        linkage.addEdge(v3, v4, Edge.Partition.LINKAGE, Edge.Type.REGULAR);

        regularReachabilityDetector = new RegularReachabilityDetector(linkage, true);
    }

    @Test
    public void testRegularNodes() {
        assertTrue(regularReachabilityDetector.reachable(v1, v2));
        assertTrue(regularReachabilityDetector.reachable(v1, v3));
        assertFalse(regularReachabilityDetector.reachable(v2, v1));
        assertFalse(regularReachabilityDetector.reachable(v3, v1));
        assertFalse(regularReachabilityDetector.reachable(v2, v3));
    }

    @Test
    public void testSelfReachable() {
        assertTrue(regularReachabilityDetector.reachable(v1, v1));
        assertTrue(regularReachabilityDetector.reachable(v2, v2));
        assertTrue(regularReachabilityDetector.reachable(v3, v3));
        assertTrue(regularReachabilityDetector.reachable(v4, v4));
        assertFalse(regularReachabilityDetector.reachable(isolated, isolated));
    }

    @Test
    public void testIsolatedNotReachable() {
        assertFalse(regularReachabilityDetector.reachable(isolated, v1));
        assertFalse(regularReachabilityDetector.reachable(v1, isolated));
    }

    @Test
    public void testVertextNotInGraphUnreachable() {
        Vertex dummy = v("dummy");
        assertFalse(regularReachabilityDetector.reachable(v1, dummy));
        assertFalse(regularReachabilityDetector.reachable(dummy, v1));
    }

    @Test
    public void testTransitivity() {
        assertTrue(regularReachabilityDetector.reachable(v1, v4));
        assertFalse(regularReachabilityDetector.reachable(v2, v4));
    }

}