package sample.Expressions;

import javafx.util.Pair;
import sample.WrongExpressionException;

import java.awt.*;
import java.util.*;

public class ExpressionFactory {


/*    public static Expression getExpression(char name) {
        // not used
        if (name >= '0' && name <= '9') {
            return new Val(Double.parseDouble(""+name));
        }
        switch (name) {
            case '+': return new Sum();
            case '-': return new Pow.Sub(null, null);
            case '*': return new Mul();
            case '/': return new Div(null, null);
            case '(': return new Bracket(true);
            case ')': return new Bracket(false);
            case '^': return new Pow(null, null);

            default: return  new Var(0, ""+name);
        }
    }*/

    public static ArrayList<Expression> getExpressionTree(String inputString, ArrayList<String> vars) throws WrongExpressionException{
        ArrayList<Expression> expressions = new ArrayList<>();
        inputString = inputString.replaceAll(" ","");
        String inputStrings[] = inputString.split(";");
        for (String currentString : inputStrings) {
            currentString = replaceComplexBrackets(currentString);
            ArrayList<Expression> expressionsArray = getExpressionArray(currentString, vars);
            Stack<Expression> stack = new Stack<>();
            ArrayList<Expression> postfixExpressions = new ArrayList<>();
            for (int i = 0; i < expressionsArray.size(); i++) {
                Expression expression = expressionsArray.get(i);
                if (expression.type == Expression.Type.VALUE || expression.type == Expression.Type.VAR) {
                    postfixExpressions.add(expression);
                } else if (expression.type == Expression.Type.FUNCTION && expression.numberOfArgs == 1) {
                    stack.push(expression);
                } else if (expression.type == Expression.Type.LEFT_BRACKET) {
                    stack.push(expression);
                } else if (expression.type == Expression.Type.RIGHT_BRACKET) {
                    while (!stack.empty()) {
                        Expression temp = stack.pop();
                        if (temp.name.equals("(")) {
                            if (!stack.empty() &&
                                    stack.peek().argumentPosition == Expression.ArgumentPosition.RIGHT) {
                                postfixExpressions.add(stack.pop());
                            }
                            break;
                        } else {
                            postfixExpressions.add(temp);
                        }
                    }
                } else if (expression.type == Expression.Type.FUNCTION) {
                    if (expression.argumentPosition == Expression.ArgumentPosition.LEFT_AND_RIGHT) {
                        while (!stack.empty()) {
                            if (((stack.peek().type == Expression.Type.FUNCTION && stack.peek().numberOfArgs == 1) || stack.peek().priority <= expression.priority)
                                    && stack.peek().type != Expression.Type.LEFT_BRACKET && stack.peek().type != Expression.Type.RIGHT_BRACKET) {
                                postfixExpressions.add(stack.pop());
                            } else {
                                break;
                            }
                        }
                    }
                    stack.push(expression);
                } else if (expression.type == Expression.Type.DIVIDER) {
                    while (!stack.empty() && stack.peek().type != Expression.Type.LEFT_BRACKET) {
                        postfixExpressions.add(stack.pop());
                    }
                }
            }
            while (!stack.empty()) {
                postfixExpressions.add(stack.pop());
            }

/*        System.out.println();
        for (int i = 0; i < postfixExpressions.size(); i++)
            System.out.print(postfixExpressions.get(i).name+"["+postfixExpressions.get(i).val+"] ");
        System.out.println();*/

            stack = new Stack();

            for (int i = 0; i < postfixExpressions.size(); i++) {
                Expression current = postfixExpressions.get(i);
                try {
                    if (current.type == Expression.Type.FUNCTION && current.numberOfArgs == 2) {
                        if (!stack.empty()) {
                            current.rightExpression = stack.pop();
                        }
                        if (!stack.empty()) {
                            current.leftExpression = stack.pop();
                        }
                        stack.push(current);
                    } else if (current.type == Expression.Type.VALUE || current.type == Expression.Type.VAR) {
                        stack.push(current);
                    } else if (current.type == Expression.Type.FUNCTION && current.numberOfArgs == 1) {
                        current.rightExpression = stack.pop();
                        stack.push(current);
                    } else if (current.type == Expression.Type.EQUALITY) {
                        Expression right = stack.pop();
                        Expression left = stack.pop();
                        if (left.type != Expression.Type.VAR && left.type != Expression.Type.VALUE) {
                            throw new WrongExpressionException("Wrong equality exception");
                        }
                        current.leftExpression = left;
                        current.rightExpression = right;
                        stack.push(current);
                    } else if (current.type == Expression.Type.DERIVATIVE) {
                        stack.push(stack.pop().getDerivative(current.leftExpression.name));
                    }
                } catch (EmptyStackException e) {
                    throw new WrongExpressionException("Wrong args for " + current.name);
                }
            }
           Expression expressionToAdd = stack.pop();
           vars = expressionToAdd.getVars();

            if (expressionToAdd.type == Expression.Type.EQUALITY) {
                for (String varName : vars)
                    if (expressionToAdd.leftExpression.name.equals(varName)) {
                        throw new WrongExpressionException("Duplicate var assignment: "+varName);
                    }
                expressions.add(0, expressionToAdd);
            } else {
                if (expressionToAdd.contains(Expression.Type.EQUALITY)) {
                    throw new WrongExpressionException("Can only have one \"=\" in a sentence");
                }
                expressions.add(expressionToAdd);
            }
        }

        ArrayList<Pair<String, Expression>> expressionsToSet = new ArrayList<>();
        for (Expression expression : expressions) {
            for (Expression varExpression : expressions) {
                if (varExpression.type == Expression.Type.EQUALITY && expression.contains(varExpression.leftExpression.name)) {
                    expressionsToSet.add(new Pair<>(varExpression.name, expression));
                }
            }
            expression.setExpressions(expressionsToSet);
            expressionsToSet = new ArrayList<>();
        }

        return expressions;
    }

