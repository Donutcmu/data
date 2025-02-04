package main.game;

import java.util.ArrayList;
import java.util.List;


public class Player {
    private String name;
    private int budget;
    private List<Minion> minions = new ArrayList<>();

    public Player(String name, int budget) { this.name = name; this.budget = budget; }

    public String getName() { return name; }
    public int getBudget() { return budget; }
    public List<Minion> getMinions() { return minions; }

    public void updateBudget() { budget += 100; } // Gain budget per turn

    public void spawnMinion(String type, int row, int col, Board board) {
        if (budget >= 100) {
            Minion minion = new Minion(type, row, col);
            board.placeMinion(minion, row, col);
            minions.add(minion);
            budget -= 100;
        }
    }

    public void takeTurn(Board board) {
        for (Minion minion : minions) {
            minion.executeStrategy(board);
        }
    }
}
