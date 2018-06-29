package ru.eventflow.lcg.parser;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SequentBuilderTest {

    @Test
    public void testSimpleSequent() {
        Sequent sequent = SequentBuilder.builder().setAntecedent("A").setSuccedent("B").build();
        assertEquals(1, sequent.getAntecedent().size());
        assertEquals(1, sequent.getSuccedent().size());
        System.out.println(sequent);
    }

    @Test
    public void testComplexAntecedent() {
        Sequent sequent = SequentBuilder.builder().setAntecedent("(S1\\NP2/(S3\\(S4/NP5)\\(NP6/NP7)))").setSuccedent("S").build();
        assertEquals(1, sequent.getAntecedent().size());
        assertEquals(1, sequent.getSuccedent().size());
        System.out.println(sequent);
    }

    @Test
    public void testLengthyAntecedent() {
        Sequent sequent = SequentBuilder.builder().setAntecedent("A", "B", "C/D").setSuccedent("B").build();
        assertEquals(3, sequent.getAntecedent().size());
        assertEquals(1, sequent.getSuccedent().size());
        System.out.println(sequent);
    }

    @Test
    public void testLengthySuccedent() {
        Sequent sequent = SequentBuilder.builder().setAntecedent("A").setSuccedent("A/B", "((B\\A))").build();
        assertEquals(1, sequent.getAntecedent().size());
        assertEquals(2, sequent.getSuccedent().size());
        System.out.println(sequent);
    }

    @Test
    public void testWhitespaces() {
        Sequent sequent = SequentBuilder.builder().setAntecedent(" A ", "   B", " (((C/D)))").setSuccedent(" B ", " D ").build();
        assertEquals(3, sequent.getAntecedent().size());
        assertEquals(2, sequent.getSuccedent().size());
        System.out.println(sequent);
    }

}
