package sample.Expressions;

import javafx.util.Pair;

import java.util.ArrayList;

public class Abs extends Expression {
    @Override
    public double getVal() {
        return Math.abs(rightExpression.getVal());
    }

    @Override
    public Expression getDerivative(String var) {
            if (this.contains(var)) {
                return new Div(new Mul(rightExpression, rightExpression.getDerivative(var)), new Abs(rightExpression));
            }
        return new Val(0);
    }

    @Override
    public Expression getIntegral() {
        return null;
    }

    public Abs(Expression right) {
        super(0, "Abs", Type.UNARY, ArgumentPosition.RIGHT,1, null, right);
    }
}
