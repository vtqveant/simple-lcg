package ru.eventflow.lcg.parser;

import ru.eventflow.lcg.category.Category;

import java.util.List;

public class Sequent {
    private List<Category> antecedent;
    private List<Category> succedent;

    public Sequent(List<Category> antecedent, List<Category> succedent) {
        this.antecedent = antecedent;
        this.succedent = succedent;
    }

    public List<Category> getAntecedent() {
        return antecedent;
    }

    public List<Category> getSuccedent() {
        return succedent;
    }

    @Override
    public String toString() {
        return "[[" + list(antecedent) + "], [" + list(succedent) + "]]";
    }

    private String list(List<Category> list) {
        String result = "";
        for (int i = 0; i < list.size(); i++) {
            result += list.get(i).getSymbol();
            if (i != list.size() - 1) {
                result += ", ";
            }
        }
        return result;
    }

}
