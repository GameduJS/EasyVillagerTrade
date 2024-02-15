package de.gamedude.evt.script.core;

public class Token {

    private final TokenType tokenType;
    public final String token;

    public Token(TokenType tokenType, String token) {
        this.tokenType = tokenType;
        this.token = token;
    }

    public  enum TokenType {
        NUMBER,
        IDENTIFIER,
        OPERATOR,
        PARENTHESIS
    }
}
