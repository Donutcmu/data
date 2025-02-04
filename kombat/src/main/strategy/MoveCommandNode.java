package main.strategy;

import main.game.Board;
import main.game.Minion;

public class MoveCommandNode extends ASTNode {
    private String direction;

    public MoveCommandNode(String direction) {
        this.direction = direction;
    }

    @Override
    public void execute(Minion minion, Board board) {
        minion.move(direction, board);
    }
}
