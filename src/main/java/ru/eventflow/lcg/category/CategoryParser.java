package ru.eventflow.lcg.category;

import java.util.ArrayList;
import java.util.List;

/**
 * A parser for syntactic categories.
 * <p>
 * Expr -> '(' Expr ')' | Expr '/' Expr | Expr '\' Expr | Expr '*' Expr | Terminal
 */
public class CategoryParser {

    public Category parse(String s) {
        return parse(tokenize(s.trim()));
    }

    public Category parse(List<Token> tokens) {
        List<Item> stack = new ArrayList<>();
        boolean accepted = bottomUp(tokens, stack);
        return (accepted ? stack.get(0).node : null);
    }

    private List<Token> tokenize(String s) {
        List<Token> tokens = new ArrayList<>();
        StringBuilder buffer = new StringBuilder();

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            Token token;
            if (c == '(') {
                token = new Token(TokenType.LP, "(");
            } else if (c == ')') {
                token = new Token(TokenType.RP, ")");
            } else if (c == '/') {
                token = new Token(TokenType.RIGHT, "/");
            } else if (c == '\\') {
                token = new Token(TokenType.LEFT, "\\");
            } else if (c == '*' || c == '.') {
                token = new Token(TokenType.MULT, "*");
            } else if (Character.isWhitespace(c)) {
                token = new Token(TokenType.WHITESPACE, " ");
            } else {
                buffer.append(c);
                continue;
            }

            flushBuffer(buffer, tokens);
            tokens.add(token);
        }

        flushBuffer(buffer, tokens);
        return tokens;
    }

    private void flushBuffer(StringBuilder buffer, List<Token> tokens) {
        if (buffer.length() > 0) {
            tokens.add(new Token(TokenType.TERMINAL, buffer.toString()));
        }
        buffer.setLength(0);
    }

    private boolean accept(List<Token> tokens, List<Item> stack) {
        return tokens.size() == 0 && stack.size() == 1 && stack.get(0).token.getTokenType() == TokenType.EXPR;
    }

    private boolean bottomUp(List<Token> tokens, List<Item> stack) {
        return accept(tokens, stack) || reduce(tokens, stack) || shift(tokens, stack);
    }

    private boolean reduce(List<Token> tokens, List<Item> stack) {
        int size = stack.size();

        if (size > 0) {
            Item t3 = stack.get(size - 1);

            // EXPR -> TERMINAL
            if (t3.token.getTokenType() == TokenType.TERMINAL) {
                stack.set(size - 1, new Item(
                        new Token(TokenType.EXPR, t3.token.getOrthography()),
                        new PrimitiveCategory(t3.token.getOrthography()))
                );
                return bottomUp(tokens, stack);
            }
        }

        if (size > 2) {
            Item t3 = stack.get(size - 1);
            Item t2 = stack.get(size - 2);
            Item t1 = stack.get(size - 3);

            Item t = null;

            // EXPR -> LP EXPR RP
            if (t1.token.getTokenType() == TokenType.LP && t2.token.getTokenType() == TokenType.EXPR && t3.token.getTokenType() == TokenType.RP) {
                t = new Item(
                        new Token(TokenType.EXPR, t2.token.getOrthography()),
                        t2.node
                );
            }

            // EXPR -> EXPR LEFT EXPR
            if (t1.token.getTokenType() == TokenType.EXPR && t2.token.getTokenType() == TokenType.LEFT && t3.token.getTokenType() == TokenType.EXPR) {
                t = new Item(
                        new Token(TokenType.EXPR, "(" + t1.token.getOrthography() + "\\" + t3.token.getOrthography() + ")"),
                        new ComplexCategory(Connective.LEFT_DIVISION, t1.node, t3.node)
                );
            }

            // EXPR -> EXPR RIGHT EXPR
            if (t1.token.getTokenType() == TokenType.EXPR && t2.token.getTokenType() == TokenType.RIGHT && t3.token.getTokenType() == TokenType.EXPR) {
                t = new Item(
                        new Token(TokenType.EXPR, "(" + t1.token.getOrthography() + "/" + t3.token.getOrthography() + ")"),
                        new ComplexCategory(Connective.RIGHT_DIVISION, t1.node, t3.node)
                );
            }

            // EXPR -> EXPR MULT EXPR
            if (t1.token.getTokenType() == TokenType.EXPR && t2.token.getTokenType() == TokenType.MULT && t3.token.getTokenType() == TokenType.EXPR) {
                t = new Item(
                        new Token(TokenType.EXPR, "(" + t1.token.getOrthography() + "*" + t3.token.getOrthography() + ")"),
                        new ComplexCategory(Connective.MULTIPLICATION, t1.node, t3.node)
                );
            }

            if (t != null) {
                stack.remove(size - 1);
                stack.remove(size - 2);
                stack.set(size - 3, t);
                return bottomUp(tokens, stack);
            }
        }

        return false;
    }

    private boolean shift(List<Token> tokens, List<Item> stack) {
        for (int i = 0; i < tokens.size(); i++) {
            if (tokens.get(i).getTokenType() == TokenType.WHITESPACE) {
                continue;
            }
            stack.add(new Item(tokens.get(i), null));
            return bottomUp(tokens.subList(i + 1, tokens.size()), stack);
        }
        return false;
    }

    private class Item {
        Token token;
        Category node;

        Item(Token token, Category node) {
            this.token = token;
            this.node = node;
        }
    }

}
