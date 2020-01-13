package sample.Expressions;

import sample.Pair;

import java.util.ArrayList;
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
    public Expression parent = null;

    public Expression(double val, String name, Type type, ArgumentPosition argumentPosition, int priority, int numberOfArgs, Expression left, Expression right, Expression parent) {
        this.val = val;
        this.name = name;
        this.priority = priority;
        setLeftExpression(left);
        setRightExpression(right);
        this.type = type;
        this.argumentPosition = argumentPosition;
        this.numberOfArgs = numberOfArgs;
        this.parent = parent;
    }

    public Expression(double val, String name, Type type, ArgumentPosition argumentPosition, int priority, int numberOfArgs, Expression left, Expression right) {
        this(val, name, type, argumentPosition, priority, numberOfArgs, left, right, null);
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
                if (name.equals(pair.key)) {
                    val = pair.value;
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
        if (parent != null && type == Type.VAR && name.equals(var)) {
            if (rightExpression == null) {
                return new Val(val);
            } else {
                return rightExpression.replaceVar(var);
            }
        } else {
            if (leftExpression != null)
                leftExpression = leftExpression.replaceVar(var);
            if (rightExpression != null)
                rightExpression = rightExpression.replaceVar(var);
        }
        return this;
    }

    public void setExpressions(ArrayList<Pair<String, Expression>> varValues) {
        if (this.type == Type.VAR) {
            for (Pair<String, Expression> pair : varValues)
                if (name.equals(pair.key)) {
                    rightExpression = pair.value;
                    break;
                }
        } else {
            if (leftExpression != null)
                leftExpression.setExpressions(varValues);
            if (rightExpression != null)
                rightExpression.setExpressions(varValues);
        }
    }

    public void setExpression(String var, Expression expression) {
        if (this.type == Type.VAR) {
                if (name.equals(var)) {
                    setRightExpression(expression);
                }
        } else {
            if (leftExpression != null)
                leftExpression.setExpression(var, expression);
            if (rightExpression != null)
                rightExpression.setExpression(var, expression);
        }
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

    public int getExpressionCount(int count) {
        if (type != Type.VALUE && type != Type.VAR) {
            count++;
            if (leftExpression != null) {
                if (rightExpression != null) {
                    count = leftExpression.getExpressionCount(count);
                    return rightExpression.getExpressionCount(count);
                }
                return leftExpression.getExpressionCount(count);
            } else if (rightExpression != null) {
                return rightExpression.getExpressionCount(count);
            }
        }
        return count;
    }

    public Expression getOptimized() throws CloneNotSupportedException{
        try {
            Expression expression = (Expression) super.clone();
            if (expression.getVars().size() == 0) {
                return new Val(expression.getVal());
            }
            if (leftExpression != null)
                expression.setLeftExpression(leftExpression.getOptimized());
            if (rightExpression != null)
                expression.setRightExpression(rightExpression.getOptimized());
            return expression;
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    public abstract double getVal();
    public abstract Expression getDerivative(String var);
    public abstract Expression getIntegral();

    public Expression clone() throws CloneNotSupportedException
    {
        Expression expression = (Expression) super.clone();
        if (leftExpression != null)
            expression.setLeftExpression(leftExpression.clone());
        if (rightExpression != null)
            expression.setRightExpression(rightExpression.clone());
        return expression;
    }

    public void setLeftExpression(Expression expression) {
        leftExpression = expression;
        if (leftExpression != null) {
            leftExpression.parent = this;
        }
    }
    public void setRightExpression(Expression expression) {
        rightExpression = expression;
        if (rightExpression != null) {
            rightExpression.parent = this;
        }
    }

    public boolean fillExpressions() {
        return false;
    }



}

//"((( (( ( (x - 2.0)) * (x - 3.0) ) / (((1.0 * 1.0) * (x - 2.0)) * (x - 3.0)) * 1.0)) + ((((1.0 * (x - 1.0)) * 1.0) * (x - 3.0)) / (((1.0 * (x - 1.0)) * 1.0) * (x - 3.0)) * 2.0)) + ((((1.0 * (x - 1.0)) * (x - 2.0)) * 1.0) / (((1.0 * (x - 1.0)) * (x - 2.0)) * 1.0) * 3.0))"
      //  ((x-2)(x-3))/