package sample.Expressions;

import java.util.ArrayList;

public class Sub extends Expression {

    @Override
    public double getVal(ArrayList<Expression> args) {
        return childExpressions.get(0).getVal(args) - childExpressions.get(1).getVal(args);
    }

    @Override
    public Expression getDerivative(String var) {
        if (childExpressions.size() != 2)
            System.err.println("Derivative. " + this.toString()+"  "+childExpressions.size());
        if (this.contains(var)) {
            return new Sub(childExpressions.get(0).getDerivative(var), childExpressions.get(1).getDerivative(var));
        }
        return new Val(0);
    }
    @Override
    public Expression getIntegral() {
        return null;
    }

    public Sub(Expression... expressions) {
        super(0, "Sub", Type.FUNCTION, ArgumentPosition.LEFT_AND_RIGHT,30, 2, null, expressions);
    }
    public Sub() {
        super(0, "Sub", Type.FUNCTION, ArgumentPosition.LEFT_AND_RIGHT,30, 2, null);
    }


    @Override
    public String toString() {
        if (childExpressions.isEmpty())
            return "-";
        if (childExpressions.size() == 1)
            return "-"+childExpressions.get(0);
        StringBuilder sb = new StringBuilder();
        for (var child : childExpressions) {
            sb.append("-");
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
    public boolean fillExpressions() {
        while (childExpressions.size() < 2)
            addChild(new Val(0));
        return true;
    }

    @Override
    public Expression getOptimized(int level) {

        if (getVars().isEmpty()) {
            return new Val(getVal());
        }


/*
        expression.childExpressions = new ArrayList<>();
        double sub = 0;
        for (var child : childExpressions) {
            if (child.getVars().isEmpty()) {
                sub += child.getVal(new ArrayList<>());
            } else
                expression.addChild(child.getOptimized(level));
        }
        if (sub != 0)
            expression.addChild(new Val(sub));
*/
       // Expression expression = clone();
//        expression.childExpressions = new ArrayList<>();
        double val = 0;
/*        for (var child : childExpressions)
            if (child.getVars().isEmpty()) {
                val += child.getVal();
            } else {
                expression.addChild(child.getOptimized(level));
            }
        if (expression.childExpressions.isEmpty()) {
            return new Val(val);
        }
        if (val != 0)
            expression.addChild(new Val(val));
        else if (expression.childExpressions.size() == 1)
            return expression.childExpressions.get(0);*/
        if (childExpressions.get(1).getVars().isEmpty()) {
            var firstChildVal = childExpressions.get(1).getVal();
            if (firstChildVal < 0) {
                return new Sum(childExpressions.get(0).getOptimized(level), new Val(-firstChildVal)).getOptimized(level);
            } else if (firstChildVal == 0) {
                return childExpressions.get(0).getOptimized(level);
            }
        } else if (level > 1 && childExpressions.get(0).equals(childExpressions.get(1))) {
            return new Val(0);
        }

        return getClone().setChild(childExpressions.get(0).getOptimized(level), 0).setChild(childExpressions.get(1).getOptimized(level), 1);
    }

    @Override
    public Expression getOpen() throws CloneNotSupportedException {
        Expression expression = super.getOpen();
        expression.childExpressions = new ArrayList<>();
        if (getVars().isEmpty()) {
            return new Val(getVal());
        }
        if (childExpressions.size() == 2) {
            if (childExpressions.get(1).type == Type.VALUE) {
                return new Sum(childExpressions.get(0), new Val(-childExpressions.get(1).getVal()));
            }

        }
        var sum = new Sum(childExpressions.get(0));
        for (var i = 1; i < childExpressions.size(); i++) {
            sum.addChild(new Mul(new Val(-1), childExpressions.get(i).getOpen()).getOpen());
        }
        return sum.getOpen();
/*        for (var child : childExpressions) {
            expression.addChild(child.getOpen());
        }*/
        //return expression;
    }
}