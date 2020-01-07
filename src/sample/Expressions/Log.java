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
        super(0, "Log", Type.BINARY, ArgumentPosition.RIGHT_AND_RIGHT, 1, left, right);
    }

    public Log(Expression left) {
        super(0, "Log", Type.UNARY, ArgumentPosition.RIGHT, 1, left, null);
    }
}
