package ru.eventflow.lcg.sequent;

import ru.eventflow.lcg.category.Category;
import ru.eventflow.lcg.category.CategoryParser;

import java.util.ArrayList;
import java.util.List;

public class SequentBuilder {

    private List<Category> antecedent = null;
    private List<Category> succedent = null;

    private SequentBuilder() {
    }

    public static synchronized SequentBuilder builder() {
        return new SequentBuilder();
    }

    public SequentBuilder setAntecedent(String... tokens) {
        this.antecedent = convert(tokens);
        return this;
    }

    public SequentBuilder setSuccedent(String... tokens) {
        this.succedent = convert(tokens);
        return this;
    }

    public Sequent build() {
        if (antecedent == null || succedent == null) {
            throw new IllegalStateException("Incomplete.");
        }
        return new Sequent(antecedent, succedent);
    }

    private List<Category> convert(String... symbols) {
        List<Category> result = new ArrayList<>(symbols.length);
        CategoryParser categoryParser = new CategoryParser();
        for (String s : symbols) {
            result.add(categoryParser.parse(s));
        }
        return result;
    }

}
