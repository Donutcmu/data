import java.util.ArrayList;
import java.util.List;

public class Player {
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
