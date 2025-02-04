package main.strategy;

import main.game.Board;
import main.game.Minion;

public abstract class ASTNode {
    public abstract void execute(Minion minion, Board board);
}


 