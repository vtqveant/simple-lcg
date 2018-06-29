package ru.eventflow.lcg.parser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.eventflow.lcg.dto.ParseDTO;

import static org.junit.Assert.assertEquals;

public class LCGParserTest {

    private LCGParser parser;

    @Before
    public void setUp() {
        parser = new ChartLCGParser(true);
    }

    /**
     * Explicitly setting an object to null in the tearDown() method... allows it to be garbage collected
     * before the end of the entire test run.
     */
    @After
    public void tearDown() {
        parser = null;
    }

    @Test
    public void testHeuristicOne_primitive() {
        Sequent sequent = SequentBuilder.builder().setAntecedent("S").setSuccedent("N").build();
        ParseDTO parse = parser.parse(sequent);
        print(parse);
        assertEquals(0, parse.getParses().size());
    }

    @Test
    public void testHeuristicOne_complex() {
        Sequent sequent = SequentBuilder.builder().setAntecedent("A/B").setSuccedent("S").build();
        ParseDTO parse = parser.parse(sequent);
        print(parse);
        assertEquals(0, parse.getParses().size());
    }

    @Test
    public void testHeuristicTwo_1() {
        Sequent sequent = SequentBuilder.builder().setAntecedent("A/B", "B").setSuccedent("S").build();
        ParseDTO parse = parser.parse(sequent);
        print(parse);
        assertEquals(0, parse.getParses().size());
    }

    @Test
    public void testHeuristicTwo_2() {
        Sequent sequent = SequentBuilder.builder().setAntecedent("B", "B/S").setSuccedent("S").build();
        ParseDTO parse = parser.parse(sequent);
        print(parse);
        assertEquals(0, parse.getParses().size());
    }

    @Test
    public void testHeuristicThree_1() {
        Sequent sequent = SequentBuilder.builder().setAntecedent("N/NP", "S/N").setSuccedent("N/NP").build();
        ParseDTO parse = parser.parse(sequent);
        print(parse);
        assertEquals(0, parse.getParses().size());
    }

    @Test
    public void testNonIntegral_1() {
        Sequent sequent = SequentBuilder.builder().setAntecedent("N\\(N\\N)", "S/S").setSuccedent("N").build();
        ParseDTO parse = parser.parse(sequent);
        print(parse);
        assertEquals(0, parse.getParses().size());
    }

    @Test
    public void testNonIntegral_2() {
        Sequent sequent = SequentBuilder.builder().setAntecedent("S\\(N/N)", "(N\\N)/(N/N)").setSuccedent("S").build();
        ParseDTO parse = parser.parse(sequent);
        print(parse);
        assertEquals(0, parse.getParses().size());
    }

    @Test
    public void testNonIntegral_3() {
        Sequent sequent = SequentBuilder.builder().setAntecedent("S\\S").setSuccedent("((S\\N)\\(S\\N))").build();
        ExponentialLCGParser parser = new ExponentialLCGParser();
        ParseDTO parse = parser.parse(sequent);
        print(parse);
        assertEquals(0, parse.getParses().size());
    }

    /**
     * This derivation is problematic because it violates a side-condition of a rule in L.
     * <pre>
     *             s -> s
     *            -------- (this step violates $\Gamma \neq \epsilon$ side-condition)
     * s -> s      -> s/s
     * -------------------
     *   s/(s/s) -> s
     * </pre>
     */
    @Test
    public void testNonIntegral_4() {
        Sequent sequent = SequentBuilder.builder().setAntecedent("S/(S/S)").setSuccedent("S").build();
        ParseDTO parse = parser.parse(sequent);
        print(parse);
        assertEquals(0, parse.getParses().size());
    }

    /**
     * Non-empty antecedent side-condition violation occurs here, so no parses are expected.
     */
    @Test
    public void testNonIntegral_5() {
        Sequent sequent = SequentBuilder.builder().setAntecedent("(N\\N)\\(S\\S)").setSuccedent("N\\N").build();
        ParseDTO parse = parser.parse(sequent);
        print(parse);
        assertEquals(0, parse.getParses().size());
    }

    /**
     * "[...] both type-raising and the non-crossed composition rules of CCG are derivable in LCG." (p. 30)
     */
    @Test
    public void testForwardRestrictedTypeRaisingDerivable() {
        Sequent sequent = SequentBuilder.builder().setAntecedent("X").setSuccedent("T/(T\\X)").build();
        ParseDTO parse = parser.parse(sequent);
        print(parse);
        assertEquals(1, parse.getParses().size());
    }

    @Test
    public void testBackwardRestrictedTypeRaisingDerivable() {
        Sequent sequent = SequentBuilder.builder().setAntecedent("X").setSuccedent("T\\(T/X)").build();
        ParseDTO parse = parser.parse(sequent);
        print(parse);
        assertEquals(1, parse.getParses().size());
    }

