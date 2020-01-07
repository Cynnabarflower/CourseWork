package sample.Expressions;

import javafx.util.Pair;

import java.util.ArrayList;

public class Sum extends Expression {
    public Sum(Expression leftExpression, Expression rightExpression) {
        super(0, "Sum", Type.BINARY, ArgumentPosition.LEFT_AND_RIGHT, 2, leftExpression, rightExpression);
    }

    public Sum() {
        super(0, "Sum", Type.BINARY, ArgumentPosition.LEFT_AND_RIGHT, 2, null, null);
    }

    @Override
    public double getVal() {
        return leftExpression.getVal() + rightExpression.getVal();
    }

    @Override
    public Expression getDerivative(String var) {
        if (this.contains(var)) {
                return new Sum(leftExpression.getDerivative(var), rightExpression.getDerivative(var));
            }
        return new Val(0);
    }

    @Override
    public Expression getIntegral() {
        return null;
    }
}
