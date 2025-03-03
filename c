import java.io.*;
import java.util.*;

/**
 * ================================
 * 1) ส่วน Config, Player, Board
 * ================================
 */
class Config {
    long spawnCost;
    long hexPurchaseCost;
    long initBudget;
    long initHp;
    long turnBudget;
    long maxBudget;
    long interestPct;
    long maxTurns;
    long maxSpawns;

    public static Config fromFile(String filename) throws IOException {
        Config c = new Config();
        Properties props = new Properties();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            props.load(br);
        }
        c.spawnCost       = Long.parseLong(props.getProperty("spawn_cost", "100"));
        c.hexPurchaseCost = Long.parseLong(props.getProperty("hex_purchase_cost", "1000"));
        c.initBudget      = Long.parseLong(props.getProperty("init_budget", "10000"));
        c.initHp          = Long.parseLong(props.getProperty("init_hp", "100"));
        c.turnBudget      = Long.parseLong(props.getProperty("turn_budget", "90"));
        c.maxBudget       = Long.parseLong(props.getProperty("max_budget", "23456"));
        c.interestPct     = Long.parseLong(props.getProperty("interest_pct", "5"));
        c.maxTurns        = Long.parseLong(props.getProperty("max_turns", "69"));
        c.maxSpawns       = Long.parseLong(props.getProperty("max_spawns", "47"));
        return c;
    }
}

class Player {
    private String name;
    private long budget;
    private long spawnsLeft;
    private long turnsTaken;
    private List<Minion> minions;

    public Player(String name, Config config) {
        this.name = name;
        this.budget = config.initBudget;
        this.spawnsLeft = config.maxSpawns;
        this.turnsTaken = 0;
        this.minions = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public long getBudget() {
        return budget;
    }

    public void setBudget(long newBudget) {
        this.budget = newBudget;
    }

    public long getSpawnsLeft() {
        return spawnsLeft;
    }

    public void decrementSpawn() {
        if (spawnsLeft > 0) spawnsLeft--;
    }

    public long getTurnsTaken() {
        return turnsTaken;
    }

    public void incrementTurns() {
        turnsTaken++;
    }

    public List<Minion> getMinions() {
        return minions;
    }

    public void addMinion(Minion m) {
        minions.add(m);
    }

    public void removeMinion(Minion m) {
        minions.remove(m);
    }

    public int countAliveMinions() {
        int count = 0;
        for (Minion m : minions) {
            if (m.isAlive()) count++;
        }
        return count;
    }
}

class Board {
    // ตัวอย่างใช้ตาราง 8x8 เพื่อความง่าย
    private Minion[][] grid;
    private int rows;
    private int cols;

    public Board(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        grid = new Minion[rows][cols];
    }

    public boolean isValid(int r, int c) {
        return (r >= 0 && r < rows && c >= 0 && c < cols);
    }

    public void placeMinion(Minion m, int r, int c) {
        if (isValid(r,c) && grid[r][c] == null) {
            grid[r][c] = m;
        }
    }

    public void moveMinion(Minion m, int newR, int newC) {
        int oldR = m.getRow();
        int oldC = m.getCol();
        if (isValid(newR,newC) && grid[oldR][oldC] == m && grid[newR][newC] == null) {
            grid[oldR][oldC] = null;
            grid[newR][newC] = m;
            m.move(newR, newC);
        }
    }

    public Minion getMinionAt(int r, int c) {
        if (!isValid(r,c)) return null;
        return grid[r][c];
    }
}

/**
 * ================================
 * 2) ส่วน Minion และ MinionKind
 * ================================
 */
interface MinionKind {
    String getName();
    int getDefenseFactor();
    String getStrategy();
}

class Minion {
    private MinionKind kind;
    private int hp;
    private int row, col;
    private boolean alive;

    private StatementNode parsedStrategyAST;
    private String rawStrategy;

    public Minion(MinionKind kind, int initHp, int row, int col) {
        this.kind = kind;
        this.hp = initHp;
        this.row = row;
        this.col = col;
        this.alive = true;
        this.rawStrategy = kind.getStrategy();

        // parse สคริปต์ของมินเนียนตอนสร้าง
        try {
            this.parsedStrategyAST = Parser.parse(rawStrategy);
        } catch(Exception e) {
            System.out.println("Parse error! Strategy = " + rawStrategy);
            e.printStackTrace();
            // ถ้า parse ไม่ได้ ก็ไม่ทำอะไร
            this.parsedStrategyAST = null;
        }
    }

    public MinionKind getKind() { return kind; }
    public int getHP() { return hp; }
    public int getRow() { return row; }
    public int getCol() { return col; }
    public boolean isAlive() { return alive; }