    @Test
    public void testNonCrossingForwardCompositionDerivable() {
        Sequent sequent = SequentBuilder.builder().setAntecedent("A/B", "B/C").setSuccedent("A/C").build();
        ParseDTO parse = parser.parse(sequent);
        print(parse);
        assertEquals(1, parse.getParses().size());
    }

    @Test
    public void testNonCrossingBackwardCompositionDerivable() {
        Sequent sequent = SequentBuilder.builder().setAntecedent("Y\\Z", "X\\Y").setSuccedent("X\\Z").build();
        ParseDTO parse = parser.parse(sequent);
        print(parse);
        assertEquals(1, parse.getParses().size());
    }

    @Test
    public void testForwardCrossedCompositionNotDerivable() {
        Sequent sequent = SequentBuilder.builder().setAntecedent("X/Y", "Y\\Z").setSuccedent("X\\Z").build();
        ParseDTO parse = parser.parse(sequent);
        print(parse);
        assertEquals(0, parse.getParses().size());
    }

    @Test
    public void testBackwardCrossedCompositionNotDerivable() {
        Sequent sequent = SequentBuilder.builder().setAntecedent("Y/Z", "X\\Y").setSuccedent("X/Z").build();
        ParseDTO parse = parser.parse(sequent);
        print(parse);
        assertEquals(0, parse.getParses().size());
    }

    /**
     * This derivation is quite nontrivial, because it requires both a type-raising and a composition to work.
     * <pre>
     *                             S -> S
     *                 -------------------
     *                  N/S, S\(N/S) -> S
     *    -------------------------------- forward composition
     *     N/(N\N), (N\N)/S, S\(N/S) -> S
     *    -------------------------------- type raising of the leftmost category
     *           N, (N\N)/S, S\(N/S) -> S
     * </pre>
     */
    @Test
    public void testNontrivialInterplay() {
        Sequent sequent = SequentBuilder.builder().setAntecedent("N", "(N\\N)/S", "S\\(N/S)").setSuccedent("S").build();
        ParseDTO parse = parser.parse(sequent);
        print(parse);
        assertEquals(1, parse.getParses().size());
    }

    /**
     * Note, that the backslashes in the derivation tree below are in Ajdukiewicz notation, whereas Fowler uses that of Steedman.
     * <pre>
     *    s -> s      n -> n
     *   -------------------- \_h
     *        n, n\s -> s             n -> n
     *   ------------------------------------- /_h
     *      n, (n\s)/n, n -> s
     *   ------------------------------------- /_i
     *         n, (n\s)/n -> s/n
     *   ------------------------------------- \_i
     *            (n\s)/n -> n\(s/n)
     *
     * </pre>
     */
    @Test
    public void testPermutation_1() {
        Sequent sequent = SequentBuilder.builder().setAntecedent("(S\\N)/N").setSuccedent("(S/N)\\N").build();
        ParseDTO parse = parser.parse(sequent);
        print(parse);
        assertEquals(1, parse.getParses().size());
    }

    @Test
    public void testIntegral_1() {
        Sequent sequent = SequentBuilder.builder().setAntecedent("S").setSuccedent("S").build();
        ParseDTO parse = parser.parse(sequent);
        print(parse);
        assertEquals(1, parse.getParses().size());
    }

    @Test
    public void testIntegral_2() {
        Sequent sequent = SequentBuilder.builder().setAntecedent("A/B").setSuccedent("A/B").build();
        ParseDTO parse = parser.parse(sequent);
        print(parse);
        assertEquals(1, parse.getParses().size());
    }

    @Test
    public void testIntegral_3() {
        Sequent sequent = SequentBuilder.builder().setAntecedent("(S\\NP)/NP", "NP").setSuccedent("S\\NP").build();
        ParseDTO parse = parser.parse(sequent);
        print(parse);
        assertEquals(1, parse.getParses().size());
    }

    /**
     * Any complex category is derivable from itself
     */
    @Test
    public void testEtaExpansion_1() {
        Sequent sequent = SequentBuilder.builder().setAntecedent("A/B").setSuccedent("A/B").build();
        ParseDTO parse = parser.parse(sequent);
        print(parse);
        assertEquals(1, parse.getParses().size());
    }

    @Test
    public void testEtaExpansion_2() {
        Sequent sequent = SequentBuilder.builder().setAntecedent("(A/B)\\C").setSuccedent("(A/B)\\C").build();
        ParseDTO parse = parser.parse(sequent);
        print(parse);
        assertEquals(1, parse.getParses().size());
    }

    @Test
    public void testEtaExpansion_3() {
        Sequent sequent = SequentBuilder.builder().setAntecedent("A\\B").setSuccedent("A\\B").build();
        ParseDTO parse = parser.parse(sequent);
        print(parse);
        assertEquals(1, parse.getParses().size());
    }

