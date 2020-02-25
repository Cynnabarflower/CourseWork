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
    public Expression getOptimized() throws CloneNotSupportedException {
        Expression expression = clone();
        expression.childExpressions = new ArrayList<>();
        expression.addChild(childExpressions.get(0).getOptimized()).addChild(childExpressions.get(1).getOptimized());

        if (expression.childExpressions.get(0).type == Type.VALUE) {
            if (expression.childExpressions.get(1).type == Type.VALUE) {
                return new Val(expression.childExpressions.get(0).val / expression.childExpressions.get(1).val);
            }
        } else if (false && expression.getChild(0) instanceof Mul) {  // NOT WORKING!!!!! INFINITE LOOP
            var firstChild = expression.getChild(1);
            Expression divs = new Mul();
            for (var child : expression.getChild(0).getChildren()) {
                divs.addChild(new Div(child, firstChild).getOptimized());
            }
            divs = ExpressionFactory.optimize(divs);
            return divs;
        } else if (expression.getChild(0) instanceof Div) {
            return new Div(expression.getChild(0).getChild(0), new Mul(expression.getChild(0).getChild(1), expression.getChild(1)).getOptimized()).getOptimized();
        } else if (expression.getChild(1) instanceof Div) {
            return new Mul(expression.getChild(0), new Div(expression.getChild(1), expression.getChild(0))).getOptimized();
        } else if (expression.getChild(0) instanceof Pow && expression.getChild(0).getChild(0).equals(expression.getChild(1))) {
            expression.getChild(0).removeChild(0);
            return new Pow(expression.getChild(1), new Sum(new Mul().addChildren(expression.getChild(0).getChildren()), new Val(-1)).getOptimized()).getOptimized();
        }  else if (expression.childExpressions.get(1).type == Type.VALUE) {
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




}
