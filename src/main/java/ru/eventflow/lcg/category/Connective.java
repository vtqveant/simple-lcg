package ru.eventflow.lcg.category;

public enum Connective {

    LEFT_DIVISION("\\"), RIGHT_DIVISION("/"), MULTIPLICATION("*");

    private String symbol;

    Connective(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }
}
