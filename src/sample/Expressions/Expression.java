package sample.Expressions;

import sample.Pair;

import java.util.*;
import java.util.stream.Collectors;

public class Expression implements Cloneable {

    public enum Type {
        LEFT_BRACKET,
        RIGHT_BRACKET,
        FUNCTION,
        VALUE,
        VAR,
        EQUALITY,
        DERIVATIVE,
        DIVIDER,
        REFERENCE
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
/*    public Expression leftExpression = null;
    public Expression rightExpression = null;*/
    protected ArrayList<Expression> childExpressions = null;
    public Expression parent = null;

    public Expression(double val, String name, Type type, ArgumentPosition argumentPosition, int priority, int numberOfArgs, Expression parent, Expression... expressions) {
        childExpressions = new ArrayList<>(2);
        this.val = val;
        this.name = name;
        this.priority = priority;
        if (expressions != null)
            addChildren(List.of(expressions));
        this.type = type;
        this.argumentPosition = argumentPosition;
        this.numberOfArgs = numberOfArgs;
        this.parent = parent;
    }

    public Expression(String name, ArgumentPosition argumentPosition, int priority, int numberOfArgs) {
        this(0, name, Type.FUNCTION, argumentPosition, priority, numberOfArgs, null);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("(");
        for (var child : childExpressions) {
            sb.append(child.toString());
            sb.append(", ");
        }
        return name + sb.toString() + ")";
    }

    public void setValues(ArrayList<Pair<String, Double>> varValues) {
        if (this.type == Type.VAR) {
            for (Pair<String, Double> pair : varValues)
                if (name.equals(pair.key)) {
                    val = pair.value;
                    break;
                }
        }
            for (var child : childExpressions)
                child.setValues(varValues);
    }

    public Expression setValue(String var, double val) {
        if (this.type == Type.VAR) {
                if (name.equals(var)) {
                    this.val = val;
                }
        } else if (type == Type.REFERENCE) {
            return this;
        }

        for (var child : childExpressions)
            child.setValue(var, val);
        return this;
    }

    public Expression setType(Type type) {
        this.type = type;
        return  this;
    }


    public Expression replaceVar(String varName) {
        if (type == Type.REFERENCE) {
            return this;
        }
        for (var i = 0; i < childExpressions.size(); i++) {
            var curretChild = childExpressions.get(i);
            if (curretChild.type == Type.VAR && curretChild.name.equals(varName)) {
                if (curretChild.getChildren().isEmpty())
                    childExpressions.set(i, new Val(curretChild.getVal( new Var("") )));
                else
                    childExpressions.set(i,curretChild.getChild(0));
            } else {
                curretChild.replaceVar(varName);
            }
        }

        return this;
    }
    public Expression replaceVar(Expression varExpression) {
        if (type == Type.REFERENCE) {
            return this;
        }
        for (var i = 0; i < childExpressions.size(); i++) {
            var curretChild = childExpressions.get(i);
            if (curretChild.type == Type.VAR && curretChild.name.equals(varExpression.name)) {
                childExpressions.set(i, varExpression.getChild(0));
            } else {
                curretChild.replaceVar(varExpression);
            }
        }

        return this;
    }

    public Expression setExpressions(ArrayList<Expression> varValues) {
        for (var varValue : varValues)
            setExpression(varValue);
        return this;
    }

    public Expression setExpression(Expression expression) {
        if (type == Type.REFERENCE) {
            return this;
        }
        var i = 0;
        if (this.type == Type.VAR || this.type == Type.FUNCTION) {
                if (name.equals(expression.name)) {
                    setChild(expression.getChild(0),0);
                    i = 1;
                }
        }
        for (;i < childExpressions.size(); i++)
            childExpressions.get(i).setExpression(expression);

        return this;
    }

    public Expression setReference(Expression expression) {
        if (type == Type.REFERENCE) {
            return this;
        }
        var i = 0;
        if (this.type == Type.VAR || this.type == Type.FUNCTION) {
            if (name.equals(expression.name)) {
                var reference = new Var("Reference", expression.getChild(0)).setType(Type.REFERENCE);
                setChild(reference,0);
                i = 1;
            }
        }
        for (;i < childExpressions.size(); i++)
            childExpressions.get(i).setReference(expression);

        return this;
    }


