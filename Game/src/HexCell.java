public class HexCell {
    private final int row;
    private final int col;
    private Minion occupant;
    private Player owner;  // ใครครอบครองช่องนี้อยู่ (null = ไม่มีใคร)

    public HexCell(int row, int col) {
        this.row = row;
        this.col = col;
        this.occupant = null;
        this.owner = null;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public Minion getOccupant() {
        return occupant;
    }

    public void setOccupant(Minion occupant) {
        this.occupant = occupant;
    }

    public boolean isOccupied() {
        return occupant != null;
    }

    public Player getOwner() {
        return owner;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }
}
