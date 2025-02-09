import java.util.ArrayList;
import java.util.List;

public class Parser {
    private static List<Token> tokens;
    private static int pos;

    public static StatementNode parse(String input) throws Exception {
        Lexer lexer = new Lexer(input);
        tokens = new ArrayList<>();
        Token t;
        do {
            t = lexer.nextToken();
            System.out.println("Token: " + t.type + " (" + t.text + ")");
            tokens.add(t);
        } while (t.type != TokenType.EOF);
        pos = 0;

        // เราจะ parse หลาย Statement วางรวมใน BlockNode (Program)
        BlockNode program = new BlockNode();
        while (!check(TokenType.EOF)) {
            // ถ้ามี ; ว่าง ๆ เกินมา ให้ข้ามไป
            int prevPos = pos;
            while (match(TokenType.SEMI)) {
                // skip empty statements
            }
            if (check(TokenType.EOF)) break;

            StatementNode stmt = parseStatement();
            if (stmt == null) break;
            program.statements.add(stmt);
            if (pos == prevPos) {
                throw new Exception("Parser is stuck at token: " + peek()); // Debug loop stuck
            }
        }

        return program;
    }
    // parseStatement แก้ให้รองรับ block/if/while/command และปิดท้ายด้วย optional SEMI
    private static StatementNode parseStatement() throws Exception {

        if (isAtEnd()) {
            return null;
        }

        StatementNode stmt;

        if (match(TokenType.LBRACE)) {
            // parse block
            BlockNode block = new BlockNode();
            while (!check(TokenType.RBRACE) && !check(TokenType.EOF)) {
                // ข้าม ; ว่าง ๆ
                while (match(TokenType.SEMI)) {}
                if (check(TokenType.RBRACE) || check(TokenType.EOF)) break;

                block.statements.add(parseStatement());
            }
            consume(TokenType.RBRACE, "Expect '}' after block");
            stmt = block;
        }
        else if (match(TokenType.IF)) {
            IfNode in = new IfNode();
            consume(TokenType.LPAREN, "Expect '(' after if");
            in.condition = parseExpression();
            consume(TokenType.RPAREN, "Expect ')' after condition");
            consume(TokenType.THEN, "Expect 'then'");
            in.thenStmt = parseStatement();
            if (match(TokenType.ELSE)) {
                in.elseStmt = parseStatement();
            }

            stmt = in;
        }
        else if (match(TokenType.WHILE)) {
            WhileNode wn = new WhileNode();
            consume(TokenType.LPAREN, "Expect '(' after while");
            wn.condition = parseExpression();
            consume(TokenType.RPAREN, "Expect ')' after condition");
            wn.body = parseStatement();
            stmt = wn;
        }
        else {
            // parseCommand
            stmt = parseCommand();
        }

        // หลังจบ statement ตัวนี้ ถ้าเจอ ; ให้ consume
        match(TokenType.SEMI);  // optional
        return stmt;
    }
    private static boolean checkNext(TokenType type) {
        if (pos + 1 >= tokens.size()) return false;
        return tokens.get(pos + 1).type == type;
    }
    private static StatementNode parseCommand() throws Exception {
        // Assignment => <identifier> = Expression
        // หรือ Action => done | move dir | shoot dir expr
        System.out.println("Parsing command at token: " + peek());
        if (check(TokenType.IDENT) && checkNext(TokenType.ASSIGN)) {
            Token ident = advance();
            consume(TokenType.ASSIGN, "Expected '='");
            AssignmentNode an = new AssignmentNode();
            an.ident = ident.text;
            an.expr = parseExpression();
            return an;
        }
        else if (match(TokenType.DONE)) {
            ActionNode ac = new ActionNode();
            ac.actionType = ActionNode.ActionType.DONE;
            return ac;
        }
        else if (match(TokenType.MOVE)) {
            System.out.println("MOVE detected, expecting direction...");

            if (!check(TokenType.UP) && !check(TokenType.DOWN) &&!check(TokenType.UPLEFT) && !check(TokenType.UPRIGHT) &&!check(TokenType.DOWNLEFT) && !check(TokenType.DOWNRIGHT)) {
                throw new Exception("MOVE command requires a direction but got: " + peek());
            }

            ActionNode ac = new ActionNode();
            ac.actionType = ActionNode.ActionType.MOVE;
            Token dir = consumeDirection();
            ac.direction = dir.type;
            return ac;
        }
        else if (match(TokenType.SHOOT)) {
            ActionNode ac = new ActionNode();
            ac.actionType = ActionNode.ActionType.SHOOT;
            Token dir = consumeDirection();
            ac.direction = dir.type;
            ac.shootExpr = parseExpression();
            return ac;
        }

        throw new Exception("Unknown statement/command at token: " + peek());
    }

