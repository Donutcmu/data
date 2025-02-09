import java.util.ArrayList;
import java.util.List;

public class HexBoard {
    public static final int BOARD_SIZE = 8; // 8x8

    private HexCell[][] cells;
    private int[] getHexDelta(int row, int col, String direction) {
        boolean oddRow = (row % 2 != 0);
        switch (direction) {
            case "up":        return new int[]{-1,  0};
            case "down":      return new int[]{+1,  0};
            case "upleft":    return oddRow ? new int[]{-1, -1} : new int[]{-1, 0};
            case "upright":   return oddRow ? new int[]{-1,  0} : new int[]{-1, +1};
            case "downleft":  return oddRow ? new int[]{+1, -1} : new int[]{+1,  0};
            case "downright": return oddRow ? new int[]{+1,  0} : new int[]{+1, +1};
            default:          return new int[]{0,0}; // เผื่อกรณี direction ผิด
        }
    }
    public HexBoard() {
        cells = new HexCell[BOARD_SIZE][BOARD_SIZE];
        for (int r = 0; r < BOARD_SIZE; r++) {
            for (int c = 0; c < BOARD_SIZE; c++) {
                cells[r][c] = new HexCell(r, c);
            }
        }
    }

    public HexCell getCell(int row, int col) {
        if (row < 0 || row >= BOARD_SIZE || col < 0 || col >= BOARD_SIZE) {
            return null;
        }
        return cells[row][col];
    }

    public boolean isInsideBoard(int row, int col) {
        return (row >= 0 && row < BOARD_SIZE && col >= 0 && col < BOARD_SIZE);
    }

    // ตัวอย่างเมธอดหกทิศ (บางกรณี)
    public List<HexCell> getAdjacentCells(int row, int col) {
        List<HexCell> adj = new ArrayList<>();
        String[] directions = {"up","down","upleft","upright","downleft","downright"};
        for (String dir : directions) {
            int[] delta = getHexDelta(row, col, dir);
            int nr = row + delta[0];
            int nc = col + delta[1];
            if (isInsideBoard(nr, nc)) {
                adj.add(cells[nr][nc]);
            }
        }
        return adj;
    }
}
