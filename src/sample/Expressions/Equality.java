package sample.Expressions;

import java.util.ArrayList;

public class Equality extends Expression {
    public Equality(Expression left, Expression right) {
        super(0, "Equals", Type.EQUALITY, ArgumentPosition.LEFT_AND_RIGHT, 999, 2, null, left, right);
    }

    public Equality() {
        super(0, "Equals", Type.EQUALITY, ArgumentPosition.LEFT_AND_RIGHT, 999, 2, null);
    }

    @Override
    public Expression getDerivative(String var) {
        return new Val(0);
    }

    @Override
    public Expression getIntegral() {
        return null;
    }
}
