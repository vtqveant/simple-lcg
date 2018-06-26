package ru.eventflow.lcg.category;

public class PrimitiveCategory implements Category {

    private final String symbol;

    public PrimitiveCategory(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public String getSymbol() {
        return symbol;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PrimitiveCategory that = (PrimitiveCategory) o;

        return symbol != null ? symbol.equals(that.symbol) : that.symbol == null;
    }

    @Override
    public int hashCode() {
        return symbol != null ? symbol.hashCode() : 0;
    }

    @Override
    public String toString() {
        return symbol;
    }

}
