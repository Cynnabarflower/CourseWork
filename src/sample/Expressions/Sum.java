package sample.Expressions;

public class Sum extends Expression {
    public Sum(Expression leftExpression, Expression rightExpression) {
        super(0, "Sum", Type.BINARY, 2, leftExpression, rightExpression);
    }

    public Sum() {
        super(0, "Sum", Type.BINARY, 2, null, null);
    }

    @Override
    public double getVal() {
        return leftExpression.getVal() + rightExpression.getVal();
    }

    @Override
    public Expression getDerivative() {
        return new Sum(leftExpression.getDerivative(), rightExpression.getDerivative());
    }

    @Override
    public Expression getIntegral() {
        return null;
    }
}
