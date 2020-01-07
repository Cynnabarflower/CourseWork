package sample.Expressions;

import javafx.util.Pair;

import java.util.ArrayList;

public class Div extends Expression {
    @Override
    public double getVal() {
            return leftExpression.getVal() / rightExpression.getVal();
    }

    @Override
    public Expression getDerivative(String var) {
        if (this.contains(var)) {
                return new Div(
                        new Sum(
                                new Mul(leftExpression.getDerivative(var), rightExpression),
                                new Mul(rightExpression.getDerivative(var), leftExpression)),
                        new Mul(rightExpression, rightExpression));
            }
        return new Val(0);
    }

    @Override
    public Expression getIntegral() {
        return null;
    }

    public Div(Expression left, Expression right) {
        super(0, "Div", Type.BINARY, ArgumentPosition.LEFT_AND_RIGHT,1, left, right);
    }

    @Override
    public String toString() {
        return leftExpression.toString() + " / " +rightExpression.toString();
    }
}
