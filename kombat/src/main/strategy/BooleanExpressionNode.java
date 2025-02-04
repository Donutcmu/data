package main.strategy;

import main.game.Board;
import main.game.Minion;

public class BooleanExpressionNode extends ASTNode {
    private String condition;

    public BooleanExpressionNode(String condition) {
        this.condition = condition;
    }

    public boolean evaluate(Minion minion, Board board) {
        return condition.equals("opponent") && minion.hasNearbyOpponent(board);
    }

    @Override
    public void execute(Minion minion, Board board) {
        // Do nothing, used for conditions
    }
}
