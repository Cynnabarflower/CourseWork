package sample.Expressions;

import java.util.ArrayList;

public class Sum extends Expression {
    public Sum(Expression... expressions) {
        super(0, "Sum", Type.FUNCTION, ArgumentPosition.LEFT_AND_RIGHT, 30, 2, null, expressions);
    }

    public Sum() {
        super(0, "Sum", Type.FUNCTION, ArgumentPosition.LEFT_AND_RIGHT, 30, 2, null);
    }

    @Override
    public double getVal(ArrayList<Expression> args) {
        double sum = 0;
        for (var child : childExpressions)
            sum += child.getVal(args);
        return sum;
    }

    @Override
    public Expression getDerivative(String var) {
        ArrayList<Expression> children = new ArrayList<>();
        for (var child : childExpressions)
            if (child.contains(var)) {
                children.add(child.getDerivative(var));
            }
        if (children.isEmpty())
            return new Val(0);
        return new Sum().addChildren(children);
    }

    @Override
    public Expression getIntegral() {
        return null;
    }

    @Override
    public Expression getOptimized() throws CloneNotSupportedException {
        Expression expression = clone();
        expression.childExpressions = new ArrayList<>();
        double sum = 0;
        ArrayList<Expression> subs = new ArrayList<>();
        for (var child : childExpressions) {
            if (child.priority == priority && child instanceof Sum) {
                expression.childExpressions.addAll(child.clone().getChildren());
            } else if (child.priority == priority && child instanceof Sub && child.getChildren().size() == 2) {
                expression.childExpressions.add(child.getChild(0));
                subs.add(child.getChild(1));
            } else if (child.getVars().isEmpty()) {
                sum += child.getVal();
            } else
                expression.addChild(child.getOptimized());
        }

        if (sum != 0)
            expression.addChild(new Val(sum));
        ArrayList<Expression> repeats = new ArrayList<>();
        ArrayList<Expression> muls = new ArrayList<>();

        for (var i = 0; i < expression.childExpressions.size(); i++) {

            for (var j = i + 1; j < expression.childExpressions.size(); j++) {
                if (expression.childExpressions.get(i).equals(expression.childExpressions.get(j))) {
                    repeats.add(expression.childExpressions.get(j));
                }
            }
            if (repeats.size() > 0) {
                var mul = new Mul(new Val(repeats.size() + 1), expression.childExpressions.get(i).clone());
                muls.add(mul);
                repeats.add(expression.childExpressions.get(i));
                expression.childExpressions.removeAll(repeats);
                repeats.clear();
                i--;
            }
        }

        expression.addChildren(muls);

        if (!subs.isEmpty()) {
            return ExpressionFactory.optimize(new Sub(expression, new Sum().addChildren(subs).getOptimized()));
        }

        if (expression.childExpressions.size() == 1)
            return expression.childExpressions.get(0);

        return expression;
    }

    @Override
    public Expression getOpen() throws CloneNotSupportedException {
        Expression expression = super.getOpen();
        expression.childExpressions = new ArrayList<>();
        for (var child : childExpressions) {
            if (child.type == type && child.priority == priority && child.name.equals(name)) {
                expression.addChildren(child.clone().getChildren());
            } else
                expression.addChild(child.getOpen());
        }
        return expression;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        for (var child : childExpressions) {
            sb.append("+");
            if (child.priority > priority)
                sb.append("(").append(child.toString()).append(")");
            else
                sb.append(child.toString());
        }
        if (sb.length() > 0)
            sb.deleteCharAt(0);
        else return "+";
        return sb.toString();
    }

    @Override
    public boolean fillExpressions() {
        while (childExpressions.size() < 2)
            addChild(new Val(0));

        return true;
    }
}
