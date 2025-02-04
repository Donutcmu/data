package main.strategy;

import main.game.Board;
import main.game.Minion;

public class IfStatementNode extends ASTNode {
    private ASTNode condition, thenBranch, elseBranch;

    public IfStatementNode(ASTNode condition, ASTNode thenBranch, ASTNode elseBranch) {
        this.condition = condition;
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
    }

    @Override
    public void execute(Minion minion, Board board) {
        if (((BooleanExpressionNode) condition).evaluate(minion, board)) {
            thenBranch.execute(minion, board);
        } else {
            elseBranch.execute(minion, board);
        }
    }

}