    @Test
    public void testIntegral_4_trimmed_1() {
        Sequent sequent = SequentBuilder.builder().setAntecedent("(A/B)/(C/D)", "(C/D)").setSuccedent("A/B").build();
        ParseDTO parse = parser.parse(sequent);
        print(parse);
        assertEquals(1, parse.getParses().size());
    }

    @Test
    public void testIntegral_7() {
        Sequent sequent = SequentBuilder.builder().setAntecedent("(A/(A/B))/(C/D)", "(C/D)", "A/B").setSuccedent("A").build();
        ParseDTO parse = parser.parse(sequent);
        print(parse);
        assertEquals(1, parse.getParses().size());
    }

    @Test
    public void testIntegral_4() {
        Sequent sequent = SequentBuilder.builder().setAntecedent("(S\\NP)/(S\\NP)", "(S\\NP)/NP", "NP").setSuccedent("S\\NP").build();
        ParseDTO parse = parser.parse(sequent);
        print(parse);
        assertEquals(1, parse.getParses().size());
    }

    /**
     * A trace in the subordinate clause "(A woman that) Jon always loved t_0", p. 28
     */
    @Test
    public void testIntegral_5() {
        Sequent sequent = SequentBuilder.builder()
                .setAntecedent("NP", "(S\\NP)/(S\\NP)", "(S\\NP)/NP", "NP")
                .setSuccedent("S")
                .build();
        ParseDTO parse = parser.parse(sequent);
        print(parse);
        assertEquals(1, parse.getParses().size());
    }

    /**
     * Who loves him
     */
    @Test
    public void testIntegral_6() {
        Sequent sequent = SequentBuilder.builder().setAntecedent("S/(NP\\S)", "(NP\\S)/NP", "NP").setSuccedent("S").build();
        ParseDTO parse = parser.parse(sequent);
        print(parse);
        assertEquals(1, parse.getParses().size());
    }

    /**
     * GMB derivations freely change N to NP when necessary, so I had to fix some of the categories to account for that.
     */
    @Test
    public void testGMB_p00_d0030() {
        SequentBuilder builder = SequentBuilder.builder();
        builder.setAntecedent(
                "N[nom]", // Local_news_reports
                "(S[dcl]\\N[nom])/S[dcl]", // said	VBD	S[dcl]\NP/S[dcl]	say	O
                "(S[dcl]/S[dcl])/(S[asup]\\NP)", // at	IN	S[dcl]/S[dcl]/S[asup]\NP	at	O
                "S[asup]\\NP", // least	JJS	S[asup]\NP	least	O
                "N[nom]", // five_mortar_shells
                "(S[dcl]\\N[nom])/NP", // hit	VBD	S[dcl]\NP/NP	hit	O
                "NP/N[nom]", // the	DT	NP/N[nom]	the	O
                "N[nom]/N[nom]", // palace	NN	N[nom]/N[nom]	palace	O
                "N[nom]", // compound	NN	N[nom]	compound	O
                "S[dcl]\\S[dcl]" // .	.	S[dcl]\S[dcl]	.	O
        );
        builder.setSuccedent("S[dcl]");
        Sequent sequent = builder.build();

        ParseDTO parse = parser.parse(sequent);
        print(parse);
        assertEquals(3, parse.getParses().size());
    }

    @Test
    public void testGMB_p00_d0283() {
        SequentBuilder builder = SequentBuilder.builder();
        builder.setAntecedent(
                "NP/N[nom]",  // The	DT
                "N[nom]", // ministry	NN
                "S[dcl]\\NP/NP", // gave	VBD
                "NP/N[nom]", // no	DT
                "N[nom]/N[nom]", // other	JJ
                "N[nom]",     // details	NNS
                "S[dcl]\\S[dcl]" // .	.
        );
        builder.setSuccedent("S[dcl]");
        Sequent sequent = builder.build();

        ParseDTO parse = parser.parse(sequent);
        print(parse);
        assertEquals(1, parse.getParses().size());
    }

    /**
     * (Fowler 2016) p. 153 (NB: the categories of 'by' and of its parent in the trees are swapped)
     */
    @Test
    public void testCCGBank_0351_4() {
        SequentBuilder builder = SequentBuilder.builder();
        builder.setAntecedent(
                "NP/N", // the
                "N", // market
                "(NP/N)\\NP", // 's
                "N", // tempo
                "(S\\NP)/(S\\NP)", // was
                "S\\NP", // helped
                "((S\\NP)\\(S\\NP))/NP", // by
                "NP/N", // the
                "N", // dollar
                "(NP/N)\\NP", // 's
                "N", // resiliency
                "NP", // he
                "(S\\S)\\NP" // said
        );
        builder.setSuccedent("S");
        Sequent sequent = builder.build();

        ParseDTO parse = parser.parse(sequent);
        print(parse);
        assertEquals(4, parse.getParses().size());
    }

    private void print(ParseDTO parse) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String s = mapper.writeValueAsString(parse);
            System.out.println(s);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
