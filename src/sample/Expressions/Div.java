package sample.Expressions;

public class Div extends Expression {
    @Override
    public double getVal() {
            return leftExpression.getVal() / rightExpression.getVal();
    }

    @Override
    public Expression getDerivative() {
        return new Div(
                new Sum(
                    new Mul(leftExpression.getDerivative(), rightExpression),
                    new Mul(rightExpression.getDerivative(), leftExpression)),
                new Mul(rightExpression, rightExpression));
    }

    @Override
    public Expression getIntegral() {
        return null;
    }

    public Div(Expression left, Expression right) {
        super(0, "Div", Type.BINARY, 1, left, right);
    }
}
