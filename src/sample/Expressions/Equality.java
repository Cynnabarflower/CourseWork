package sample.Expressions;

import java.util.ArrayList;

public class Equality extends Expression {
    public Equality(Expression left, Expression right) {
        super(0, "Equals", Type.EQUALITY, ArgumentPosition.LEFT_AND_RIGHT, 999, left, right);
    }

    @Override
    public double getVal() {
        return 0;
    }

    @Override
    public Expression getDerivative(String var) {
        return null;
    }

    @Override
    public Expression getIntegral() {
        return null;
    }
}
