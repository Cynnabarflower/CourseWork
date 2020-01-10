package sample.Expressions;

import java.util.ArrayList;

public class Derivative extends Expression {

    private String var = null;

    @Override
    public double getVal() {
        if (var.isEmpty()) {
            ArrayList<String> vars = getVars();
            if (vars.size() > 1) {
                return 0;
            } else if (vars.size() == 1) {
                return getDerivative(vars.get(0)).getVal();
            } else return 0;
        } else
            return getDerivative(var).getVal();
    }

    @Override
    public Expression getDerivative(String var) {
        return rightExpression.getDerivative(var);
    }

    @Override
    public Expression getIntegral() {
        return null;
    }

    public Derivative(Expression right) {
        super(0, "Derivative", Type.FUNCTION, ArgumentPosition.LEFT, 0,1, null, right);
        var = "";
    }


    public Derivative(Expression right, String var) {
        super(0, "Derivative", Type.FUNCTION, ArgumentPosition.LEFT, 0,1, null, right);
        this.var = var;
    }

    @Override
    public Expression getOptimized() throws CloneNotSupportedException {
        if (var.isEmpty()) {
            if (rightExpression != null) {
                ArrayList<String> vars = rightExpression.getVars();
                if (vars.size() == 1) {
                    return super.getOptimized().getDerivative(vars.get(0)).getOptimized();
                } else if (vars.size() == 0) {
                    return new Val(0);
                } else
                    return null;
            } else
                return null;
        }
        return super.getOptimized().getDerivative(var).getOptimized();
    }

    public void setVar(String var) {
        this.var = var;
    }

    @Override
    public String toString() {
        if (rightExpression != null && rightExpression.type == Type.FUNCTION) {
            return "("+rightExpression+")\'";
        }
        return rightExpression.toString()+"\'";
    }
}
