package sample.Expressions;

public class Sub extends Expression {

    @Override
    public double getVal() {
        return leftExpression.getVal() - rightExpression.getVal();
    }

    @Override
    public Expression getDerivative() {
        return new Sub(leftExpression.getDerivative(), rightExpression.getDerivative());
    }
    @Override
    public Expression getIntegral() {
        return null;
    }

    public Sub(Expression left, Expression right) {
        super(0, "Sub", Type.BINARY, 2, left, right);
    }
}
