package ru.eventflow.lcg.frame;

import ru.eventflow.lcg.sequent.Sequent;
import ru.eventflow.lcg.sequent.SequentBuilder;

public class FrameExampleRunner {

    public static void main(String[] args) {
        Sequent premise = SequentBuilder.builder().setAntecedent("A", "B", "C").setSuccedent("D").build();
        Sequent conclusion = SequentBuilder.builder().setAntecedent("B", "C").setSuccedent("A\\D").build();
        System.out.println(premise);
        System.out.println(conclusion);

        // Woman that Jon always loved (Fowler, 2016, p. 28) -- Note the use of type-raising in the parse tree!
        Sequent sequent = SequentBuilder.builder()
                .setAntecedent("N", "N\\N/(S/NP)", "NP", "S\\NP/(S\\NP)", "S\\NP/NP", "NP")
                .setSuccedent("N")
                .build();
        Frame frame = ProofFrameBuilder.builder().setSequent(sequent).build();
        System.out.println("----------- Proof frame -----------");
        System.out.println();
        System.out.println(sequent);
        System.out.println();
        System.out.println(frame);
    }

}
