package sample.Expressions;

import sample.Main;
import sample.Pair;
import sample.WrongExpressionException;

import java.util.*;
import java.util.stream.Collectors;

import static sample.WrongExpressionException.Type.INCORRECT_BRACKETS;

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

    public static ArrayList<Expression> getExpressionTree(String inputString, ArrayList<Expression> vars) {
        if (vars == null)
            vars = new ArrayList<>();
        ArrayList<Expression> expressions = new ArrayList<>();
        inputString = inputString.replaceAll(" ", "");
        String[] inputStrings = inputString.split(";");
        for (String currentString : inputStrings) {
            if (currentString.isEmpty()) {
                continue;
            }

            try {
                currentString = replaceComplexBrackets(currentString);
                if (currentString.contains("=")) {
                    var parts = currentString.split("=");
                    if (parts.length != 2) {
                        throw new WrongExpressionException(WrongExpressionException.Type.INCORRECT_EQUALITY);
                    }
                    var left = getExpressionArray(parts[0], vars);
                    ArrayList<String> args = new ArrayList<>();
                    if (!(left.get(0) instanceof Var))
                        throw new WrongExpressionException(WrongExpressionException.Type.INCORRECT_NAME);
                    Var varExpression = (Var) left.get(0);
                    for (var j = 1; j < left.size(); j++) {
                        var childExpression = left.get(j);
                        if (childExpression.type == Expression.Type.VAR) {
                            childExpression.type = Expression.Type.FUNCTION;
                            childExpression.numberOfArgs = 1;
                            args.add(childExpression.name);
                        }
                    }
                    varExpression.type = Expression.Type.FUNCTION;
                    varExpression.argumentPosition = Expression.ArgumentPosition.RIGHT;
                    varExpression.numberOfArgs = args.size() + 1;  // 1 extra for the foo expression
                    varExpression.setArgNames(args);
                    vars.add(varExpression);
                    var child = getExpressionTree(getExpressionArray(parts[1], vars));
/*                var functionChildren = child.getChildren(Expression.Type.FUNCTION, new HashSet<>());
                for (var functionChild : functionChildren) {
                    boolean contains = false;
                    for (var variable : vars) {
                        if (variable.equals(functionChild)) {
                            contains = true;
                            break;
                        }
                        if (!contains) {

                        }
                    }
                }*/
                    varExpression.setChild(child, 0);
                    var reference = new Var("Reference", varExpression.getChild(0));
                    reference.setType(Expression.Type.REFERENCE);
                    varExpression.getChild(0).setExpression(new Var(varExpression.name, reference));

                    expressions.add(varExpression);
                } else {
                    ArrayList<Expression> expressionsArray = getExpressionArray(currentString, vars);
                    var currentExpression = getExpressionTree(expressionsArray);
                    expressions.add(currentExpression);
                }
            } catch (WrongExpressionException e) {
                Main.warnings.add(e);
                e.printStackTrace();
            }
        }

        ArrayList<Expression> expressionsToSet = new ArrayList<>();
        for (Expression expression : expressions) {
            if (expression.type == Expression.Type.EQUALITY) {
                try {
                    expressionsToSet.add(expression.getChild(0).clone().setChild(expression.getChild(1).clone(), 0));
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }
                ;
            } else {
                expression.setExpressions(expressionsToSet);
            }
        }

        for (var i = vars.size() - 1; i >= 0; i--) {
            for (var j = 0; j < i; j++)
                if (vars.get(i).name.equals(vars.get(j).name)) {
                    vars.remove(j);
                    j--;
                    i--;
                }
        }

        for (var i = 0; i < expressions.size(); i++) {

            for (var varExpression : vars) {
                if (varExpression.getVars().isEmpty()) {
                    if (expressions.get(i).name.equals(varExpression.name)) {
                        expressions.set(i, varExpression.getChild(0));
                    } else {
                        expressions.get(i).replaceVar(varExpression);
                    }
                } else {
                    expressions.get(i).setExpression(varExpression);
                }
            }

        try {
            System.out.println(expressions.get(i));
            expressions.set(i, ExpressionFactory.optimize(ExpressionFactory.open(expressions.get(i))));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

        return expressions;
    }

    private static Expression getExpressionTree(ArrayList<Expression> expressionsArray) throws WrongExpressionException {
        Stack<Expression> stack = new Stack<>();
        ArrayList<Expression> postfixExpressions = new ArrayList<>();

        for (int i = 0; i < expressionsArray.size(); i++) {
            if (expressionsArray.get(i).type == Expression.Type.LEFT_BRACKET) {
                int brackets = 1;
                ArrayList<Expression> fooArgs = new ArrayList<>();
                ArrayList<Expression> argParts = new ArrayList<>();
                int j = i + 1;
                while (brackets > 0) {
                    var currentArg = expressionsArray.get(j);

                    if (brackets == 1 && currentArg.type == Expression.Type.DIVIDER && currentArg.argumentPosition == Expression.ArgumentPosition.NONE) {
                        if (!argParts.isEmpty()) {
                            fooArgs.add(getExpressionTree(argParts));
                            argParts.clear();
                        }
                    } else {
                        if (currentArg.type == Expression.Type.LEFT_BRACKET) {
                            brackets++;
                            argParts.add(currentArg);
                        } else if (currentArg.type == Expression.Type.RIGHT_BRACKET) {
                            brackets--;
                            argParts.add(currentArg);
                        } else {
                            argParts.add(currentArg);
                        }
                    }
                    j++;
                }
                Expression foo = null;
                boolean firstArg = true;
                if (i > 0 && (expressionsArray.get(i - 1).argumentPosition == Expression.ArgumentPosition.RIGHT)) {
                    foo = expressionsArray.get(i - 1);
                    firstArg = true;
                } else if (j < expressionsArray.size() && expressionsArray.get(j).argumentPosition == Expression.ArgumentPosition.LEFT) {
                    foo = expressionsArray.get(j);
                    firstArg = false;
                } else {
                    if (argParts.size() > 2) {
                        if (argParts.get(0).type == Expression.Type.LEFT_BRACKET && argParts.get(argParts.size() - 1).type == Expression.Type.RIGHT_BRACKET) {
                            argParts.remove(0);
                            argParts.remove(argParts.size() - 1);
                        }

                        var poly = getExpressionTree(argParts);
                        argParts.clear();
                        for (; j > i; j--)
                            expressionsArray.remove(i);

                        expressionsArray.add(i, new Bracket(true));
                        expressionsArray.add(i + 1, poly);
                        expressionsArray.add(i + 2, new Bracket(false));


                    }
                    continue;
                }
                if (!argParts.isEmpty()) {
                    fooArgs.add(getExpressionTree(argParts));
                }
                if (!fooArgs.isEmpty()) {
                    foo.addChildren(fooArgs);
                    if (foo.numberOfArgs > foo.getChildren().size()) {
                        if (!foo.fillExpressions()) {
                            throw new WrongExpressionException(WrongExpressionException.Type.WRONG_ARGS_QUAN, foo.toString());
                        }
                    } else if (foo.numberOfArgs < foo.getChildren().size()) {
                        throw new WrongExpressionException(WrongExpressionException.Type.WRONG_ARGS, foo.toString());
                    }
                    for (; j > i; j--)
                        expressionsArray.remove(i);

                }
            }
        }

        for (int i = 0; i < expressionsArray.size(); i++) {
            Expression expression = expressionsArray.get(i);
            if (expression.type == Expression.Type.VALUE || expression.type == Expression.Type.VAR) {
                postfixExpressions.add(expression);
            } else if (expression.numberOfArgs - expression.getChildren().size() == 1) {
                if (expression.argumentPosition == Expression.ArgumentPosition.LEFT) {
                    postfixExpressions.add(expression);
                } else if (expression.argumentPosition == Expression.ArgumentPosition.RIGHT) {
                    stack.push(expression);
                } else if (expression.argumentPosition == Expression.ArgumentPosition.LEFT_AND_RIGHT) {
                    stack.push(expression);
                } else {
                    throw new WrongExpressionException(WrongExpressionException.Type.WRONG_ARGS_QUAN, expression.toString());
                }
            } else if (expression.numberOfArgs - expression.getChildren().size() == 2) {
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
            } else if (expression.numberOfArgs - expression.getChildren().size() > 2) {
                if (expression.argumentPosition == Expression.ArgumentPosition.RIGHT) {
                    stack.push(expression);
                }
            } else if (expression.type == Expression.Type.LEFT_BRACKET) {
                stack.push(expression);
            } else if (expression.type == Expression.Type.RIGHT_BRACKET) {
                while (!stack.empty()) {

                    Expression temp = stack.pop();
                    if (temp.type == Expression.Type.LEFT_BRACKET) {
                        if (!stack.empty() &&
                                stack.peek().argumentPosition == Expression.ArgumentPosition.RIGHT) {
                            postfixExpressions.add(stack.pop());
                        }
                        break;
                    } else {
                        postfixExpressions.add(temp);
                    }
                }

                // postfixExpressions.add(new Bracket());
            } else if (expression.type == Expression.Type.DIVIDER) {
                if (expression.argumentPosition == Expression.ArgumentPosition.NONE)
                    while (!stack.empty() && stack.peek().type != Expression.Type.LEFT_BRACKET) {
                        postfixExpressions.add(stack.pop());
                    }
                else {
                    postfixExpressions.add(expression);
                }
            } else {
                postfixExpressions.add(expression);
            }
        }
        while (!stack.empty()) {
            postfixExpressions.add(stack.pop());
        }

        stack = new Stack();

        for (int i = 0; i < postfixExpressions.size(); i++) {
            Expression current = postfixExpressions.get(i);
            try {
                if (current.type == Expression.Type.FUNCTION) {
                    if (current.numberOfArgs - current.getChildren().size() > 1) {
                        Stack<Expression> args = new Stack<>();
                        int leftArgs = current.numberOfArgs - current.getChildren().size();
                        while (leftArgs > 0) {
                            if (stack.empty()) {
                                while (!args.isEmpty())
                                    current.addChild(args.pop());
                                if (!current.fillExpressions()) {
                                    throw new WrongExpressionException(WrongExpressionException.Type.WRONG_ARGS_QUAN, "(" + (current.numberOfArgs - leftArgs) + "/" + current.numberOfArgs + ") " + current.name);
                                } else {
                                    break;
                                }
                            }
                            args.add(stack.pop());
                            leftArgs--;
                        }
                        while (!args.isEmpty())
                            current.addChild(args.pop());
                        stack.push(current);
                    } else if (current.numberOfArgs - current.getChildren().size() == 1) {
                        current.addChild(stack.pop());
                        stack.push(current);
                    } else {
                        stack.push(current);
                    }
                } else if (current.type == Expression.Type.VALUE || current.type == Expression.Type.VAR) {
                    stack.push(current);
                } else if (current.type == Expression.Type.EQUALITY) {
                    Expression right = stack.pop();
                    Expression left = stack.pop();
                    if (left.type != Expression.Type.VAR && left.type != Expression.Type.VALUE) {
                        throw new WrongExpressionException(WrongExpressionException.Type.INCORRECT_EQUALITY);
                    }
                    current.setChild(left, 0);
                    current.setChild(right, 1);
                    stack.push(current);
                } else if (current.type == Expression.Type.DERIVATIVE) {
                    stack.push(stack.pop().getDerivative(current.getChild(0).name));
                } else {
                    stack.push(current);
                }
            } catch (EmptyStackException e) {
                throw new WrongExpressionException(WrongExpressionException.Type.WRONG_ARGS, current.toString());
            }
        }
        var expressionToAdd = stack.pop();
        if (expressionToAdd.type == Expression.Type.DIVIDER) {
            ArrayList<Expression> points = new ArrayList<>();
            points.add(expressionToAdd);
            while (!stack.empty()) {
                if (stack.peek().type == Expression.Type.DIVIDER) {
                    points.add(stack.pop());
                } else {
                    throw new WrongExpressionException(WrongExpressionException.Type.OTHER, "Can\'t have points and expression in one line");
                }
            }
            return getPolynom(points);//ExpressionFactory.optimize(getPolynom(points));

        } else if (!stack.empty()) {
            throw new WrongExpressionException(WrongExpressionException.Type.OPERATOR_MISSED, expressionToAdd.toString());
        }

        if (expressionToAdd.type == Expression.Type.EQUALITY) {
            if (expressionToAdd.getChild(1).contains(expressionToAdd.getChild(0).name)) {
                throw new WrongExpressionException(WrongExpressionException.Type.DUPLICATE_VAR_ASSIGNMENT, expressionToAdd.getChild(0).name);
            }

        } else {
            /*if (expressionToAdd.contains(Expression.Type.EQUALITY)) {
                throw new WrongExpressionException("Can only have one \"=\" in a sentence");
            }*/

        }
        return expressionToAdd;
    }


    private static ArrayList<Expression> getExpressionArray(String s, ArrayList<Expression> vars) {

        if (vars != null) {
            vars.sort((s1, t1) -> t1.name.length() - s1.name.length());
        } else {
            vars = new ArrayList<>();
        }

        ArrayList<Expression> expressions = new ArrayList<>();
        while (!s.isEmpty()) {
            if (s.startsWith("log") && (s.length() == 3 || !Character.isAlphabetic(s.charAt(3)))) {
                s = s.substring(3);
                addExpression(expressions, new Log());
            } else if (s.startsWith("ln") && (s.length() == 2 || !Character.isAlphabetic(s.charAt(2)))) {
                s = s.substring(2);
                addExpression(expressions, new Log(new Val(Math.E)));
            } else if (s.startsWith("abs") && (s.length() == 3 || !Character.isAlphabetic(s.charAt(3)))) {
                s = s.substring(3);
                addExpression(expressions, new Abs());
            } else if (s.startsWith("floor") && (s.length() == 5 || !Character.isAlphabetic(s.charAt(5)))) {
                s = s.substring(5);
                addExpression(expressions, new Floor());
            } else if (s.startsWith("divider") && (s.length() == 7 || !Character.isAlphabetic(s.charAt(7)))) {
                s = s.substring(7);
                addExpression(expressions, new Divider(Expression.ArgumentPosition.RIGHT));
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
                for (Expression varExpression : vars)
                    if (s.startsWith(varExpression.name) && (s.length() == varExpression.name.length() || !Character.isAlphabetic(s.charAt(varExpression.name.length())))) {
                        try {
                            addExpression(expressions, varExpression.clone());
                        } catch (CloneNotSupportedException e) {
                            e.printStackTrace();
                            //addExpression(expressions, new Var(0, varExpression.name));
                        }
                        s = s.substring(varExpression.name.length());
                        varFounded = true;
                        break;
                    }

                if (!varFounded) {
                    char symb = s.charAt(0);
                    switch (symb) {
                        case '+':
                            addExpression(expressions, new Sum());
                            break;
                        case '-':
                            if (expressions.isEmpty() || expressions.get(expressions.size() - 1).type == Expression.Type.DIVIDER
                                    || expressions.get(expressions.size() - 1).type == Expression.Type.EQUALITY
                                    || expressions.get(expressions.size() - 1).type == Expression.Type.LEFT_BRACKET
                                    || (expressions.get(expressions.size() - 1).type == Expression.Type.FUNCTION) && expressions.get(expressions.size() - 1).argumentPosition == Expression.ArgumentPosition.LEFT_AND_RIGHT) {
                                if (s.length() > 1 && Character.isDigit(s.charAt(1))){
                                        StringBuilder dig = new StringBuilder();
                                        s = s.substring(1);
                                        while (Character.isDigit(s.charAt(0))) {
                                            dig.append(s.charAt(0));
                                            s = s.substring(1);
                                        }
                                        addExpression(expressions, new Val(-Double.parseDouble(dig.toString())));
                                } else
                                    addExpression(expressions, new Mul(new Val(-1)));
                            } else {
                                addExpression(expressions, new Sub());
                            }
                            break;
                        case '*':
                            addExpression(expressions, new Mul());
                            break;
                        case '/':
                            addExpression(expressions, new Div());
                            break;
                        case '(':
                            addExpression(expressions, new Bracket(true));
                            break;
                        case ')':
                            addExpression(expressions, new Bracket(false));
                            break;
                        case '^':
                            addExpression(expressions, new Pow());
                            break;
                        case '=':
                            addExpression(expressions, new Equality());
                            break;
                        case ',':
                            addExpression(expressions, new Divider());
                            break;
                        case '\'':
                            addExpression(expressions, new Derivative());
                            break;
                        case '?':
                            addExpression(expressions, new Ternary());
                            break;
                        default:
                            StringBuilder varName = new StringBuilder();
                            while (Character.isAlphabetic(symb)) {
                                varName.append(symb);
                                s = s.substring(1);
                                if (s.isEmpty())
                                    break;
                                symb = s.charAt(0);
                            }
                            var toAdd = new Var(varName.toString(), 0);
                            addExpression(expressions, toAdd);
                            if (s.startsWith("(")) {
                                addExpression(expressions, new Bracket(true));
                                int openBrackets = 1;
                                s = s.substring(1);
                                toAdd.setType(Expression.Type.FUNCTION);
                                toAdd.argumentPosition = Expression.ArgumentPosition.RIGHT;
                                int numberOfArgs = 0;
                                StringBuilder argBuilder = new StringBuilder();
                                while (openBrackets > 0) {
                                    if (s.startsWith(")")) {
                                        openBrackets--;
                                    } else if (s.startsWith("(")) {
                                        openBrackets++;
                                    } else if (s.startsWith(",")) {
                                        if (argBuilder.length() > 0) {
                                            expressions.addAll(getExpressionArray(argBuilder.toString(), vars));
                                            argBuilder = new StringBuilder();
                                            numberOfArgs++;
                                        }
                                    } else {
                                        argBuilder.append(s.charAt(0));
                                    }
                                    s = s.substring(1);
                                }
                                if (argBuilder.length() > 0) {
                                    expressions.addAll(getExpressionArray(argBuilder.toString(), vars));
                                    toAdd.numberOfArgs = numberOfArgs + 1;
                                }
                                addExpression(expressions, new Bracket(false));
                            }
                            //vars.add(new Var(0, varName.toString()));
                            continue;
                    }
                    s = s.substring(1);
                }
            }
        }

        return expressions;
    }

    ;

    private static void addExpression(ArrayList<Expression> expressions, Expression expression) {
        if (expression.type == Expression.Type.LEFT_BRACKET && !expressions.isEmpty() && expressions.get(expressions.size() - 1).type == Expression.Type.RIGHT_BRACKET) {
            expressions.add(new Mul());
        }
        expressions.add(expression);
    }

    private static String replaceComplexBrackets(String s) throws WrongExpressionException {
        Stack<Character> brackets = new Stack();
        StringBuilder formated = new StringBuilder();
        int i = 0;
        for (; i < s.length(); i++) {
            char current = s.charAt(i);
            if (current == '{' || current == '[' || current == '(') {
                brackets.push(current);
                formated.append(current);
            } else if (current == '}') {
                if (!brackets.empty() && brackets.peek() == '{') {
                    brackets.pop();
                    formated.insert(formated.lastIndexOf("{"), "divider(");
                    formated.deleteCharAt(formated.lastIndexOf("{"));
                    formated.append(")");
                } else break;
            } else if (!brackets.empty() && current == ']') {
                if (brackets.peek() == '[') {
                    brackets.pop();
                    formated.insert(formated.lastIndexOf("["), "floor(");
                    formated.deleteCharAt(formated.lastIndexOf("["));
                    formated.append(")");
                } else break;
            } else if (!brackets.empty() && current == ')') {
                if (brackets.peek() == '(') {
                    brackets.pop();
                    formated.append(current);
                } else break;

            } else if (current == '|') {
                if (i == 0 || !(Character.isAlphabetic(s.charAt(i - 1)) || Character.isDigit(s.charAt(i - 1)))) {
                    brackets.push(current);
                    formated.append(current);
                } else if (!brackets.empty() && brackets.peek() == '|') {
                    formated.insert(formated.lastIndexOf("|"), "abs(");
                    formated.deleteCharAt(formated.lastIndexOf("|"));
                    formated.append(")");
                    brackets.pop();
                } else break;
            } else {
                formated.append(current);
            }
        }
        if (i < s.length() - 1 || !brackets.isEmpty()) {
            System.out.println("Incorrect brackets");
            throw new WrongExpressionException(INCORRECT_BRACKETS, brackets.isEmpty() ? "" : " \'"+brackets.pop().toString()+"\'");
        }
        return formated.toString();
    }


    /*
        public static Expression getPolynom(ArrayList<Double> xs, ArrayList<Double> ys) {
            Expression mainExpression = new Val(0);
            Expression expression = new Sub(new Var("x"), new Var("t"));
            Expression expression2 = new Sub(new Var("x"), new Var("t"));
            for (int i = 0; i < xs.size(); i++) {
                Expression series = getSeries(expression, "t", xs, "*");
                Expression temp = series;
                while (temp != null &&
                        temp.rightExpression != null &&
                        temp.rightExpression.rightExpression != null) {

                    if (temp.rightExpression.rightExpression.val == xs.get(i)) {
                        temp.setRightExpression(new Val(1));
                        break;
                    }
                    temp = temp.leftExpression;
                }
                Expression series2 = getSeries(expression2, "t", xs, "*");
                temp = series2;
                while (temp != null && temp.rightExpression != null && temp.rightExpression.rightExpression != null) {
                    if (temp.rightExpression.rightExpression.val == xs.get(i)) {
                        temp.setRightExpression(new Val(1));
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
    */
    public static Expression getPolynom(ArrayList<Expression> points) {
        boolean varIsUsed = false;
        String varsToUse[] = {"x", "t", "p", "y", "u", "polynom_argument"};
        ArrayList<String> usedVars = new ArrayList<>();
        for (Expression point : points) {
            usedVars.addAll(point.getVars());
        }
        usedVars = (ArrayList<String>) usedVars.stream().distinct().collect(Collectors.toList());
        for (int i = 0; i < varsToUse.length; i++) {
            varIsUsed = false;
            for (String usedVar : usedVars) {
                if (usedVar.equals(varsToUse[i])) {
                    varIsUsed = true;
                    break;
                }
            }
            if (!varIsUsed) {
                return getPolynom(points, varsToUse[i]);
            }
        }
        Main.showException("Consider using different var names");
        return null;
    }

    public static Expression getPolynom(ArrayList<Expression> points, String polynomVar) {
        Expression mainExpression = new Val(0);
        Expression expression = new Sub(new Var(polynomVar), new Var("polynomPar"));
        Expression expression2 = new Sub(new Var(polynomVar), new Var("polynomPar"));
        try {
            for (int i = 0; i < points.size(); i++) {
                ArrayList<Expression> copyPoints = new ArrayList<>();
                for (int j = 0; j < i; j++) {
                    copyPoints.add(points.get(j).getChild(0).clone());
                }
                Expression series = getSeries(expression, "polynomPar", copyPoints, "*");
                copyPoints = new ArrayList<>();
                for (int j = i + 1; j < points.size(); j++) {
                    copyPoints.add(points.get(j).getChild(0).clone());
                }
                series = new Mul(series, getSeries(expression, "polynomPar", copyPoints, "*"));

                copyPoints = new ArrayList<>();
                for (int j = 0; j < i; j++) {
                    copyPoints.add(points.get(j).getChild(0).clone());
                }
                Expression series2 = getSeries(expression2, "polynomPar", copyPoints, "*");
                copyPoints = new ArrayList<>();
                for (int j = i + 1; j < points.size(); j++) {
                    copyPoints.add(points.get(j).getChild(0).clone());
                }
                series2 = new Mul(series2, getSeries(expression2, "polynomPar", copyPoints, "*"));


                series2.setExpression(new Var(polynomVar, points.get(i).getChild(0)));
                series2 = series2.replaceVar(polynomVar);
                mainExpression = new Sum(mainExpression, new Mul(new Div(series, series2), points.get(i).getChild(1)));
            }
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return mainExpression;
    }

    public static Expression getSeries(Expression expression, int from, int to, int step, String var, ArrayList<Expression> values, String operation) {
        return getSeries(expression, from, to, step, var, values, operation.equals("*") ? new Mul() : operation.equals("+") ? new Sum() : new Sum());
    }

    public static Expression getSeries(Expression expression, int from, int to, int step, String var, ArrayList<Expression> values, Expression operation) {
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
            values = new ArrayList<Expression>();
            for (int i = from; i <= to; i++)
                values.add(new Val(i));
            to = to - from;
            from = 0;
        } else if (values.size() < from || values.size() < to) {
            Main.showException("Error when making series");
            return new Val(0);
        }
        Expression seriesExpression = null;
        try {
            seriesExpression = operation.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            Main.showException("Wrong series expression");
            return new Val(0);
        }
        ;

        try {
            for (; from <= to; from += step) {
                Expression iterationExpression = expression.clone();
                iterationExpression.setExpression(new Var(var, values.get(from)));
                iterationExpression = iterationExpression.replaceVar(var);
                seriesExpression.addChild(iterationExpression);
            }
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        ;
        if (seriesExpression.childExpressions.isEmpty()) {
            if (seriesExpression.fillExpressions()) {
                return seriesExpression;
            }
            Main.showException("Wrong series range");
            return new Val(1);
        } else if (seriesExpression.childExpressions.size() == 1) {
            return seriesExpression.childExpressions.get(0);
        }

        return seriesExpression;
    }

    public static Expression getSeries(Expression expression, int depth, String var, String op) {
        return getSeries(expression, 1, depth, 1, var, null, op);
    }

    public static Expression getSeries(Expression expression, int depth, String var, Expression op) {
        return getSeries(expression, 1, depth, 1, var, null, op);
    }

    public static Expression getSeries(Expression expression, String var, ArrayList<Expression> values, String operation) {
        return getSeries(expression, 0, values.size() - 1, 1, var, values, operation);
    }

    public static Expression optimize(Expression expression) {
        if (false)
            return expression;
        int ops = expression.getExpressionCount(0);
        int depth = expression.getMaxDepth(0);
        int derivatives = expression.getChildren(Expression.Type.DERIVATIVE, new HashSet<>()).size();
        int depth1 = 0;
        int ops1 = 0;
        int derivatives1 = 0;
        try {
            Expression expression1 = expression.getOptimized();
            ops1 = expression1.getExpressionCount(0);
            depth1 = expression1.getMaxDepth(0);
            derivatives1 = expression1.getChildren(Expression.Type.DERIVATIVE, new HashSet<>()).size();
            if (ops1 < ops || depth1 < depth || derivatives1 < derivatives) {
                return optimize(expression1);
            } else if (ops == ops1 && depth == depth1) {
                return expression1;
            } else {
                return expression;
            }
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    public static Expression open(Expression expression) {

        int ops = expression.getMaxDepth(0);
        try {
            Expression expression1 = expression.getOpen();
            if (expression1.getMaxDepth(0) < ops) {
                return open(expression1);
            } else {
                System.out.println("OPEN:");
                System.out.println(expression);
                return expression;
            }
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }


    public static ArrayList<Pair<Double, Double>> getPoints(Expression expression, String varName, double from, double to, int minSize, double eps) {
        double leng = Math.abs(to - from);
        double step = leng / (minSize - 1);
        Pair<Double, Double> pair;
        int doubleAcc = 0;
        ArrayList<Pair<Double, Double>> points = new ArrayList<>();
        for (double i = 0; i < minSize || from <= to; i++) {
            pair = new Pair<>(from, expression.getVal(new Var(varName, from)));
            if (eps > 0 && !points.isEmpty() && !Double.isNaN(pair.value)) {
                if (Math.abs(pair.value - points.get(points.size() - 1).value) > eps / step) {
                    if (doubleAcc < 3) {
                        i--;
                        step /= 2;
                        doubleAcc++;
                        continue;
                    }
                } else if (doubleAcc > 0) {
                    doubleAcc--;
                    step *= 2;
                }
            }
            points.add(pair);
            from += step;
        }
        return points;
    }

    public static ArrayList<Pair<Double, Double>> getPoints(Expression expression, String varName, double from, double to, int minSize) {
        return getPoints(expression, varName, from, to, minSize, 0);
    }

    public static ArrayList<Pair<Double, Double>> getPoints(ArrayList<Expression> expressions, String varName, double from, double to, int minSize, double eps) {
        ArrayList<Pair<Double, Double>> points = new ArrayList<>();
        for (Expression expression : expressions) {
            points.addAll(getPoints(expression, varName, from, to, minSize, eps));
            points.add(new Pair<>(Double.NaN, Double.NaN));
        }
        return points;
    }

    public static ArrayList<Pair<Double, Double>> getPoints(ArrayList<Expression> expressions, String varName, double from, double to) {
        return getPoints(expressions, varName, from, to, Math.min((int) Math.abs(to - from), 50), 0);
    }

}
