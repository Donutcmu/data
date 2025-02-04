package main.strategy;

import java.util.List;

public class Parser {
    private List<Token> tokens;
    private int pos = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public ASTNode parse() {
        return parseStatement();
    }

    private ASTNode parseStatement() {
        if (match("if")) {
            ASTNode condition = parseExpression();
            expect("then");
            ASTNode thenBranch = parseStatement();
            expect("else");
            ASTNode elseBranch = parseStatement();
            return new IfStatementNode(condition, thenBranch, elseBranch);
        } else if (match("move")) {
            return new MoveCommandNode(tokens.get(pos++).value);
        } else if (match("shoot")) {
            return new ShootCommandNode(tokens.get(pos++).value);
        } else if (match("done")) {
            return new DoneCommandNode();
        }
        throw new RuntimeException("Unexpected token: " + tokens.get(pos).value);
    }

    private ASTNode parseExpression() {
        return new BooleanExpressionNode(tokens.get(pos++).value);
    }

    private boolean match(String expected) {
        if (pos < tokens.size() && tokens.get(pos).value.equals(expected)) {
            pos++;
            return true;
        }
        return false;
    }

    private void expect(String expected) {
        if (!match(expected)) {
            throw new RuntimeException("Expected '" + expected + "'");
        }
    }
}