    public void move(int newR, int newC) {
        this.row = newR;
        this.col = newC;
    }

    public void takeDamage(int dmg) {
        int finalDamage = dmg - kind.getDefenseFactor();
        if (finalDamage < 0) finalDamage = 0;
        this.hp -= finalDamage;
        if (this.hp <= 0) {
            this.hp = 0;
            this.alive = false;
        }
    }

    public void executeStrategy(Board board, Player self, Player opponent) {
        if (!alive) return;
        if (parsedStrategyAST == null) {
            System.out.println("Minion [" + kind.getName() + "] at ("+row+","+col+") has no valid strategy. => done");
            return;
        }

        ExecutionContext ctx = new ExecutionContext(board, self, opponent, this);
        parsedStrategyAST.execute(ctx);
    }
}

/**
 * ================================
 * 3) ส่วน Parser/Interpreter
 * ================================
 */
enum TokenType {
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

// =============================
// AST: StatementNode
// =============================
abstract class StatementNode {
    public abstract void execute(ExecutionContext ctx);
}

class BlockNode extends StatementNode {
    public List<StatementNode> statements = new ArrayList<>();
    @Override
    public void execute(ExecutionContext ctx) {
        for (StatementNode stmt : statements) {
            if (ctx.stopMinionActions) break; // ถ้า done แล้ว ให้หยุด
            stmt.execute(ctx);
        }
    }
}

class AssignmentNode extends StatementNode {
    public String ident;
    public ExpressionNode expr;
    @Override
    public void execute(ExecutionContext ctx) {
        long val = expr.eval(ctx);
        ctx.setVariable(ident, val);
    }
}

class IfNode extends StatementNode {
    public ExpressionNode condition;
    public StatementNode thenStmt;
    public StatementNode elseStmt;
    @Override
    public void execute(ExecutionContext ctx) {
        long condVal = condition.eval(ctx);
        if (condVal != 0) {
            thenStmt.execute(ctx);
        } else {
            elseStmt.execute(ctx);
        }
    }
}

class WhileNode extends StatementNode {
    public ExpressionNode condition;
    public StatementNode body;
    @Override
    public void execute(ExecutionContext ctx) {
        while(condition.eval(ctx) != 0 && !ctx.stopMinionActions) {
            body.execute(ctx);
        }
    }
}

class ActionNode extends StatementNode {
    public enum ActionType { DONE, MOVE, SHOOT }
    public ActionType actionType;
    public TokenType direction;
    public ExpressionNode shootExpr;
    @Override
    public void execute(ExecutionContext ctx) {
        if (ctx.stopMinionActions) return;
        switch(actionType) {
            case DONE:
                ctx.stopMinionActions = true;
                break;
            case MOVE:
                doMove(ctx);
                break;
            case SHOOT:
                doShoot(ctx);
                break;
        }
    }
    private void doMove(ExecutionContext ctx) {
        Minion m = ctx.getCurrentMinion();
        int r = m.getRow();
        int c = m.getCol();
        switch(direction) {
            case UP:        r = r - 1; break;
            case DOWN:      r = r + 1; break;
            case UPLEFT:    r = r - 1; c = c - 1; break;
            case UPRIGHT:   r = r - 1; c = c + 1; break;
            case DOWNLEFT:  r = r + 1; c = c - 1; break;
            case DOWNRIGHT: r = r + 1; c = c + 1; break;
            default: break;
        }
        ctx.getBoard().moveMinion(m, r, c);
    }
    private void doShoot(ExecutionContext ctx) {
        long dmg = shootExpr.eval(ctx);
        Minion m = ctx.getCurrentMinion();
        int r = m.getRow();
        int c = m.getCol();
        switch(direction) {
            case UP:        r = r - 1; break;
            case DOWN:      r = r + 1; break;
            case UPLEFT:    r = r - 1; c = c - 1; break;
            case UPRIGHT:   r = r - 1; c = c + 1; break;
            case DOWNLEFT:  r = r + 1; c = c - 1; break;
            case DOWNRIGHT: r = r + 1; c = c + 1; break;
            default: break;
        }
        Minion target = ctx.getBoard().getMinionAt(r, c);
        if (target != null && target.isAlive()) {
            target.takeDamage((int) dmg);
        }
    }
}

// =============================
// AST: ExpressionNode
// =============================
abstract class ExpressionNode {
    public abstract long eval(ExecutionContext ctx);
}

class NumberNode extends ExpressionNode {
    public long value;
    public NumberNode(long v) { this.value = v; }
    @Override
    public long eval(ExecutionContext ctx) {
        return value;
    }
}

class VarNode extends ExpressionNode {
    public String name;
    public VarNode(String n) { this.name = n; }
    @Override
    public long eval(ExecutionContext ctx) {
        return ctx.getVariable(name);
    }
}

class BinaryOpNode extends ExpressionNode {
    public enum Op { ADD, SUB, MUL, DIV, MOD, POW }
    public ExpressionNode left;
    public ExpressionNode right;
    public Op op;
    @Override
    public long eval(ExecutionContext ctx) {
        long l = left.eval(ctx);
        long r = right.eval(ctx);
        switch(op) {
            case ADD: return l + r;
            case SUB: return l - r;
            case MUL: return l * r;
            case DIV: return (r==0)? 0 : (l / r);
            case MOD: return (r==0)? 0 : (l % r);
            case POW: return (long) Math.pow(l, r);
        }
        return 0;
    }
}

// =============================
// ExecutionContext
// =============================
class ExecutionContext {
    private Board board;
    private Player self;
    private Player opponent;
    private Minion currentMinion;
    private Map<String, Long> variables = new HashMap<>();

