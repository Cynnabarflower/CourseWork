package sample.Expressions;

public class Sub extends Expression {

    @Override
    public double getVal() {
        return leftExpression.getVal() - rightExpression.getVal();
    }

    @Override
    public Expression getDerivative(String var) {
        if (this.contains(var)) {
            return new Sub(leftExpression.getDerivative(var), rightExpression.getDerivative(var));
        }
        return new Val(0);
    }
    @Override
    public Expression getIntegral() {
        return null;
    }

    public Sub(Expression left, Expression right) {
        super(0, "Sub", Type.FUNCTION, ArgumentPosition.LEFT_AND_RIGHT,2,2, left, right);
    }
    public Sub() {
        super(0, "Sub", Type.FUNCTION, ArgumentPosition.LEFT_AND_RIGHT,2,2, new Val(0), new Val(0));
    }

    @Override
    public String toString() {
        String sLeft = leftExpression.toString();
        String sRight = rightExpression.toString();
        if (leftExpression.priority > priority) {
            sLeft = "("+sLeft+")";
        }
        if (rightExpression.priority > priority) {
            sRight = "("+sRight+")";
        }
        return sLeft + "-" + sRight;
    }
}