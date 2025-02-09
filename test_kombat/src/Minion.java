public class Minion {
    private MinionKind kind;
    private int hp;
    private int row, col;
    private boolean alive;
    private String ownerName;
    private StatementNode parsedStrategyAST;
    private String rawStrategy;

    public Minion(MinionKind kind, int initHp, int row, int col,String ownerName) {
        this.ownerName = ownerName;
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
    public String getOwnerName() { return ownerName; }
    public boolean isAlive() { return alive; }

    public void move(int newR, int newC) {
        System.out.println("Updating minion position from (" + row + "," + col + ") to (" + newR + "," + newC + ")");
        this.row = newR;
        this.col = newC;
    }
    public void takeDamage(int dmg) {
        int finalDamage = dmg - kind.getDefenseFactor();
        if (finalDamage < 0) finalDamage = 0;
        this.hp -= finalDamage;
        System.out.println("Minion at (" + row + "," + col + ") took damage. HP left: " + hp);
        if (this.hp <= 0) {
            this.hp = 0;
            this.alive = false;
            System.out.println("Minion at (" + row + "," + col + ") has died!");
        }
    }
    public void executeStrategy(Board board, Player self, Player opponent) {
        if (!alive) return;
        if (parsedStrategyAST == null) {
            System.out.println("Minion [" + kind.getName() + "] at ("+row+","+col+") has no valid strategy. => done");
            return;
        }
        //System.out.println("Executing strategy for Minion [" + kind.getName() + "] at ("+row+","+col+")...");
        ExecutionContext ctx = new ExecutionContext(board, self, opponent, this);
        parsedStrategyAST.execute(ctx);
        //System.out.println("Finished executing strategy for Minion [" + kind.getName() + "] at ("+row+","+col+").");
    }
}
