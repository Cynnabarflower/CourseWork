package sample.Expressions;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;

public abstract class Expression implements Cloneable {

    public enum Type {
        LEFT_BRACKET,
        RIGHT_BRACKET,
        FUNCTION,
        VALUE,
        VAR,
        EQUALITY,
        DERIVATIVE,
        DIVIDER
    }

    public enum ArgumentPosition {
        LEFT,
        RIGHT,
        LEFT_AND_RIGHT,
        NONE
    }

    public double val = 0;
    public String name;
    public Type type;
    public ArgumentPosition argumentPosition;
    public int priority;
    public int numberOfArgs;
    public Expression leftExpression = null;
    public Expression rightExpression = null;

    public Expression(double val, String name, Type type, ArgumentPosition argumentPosition, int priority, int numberOfArgs, Expression left, Expression right) {
        this.val = val;
        this.name = name;
        this.priority = priority;
        this.leftExpression = left;
        this.rightExpression = right;
        this.type = type;
        this.argumentPosition = argumentPosition;
        this.numberOfArgs = numberOfArgs;
    }


    @Override
    public String toString() {
        String left = leftExpression == null ? "" : leftExpression.toString();
        String right = rightExpression == null ? "" : rightExpression.toString();
        String name = type == Type.VALUE ? ""+val : type == Type.VAR? this.name : this.name+"("+left+","+right+")";
        return name;
    }

    public void setValues(ArrayList<Pair<String, Double>> varValues) {
        if (this.type == Type.VAR) {
            for (Pair<String, Double> pair : varValues)
                if (name.equals(pair.getKey())) {
                    val = pair.getValue();
                    break;
                }
        }
            if (leftExpression != null)
                leftExpression.setValues(varValues);
            if (rightExpression != null)
                rightExpression.setValues(varValues);
    }

    public Expression setValue(String var, double val) {
        if (this.type == Type.VAR) {
                if (name.equals(var)) {
                    this.val = val;
                }
        }
        if (leftExpression != null)
            leftExpression.setValue(var, val);
        if (rightExpression != null)
            rightExpression.setValue(var, val);
        return this;
    }

    public Expression replaceVar(String var) {
        if (leftExpression != null) {
            if (this.leftExpression.type == Type.VAR) {
                if (leftExpression.name.equals(var)) {
                    this.leftExpression = new Val(leftExpression.val);
                }
            } else {
                leftExpression.replaceVar(var);
            }
        }
        if (rightExpression != null) {
            if (rightExpression.type == Type.VAR) {
                if (rightExpression.name.equals(var)) {
                    this.rightExpression = new Val(rightExpression.val);
                }
            } else {
                rightExpression.replaceVar(var);
            }
        }
        return this;
    }

    public void setExpressions(ArrayList<Pair<String, Expression>> varValues) {
        if (this.type == Type.VAR) {
            for (Pair<String, Expression> pair : varValues)
                if (name.equals(pair.getKey())) {
                    rightExpression = pair.getValue();
                    break;
                }
        }
        if (leftExpression != null)
            leftExpression.setExpressions(varValues);
        if (rightExpression != null)
            rightExpression.setExpressions(varValues);
    }

    public ArrayList<String> getVars() {
        ArrayList<String> vars = new ArrayList<>();
        if (type == Type.VAR) {
            vars.add(name);
        }
        if (leftExpression != null)
            vars.addAll(leftExpression.getVars());
        if (rightExpression != null)
            vars.addAll(rightExpression.getVars());
        return (ArrayList<String>) vars.stream().distinct().collect(Collectors.toList());
    }

    public boolean contains(String name) {
        return this.name.equals(name) | (leftExpression != null && leftExpression.contains(name)) | (rightExpression != null && rightExpression.contains(name));
    }

    public boolean contains(ArrayList<String> names) {
        for (String name : names)
            if (contains(name))
                return true;
            return false;
    }

    public boolean contains(Type type) {
        return this.type == type | (leftExpression != null && leftExpression.contains(type)) | (rightExpression != null && rightExpression.contains(type));
    }

    public Expression optimize() {
        if (leftExpression != null)
            leftExpression = leftExpression.optimize();
        if (rightExpression != null)
            rightExpression = rightExpression.optimize();
        return this;
    }

    public abstract double getVal();
    public abstract Expression getDerivative(String var);
    public abstract Expression getIntegral();

    public Expression clone() throws
            CloneNotSupportedException
    {
        Expression expression = (Expression) super.clone();
        if (leftExpression != null)
            expression.leftExpression = leftExpression.clone();
        if (rightExpression != null)
            expression.rightExpression = rightExpression.clone();
        return expression;
    }

}

//"((( (( ( (x - 2.0)) * (x - 3.0) ) / (((1.0 * 1.0) * (x - 2.0)) * (x - 3.0)) * 1.0)) + ((((1.0 * (x - 1.0)) * 1.0) * (x - 3.0)) / (((1.0 * (x - 1.0)) * 1.0) * (x - 3.0)) * 2.0)) + ((((1.0 * (x - 1.0)) * (x - 2.0)) * 1.0) / (((1.0 * (x - 1.0)) * (x - 2.0)) * 1.0) * 3.0))"
      //  ((x-2)(x-3))/