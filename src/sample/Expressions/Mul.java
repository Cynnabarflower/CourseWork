package sample.Expressions;

import javafx.util.Pair;

import java.util.ArrayList;

public class Mul extends Expression {
    @Override
    public double getVal() {
        return leftExpression.getVal() * rightExpression.getVal();
    }

    public Mul(Expression leftExpression, Expression rightExpression) {
        super(0, "Mul", Type.FUNCTION, ArgumentPosition.LEFT_AND_RIGHT ,1, 2, leftExpression, rightExpression);
    }
    public Mul() {
        super(0, "Mul", Type.FUNCTION, ArgumentPosition.LEFT_AND_RIGHT, 1, 2,new Val(1), null);
    }

    @Override
    public Expression getDerivative(String var) {
        if (this.contains(var)) {
                /*Expression leftVal = new Val(leftExpression.getVal());
                Expression rightVal = new Val(rightExpression.getVal());*/
                Expression leftDerivative = leftExpression.getDerivative(var);
                Expression rightDerivative = rightExpression.getDerivative(var);
                Expression leftMul = new Mul(leftExpression, rightDerivative);
                Expression rightMull = new Mul(rightExpression, leftDerivative);
                return new Sum(leftMul, rightMull);
            }
        return new Val(0);
    }

    @Override
    public Expression optimize() {
        Expression expression = super.optimize();
        if (expression.leftExpression.type == Type.VALUE && expression.rightExpression.type == Type.VALUE) {
            return new Val(expression.leftExpression.val * expression.rightExpression.val);
        }
        if (expression.leftExpression.type == Type.VALUE && expression.leftExpression.val == 0 ||
                expression.rightExpression.type == Type.VALUE && expression.rightExpression.val == 0) {
            return new Val(0);
        }
        return expression;
    }

    @Override
    public Expression getIntegral() {
        return null;
    }

    @Override
    public String toString() {
        String sLeft = leftExpression.toString();
        String sRight = rightExpression.toString();
        if (leftExpression.priority > priority) {
            sLeft = "("+sLeft+")";
        }
        if (rightExpression.priority > priority) {
            sRight = "("+sRight+")";
        }
        return sLeft + "*" + sRight;
    }
}
