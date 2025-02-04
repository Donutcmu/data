package main.strategy;
import main.game.Board;
import main.game.Minion;

public class DoneCommandNode extends ASTNode {
    @Override
    public void execute(Minion minion, Board board) {
        // End minion's actions
    }
}
