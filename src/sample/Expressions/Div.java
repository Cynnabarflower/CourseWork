package sample.Expressions;

import sample.WrongExpressionException;

import java.util.ArrayList;

public class Div extends Expression {
    @Override
    public double getVal(ArrayList<Expression> args) {

            return childExpressions.get(0).getVal(args) / childExpressions.get(1).getVal(args);
    }

    @Override
    public Expression getDerivative(String var) {
        if (childExpressions.size() != 2)
            System.err.println("Derivative. " + this.toString()+"  "+childExpressions.size());
        if (this.contains(var)) {
                return new Div(
                        new Sub(
                                new Mul(childExpressions.get(0).getDerivative(var), childExpressions.get(1)),
                                new Mul(childExpressions.get(1).getDerivative(var), childExpressions.get(0))),
                        new Mul(childExpressions.get(1), childExpressions.get(1)));
            }
        return new Val(0);
    }

    @Override
    public Expression getOptimized(int level) {
        Expression expression = getClone();
        expression.childExpressions = new ArrayList<>();
        expression.addChild(childExpressions.get(0).getOptimized(level)).addChild(childExpressions.get(1).getOptimized(level));

        if (expression.childExpressions.get(0).type == Type.VALUE) {
            if (expression.childExpressions.get(1).type == Type.VALUE) {
                return new Val(expression.childExpressions.get(0).getVal() / expression.childExpressions.get(1).getVal());
            }
        } else if (level > 0 && expression.getChild(0) instanceof Div) {
            return new Div(expression.getChild(0).getChild(0), new Mul(expression.getChild(0).getChild(1), expression.getChild(1)).getOptimized(level)).getOptimized(level);
        } else if (level > 0 && expression.getChild(1) instanceof Div) {
            return new Mul(expression.getChild(0), new Div(expression.getChild(1), expression.getChild(0))).getOptimized(level);
        } else if (level > 1 && expression.getChild(0) instanceof Pow && expression.getChild(0).getChild(0).equals(expression.getChild(1))) {
            expression.getChild(0).removeChild(0);
            return new Pow(expression.getChild(1), new Sum(new Mul().addChildren(expression.getChild(0).getChildren()), new Val(-1)).getOptimized(level)).getOptimized(level);
        }  else if (level > 0 && expression.childExpressions.get(1).type == Type.VALUE) {
            if (expression.childExpressions.get(1).val == 1) {
                return expression.childExpressions.get(0);
            } else if (expression.childExpressions.get(1).val == -1) {
                return new Mul(new Val(-1), expression.childExpressions.get(0));
            }
        }

        return expression;
    }

    @Override
    public Expression getIntegral() {
        return null;
    }

    public Div(Expression... expressions) {
        super(0, "Div", Type.FUNCTION, ArgumentPosition.LEFT_AND_RIGHT, 20, 2, null, expressions);
    }

    @Override
    public boolean fillExpressions() {
        while (childExpressions.size() < 2)
            addChild(new Val(0));
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (var child : childExpressions) {
            sb.append("/");
            if (child.priority >= priority && child.getChildren().size() > 1)
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
        var expression = super.getOpen();
        var child = expression.getChild(0);
/*        if (child instanceof Mul) {
            var open = new Mul();
            for (var childOfChild : child.getChildren()) {
                open.addChild(new Div(childOfChild).addChildren(expression.clone().removeChild(0).getChildren()));
            }
            return open;
        }
        if (child instanceof Sum) {
            var open = new Sum();
            for (var childOfChild : child.getChildren()) {
                open.addChild(new Div(childOfChild).addChildren(expression.clone().removeChild(0).getChildren()));
            }
            return open;
        }*/
        return expression;
    }
}
