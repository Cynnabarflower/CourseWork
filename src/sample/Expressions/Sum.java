package sample.Expressions;

public class Sum extends Expression {
    public Sum(Expression leftExpression, Expression rightExpression) {
        super(0, "Sum", Type.FUNCTION, ArgumentPosition.LEFT_AND_RIGHT, 30, 2, leftExpression, rightExpression);
    }

    public Sum() {
        super(0, "Sum", Type.FUNCTION, ArgumentPosition.LEFT_AND_RIGHT, 30, 2,new Val(0), new Val(0));
    }

    @Override
    public double getVal() {
        return leftExpression.getVal() + rightExpression.getVal();
    }

    @Override
    public Expression getDerivative(String var) {
        if (this.contains(var)) {
                return new Sum(leftExpression.getDerivative(var), rightExpression.getDerivative(var));
            }
        return new Val(0);
    }

    @Override
    public Expression getIntegral() {
        return null;
    }

    @Override
    public Expression getOptimized() throws CloneNotSupportedException {
        Expression expression = super.getOptimized();
        if (expression.type == Type.VALUE)
            return expression;
        if (expression.leftExpression.type == Type.VALUE && expression.rightExpression.type == Type.VALUE) {
            return new Val(expression.leftExpression.val + expression.rightExpression.val);
        }
        if (expression.leftExpression.type == Type.VALUE && expression.leftExpression.val == 0) {
            return expression.rightExpression;
        }
        if (expression.rightExpression.type == Type.VALUE && expression.rightExpression.val == 0) {
            return expression.leftExpression;
        }
        return expression;
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
        return sLeft + "+" + sRight;
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
}
