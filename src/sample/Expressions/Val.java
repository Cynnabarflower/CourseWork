package sample.Expressions;

import javafx.util.Pair;

import java.util.ArrayList;

public class Val extends Expression {
    @Override
    public double getVal() {
        return val;
    }

    @Override
    public Expression getDerivative(String var) {
        return new Val(0);
    }

    @Override
    public Expression getIntegral() {
        return null;
    }

    public Val(double val) {
        super(val, "Val", Type.VALUE, ArgumentPosition.NONE, 0, null, null);
    }

}
