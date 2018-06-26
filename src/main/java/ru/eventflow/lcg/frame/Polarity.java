package ru.eventflow.lcg.frame;

/**
 * "The intuition behind polarities is that a positive polarity is a resource and a negative polarity
 * is a requirement of a resource, and the two need to eventually be matched up." (Fowler 2016, p. 34)
 */
public enum Polarity {
    POSITIVE("+"), NEGATIVE("-");

    private String orthography;

    Polarity(String orthography) {
        this.orthography = orthography;
    }

    public String getOrthography() {
        return orthography;
    }
}