    public boolean stopMinionActions = false;

    public ExecutionContext(Board b, Player s, Player o, Minion m) {
        this.board = b;
        this.self = s;
        this.opponent = o;
        this.currentMinion = m;
    }

    public Board getBoard() { return board; }
    public Minion getCurrentMinion() { return currentMinion; }

    public long getVariable(String name) {
        if (!variables.containsKey(name)) return 0;
        return variables.get(name);
    }

    public void setVariable(String name, long val) {
        variables.put(name, val);
    }
}

// =============================
// Parser (ปรับแก้ parseStatement ให้ยืดหยุ่นเรื่อง ;
// =============================
class Parser {
    private static List<Token> tokens;
    private static int pos;

    public static StatementNode parse(String input) throws Exception {
        Lexer lexer = new Lexer(input);
        tokens = new ArrayList<>();
        Token t;
        do {
            t = lexer.nextToken();
            tokens.add(t);
        } while (t.type != TokenType.EOF);
        pos = 0;

        // เราจะ parse หลาย Statement วางรวมใน BlockNode (Program)
        BlockNode program = new BlockNode();

        while (!check(TokenType.EOF)) {
            // ถ้ามี ; ว่าง ๆ เกินมา ให้ข้ามไป
            while (match(TokenType.SEMI)) {
                // skip empty statements
            }
            if (check(TokenType.EOF)) break;

            StatementNode stmt = parseStatement();
            program.statements.add(stmt);
        }

        return program;
    }

