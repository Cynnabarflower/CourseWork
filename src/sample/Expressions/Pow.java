package sample.Expressions;

import java.util.ArrayList;

public class Pow extends Expression {
    @Override
    public double getVal() {
        return Math.pow(leftExpression.getVal(), rightExpression.getVal());
    }

    @Override
    public Expression getDerivative(String var) {
        if (this.contains(var)) {
                return new Mul(
                        new Pow(leftExpression, new Sub(rightExpression, new Val(1))),
                        new Sum(
                                new Mul(rightExpression, leftExpression.getDerivative(var)),
                                new Mul(leftExpression, new Mul(new Log(new Val(Math.E), leftExpression), rightExpression.getDerivative(var)))
                        ));
            }
        return new Val(0);
    }

    @Override
    public Expression getIntegral() {
        return null;
    }

    public Pow(Expression left, Expression right) {
        super(0, "Pow", Type.FUNCTION, ArgumentPosition.LEFT_AND_RIGHT, 10, 2, left, right);
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
        return sLeft + "^" + sRight;
    }
}