    private static ArrayList<Expression> getExpressionArray(String s, ArrayList<String> vars) {
        if (vars != null) {
            vars.sort((s1, t1) -> t1.length() - s1.length());
        }
        boolean gotComma = false;
        ArrayList<Expression> expressions = new ArrayList<>();
        while (!s.isEmpty()) {
            if (s.startsWith("log")) {
                s = s.substring(3);
                addExpression(expressions, new Log(null, null));
            } else if (s.startsWith("ln")) {
                s = s.substring(2);
                addExpression(expressions, new Log());
            } else if (s.startsWith("abs")) {
                s = s.substring(3);
                addExpression(expressions, new Abs(null));
            } else if (s.startsWith("floor")) {
                s = s.substring(5);
                addExpression(expressions, new Floor(null));
            } else if (Character.isDigit(s.charAt(0))) {

                StringBuilder str = new StringBuilder(s.charAt(0));
                boolean frac = false;
                while (!s.isEmpty() && (Character.isDigit(s.charAt(0)) || ((s.charAt(0) == '.')))) {
                    if (s.charAt(0) == '.') {
                        if (!frac) {
                            str.append(s.charAt(0));
                            frac = true;
                        }
                    } else {
                        str.append(s.charAt(0));
                    }
                    s = s.substring(1);
                }
                addExpression(expressions, new Val(Double.parseDouble(str.toString())));
            } else {
                boolean varFounded = false;
                if (vars != null) {
                    for (String varName : vars)
                        if (s.startsWith(varName)) {
                            addExpression(expressions, new Var(0, varName));
                            s = s.substring(varName.length());
                            varFounded = true;
                            break;
                        }

                }
                if (!varFounded) {
                    char symb = s.charAt(0);
                    switch (symb) {
                        case '+':
                            addExpression(expressions, new Sum());
                            break;
                        case '-':
                            addExpression(expressions, new Sub());
                            break;
                        case '*':
                            addExpression(expressions, new Mul());
                            break;
                        case '/':
                            addExpression(expressions, new Div(null, null));
                            break;
                        case '(':
                            addExpression(expressions, new Bracket(true));
                            break;
                        case ')':
                            addExpression(expressions, new Bracket(false));
                            break;
                        case '^':
                            addExpression(expressions, new Pow(null, null));
                            break;
                        case '=':
                            addExpression(expressions, new Equality(null, null));
                            break;
                        case ',':
                            addExpression(expressions, new Divider());
                            break;
                        default:
                            addExpression(expressions, new Var(0, "" + symb));
                    }
                    s = s.substring(1);
                }
            }
            gotComma = false;
        }

        return expressions;
    };

    private static void addExpression(ArrayList<Expression> expressions, Expression expression) {
        if (!expressions.isEmpty())
        { if ((expressions.get(expressions.size()-1).type == Expression.Type.VALUE || expressions.get(expressions.size()-1).type == Expression.Type.RIGHT_BRACKET || expressions.get(expressions.size()-1).type == Expression.Type.VAR) &&
                (expression.type == Expression.Type.VALUE || expression.type == Expression.Type.VAR || expression.type == Expression.Type.LEFT_BRACKET)) {
                expressions.add(new Mul());
            }
        }
        expressions.add(expression);
    }

