public enum TokenType {
    IDENT, NUMBER,
    ASSIGN, // =
    PLUS, MINUS, STAR, SLASH, PERCENT, CARET, // + - * / % ^
    LPAREN, RPAREN, LBRACE, RBRACE, // ( ) { }
    IF, THEN, ELSE, WHILE, MOVE, SHOOT, DONE,
    UP, DOWN, UPLEFT, UPRIGHT, DOWNLEFT, DOWNRIGHT,
    SEMI, // ;
    EOF
}

class Token {
    public final TokenType type;
    public final String text;

    public Token(TokenType type, String text) {
        this.type = type;
        this.text = text;
    }

    public String toString() {
        return "["+type+","+text+"]";
    }
}

class Lexer {
    private String input;
    private int pos;
    private int length;

    public Lexer(String input) {
        this.input = input;
        this.pos = 0;
        this.length = input.length();
    }

    public Token nextToken() {
        while (pos < length && Character.isWhitespace(peek())) {
            pos++;
        }
        if (pos >= length) {
            return new Token(TokenType.EOF, "");
        }
        char c = peek();

        // ตัวอย่างการเช็คอักขระ
        if (c == '=') { pos++; return new Token(TokenType.ASSIGN, "="); }
        if (c == '+') { pos++; return new Token(TokenType.PLUS, "+"); }
        if (c == '-') { pos++; return new Token(TokenType.MINUS, "-"); }
        if (c == '*') { pos++; return new Token(TokenType.STAR, "*"); }
        if (c == '/') { pos++; return new Token(TokenType.SLASH, "/"); }
        if (c == '%') { pos++; return new Token(TokenType.PERCENT, "%"); }
        if (c == '^') { pos++; return new Token(TokenType.CARET, "^"); }
        if (c == '(') { pos++; return new Token(TokenType.LPAREN, "("); }
        if (c == ')') { pos++; return new Token(TokenType.RPAREN, ")"); }
        if (c == '{') { pos++; return new Token(TokenType.LBRACE, "{"); }
        if (c == '}') { pos++; return new Token(TokenType.RBRACE, "}"); }
        if (c == ';') { pos++; return new Token(TokenType.SEMI, ";"); }

        // ตัวเลข
        if (Character.isDigit(c)) {
            int start = pos;
            while (pos < length && Character.isDigit(peek())) {
                pos++;
            }
            String numStr = input.substring(start, pos);
            return new Token(TokenType.NUMBER, numStr);
        }
        // identifier / keyword
        if (Character.isLetter(c)) {
            int start = pos;
            while (pos < length && (Character.isLetterOrDigit(peek()) || peek()=='_')) {
                pos++;
            }
            String word = input.substring(start, pos);
            String lower = word.toLowerCase();
            switch(lower) {
                case "if":    return new Token(TokenType.IF, word);
                case "then":  return new Token(TokenType.THEN, word);
                case "else":  return new Token(TokenType.ELSE, word);
                case "while": return new Token(TokenType.WHILE, word);
                case "move":  return new Token(TokenType.MOVE, word);
                case "shoot": return new Token(TokenType.SHOOT, word);
                case "done":  return new Token(TokenType.DONE, word);
                case "up":    return new Token(TokenType.UP, word);
                case "down":  return new Token(TokenType.DOWN, word);
                case "upleft":    return new Token(TokenType.UPLEFT, word);
                case "upright":   return new Token(TokenType.UPRIGHT, word);
                case "downleft":  return new Token(TokenType.DOWNLEFT, word);
                case "downright": return new Token(TokenType.DOWNRIGHT, word);
            }
            return new Token(TokenType.IDENT, word);
        }
        // ถ้าไม่รู้จัก ก็ข้าม
        pos++;
        return nextToken();
    }

    private char peek() {
        if (pos >= length) return '\0';
        return input.charAt(pos);
    }
}

