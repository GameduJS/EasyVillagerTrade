package de.gamedude.evt.script.core;

import java.util.ArrayList;
import java.util.List;

public class Tokenizer {

    private int position = 0;
    private String content;

    private Tokenizer(String content) {
        this.content = content;
    }

    public List<Token> tokenizeContent() {
        List<Token> tokens = new ArrayList<>();

        while(position < content.length()) {
            char c = content.charAt(position);

            if(Character.isWhitespace(c))
                continue;
            if(Character.isLetter(c))
                tokens.add(compileIdentifier());
            else if(Character.isDigit(c))
                tokens.add(compileDigit());
            else if(c == '(')
                tokens.add(compileParentheses());
            position++;
        }
        return tokens;
    }


    private Token compileIdentifier() {
        StringBuilder identifierBuilder = new StringBuilder();
        while (position < content.length() && Character.isLetterOrDigit(content.charAt(position))) {
            identifierBuilder.append(content.charAt(position));
            position++;
        }
        return new Token(Token.TokenType.IDENTIFIER, identifierBuilder.toString());
    }

    private Token compileDigit() {
        StringBuilder numberBuilder = new StringBuilder();
        while (position < content.length() && (Character.isDigit(content.charAt(position)))) {
            numberBuilder.append(content.charAt(position));
            position++;
        }
        return new Token(Token.TokenType.NUMBER, numberBuilder.toString());
    }

    private Token compileParentheses() {
        StringBuilder parenthesesBuilder = new StringBuilder();
        while (position < content.length() && Character.isLetterOrDigit(content.charAt(position)) && content.charAt(position) != ')') {
            parenthesesBuilder.append(content.charAt(position));
            position++;
        }
        return new Token(Token.TokenType.PARENTHESIS, parenthesesBuilder.toString());
    }

}
