package sample.Expressions;

import javafx.util.Pair;

import java.util.ArrayList;

public class Var extends Expression {
    public Var(double val, String name) {
        super(val, name, Type.VAR, ArgumentPosition.NONE,0,0, null, null);
    }
    public Var(String name) {
        super(0, name, Type.VAR, ArgumentPosition.NONE,0, 0,null, null);
    }

    @Override
    public double getVal() {
        if (rightExpression == null) {
            return val;
        }
        return rightExpression.getVal();
    }

    @Override
    public Expression getDerivative(String var)  {
        if (this.contains(var)) {
                return new Val(1);
            }
        return new Val(0);
    }

    @Override
    public Expression getIntegral() {
        return null;
    }

    @Override
    public String toString() {
        if (rightExpression == null)
            return name;
        // there must be a better way
        return name + "("+(""+rightExpression.getVars()).substring(1).replace("]", "")+")";
    }
}
