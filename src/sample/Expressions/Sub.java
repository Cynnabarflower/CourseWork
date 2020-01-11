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

    public Sub(Expression right) {
        super(0, "Sub", Type.FUNCTION, ArgumentPosition.RIGHT,0,1, new Val(0), null);
    }


    @Override
    public String toString() {
        String sLeft = leftExpression.toString();
        String sRight = rightExpression.toString();
        if (leftExpression.priority > priority) {
            sLeft = "("+sLeft+")";
        } else if (leftExpression.type == Type.VALUE && leftExpression.val == 0) {
            sLeft = "";
        }
        if (rightExpression.priority > priority) {
            sRight = "("+sRight+")";
        }
        return sLeft + "-" + sRight;
    }

    @Override
    public boolean fillExpressions() {
        if (leftExpression == null) {
            leftExpression = new Val(0);
        }
        if (rightExpression == null) {
            rightExpression = new Val(0);
        }
        return true;
    }

    @Override
    public Expression getOptimized() throws CloneNotSupportedException {
        Expression expression = super.getOptimized();
        if (expression.type == Type.VALUE)
            return expression;
        if (expression.rightExpression.type == Type.VALUE) {
            if (expression.rightExpression.val == 0) {
                return expression.leftExpression;
            }
            if (expression.leftExpression.type == Type.VALUE) {
                return new Val(expression.leftExpression.val - expression.rightExpression.val);
            }
        }
        return expression;
    }
}