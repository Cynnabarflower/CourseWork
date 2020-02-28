package sample.Expressions;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Collection;

public class Var extends Expression {

    private ArrayList<String> argNames;

    public Var(String name, double val) {
        super(val, name, Type.VAR, ArgumentPosition.NONE,0,0, null);
        addChild(new Val(val));
        argNames = new ArrayList<>();
    }
    public Var(String name) {
        super(0, name, Type.VAR, ArgumentPosition.NONE,0, 0, null);
        argNames = new ArrayList<>();
    }
    public Var(String name, Expression... expressions) {
        super(0, name, Type.VAR, ArgumentPosition.NONE,0, 0, null, expressions);
        argNames = new ArrayList<>();
    }

    @Override
    public ArrayList<String> getVars() {
        if (type == Type.VAR)
            return new ArrayList<>(){{ this.add(name); }};
        return argNames;
    }

    @Override
    public double getVal(ArrayList<Expression> args) {

        if (childExpressions.size() < 2) {
            for (var arg : args)
                if (arg.name.equals(name))
                    return arg.getChild(0).getVal(args);
            if (childExpressions.isEmpty())
                return val;
            return getChild(0).getVal(args);
        }


        Expression expression = childExpressions.get(0);
        ArrayList<Expression> fooArgs = new ArrayList<>();
        for (var i = 1; i < numberOfArgs; i++) {
                fooArgs.add(new Var(argNames.get(i-1), childExpressions.get(i).getVal(args)));
        }

        for (var arg : args)
            if (!argNames.contains(arg.name))
                fooArgs.add(arg);

        return expression.getVal(fooArgs);
    }

    @Override
    public Expression getDerivative(String var)  {
        if (name.equals(var)) {
            return new Val(1);
        }

        if (childExpressions.isEmpty())
            return new Val(0);

        if (childExpressions.size() == 1)
            return childExpressions.get(0).getDerivative(var);

        return childExpressions.get(0).getDerivative(var);
    }

    @Override
    public Expression getIntegral() {
        return null;
    }


    @Override
    public String toString() {

        if (type == Type.VAR)
            return name;
        // there must be a better way
        StringBuilder sb = new StringBuilder(name+"(");
        if (!argNames.isEmpty()) {
            for (var arg : argNames) {
                sb.append(arg);
                sb.append(",");
            }
            sb.deleteCharAt(sb.length() - 1);
        }

        return sb.toString()+")";
    }

    @Override
    public Expression getOpen() throws CloneNotSupportedException {
        return clone();
    }

    @Override
    public Expression getOptimized(int level) {
        Expression expression = getClone();
       // expression.childExpressions = new ArrayList<>();

        if (type == Type.VAR) {
            return expression;
        }

        if (type == Type.REFERENCE) {
            return expression;
        }

/*
        if (numberOfArgs == 0 && !childExpressions.isEmpty())
            return childExpressions.get(0).clone();

        expression.addChild(childExpressions.get(0).getOptimized(level));
        if (childExpressions.size() == numberOfArgs + 1) {
            for (var i = 1; i <= numberOfArgs; i++) {
                var varChild = childExpressions.get(i);
                if (!varChild.getChildren().isEmpty()) {
                    expression.addChild(varChild.clone().setChild((varChild.getChild(0).getOptimized(level)), 0));
                } else {
                    expression.addChild(varChild.clone());
                }
            }
        }*/

        return expression;
    }

    public Expression clearArgs() {
        if (childExpressions.size() >= numberOfArgs) {
            for (var i = 1; i < numberOfArgs; i++)
                if (childExpressions.get(i).getChildren().isEmpty()) {
                    childExpressions.get(i).childExpressions.clear();
                }
        }
        return this;
    }

    public Expression addArgName(String argName) {
        this.argNames.add(argName);
        return this;
    }

    public Expression setArgNames(ArrayList<String> argNames) {
        this.argNames.clear();
        this.argNames.addAll(argNames);
        return this;
    }


    @Override
    public Expression clone() throws CloneNotSupportedException {
        Var clone = (Var) super.clone();
        // since strings are immutable any change will lead to a new ref
/*        clone.argNames = new ArrayList<>();
        for (var arg : argNames)
            clone.argNames.add(new String(arg));*/
        return clone;
    }
}