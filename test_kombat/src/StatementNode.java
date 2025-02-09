import java.util.ArrayList;
import java.util.List;

abstract class StatementNode {
    public abstract void execute(ExecutionContext ctx);
}

class BlockNode extends StatementNode {
    public List<StatementNode> statements = new ArrayList<>();
    @Override
    public void execute(ExecutionContext ctx) {
        for (StatementNode stmt : statements) {
            if (ctx.stopMinionActions){
                System.out.println("minion stopped");
                break;
            } // ถ้า done แล้ว ให้หยุด
            //System.out.println("Executing statement: " + stmt.getClass().getSimpleName());
            stmt.execute(ctx);
        }
    }
}

class AssignmentNode extends StatementNode {
    public String ident;
    public ExpressionNode expr;
    @Override
    public void execute(ExecutionContext ctx) {
        long newValue = expr.eval(ctx);
        //System.out.println("Assigning " + ident + " = " + newValue);
        ctx.setVariable(ident, newValue);
    }
}

class IfNode extends StatementNode {
    public ExpressionNode condition;
    public StatementNode thenStmt;
    public StatementNode elseStmt;
    @Override
    public void execute(ExecutionContext ctx) {
        long condVal = condition.eval(ctx);
        if (condVal != 0) {
            thenStmt.execute(ctx);
        } else {
            elseStmt.execute(ctx);
        }
    }
}

class WhileNode extends StatementNode {
    public ExpressionNode condition;
    public StatementNode body;
    @Override
    public void execute(ExecutionContext ctx) {
        int loopCount = 0;
        long conditionValue = condition.eval(ctx);
        //System.out.println("Entering WHILE loop with condition: " + " => " + conditionValue);

        while(condition.eval(ctx) != 0 && !ctx.stopMinionActions) {
            //System.out.println("Executing while-loop iteration " + loopCount);
            body.execute(ctx);
            conditionValue = condition.eval(ctx);
            loopCount++;
            if (loopCount > 13) { // Failsafe
                System.out.println("ERROR: While loop stuck in infinite loop!");
                break;
            }
        }
    }
}

class ActionNode extends StatementNode {
    public enum ActionType { DONE, MOVE, SHOOT }
    public ActionType actionType;
    public TokenType direction;
    public ExpressionNode shootExpr;
    @Override
    public void execute(ExecutionContext ctx) {
        if (ctx.stopMinionActions) return;
        switch(actionType) {
            case DONE:
                ctx.stopMinionActions = true;
                break;
            case MOVE:
                doMove(ctx);
                break;
            case SHOOT:
                doShoot(ctx);
                break;
        }
    }
    private void doMove(ExecutionContext ctx) {
        Minion m = ctx.getCurrentMinion();
        int oldRow = m.getRow();
        int oldCol = m.getCol();
        int newRow = oldRow;
        int newCol = oldCol;

        switch(direction) {
            case UP:        newRow = oldRow - 1; break;
            case DOWN:      newRow = oldRow + 1; break;
            case UPLEFT:    newRow = oldRow - 1; newCol = oldCol - 1; break;
            case UPRIGHT:   newRow = oldRow - 1; newCol = oldCol + 1; break;
            case DOWNLEFT:  newRow = oldRow + 1; newCol = oldCol - 1; break;
            case DOWNRIGHT: newRow = oldRow + 1; newCol = oldCol + 1; break;
            default: break;
        }

        System.out.println("Minion at (" + oldRow + "," + oldCol + ") attempting move to (" + newRow + "," + newCol + ")");

        ctx.getBoard().moveMinion(m, newRow, newCol);

        // Check if movement was successful
        System.out.println("Minion final position: (" + m.getRow() + "," + m.getCol() + ")");
    }
    private void doShoot(ExecutionContext ctx) {
        long dmg = shootExpr.eval(ctx);
        System.out.println("do shoot");
        Minion m = ctx.getCurrentMinion();
        int r = m.getRow();
        int c = m.getCol();
        switch(direction) {
            case UP:        r = r - 1; break;
            case DOWN:      r = r + 1; break;
            case UPLEFT:    r = r - 1; c = c - 1; break;
            case UPRIGHT:   r = r - 1; c = c + 1; break;
            case DOWNLEFT:  r = r + 1; c = c - 1; break;
            case DOWNRIGHT: r = r + 1; c = c + 1; break;
            default: break;
        }
        if (!ctx.getBoard().isValid(r, c, true)) {
            System.out.println("No enemy to shoot at (" + r + "," + c + ")");
            return;
        }

        Minion target = ctx.getBoard().getMinionAt(r, c);

        if (target != null && target.isAlive()) {
            if (ctx.isOpponentMinion(target)) {
                target.takeDamage(50);

                System.out.println(Main.ANSI_GREEN+"Minion at (" + r + "," + c + ") took " + dmg + " damage. HP left: " + target.getHP()+ Main.ANSI_RESET);

                if (!target.isAlive()) {
                    System.out.println("Minion at (" + r + "," + c + ") has died!");
                }
            } else {
                System.out.println("Friendly fire prevented: Minion at (" + r + "," + c + ") is an ally.");
            }
        }else {
            System.out.println("No valid target to shoot at (" + r + "," + c + ")");
        }
    }
}
