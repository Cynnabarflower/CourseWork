package sample.Expressions;

import javafx.util.Pair;

import java.math.BigDecimal;
import java.util.ArrayList;

public class Val extends Expression {
    @Override
    public double getVal(ArrayList<Expression> args) {
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
        super(val, "Val", Type.VALUE, ArgumentPosition.NONE, 0, 0, null);
    }

    @Override
    public String toString() {
        if (Double.isNaN(val) || Double.isInfinite(val))
            return "("+val+")";
        return val > 0 ? new BigDecimal(val).toPlainString() : "(" +new BigDecimal(val).toPlainString()+ ")";
    }
}
