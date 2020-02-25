package sample.Expressions;

import java.util.ArrayList;

public class Pow extends Expression {
    @Override
    public double getVal(ArrayList<Expression> args) {
        return Math.pow(childExpressions.get(0).getVal(args), childExpressions.get(1).getVal(args));
    }

    @Override
    public Expression getDerivative(String var) {
        if (childExpressions.size() != 2)
            System.err.println("Derivative. " + this.toString()+"  "+childExpressions.size());
        if (this.contains(var)) {
                return new Mul(
                        new Pow(childExpressions.get(0), new Sub(childExpressions.get(1), new Val(1)) ),
                        new Sum(
                                new Mul(childExpressions.get(1), childExpressions.get(0).getDerivative(var)),
                                new Mul(childExpressions.get(0), new Mul(new Log(new Val(Math.E), childExpressions.get(0)), childExpressions.get(1).getDerivative(var)))
                        ));
            }
        return new Val(0);
    }

    @Override
    public Expression getIntegral() {
        return null;
    }

    public Pow(Expression... expressions) {
        super(0, "Pow", Type.FUNCTION, ArgumentPosition.LEFT_AND_RIGHT, 10, 2, null, expressions);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (var child : childExpressions) {
            sb.append("^");
            if (child.priority > priority)
                sb.append("(").append(child.toString()).append(")");
            else
                sb.append(child.toString());
        }
        if (sb.length() > 0)
            sb.deleteCharAt(0);
        return sb.toString();
    }

    @Override
    public Expression getOpen() throws CloneNotSupportedException {
        Expression expression = this.clone();
        return expression;
    }

    @Override
    public Expression getOptimized() throws CloneNotSupportedException {
        Expression expression = this.clone();
        if (expression.getVars().isEmpty()) {
            return new Val(getVal(new ArrayList<>()));
        }

        if (childExpressions.get(0) instanceof Pow) {
            expression.removeChild(0);
            return childExpressions.get(0).clone().addChildren(expression.getChildren()).getOptimized();
        }

        expression.childExpressions = new ArrayList<>();

        expression.childExpressions.add(childExpressions.get(0));
        var mul = new Mul();
        for (var i = 1; i < childExpressions.size(); i++) {
            var child = childExpressions.get(i).getOptimized();
            mul.addChild(child);
        }
        expression.childExpressions.add(mul.getOptimized());
        return expression;
    }
}
