package sample.Expressions;

import java.util.ArrayList;
import java.util.Stack;

public class ExpressionFactory {
    public static Expression getExpression(String name) {
        if (name.length() == 1) {
            return getExpression(name.charAt(0));
        } else {
            if (name.startsWith("log")) {
                return new Log(null, null);
            } else if (name.startsWith("ln")) {
                return new Log(new Val(Math.E));
            } else if (name.startsWith("abs")) {
                return new Abs(null);
            } else if (name.startsWith("floor")) {
                return new Floor(null);
            }
            return null;
        }
    }

    public static Expression getExpression(char name) {
        if (name >= '0' && name <= '9') {
            return new Val(Double.parseDouble(""+name));
        }
        switch (name) {
            case '+': return new Sum();
            case '-': return new Sub(null, null);
            case '*': return new Mul();
            case '/': return new Div(null, null);
            case '(': return new Bracket(true);
            case ')': return new Bracket(false);
            case '^': return new Pow(null, null);

            default: return  new Var(12, ""+name);
        }
    }

    public static Expression getExpressionTree(String inputString){
        inputString = inputString.replaceAll(" ","");
        String inputStrings[] = inputString.split(";");
        String currentString = inputStrings[0];
        currentString = replaceComplexBrackets(currentString);
        ArrayList<Expression> expressions = getExpressionArray(currentString);
        Stack<Expression> stack = new Stack<>();
        ArrayList<Expression> postfixExpressions = new ArrayList<>();
        for (int i = 0; i < expressions.size(); i++) {
            Expression expression = expressions.get(i);
            if (expression.type == Expression.Type.VALUE) {
                postfixExpressions.add(expression);
            } else if (expression.type == Expression.Type.UNARY) {
                stack.push(expression);
            } else if (expression.type == Expression.Type.LEFT_BRACKET) {
                stack.push(expression);
            } else if (expression.type == Expression.Type.RIGHT_BRACKET) {
                while (!stack.empty()) {
                    Expression temp = stack.pop();
                    if (temp.name.equals("(")) {
                        break;
                    } else {
                        postfixExpressions.add(temp);
                    }
                }
            } else  if (expression.type == Expression.Type.BINARY) {
                while (!stack.empty()) {
                    if ((stack.peek().type == Expression.Type.UNARY || stack.peek().priority < expression.priority)
                            && stack.peek().type != Expression.Type.LEFT_BRACKET && stack.peek().type != Expression.Type.RIGHT_BRACKET) {
                        postfixExpressions.add(stack.pop());
                    } else {
                        break;
                    }
                }
                stack.push(expression);
            }
        }
        while (!stack.empty()) {
            postfixExpressions.add(stack.pop());
        }

        System.out.println();
        for (int i = 0; i < postfixExpressions.size(); i++)
            System.out.print(postfixExpressions.get(i).name+"["+postfixExpressions.get(i).val+"] ");
        System.out.println();

        stack = new Stack();
        for (int i = 0; i < postfixExpressions.size(); i++) {
            Expression current = postfixExpressions.get(i);
            if (current.type == Expression.Type.BINARY) {
                Expression right = stack.pop();
                Expression left = stack.pop();
                current.leftExpression = left;
                current.rightExpression = right;
                stack.push(current);
            } else if (current.type == Expression.Type.VALUE) {
                stack.push(current);
            } else if (current.type == Expression.Type.UNARY) {
                current.rightExpression = stack.pop();
                stack.push(current);
            }
        }
        return stack.pop();

    }

    private static ArrayList<Expression> getExpressionArray(String s) {

        ArrayList<Expression> expressions = new ArrayList<>();
        while (!s.isEmpty()) {
            if (s.startsWith("log")) {
                s = s.substring(3);
                expressions.add(new Log(null, null));
            } else if (s.startsWith("ln")) {
                s = s.substring(2);
                expressions.add(new Log(new Val(Math.E)));
            } else if (s.startsWith("abs")) {
                s = s.substring(3);
                expressions.add(new Abs(null));
            } else if (s.startsWith("floor")) {
                s = s.substring(5);
                expressions.add(new Floor(null));
            } else if (Character.isDigit(s.charAt(0))) {
                StringBuilder str = new StringBuilder(s.charAt(0));
                boolean frac = false;
                while (!s.isEmpty() && (Character.isDigit(s.charAt(0)) || ((s.charAt(0) == '.' || s.charAt(0) == ',')))) {
                    if (s.charAt(0) == '.' || s.charAt(0) == ',') {
                        if (!frac) {
                            str.append(s.charAt(0));
                        }
                    } else {
                        str.append(s.charAt(0));
                    }
                    s = s.substring(1);
                }
                expressions.add(new Val(Double.parseDouble(str.toString())));
            } else {
                expressions.add(ExpressionFactory.getExpression(s.charAt(0)));
                s = s.substring(1);
            }
        }

        return expressions;
    };

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
}
