package sample.Expressions;

public class Div extends Expression {
    @Override
    public double getVal() {
            return leftExpression.getVal() / rightExpression.getVal();
    }

    @Override
    public Expression getDerivative(String var) {
        if (this.contains(var)) {
                return new Div(
                        new Sub(
                                new Mul(leftExpression.getDerivative(var), rightExpression),
                                new Mul(rightExpression.getDerivative(var), leftExpression)),
                        new Mul(rightExpression, rightExpression));
            }
        return new Val(0);
    }

    @Override
    public Expression getOptimized() throws CloneNotSupportedException {
        Expression expression = super.getOptimized();
        if (expression.type == Type.VALUE)
            return expression;
        if (expression.leftExpression.type == Type.VALUE) {
            if (expression.rightExpression.type == Type.VALUE) {
                return new Val(expression.leftExpression.val / expression.rightExpression.val);
            }
        } else if (expression.rightExpression.type == Type.VALUE) {
            if (expression.rightExpression.val == 1) {
                return expression.leftExpression;
            }
        }

        return expression;
    }

    @Override
    public Expression getIntegral() {
        return null;
    }

    public Div(Expression left, Expression right) {
        super(0, "Div", Type.FUNCTION, ArgumentPosition.LEFT_AND_RIGHT, 20,2, left, right);
    }

    @Override
    public boolean fillExpressions() {
        if (leftExpression == null) {
            leftExpression = new Val(0);
        }
        if (rightExpression == null) {
            rightExpression = new Val(1);
        }
        return true;
    }

    @Override
    public String toString() {
        String sLeft = leftExpression.toString();
        String sRight = rightExpression.toString();
        if (leftExpression.priority > priority || true) {
            sLeft = "("+sLeft+")";
        }
        if (rightExpression.priority > priority || true) {
            sRight = "("+sRight+")";
        }
        return sLeft + "/" + sRight;
    }




}
