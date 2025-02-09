public class Board {
    private Minion[][] grid;
    private int rows;
    private int cols;

    public Board(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        grid = new Minion[rows][cols];
    }

    public void printBoard() {
        System.out.println("Current Board State:");

        removeMinion();
        String [] player = new String[2];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {


                if (grid[r][c] == null) {
                    System.out.print("[ ] "); // Empty space
                } else {
                    String owner = grid[r][c].getOwnerName();

                    if (player[0] == null) {
                        player[0] = owner; // First player detected
                    } else if (player[1] == null && !owner.equals(player[0])) {
                        player[1] = owner; // Second player detected
                    }

                    if (owner.equals(player[0])) {
                        System.out.print(Main.ANSI_CYAN + "[M] " + Main.ANSI_RESET);
                    } else if (owner.equals(player[1])) {
                        System.out.print(Main.ANSI_RED+"[M] "+Main.ANSI_RESET);
                    }



                }
            }
            System.out.println(); // New line for each row
        }
        System.out.println();
    }



    //overload use for attack
    public boolean isValid(int r, int c, boolean allowOccupied) {
        boolean withinBounds = (r >= 0 && r < rows) && (c >= 0 && c < cols);

        if (!withinBounds) {
            System.out.println("Invalid position: (" + r + "," + c + ") is out of bounds.");
            return false;
        }

        boolean isEmpty = (grid[r][c] == null);

        // Allow occupied tiles if it's for shooting (not for movement)
        if (!isEmpty && allowOccupied) {
            System.out.println("Valid target at (" + r + "," + c + ") for shooting.");
            return true;
        }

        return isEmpty;
    }


    public void placeMinion(Minion m, int r, int c) {
        if (isValid(r,c,false) && grid[r][c] == null) {
            grid[r][c] = m;
        }
    }

    public void moveMinion(Minion m, int newR, int newC) {
        int oldR = m.getRow();
        int oldC = m.getCol();

        //System.out.println("Attempting to move Minion [" + m + "] from (" + oldR + "," + oldC + ") to (" + newR + "," + newC + ")");

        if (!isValid(newR, newC,false)) {
            System.out.println("Move blocked! (" + newR + "," + newC + ") is not a valid position.");
            return;
        }

        if (grid[oldR][oldC] != m) {
            System.out.println("ERROR: Expected minion at (" + oldR + "," + oldC + "), but found something else!");
            return;
        }

        if (grid[newR][newC] != null) {
            System.out.println("Move failed! Destination (" + newR + "," + newC + ") is occupied.");
            return;
        }

        // Perform move
        grid[oldR][oldC] = null;
        grid[newR][newC] = m;
        m.move(newR, newC);

        System.out.println("Move successful! Minion is now at (" + m.getRow() + "," + m.getCol() + ")");
    }

    public Minion getMinionAt(int r, int c) {
        if (!isValid(r,c,true)) return null;
        return grid[r][c];
    }

    public Minion findClosestOpponent(int row, int col, Player self) {
        Minion closest = null;
        int minDistance = Integer.MAX_VALUE;

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Minion m = getMinionAt(r, c);
                if (m != null && !self.getMinions().contains(m)) { // Ensure it's an opponent
                    int distance = Math.abs(r - row) + Math.abs(c - col);
                    if (distance < minDistance) {
                        minDistance = distance;
                        closest = m;
                    }
                }
            }
        }
        return closest;
    }

    public void removeMinion() {
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                if(grid[r][c] != null) {
                    Minion m = getMinionAt(r, c);
                    if(!m.isAlive()){
                        grid[r][c] = null;
                    }
                }

            }
        }
    }


}
