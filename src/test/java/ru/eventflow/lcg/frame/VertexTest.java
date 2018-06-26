package ru.eventflow.lcg.frame;

import org.junit.Test;
import ru.eventflow.lcg.category.PrimitiveCategory;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class VertexTest {

    @Test
    public void testVertexIdentity() {
        Vertex v1 = new Vertex(new PrimitiveCategory("A"), Polarity.POSITIVE);
        Vertex v2 = new Vertex(new PrimitiveCategory("B"), Polarity.NEGATIVE);
        Vertex v3 = new Vertex(new PrimitiveCategory("C"), Polarity.POSITIVE);

        Set<Vertex> set = new HashSet<>();
        set.add(v1);
        set.add(v2);
        set.add(v3);
        print(set);
        assertEquals(3, set.size());

        // set's unchanged
        set.add(v2);
        set.add(v3);
        assertEquals(3, set.size());

        // filtered properly
        Set<Vertex> negative = set.stream().filter(vertex -> vertex.getPolarity() == Polarity.NEGATIVE).collect(Collectors.toSet());
        System.out.println("negative");
        print(negative);
        assertEquals(1, negative.size());

        Set<Vertex> positive = set.stream().filter(vertex -> vertex.getPolarity() == Polarity.POSITIVE).collect(Collectors.toSet());
        System.out.println("positive");
        print(positive);
        assertEquals(2, positive.size());
    }

    private void print(Set<Vertex> set) {
        for (Iterator<Vertex> it = set.iterator(); it.hasNext(); ) {
            Vertex v = it.next();
            System.out.println(v);
        }
        System.out.println();
    }
}
