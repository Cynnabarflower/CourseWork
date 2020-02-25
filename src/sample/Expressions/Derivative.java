package sample.Expressions;

import java.util.ArrayList;

public class Derivative extends Expression {

    private String derVar = null;

    @Override
    public double getVal(ArrayList<Expression> args) {
        if (derVar.isEmpty()) {
            ArrayList<String> vars = getVars();
            if (vars.size() > 1) {
                return 0;
            } else if (vars.size() == 1) {
                return childExpressions.get(0).getDerivative(vars.get(0)).getVal(args);
            } else return 0;
        } else
            return childExpressions.get(0).getDerivative(derVar).getVal(args);
    }

    @Override
    public Expression getDerivative(String var) {
        return getClone().setChild(childExpressions.get(0).getDerivative(var), 0);
    }

    @Override
    public Expression getIntegral() {
        return null;
    }

    public Derivative(Expression expression) {
        super(0, "Derivative", Type.FUNCTION, ArgumentPosition.LEFT, 0,1, null, expression);
        derVar = "";
    }

    public Derivative() {
        super(0, "Derivative", Type.FUNCTION, ArgumentPosition.LEFT, 0,1, null);
        derVar = "";
    }


    public Derivative(Expression expressions, String var) {
        super(0, "Derivative", Type.FUNCTION, ArgumentPosition.LEFT, 0,1, null, expressions);
        this.derVar = var;
    }

    @Override
    public Expression getOptimized() throws CloneNotSupportedException {
        if (derVar.isEmpty()) {
            if (childExpressions.get(0) != null) {
                ArrayList<String> vars = childExpressions.get(0).getVars();
                if (vars.size() == 1) {
                    return ExpressionFactory.optimize(childExpressions.get(0)).getDerivative(vars.get(0));
                } else if (vars.size() == 0) {
                    return new Val(0);
                } else
                    return clone();
            } else
                return clone();
        }
        return ExpressionFactory.optimize(childExpressions.get(0)).getDerivative(derVar).getOptimized();
    }

    public void setDerVar(String derVar) {
        this.derVar = derVar;
    }

    @Override
    public String toString() {
        if (childExpressions.get(0) != null && childExpressions.get(0).type == Type.FUNCTION) {
            return "("+childExpressions.get(0)+")\'";
        }
        return childExpressions.get(0).toString()+"\'";
    }
}
