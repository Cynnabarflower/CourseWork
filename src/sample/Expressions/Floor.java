package sample.Expressions;

import javafx.util.Pair;

import java.util.ArrayList;

public class Floor extends Expression {
    @Override
    public double getVal() {
        return Math.floor(rightExpression.getVal());
    }

    @Override
    public Expression getDerivative(String var) {
        if (this.contains(var)) {
                return rightExpression.getDerivative(var);
            }
        return new Val(0);
    }

    @Override
    public Expression getIntegral() {
        return null;
    }

    public Floor(Expression right) {
        super(0, "Floor", Type.FUNCTION, ArgumentPosition.RIGHT, 1, 1,null,  right);
    }
}
