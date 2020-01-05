package sample.Expressions;

public class Mul extends Expression {
    @Override
    public double getVal() {
        return leftExpression.getVal() * rightExpression.getVal();
    }

    public Mul(Expression leftExpression, Expression rightExpression) {
        super(0, "Mul", Type.BINARY, 1, leftExpression, rightExpression);
    }
    public Mul() {
        super(0, "Mul", Type.BINARY, 1, null, null);
    }

    @Override
    public Expression getDerivative() {
        Expression leftVal = new Val(leftExpression.getVal());
        Expression rightVal = new Val(rightExpression.getVal());
        Expression leftDerivative = leftExpression.getDerivative();
        Expression rightDerivative = rightExpression.getDerivative();
        Expression leftMul = new Mul(leftVal, rightDerivative);
        Expression rightMull = new Mul(rightVal, leftDerivative);
        return new Sum(leftMul, rightMull);
    }

    @Override
    public Expression getIntegral() {
        return null;
    }
}
