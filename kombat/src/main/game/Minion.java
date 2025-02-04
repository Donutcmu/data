package main.game;
import main.strategy.ASTNode;
import main.strategy.Strategy;

public class Minion {
    private String type;
    private int row, col;
    private int hp = 100;
    private Strategy strategy;
    private ASTNode parsedStrategy;

    public Minion(String type, int row, int col, String strategyCode) {
        this.type = type;
        this.row = row;
        this.col = col;
        this.strategy = new Strategy(strategyCode);
        this.parsedStrategy = strategy.getAST();
    }

    public Minion(String type, int row, int col) {
        this.type = type;
        this.row = row;
        this.col = col;
    }

    public void executeStrategy(Board board) {
        if (parsedStrategy != null) {
            parsedStrategy.execute(this, board);
        }
    }

    public void move(String direction, Board board) {
        int newRow = row, newCol = col;
        switch (direction) {
            case "up": newRow--; break;
            case "down": newRow++; break;
        }
        if (board.isValidHex(newRow, newCol) && !board.isOccupied(newRow, newCol)) {
            board.moveMinion(row, col, newRow, newCol);
            row = newRow;
            col = newCol;
        }
    }

    public void attack(String direction, Board board) {
        int targetRow = row, targetCol = col;
        switch (direction) {
            case "up": targetRow--; break;
            case "down": targetRow++; break;
        }
        if (board.isValidHex(targetRow, targetCol) && board.isOccupied(targetRow, targetCol)) {
            Minion target = board.getMinion(targetRow, targetCol);
            int damage = Math.max(10 , 1);//no target.defenseFactor
            target.hp -= damage;
            if (target.hp <= 0) {
                board.removeMinion(targetRow, targetCol);
            }
        }
    }

    public boolean hasNearbyOpponent(Board board) {
        return board.findClosestOpponent(row, col) != null;
    }
}
