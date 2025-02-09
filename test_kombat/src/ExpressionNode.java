abstract class ExpressionNode {
    public abstract long eval(ExecutionContext ctx);
}

class NumberNode extends ExpressionNode {
    public long value;
    public NumberNode(long v) { this.value = v; }
    @Override
    public long eval(ExecutionContext ctx) {
        //System.out.println("Evaluating number: " + value);
        return value;
    }
}

class VarNode extends ExpressionNode {
    public String name;
    public VarNode(String n) { this.name = n; }
    @Override
    public long eval(ExecutionContext ctx) {
        long value = ctx.getVariable(name); // Fetch variable from context
        //System.out.println("Evaluating variable: " + name + " => " + value);
        return value;
    }
}

class BinaryOpNode extends ExpressionNode {
    public enum Op { ADD, SUB, MUL, DIV, MOD, POW }
    public ExpressionNode left;
    public ExpressionNode right;
    public Op op;
    @Override
    public long eval(ExecutionContext ctx) {
        long l = left.eval(ctx);
        long r = right.eval(ctx);
        long result = 0;

        switch(op) {
            case ADD:
                result = l + r;
                break;
            case SUB:
                result = l - r;
                break;
            case MUL:
                result = l * r;
                break;
            case DIV:
                result = (r == 0) ? 0 : (l / r);
                break;
            case MOD:
                result = (r == 0) ? 0 : (l % r);
                break;
            case POW:
                result = (long) Math.pow(l, r);
                break;
        }
        // Debug output
        //System.out.println("Evaluating BinaryOpNode: " + l + " " + op + " " + r + " => " + result);

        return result;
    }
}