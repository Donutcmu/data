public class Minion {
    private Player owner;
    private int row;
    private int col;
    private long hp;
    private long defenseFactor;
    private MinionStrategy strategy;

    public Minion(Player owner, int row, int col, long hp, long defenseFactor, MinionStrategy strategy) {
        this.owner = owner;
        this.row = row;
        this.col = col;
        this.hp = hp;
        this.defenseFactor = defenseFactor;
        this.strategy = strategy;
    }

    public Player getOwner() {
        return owner;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public void setPosition(int newRow, int newCol) {
        this.row = newRow;
        this.col = newCol;
    }

    public long getHp() {
        return hp;
    }

    public void setHp(long hp) {
        this.hp = hp;
    }

    public long getDefenseFactor() {
        return defenseFactor;
    }

    public MinionStrategy getStrategy() {
        return strategy;
    }

    public boolean isAlive() {
        return hp > 0;
    }
}
