package sample.Expressions;

import javafx.util.Pair;

import java.util.ArrayList;

public class Log extends Expression {


    @Override
    public double getVal(ArrayList<Expression> args) {

        return Math.log(childExpressions.get(1).getVal(args))/Math.log(childExpressions.get(0).getVal(args));
    }

    @Override
    public Expression getDerivative(String var) {
        if (this.contains(var)) {
                return new Div(
                        new Sub(
                                new Div(
                                        new Mul(new Log(new Val(Math.E), childExpressions.get(0)), childExpressions.get(1).getDerivative(var)),
                                        childExpressions.get(1)
                                ),
                                new Div(
                                        new Mul(childExpressions.get(0).getDerivative(var), new Log(childExpressions.get(1).getDerivative(var))),
                                        childExpressions.get(0)
                                )
                        ),
                        new Mul(new Log(childExpressions.get(0)), new Log(childExpressions.get(0)))
                );
            }
        return new Val(0);
    }

    @Override
    public Expression getIntegral() {
        return null;
    }

    public Log(Expression base, Expression expression) {
        super(0, "Log", Type.FUNCTION, ArgumentPosition.RIGHT, 10, 2, null, base, expression);
    }

    public Log(Expression right) {
        super(0, "Ln", Type.FUNCTION, ArgumentPosition.RIGHT, 10, 2, null,  new Val(Math.E), right);
    }

    public Log() {
        super(0, "Ln", Type.FUNCTION, ArgumentPosition.RIGHT, 10, 1, null, new Val(Math.E));
    }

    @Override
    public String toString() {
        if (name.equals("Log")) {
            return "Log("+childExpressions.get(0)+", "+childExpressions.get(1)+")";
        }
        return name +"("+childExpressions.get(1)+")";
    }

    @Override
    public boolean fillExpressions() {
/*        if (childExpressions.get(1) != null && childExpressions.get(0) == null) {
            setChild(new Val(Math.E), 0);
            name = "Ln";
            return true;
        }*/
        return false;
    }

    @Override
    public Expression getOptimized(int level) {
        Expression expression = super.getOptimized(level);
        if (expression.type == Type.VALUE)
            return expression;
        if (expression.childExpressions.get(0).type == Type.VALUE) {
            if (level > 0 && expression.childExpressions.get(0).val == 1) {
                return new Val(Double.POSITIVE_INFINITY);
            }
            if (level > 0 && expression.childExpressions.get(0).val == 0) {
                return new Val(Double.NaN);
            }
            if (expression.childExpressions.get(1).type == Type.VALUE &&
                expression.childExpressions.get(0).val == expression.childExpressions.get(1).val) {
                return new Val(1);
            }
        }

        return expression;
    }

    @Override
    public Expression addChild(Expression expression) {
        if (childExpressions.size() > 1)
            removeChild(1);
        return super.addChild(expression);
    }

    @Override
    public Expression setChild(Expression expression, int i) {
        i = i == 0 ? 0 : 1;
        return super.setChild(expression, i);
    }
}
