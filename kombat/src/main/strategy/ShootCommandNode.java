package main.strategy;

import main.game.Board;
import main.game.Minion;

public class ShootCommandNode extends ASTNode {
    private String direction;

    public ShootCommandNode(String direction) {
        this.direction = direction;
    }

    @Override
    public void execute(Minion minion, Board board) {
        minion.attack(direction, board);
    }

}
