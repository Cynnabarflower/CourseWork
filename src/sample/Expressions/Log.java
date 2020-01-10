package sample.Expressions;

import javafx.util.Pair;

import java.util.ArrayList;

public class Log extends Expression {


    @Override
    public double getVal() {

        return Math.log(rightExpression.getVal())/Math.log(leftExpression.getVal());
    }

    @Override
    public Expression getDerivative(String var) {
        if (this.contains(var)) {
                return new Div(
                        new Sub(
                                new Div(
                                        new Mul(new Log(leftExpression), rightExpression.getDerivative(var)),
                                        rightExpression
                                ),
                                new Div(
                                        new Mul(leftExpression.getDerivative(var), new Log(rightExpression.getDerivative(var))),
                                        leftExpression
                                )
                        ),
                        new Mul(new Log(leftExpression), new Log(leftExpression))
                );
            }
        return new Val(0);
    }

    @Override
    public Expression getIntegral() {
        return null;
    }

    public Log(Expression left, Expression right) {
        super(0, "Log", Type.FUNCTION, ArgumentPosition.RIGHT, 1, 2, left, right);
    }

    public Log(Expression right) {
        super(0, "Ln", Type.FUNCTION, ArgumentPosition.RIGHT, 1, 1, new Val(Math.E), right);
    }

    public Log() {
        super(0, "Ln", Type.FUNCTION, ArgumentPosition.RIGHT, 1, 1, new Val(Math.E), null);
    }

    @Override
    public String toString() {
        if (name.equals("Log")) {
            return "Log("+leftExpression+", "+rightExpression+")";
        }
        return name +"("+rightExpression+")";
    }

    @Override
    public boolean fillExpressions() {
        if (rightExpression != null && leftExpression == null) {
            leftExpression = new Val(Math.E);
            name = "Ln";
            return true;
        }
        return false;
    }

    @Override
    public Expression getOptimized() throws CloneNotSupportedException {
        Expression expression = super.getOptimized();
        if (expression.type == Type.VALUE)
            return expression;
        if (expression.leftExpression.type == Type.VALUE) {
            if (expression.leftExpression.val == 1) {
                return new Val(Double.POSITIVE_INFINITY);
            }
            if (expression.leftExpression.val == 0) {
                return new Val(Double.NaN);
            }
            if (expression.rightExpression.type == Type.VALUE &&
                expression.leftExpression.val == expression.rightExpression.val) {
                return new Val(1);
            }
        }

        return expression;
    }
}
