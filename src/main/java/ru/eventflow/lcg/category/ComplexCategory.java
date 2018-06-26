package ru.eventflow.lcg.category;

import java.util.Objects;

public class ComplexCategory implements Category {

    private Connective connective;
    private Category left;
    private Category right;

    public ComplexCategory(Connective connective, Category left, Category right) {
        this.connective = connective;
        this.left = left;
        this.right = right;
    }

    @Override
    public String getSymbol() {
        return "(" + left.getSymbol() + connective.getSymbol() + right.getSymbol() + ")";
    }

    public Connective getConnective() {
        return connective;
    }

    public Category getLeft() {
        return left;
    }

    public Category getRight() {
        return right;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ComplexCategory that = (ComplexCategory) o;

        if (connective != that.connective) return false;
        if (left != null ? !left.equals(that.left) : that.left != null) return false;
        return right != null ? right.equals(that.right) : that.right == null;
    }

    @Override
    public int hashCode() {
        return Objects.hash(connective, left, right);
    }

    @Override
    public String toString() {
        return getSymbol();
    }

}
