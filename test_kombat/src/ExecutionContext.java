import java.util.HashMap;
import java.util.Map;

public class ExecutionContext {
    private Board board;
    private Player self;
    private Player opponent;
    private Minion currentMinion;
    private Map<String, Long> variables = new HashMap<>();


    public boolean stopMinionActions = false;

    public ExecutionContext(Board b, Player s, Player o, Minion m) {
        this.board = b;
        this.self = s;
        this.opponent = o;
        this.currentMinion = m;
    }

    public Board getBoard() { return board; }
    public Minion getCurrentMinion() { return currentMinion; }
    public Player getSelf() { return self; }
    public Player getOpponent() { return opponent; }

    public long getVariable(String name) {
        long value = variables.getOrDefault(name, 0L);
        //System.out.println("Retrieving variable: " + name + " => " + value);
        return value;
    }
    public void setVariable(String name, long val) {
        //System.out.println("Updating variable: " + name + " = " + val);
        variables.put(name, val);
    }
    public boolean isOpponentMinion(Minion minion) {
        return opponent.getMinions().contains(minion);
    }
}