    // parseStatement แก้ให้รองรับ block/if/while/command และปิดท้ายด้วย optional SEMI
    private static StatementNode parseStatement() throws Exception {
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
            consume(TokenType.ELSE, "Expect 'else'");
            in.elseStmt = parseStatement();
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

    private static StatementNode parseCommand() throws Exception {
        // Assignment => <identifier> = Expression
        // หรือ Action => done | move dir | shoot dir expr
        if (check(TokenType.IDENT)) {
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
        if (check(TokenType.UP) || check(TokenType.DOWN) ||
                check(TokenType.UPLEFT) || check(TokenType.UPRIGHT) ||
                check(TokenType.DOWNLEFT) || check(TokenType.DOWNRIGHT))
        {
            return advance();
        }
        throw new Exception("Expect direction but got: " + peek());
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

    // --------------------
    // Helper
    // --------------------
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
        if (!isAtEnd()) pos++;
        return tokens.get(pos-1);
    }
    private static boolean isAtEnd() {
        return peek().type == TokenType.EOF;
    }
    private static Token peek() {
        return tokens.get(pos);
    }
}

/**
 * ================================
 * 4) ตัวอย่าง MinionKind 4 ชนิด
 * ================================
 */
class MeleeMinion implements MinionKind {
    @Override
    public String getName() { return "Melee"; }
    @Override
    public int getDefenseFactor() { return 2; }
    @Override
    public String getStrategy() {
        // แก้ไขเป็นคนละ statement ชัดเจน ปิดท้ายด้วย ; (optional semicolon หลัง done ก็ได้)
        return ""
                + "move up;\n"
                + "move upright;\n"
                + "shoot downleft 10;\n"
                + "done;";
    }
}

class RangedMinion implements MinionKind {
    @Override
    public String getName() { return "Ranged"; }
    @Override
    public int getDefenseFactor() { return 0; }
    @Override
    public String getStrategy() {
        // ตัวอย่าง if/then/else + block
        return ""
                + "if (1) then {\n"
                + "    move up;\n"
                + "} else {\n"
                + "    move down;\n"
                + "}\n"
                + "done;";
    }
}

class TankMinion implements MinionKind {
    @Override
    public String getName() { return "Tank"; }
    @Override
    public int getDefenseFactor() { return 5; }
    @Override
    public String getStrategy() {
        // while(3) { move up; }
        // done;
        return ""
                + "while(3) {\n"
                + "    move up;\n"
                + "}\n"
                + "done;";
    }
}

class AssassinMinion implements MinionKind {
    @Override
    public String getName() { return "Assassin"; }
    @Override
    public int getDefenseFactor() { return 1; }
    @Override
    public String getStrategy() {
        // x = 10;
        // while(x) {
        //    x = x - 1;
        //    move up;
        // }
        // shoot down 5;
        // done;
        return ""
                + "x = 10;\n"
                + "while(x) {\n"
                + "    x = x - 1;\n"
                + "    move up;\n"
                + "}\n"
                + "shoot down 5;\n"
                + "done;";
    }
}

/**
 * ================================
 * 5) คลาสหลัก (Main)
 * ================================
 */
public class KombatGameExample2 {
    public static void main(String[] args) {
        try {
            // 1) โหลด config (แก้ path ตามต้องการ)
            Config config = Config.fromFile("C:\\Users\\ASUS ROG\\IdeaProjects\\KombatGameExample\\src\\config.txt");

            // 2) สร้าง Player
            Player p1 = new Player("Player1", config);
            Player p2 = new Player("Player2", config);

            // 3) Board 8x8
            Board board = new Board(8, 8);

            // 4) สร้าง MinionKind
            MinionKind mkMelee    = new MeleeMinion();
            MinionKind mkRanged   = new RangedMinion();
            MinionKind mkTank     = new TankMinion();
            MinionKind mkAssassin = new AssassinMinion();

            // 5) spawn มินเนียน
            spawnMinion(p1, board, mkMelee, config, 6,0);
            spawnMinion(p1, board, mkTank, config, 7,0);
            spawnMinion(p2, board, mkRanged, config, 0,7);
            spawnMinion(p2, board, mkAssassin, config, 1,7);

            // 6) วนเทิร์น
            long turnCount = 0;
            while (turnCount < config.maxTurns) {
                turnCount++;
                System.out.println("\n=== TURN " + turnCount + " ===");

                // เทิร์น P1
                takeTurn(p1, p2, board, config);
                if (checkGameOver(p1, p2, turnCount, config)) break;

                // เทิร์น P2
                takeTurn(p2, p1, board, config);
                if (checkGameOver(p1, p2, turnCount, config)) break;
            }

            // สรุปผล
            System.out.println("\nGame ended at turn " + turnCount);
            int p1Alive = p1.countAliveMinions();
            int p2Alive = p2.countAliveMinions();
            System.out.println("Player1 alive minions: " + p1Alive);
            System.out.println("Player2 alive minions: " + p2Alive);
            if (p1Alive > p2Alive) {
                System.out.println("Player1 wins!");
            } else if (p2Alive > p1Alive) {
                System.out.println("Player2 wins!");
            } else {
                System.out.println("Draw or tie!");
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private static void spawnMinion(Player player, Board board, MinionKind kind, Config config, int row, int col) {
        if (player.getSpawnsLeft() > 0 && player.getBudget() >= config.spawnCost) {
            player.setBudget(player.getBudget() - config.spawnCost);
            player.decrementSpawn();
            Minion m = new Minion(kind, (int) config.initHp, row, col);
            board.placeMinion(m, row, col);
            player.addMinion(m);
            System.out.println(player.getName() + " spawned a [" + kind.getName() + "] at (" + row + "," + col + ").");
        }
    }

    private static void takeTurn(Player current, Player other, Board board, Config config) {
        current.incrementTurns();
        long oldBudget = current.getBudget();
        long interest = (long) (oldBudget * (config.interestPct/100.0));
        long newBudget = oldBudget + config.turnBudget + interest;
        if (newBudget > config.maxBudget) newBudget = config.maxBudget;
        current.setBudget(newBudget);

        // สั่งมินเนียน
        for (Minion m : current.getMinions()) {
            if (!m.isAlive()) continue;
            m.executeStrategy(board, current, other);
        }
    }

    private static boolean checkGameOver(Player p1, Player p2, long turnCount, Config config) {
        if (p1.countAliveMinions() == 0 || p2.countAliveMinions() == 0) {
            return true;
        }
        if (turnCount >= config.maxTurns) {
            return true;
        }
        return false;
    }
}