    public ArrayList<String> getVars() {
        ArrayList<String> vars = new ArrayList<>();
        if (type == Type.VAR) {
            vars.add(name);
        }
        if (type != Type.REFERENCE)
            for (var child : childExpressions)
                vars.addAll(child.getVars());
        return (ArrayList<String>) vars.stream().distinct().collect(Collectors.toList());
    }

    public ArrayList<Expression> getExpressions(Type type) {
        ArrayList<Expression> expressions = new ArrayList<>();
        if (this.type == type) {
            expressions.add(this);
        }
        for (var child : childExpressions)
            expressions.addAll(child.getExpressions(type));
        return expressions;
    }

    public ArrayList<Expression> getExpressions(String name) {
        ArrayList<Expression> expressions = new ArrayList<>();
        if (this.name.equals(name)) {
            expressions.add(this);
        }
        for (var child : childExpressions)
            expressions.addAll(child.getExpressions(name));
        return expressions;
    }


    public boolean contains(ArrayList<String> names) {
        for (String name : names)
            if (contains(name))
                return true;
            return false;
    }

    public boolean contains(String name) {

        if (this.name.equals(name))
            return true;

        if (type == Type.REFERENCE)
            return childExpressions.get(0).name.equals(name);

        for (var child : childExpressions)
            if (child.contains(name))
                return true;

        return  false;
    }


    public boolean contains(Type type){
        if (this.type == type)
            return true;

        var iter = childExpressions.iterator();
        if (this.type == Type.REFERENCE)
            return childExpressions.get(0).type == type;

        while (iter.hasNext())
            if (iter.next().contains(type))
                return true;
        return  false;
    }


    public boolean contains(Expression expression) {

        if (this == expression)
            return true;
        if (type == Type.REFERENCE) {
            return childExpressions.get(0) == expression;
        }
        var iter = childExpressions.iterator();
        while (iter.hasNext())
            if (iter.next().contains(expression))
                return true;
        return  false;
    }

    public int getExpressionCount(int count) {
        if (type == Type.REFERENCE)
            return count + 1;

        for (var child : childExpressions)
                count += child.getExpressionCount(0);
        return count + 1;
    }

    public int getMaxDepth(int count) {

        if (type == Type.REFERENCE) {
            return count + 1;
        }
        int maxDepth = 0;
        for (var child : childExpressions)
                maxDepth = Math.max(child.getMaxDepth(0), maxDepth);
        return count + 1 + maxDepth;
    }

