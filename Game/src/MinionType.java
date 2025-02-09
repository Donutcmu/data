public enum MinionType {
    MELEE(100, 100, 2),
    RANGED(150, 80, 1),
    TANK(200, 150, 5),
    ASSASSIN(180, 60, 0);

    private long cost;
    private long baseHp;
    private long defenseFactor;

    MinionType(long cost, long baseHp, long defenseFactor) {
        this.cost = cost;
        this.baseHp = baseHp;
        this.defenseFactor = defenseFactor;
    }

    public long getCost() {
        return cost;
    }

    public long getBaseHp() {
        return baseHp;
    }

    public long getDefenseFactor() {
        return defenseFactor;
    }
}
