package ru.eventflow.lcg.frame;

import org.junit.Before;
import org.junit.Test;
import ru.eventflow.lcg.category.PrimitiveCategory;
import ru.eventflow.lcg.sequent.Sequent;
import ru.eventflow.lcg.sequent.SequentBuilder;
import ru.eventflow.lcg.validate.LinkageValidator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ProofFrameTest {

    private ProofFrame proofFrame;
    private LinkageValidator validator;

    @Before
    public void setUp() {
        Sequent sequent = SequentBuilder.builder().setAntecedent("S/(NP\\S)", "(NP\\S)/NP", "NP").setSuccedent("S").build(); // Who loves him
        proofFrame = ProofFrameBuilder.builder().setSequent(sequent).build();
        validator = new LinkageValidator(proofFrame.getLinkage());
    }

    @Test
    public void testRegularAcyclic() {
        boolean acyclic = validator.isRegularAcyclic();
        assertTrue(acyclic);
    }

    @Test
    public void testRegularCyclic() {
        Linkage cyclic = new Linkage();
        Vertex v1 = new Vertex(new PrimitiveCategory("A"), Polarity.POSITIVE);
        Vertex v2 = new Vertex(new PrimitiveCategory("B"), Polarity.NEGATIVE);
        cyclic.addVertex(v1);
        cyclic.addVertex(v2);
        cyclic.addRegularEdge(v1, v2, Hyperedge.Partition.LINKAGE);
        cyclic.addRegularEdge(v2, v1, Hyperedge.Partition.LINKAGE);

        boolean acyclic = new LinkageValidator(cyclic).isRegularAcyclic();
        assertFalse(acyclic);
    }

}
