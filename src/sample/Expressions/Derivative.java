package sample.Expressions;

import java.util.ArrayList;

public class Derivative extends Expression {
    @Override
    public double getVal() {
        ArrayList<String> vars = getVars();
        if (vars.size() > 1) {
            //throw new Exception("");
            return 0;
        } else if (vars.size() == 1) {
            return getDerivative(vars.get(0)).getVal();
        } else return 0;
    }

    @Override
    public Expression getDerivative(String var) {
        return rightExpression.getDerivative(var);
    }

    @Override
    public Expression getIntegral() {
        return null;
    }

    @Override
    public void optimize() {

        super.optimize();
    }

    public Derivative(Expression right) {
        super(0, "Derivative", Type.DERIVATIVE, ArgumentPosition.LEFT, 0, null, right);

    }
}
