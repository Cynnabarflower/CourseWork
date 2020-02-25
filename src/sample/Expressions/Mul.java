package sample.Expressions;

import java.util.ArrayList;
import java.util.HashSet;

public class Mul extends Expression {
    @Override
    public double getVal(ArrayList<Expression> args) {
        for (var valChild : childExpressions)
            if (valChild.type == Type.VALUE && valChild.getVal() == 0)
                return 0;
        double val = 1;
        for (var child : childExpressions)
            val *= child.getVal(args);
        return val;
    }

    public Mul(Expression... expressions) {
        super(0, "Mul", Type.FUNCTION, ArgumentPosition.LEFT_AND_RIGHT ,20, 2, null, expressions);
    }
    public Mul() {
        super(0, "Mul", Type.FUNCTION, ArgumentPosition.LEFT_AND_RIGHT, 20, 2, null);
    }

    @Override
    public Expression getDerivative(String var) {
        if (contains(var)) {
            if (childExpressions.size() > 2) {
                Expression clone = getClone();
                clone.removeChild(0);
                return getDerivativeMul(clone, childExpressions.get(0), var);
               /* Expression der = getDerivativeMul(childExpressions.get(0), childExpressions.get(1), var);
                for (var i = 2; i < childExpressions.size(); i++) {
                    der = getDerivativeMul(der, childExpressions.get(i), var);
                }
                return der;*/
            } else if (childExpressions.size() == 2) {

                return getDerivativeMul(childExpressions.get(0), childExpressions.get(1), var);

            } else {
                return new Val(0);
            }
        }
        return new Val(0);
    }

    private Expression getDerivativeMul(Expression child0, Expression child1, String var) {
        if (child1.type == Type.VALUE) {
            if (child1.getVal() == 0)
                return new Val(0);
            else if (child1.getVal() == 1)
                return child0.getDerivative(var);
            return new Mul(child1, child0.getDerivative(var));
        }
        Expression leftDerivative = child0.getDerivative(var);
        Expression rightDerivative = child1.getDerivative(var);
        Expression leftMul = new Mul(child0, rightDerivative);
        Expression rightMull = new Mul(child1, leftDerivative);
        return new Sum(leftMul, rightMull);
    }

    @Override
    public Expression getOptimized() throws CloneNotSupportedException{
        Expression expression = clone();
        if (expression.getVars().isEmpty()) {
            return new Val(getVal());
        }
        expression.childExpressions = new ArrayList<>();
        double mul = 1;
        ArrayList<Expression> denoms = new ArrayList<>();
        for (var child : childExpressions) {
            if (child instanceof Mul && child.priority == priority) {
                expression.childExpressions.addAll(child.clone().getChildren());
            } else if (child.getVars().isEmpty()) {
                mul *= child.getVal(new ArrayList<>());
                if (mul == 0)
                    return new Val(0);
            } else if (child instanceof Div) {
                expression.addChild(child.getChild(0).getOptimized());
                denoms.add(child.getChild(1).getOptimized());
            } else {
                expression.addChild(child.getOptimized());
            }
        }
        if (mul != 1)
            expression.addChild(new Val(mul));

        ArrayList<Expression> repeats = new ArrayList<>();
        ArrayList<Expression> powers = new ArrayList<>();
        for (var i = 0; i < expression.childExpressions.size(); i++) {
            var child = expression.childExpressions.get(i);
            if (child.getVars().isEmpty() && child.getVal() == 0)
                return new Val(0);
            repeats.clear();
            powers.clear();
            for (var j = i + 1; j < expression.childExpressions.size(); j++) {
                var child2 = expression.childExpressions.get(j);
                if (child.equals(child2)) {
                    repeats.add(child2);
                } else if (child2 instanceof Pow  && child.equals(child2.getChild(0))) {
                    powers.add(child2);
                }
            }
            if (!powers.isEmpty()) {
                var sum = new Sum();
                for (var pow : powers)
                    if (pow.getChildren().size() == 2)
                        sum.addChild(pow.getChild(1).getOptimized());
                if (!repeats.isEmpty()) {
                    sum.addChild(new Val(repeats.size() + 1));
                }
                expression.removeChildren(powers);
                expression.removeChildren(repeats);
                expression.removeChild(child);
                expression.addChild(new Pow(child, sum.getOptimized()));
                i--;
            } else if (!repeats.isEmpty()) {
                var pow = new Pow(child, new Val(repeats.size() + 1));
                expression.removeChildren(repeats);
                expression.removeChild(child);
                expression.addChild(pow);
                i--;
            }

        }
        if (!denoms.isEmpty()) {
            if (expression.getChildren().size() == 1)
                return new Div(expression.getChild(0),  new Mul().addChildren(denoms).getOptimized()).getOptimized();
            return new Div(expression,  new Mul().addChildren(denoms).getOptimized()).getOptimized();
        }

        if (expression.childExpressions.size() == 1)
            return expression.childExpressions.get(0);
        return expression;
    }

    @Override
    public Expression getOpen() throws CloneNotSupportedException {
        Expression expression = this.clone();
        expression.childExpressions = new ArrayList<>();
        for (var child : childExpressions) {
            if (child.type == type && child.priority == priority && child.name.equals(name)) {
                expression.childExpressions.addAll(child.clone().getChildren());
            } else
                expression.addChild(child.getOpen());
        }
        return expression;
    }

    @Override
    public Expression getIntegral() {
        return null;
    }

    @Override
    public String toString() {
        if (childExpressions.size() == 1)
            return "MUL(" + childExpressions.get(0).toString() + ")";
        StringBuilder sb = new StringBuilder();
        for (var child : childExpressions) {
            sb.append("*");
            if (child.priority > priority)
                sb.append("(").append(child.toString()).append(")");
            else
                sb.append(child.toString());
        }
        if (sb.length() > 0)
            sb.deleteCharAt(0);
        else return "*";
        return sb.toString();
    }

    @Override
    public boolean fillExpressions() {
        while (childExpressions.size() < 2)
            addChild(new Val(1));
        return true;
    }
}
