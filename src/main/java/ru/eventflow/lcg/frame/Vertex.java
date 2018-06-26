package ru.eventflow.lcg.frame;

import ru.eventflow.lcg.category.Category;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class Vertex {

    private static AtomicInteger counter = new AtomicInteger();

    private final int id = counter.incrementAndGet();
    private final Category category;
    private final Polarity polarity;
    private final Type type;

    public Vertex(Category category, Polarity polarity) {
        this(category, polarity, Type.ATOM);
    }

    public Vertex(Category category, Polarity polarity, Type type) {
        this.category = category;
        this.polarity = polarity;
        this.type = type;
    }

    public Category getCategory() {
        return category;
    }

    public Polarity getPolarity() {
        return polarity;
    }

    public Type getType() {
        return type;
    }

    public int getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vertex vertex = (Vertex) o;
        return id == vertex.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return category + " (" + polarity.getOrthography() + ") " + id;
    }

    public enum Type {
        ATOM, SIBLING
    }
}
