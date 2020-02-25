package sample.Expressions;

import javafx.util.Pair;

import java.util.ArrayList;

public class Abs extends Expression {
    @Override
    public double getVal(ArrayList<Expression> args) {
        return Math.abs(childExpressions.get(0).getVal(args));
    }

    @Override
    public Expression getDerivative(String var) {
            if (this.contains(var)) {
                return new Div(new Mul(childExpressions.get(0), childExpressions.get(0).getDerivative(var)), new Abs(childExpressions.get(0)));
            }
        return new Val(0);
    }

    @Override
    public Expression getIntegral() {
        return null;
    }

    public Abs(Expression expression) {
        super(0, "Abs", Type.FUNCTION, ArgumentPosition.RIGHT,0,1, null , expression);
    }
    public Abs() {
        super(0, "Abs", Type.FUNCTION, ArgumentPosition.RIGHT,0,1, null);
    }

    @Override
    public String toString() {

        return childExpressions.isEmpty() ? "abs()" : "|" + childExpressions.get(0) + "|";
    }
}
