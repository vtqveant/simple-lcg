package ru.eventflow.lcg.category;

public class Token {

    private TokenType tokenType;
    private String orthography;

    Token(TokenType tokenType, String orthography) {
        this.tokenType = tokenType;
        this.orthography = orthography;
    }

    TokenType getTokenType() {
        return tokenType;
    }

    String getOrthography() {
        return orthography;
    }

    @Override
    public String toString() {
        return String.format("%-10s '%s'", tokenType.name(), orthography);
    }

}