    private static String replaceComplexBrackets(String s) {
        Stack<Character> brackets = new Stack();
        String betweenModulus = "";
        StringBuilder formated = new StringBuilder();
        int i = 0;
        for (; i < s.length(); i++) {
            char current = s.charAt(i);
            if (current == '{' || current == '[' || current == '(') {
                brackets.push(current);
                formated.append(current);
            } else if (current == '}') {
                if (brackets.peek() == '{') {
                    brackets.pop();
                    formated.append(current);
                } else break;
            } else if (current == ']') {
                if (brackets.peek() == '[') {
                    brackets.pop();
                    formated.insert(formated.lastIndexOf("["),"floor(");
                    formated.deleteCharAt(formated.lastIndexOf("["));
                    formated.append(")");
                } else break;
            } else if (current == ')') {
                if (brackets.peek() == '(') {
                    brackets.pop();
                    formated.append(current);
                } else break;

            } else if (current == '|') {
                if (i == 0 || !(Character.isAlphabetic(s.charAt(i-1)) || Character.isDigit(s.charAt(i-1)))) {
                    brackets.push(current);
                    formated.append(current);
                } else if (brackets.peek() == '|') {
                    formated.insert(formated.lastIndexOf("|"),"abs(");
                    formated.deleteCharAt(formated.lastIndexOf("|"));
                    formated.append(")");
                    brackets.pop();
                } else break;
            } else {
                formated.append(current);
            }
        }
        if (i < s.length()-1) {
            System.out.println("Incorrect brackets");
            return null;
        }
        return formated.toString();
    }

    public static Expression getPolynom(ArrayList<Pair<Double, Double>> points) {
        ArrayList<Double> xs = new ArrayList<>();
        ArrayList<Double> ys = new ArrayList<>();
        for (Pair<Double, Double> point : points) {
            xs.add(point.getKey());
            ys.add(point.getValue());
        }
        return getPolynom(xs,ys);
    }

    public static Expression getPolynom(ArrayList<Double> xs, ArrayList<Double> ys) {
        Expression mainExpression = new Val(0);
        Expression expression = new Sub(new Var("x"), new Var("t"));
        Expression expression2 = new Sub(new Var("x"), new Var("t"));
        for (int i = 0; i < xs.size(); i++) {
            Expression series = getSeries(expression, "t", xs, "*");
            Expression temp = series;
            while (temp != null && temp.rightExpression != null && temp.rightExpression.rightExpression != null) {
                if (temp.rightExpression.rightExpression.val == xs.get(i)) {
                    temp.rightExpression = new Val(1);
                    break;
                }
                temp = temp.leftExpression;
            }
            Expression series2 = getSeries(expression2, "t", xs, "*");
            temp = series2;
            while (temp != null && temp.rightExpression != null && temp.rightExpression.rightExpression != null) {
                if (temp.rightExpression.rightExpression.val == xs.get(i)) {
                    temp.rightExpression = new Val(1);
                    break;
                }
                temp = temp.leftExpression;
            }
            series2.setValue("x", xs.get(i)).replaceVar("x");
            mainExpression = new Sum(mainExpression, new Mul(new Div(series, series2), new Val(ys.get(i))));
        }
        mainExpression.replaceVar("t");
        return mainExpression;
    }

    public static Expression getSeries(Expression expression, int from, int to, int step,  String var, ArrayList<Double> values, String operation) {
        if (var.isEmpty()) {
            ArrayList<String> vars = expression.getVars();
            if (vars.size() == 1) {
                var = vars.get(0);
            } else if (vars.size() == 0) {

            } else {
                for (String varName : vars) {
                    if (varName.equals("i") || varName.equals("n")) {
                        var = varName;
                        break;
                    }
                }
            }
        }
        if (values == null) {
            values = new ArrayList<Double>();
            for (int i = from; i <= to; i++)
                values.add((double)i);
            to = to - from;
            from = 0;
        } else if (values.size() < from || values.size() < to) {
            System.out.println("Cant take series");
            return null;
        }
        Expression seriesExpression = new Val(0);
        if (operation.equals("*")) {
            seriesExpression = new Val(1);
        }

        try {
            for (; from <= to; from += step) {
                Expression iterationExpression = expression.clone();
                iterationExpression.setValue(var, values.get(from));
                if (operation.equals("*")) {
                    seriesExpression = new Mul(seriesExpression, iterationExpression.clone());
                } else {
                    seriesExpression = new Sum(seriesExpression, iterationExpression.clone());
                }
            }
        } catch (CloneNotSupportedException e) { e.printStackTrace(); };
        return seriesExpression;
    }

    public static  Expression getSeries(Expression expression, int depth, String var, String op) {
        return getSeries(expression, 1, depth, 1, var, null, op);
    }

    public static Expression getSeries(Expression expression, String var, ArrayList<Double> values, String operation) {
        return getSeries(expression, 0, values.size()-1, 1, var, values, operation);
    }
}