    public Expression getOptimized() throws CloneNotSupportedException{
        try {
            Expression expression = (Expression) super.clone();
            if (getVars().size() == 0) {
                return new Val(expression.getVal(new ArrayList<>()));
            }
            expression.childExpressions = new ArrayList<>();
            for (var child : childExpressions)
                if (child.type != Type.REFERENCE)
                    expression.childExpressions.add(child.getOptimized());
                else
                    expression.childExpressions.add(child);
            if (numberOfArgs > 1 && expression.getChildren().size() == 1)
                return expression.getChild(0);
            return expression;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Expression getOpen() throws CloneNotSupportedException{
        try {
            Expression expression = (Expression) super.clone();
            if (expression.getVars().isEmpty()) {
                return new Val(expression.getVal(new ArrayList<>()));
            }
            expression.childExpressions = new ArrayList<>();
            for (var child : childExpressions)
                if (child.type != Type.REFERENCE)
                    expression.childExpressions.add(child.getOpen());
                else
                    expression.childExpressions.add(child);
            return expression;
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    public double getVal(ArrayList<Expression> args) {return  0;};
    public double getVal(Expression arg) { return getVal(new ArrayList<>() {{add(arg);}}); };
    public double getVal() { return getVal(new ArrayList<>() {{add(new Var(""));}}); };
    public Expression getDerivative(String var) { return null; };
    public Expression getIntegral() { return null;};

    public Expression clone() throws CloneNotSupportedException
    {
        Expression expression = (Expression) super.clone();
        expression.childExpressions = new ArrayList<>();
        if (type == Type.REFERENCE) {
            expression.addChild(getChild(0));
            return expression;
        }
        for (var i = 0; i < childExpressions.size(); i++) {
            var childClone = childExpressions.get(i).clone();
            if (childClone.contains(Type.REFERENCE)) {
                var references = childClone.getChildren(this, new HashSet<>());
                for (var reference : references)
                    reference.setChild(expression, 0);
            }
            expression.addChild(childClone);
        }
        return expression;
    }


    public Expression setChild(Expression expression, int i) {

        if (expression != null) {
            expression.parent = this;
        }

        if (childExpressions.size() == i) {
            childExpressions.add(expression);
            return this;
        }

        if (childExpressions.size() < i) {
                return null;
        }

        childExpressions.set(i, expression);
        return this;
    }

    public Expression addChild(Expression expression) {
        if (expression != null) {
            expression.parent = this;
        }
        childExpressions.add(expression);
        return this;
    }


    public Expression addChildren(Collection<Expression> expressions) {
        for (var expression : expressions)
            addChild(expression);
        return this;
    }

    public Expression removeChild(Expression expression) {
        childExpressions.remove(expression);
        return this;
    }

    public Expression removeChildren(ArrayList<Expression> expression) {
        childExpressions.removeAll(expression);
        return this;
    }
    public Expression removeChildren() {
        childExpressions.clear();
        return this;
    }

    public Expression removeChild(int i) {
        childExpressions.remove(i);
        return this;
    }


    public Expression getChild(int i) {
        return childExpressions.size() > i ? childExpressions.get(i) : null;
    }

    public ArrayList<Expression> getChildren() {
        return childExpressions;
    }

    public ArrayList<Expression> getChildren(Expression expression, HashSet<Expression> set) {
        var iter = childExpressions.iterator();
        if (type == Type.REFERENCE)
            if (iter.hasNext()) {
                var child = iter.next();
                if (child == expression)
                    set.add(this);
                return new ArrayList<>(set);
            }
        while (iter.hasNext()) {
            var child = iter.next();
            if (child == expression)
                if (set.contains(this))
                    break;
                else
                    set.add(this);
            else
            if (child.contains(expression)) {
                child.getChildren(expression, set);
            }
        }
        return new ArrayList<>(set);
    }

    public ArrayList<Expression> getChildren(String name, HashSet<Expression> set) {
        if (this.name.equals(name))
            if (set.contains(this))
                return new ArrayList<Expression>(set);
            else
                set.add(this);

        for (var child : childExpressions)
            if (child.contains(name)) {
                child.getChildren(name, set);
            }
        return new ArrayList<>(set);
    }

    public ArrayList<Expression> getChildren(Type type, HashSet<Expression> set) {
        if (this.type == type)
            if (set.contains(this))
                return new ArrayList<Expression>(set);
            else
                set.add(this);

        for (var child : childExpressions)
            if (child.contains(type)) {
                child.getChildren(type, set);
            }
        return new ArrayList<>(set);
    }

    @Override
    public boolean equals(Object obj) {
        boolean eq = super.equals(obj) || (
                        obj != null
                        && (getClass().isInstance(obj)
                        && ((Expression)obj).type == this.type
                        && ((Expression)obj).priority == this.priority)
                        && ((Expression)obj).toString().equals(this.toString()));
        // check children eq
        return eq;
    }


    public boolean fillExpressions() {
        return false;
    }

    public void getInstance() {

    }

    public Expression getClone(){
        try {
            return clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }


}

//"((( (( ( (x - 2.0)) * (x - 3.0) ) / (((1.0 * 1.0) * (x - 2.0)) * (x - 3.0)) * 1.0)) + ((((1.0 * (x - 1.0)) * 1.0) * (x - 3.0)) / (((1.0 * (x - 1.0)) * 1.0) * (x - 3.0)) * 2.0)) + ((((1.0 * (x - 1.0)) * (x - 2.0)) * 1.0) / (((1.0 * (x - 1.0)) * (x - 2.0)) * 1.0) * 3.0))"
      //  ((x-2)(x-3))/