package main.game;

public class Board {
    private Minion[][] grid = new Minion[8][8];

    public boolean isOccupied(int row, int col) { return grid[row][col] != null; }

    public boolean isValidHex(int row, int col) { return row >= 0 && row < 8 && col >= 0 && col < 8; }

    public void placeMinion(Minion minion, int row, int col) {
        if (isValidHex(row, col) && !isOccupied(row, col)) {
            grid[row][col] = minion;
        }
    }

    public void moveMinion(int oldRow, int oldCol, int newRow, int newCol) {
        if (isValidHex(newRow, newCol) && !isOccupied(newRow, newCol)) {
            grid[newRow][newCol] = grid[oldRow][oldCol];
            grid[oldRow][oldCol] = null;
        }
    }
    public Minion getMinion(int row, int col) {
        return isValidHex(row, col) ? grid[row][col] : null;
    }

    public void removeMinion(int row, int col) {
        if (isValidHex(row, col)) {
            grid[row][col] = null;
        }
    }

    public Minion findClosestOpponent(int row, int col) {
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                if (grid[r][c] != null && !grid[r][c].equals(grid[row][col])) {
                    return grid[r][c]; // Return first found opponent
                }
            }
        }
        return null;
    }
}
