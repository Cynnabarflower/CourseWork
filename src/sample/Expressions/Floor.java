package sample.Expressions;

import javafx.util.Pair;

import java.util.ArrayList;

public class Floor extends Expression {
    @Override
    public double getVal(ArrayList<Expression> args) {
        return Math.floor(childExpressions.get(0).getVal(args));
    }

    @Override
    public Expression getDerivative(String var) {
        if (this.contains(var)) {
                return childExpressions.get(0).getDerivative(var);
            }
        return new Val(0);
    }

    @Override
    public Expression getIntegral() {
        return null;
    }

    public Floor(Expression right) {
        super(0, "Floor", Type.FUNCTION, ArgumentPosition.RIGHT, 0, 1,null,  right);
    }
    public Floor() {
        super(0, "Floor", Type.FUNCTION, ArgumentPosition.RIGHT, 0, 1,null);
    }
}
