package sample.Expressions;

import javafx.util.Pair;

import java.util.ArrayList;

public class Mul extends Expression {
    @Override
    public double getVal() {
        return leftExpression.getVal() * rightExpression.getVal();
    }

    public Mul(Expression leftExpression, Expression rightExpression) {
        super(0, "Mul", Type.BINARY, ArgumentPosition.LEFT_AND_RIGHT ,1, leftExpression, rightExpression);
    }
    public Mul() {
        super(0, "Mul", Type.BINARY, ArgumentPosition.LEFT_AND_RIGHT, 1, null, null);
    }

    @Override
    public Expression getDerivative(String var) {
        if (this.contains(var)) {
                Expression leftVal = new Val(leftExpression.getVal());
                Expression rightVal = new Val(rightExpression.getVal());
                Expression leftDerivative = leftExpression.getDerivative(var);
                Expression rightDerivative = rightExpression.getDerivative(var);
                Expression leftMul = new Mul(leftVal, rightDerivative);
                Expression rightMull = new Mul(rightVal, leftDerivative);
                return new Sum(leftMul, rightMull);
            }
        return new Val(0);
    }

    @Override
    public void optimize() {
        super.optimize();
    }

    @Override
    public Expression getIntegral() {
        return null;
    }
}
