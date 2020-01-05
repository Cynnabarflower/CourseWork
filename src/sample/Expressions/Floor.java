package sample.Expressions;

public class Floor extends Expression {
    @Override
    public double getVal() {
        return Math.floor(rightExpression.getVal());
    }

    @Override
    public Expression getDerivative() {
        return rightExpression.getDerivative();
    }

    @Override
    public Expression getIntegral() {
        return null;
    }

    public Floor(Expression right) {
        super(0, "Floor", Type.UNARY, 1, null,  right);
    }
}
