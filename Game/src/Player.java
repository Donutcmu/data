import java.util.ArrayList;
import java.util.List;

public class Player {
    private String name;
    private long budget;
    private long maxBudget;
    private long spawnsUsed;
    private long maxSpawns;
    private List<Minion> minions;

    public Player(String name, long initialBudget, long maxBudget, long maxSpawns) {
        this.name = name;
        this.budget = initialBudget;
        this.maxBudget = maxBudget;
        this.maxSpawns = maxSpawns;
        this.minions = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public long getBudget() {
        return budget;
    }

    public void setBudget(long newBudget) {
        this.budget = Math.min(newBudget, maxBudget);
    }

    public long getMaxBudget() {
        return maxBudget;
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

    public long getSpawnsUsed() {
        return spawnsUsed;
    }

    public long getMaxSpawns() {
        return maxSpawns;
    }

    public void incrementSpawnsUsed() {
        spawnsUsed++;
    }

    public boolean canSpawnMore() {
        return spawnsUsed < maxSpawns;
    }

    // ดอกเบี้ยแบบง่าย (หากจะให้เต็มตามสเปคต้องปรับ)
    public void applyInterest(long turnCount, long interestPct) {
        double m = (double) this.budget;
        double r = (double) interestPct;
        double interest = m * (r / 100.0);
        long added = (long) interest;
        setBudget(this.budget + added);
    }
}
