package sample.Expressions;

public class Abs extends Expression {
    @Override
    public double getVal() {
        return Math.abs(rightExpression.getVal());
    }

    @Override
    public Expression getDerivative() {
        return new Div(new Mul(rightExpression, rightExpression.getDerivative()), new Abs(rightExpression));
    }

    @Override
    public Expression getIntegral() {
        return null;
    }

    public Abs(Expression right) {
        super(0, "Abs", Type.UNARY, 1, null, right);
    }
}
