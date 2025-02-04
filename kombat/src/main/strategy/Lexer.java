package main.strategy;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

class Token {
    String type, value;

    Token(String type, String value) {
        this.type = type;
        this.value = value;
    }
}

public class Lexer {
    private String input;
    private int pos = 0;
    private static final Set<String> keywords = Set.of("if", "then", "else", "while", "move", "shoot", "done");

    public Lexer(String input) {
        this.input = input;
    }

    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();
        while (pos < input.length()) {
            char current = input.charAt(pos);
            if (Character.isWhitespace(current)) {
                pos++;
            } else if (Character.isDigit(current)) {
                tokens.add(new Token("NUMBER", readNumber()));
            } else if (Character.isLetter(current)) {
                String word = readWord();
                tokens.add(new Token(keywords.contains(word) ? "KEYWORD" : "IDENTIFIER", word));
            } else {
                tokens.add(new Token("SYMBOL", String.valueOf(current)));
                pos++;
            }
        }
        return tokens;
    }

    private String readNumber() {
        StringBuilder num = new StringBuilder();
        while (pos < input.length() && Character.isDigit(input.charAt(pos))) {
            num.append(input.charAt(pos++));
        }
        return num.toString();
    }

    private String readWord() {
        StringBuilder word = new StringBuilder();
        while (pos < input.length() && Character.isLetter(input.charAt(pos))) {
            word.append(input.charAt(pos++));
        }
        return word.toString();
    }
}