    private static Token consumeDirection() throws Exception {
        // direction -> up/down/upleft/upright/downleft/downright
        System.out.println("Checking direction token before consuming: " + peek());
        if (check(TokenType.UP) || check(TokenType.DOWN) ||
                check(TokenType.UPLEFT) || check(TokenType.UPRIGHT) ||
                check(TokenType.DOWNLEFT) || check(TokenType.DOWNRIGHT))
        {
            Token dir = advance();
            System.out.println("Consumed direction: " + dir.type);
            return dir;
        }
        throw new Exception("Invalid direction. Expected one of [UP, DOWN, UPLEFT, UPRIGHT, DOWNLEFT, DOWNRIGHT], but got: " + peek().type);

    }

    private static ExpressionNode parseExpression() throws Exception {
        // Expression -> Expression + Term | Expression - Term | Term
        ExpressionNode expr = parseTerm();
        while (true) {
            if (match(TokenType.PLUS)) {
                BinaryOpNode bin = new BinaryOpNode();
                bin.op = BinaryOpNode.Op.ADD;
                bin.left = expr;
                bin.right = parseTerm();
                expr = bin;
            }
            else if (match(TokenType.MINUS)) {
                BinaryOpNode bin = new BinaryOpNode();
                bin.op = BinaryOpNode.Op.SUB;
                bin.left = expr;
                bin.right = parseTerm();
                expr = bin;
            }
            else {
                break;
            }
        }
        return expr;
    }

    private static ExpressionNode parseTerm() throws Exception {
        ExpressionNode expr = parseFactor();
        while (true) {
            if (match(TokenType.STAR)) {
                BinaryOpNode bin = new BinaryOpNode();
                bin.op = BinaryOpNode.Op.MUL;
                bin.left = expr;
                bin.right = parseFactor();
                expr = bin;
            }
            else if (match(TokenType.SLASH)) {
                BinaryOpNode bin = new BinaryOpNode();
                bin.op = BinaryOpNode.Op.DIV;
                bin.left = expr;
                bin.right = parseFactor();
                expr = bin;
            }
            else if (match(TokenType.PERCENT)) {
                BinaryOpNode bin = new BinaryOpNode();
                bin.op = BinaryOpNode.Op.MOD;
                bin.left = expr;
                bin.right = parseFactor();
                expr = bin;
            }
            else {
                break;
            }
        }
        return expr;
    }

    private static ExpressionNode parseFactor() throws Exception {
        ExpressionNode left = parsePower();
        while (match(TokenType.CARET)) {
            BinaryOpNode bin = new BinaryOpNode();
            bin.op = BinaryOpNode.Op.POW;
            bin.left = left;
            bin.right = parsePower();
            left = bin;
        }
        return left;
    }

    private static ExpressionNode parsePower() throws Exception {
        if (match(TokenType.LPAREN)) {
            ExpressionNode expr = parseExpression();
            consume(TokenType.RPAREN, "Expect ')' ");
            return expr;
        }
        if (check(TokenType.NUMBER)) {
            Token num = advance();
            return new NumberNode(Long.parseLong(num.text));
        }
        if (check(TokenType.IDENT)) {
            Token id = advance();
            return new VarNode(id.text);
        }
        throw new Exception("Expect expression at token: " + peek());
    }

    private static boolean check(TokenType type) {
        if (isAtEnd()) return false;
        return peek().type == type;
    }
    private static boolean match(TokenType type) {
        if (check(type)) {
            advance();
            return true;
        }
        return false;
    }
    private static Token consume(TokenType type, String msg) throws Exception {
        if (!check(type)) {
            throw new Exception(msg + ", but got " + peek());
        }
        return advance();
    }

    private static Token advance() {
        if (!isAtEnd()) {
            Token token = tokens.get(pos);
            pos++;
            System.out.println("Advancing token to: " + token.type);
            return token;
        }
        return tokens.get(pos - 1);
    }
    private static boolean isAtEnd() {
        return pos >= tokens.size() || peek().type == TokenType.EOF;
    }
    private static Token peek() {
        return tokens.get(pos);
    }

}
