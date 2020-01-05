package sample.Expressions;

public class Pow extends Expression {
    @Override
    public double getVal() {
        return Math.pow(leftExpression.getVal(), rightExpression.getVal());
    }

    @Override
    public Expression getDerivative() {

        return new Mul(
                new Pow(leftExpression, new Sub(rightExpression, new Val(1))),
                new Sum(
                        new Mul(rightExpression, leftExpression.getDerivative()),
                        new Mul(leftExpression, new Mul(new Log(new Val(Math.E), leftExpression.getDerivative()), rightExpression))
                ));
    }

    @Override
    public Expression getIntegral() {
        return null;
    }

    public Pow(Expression left, Expression right) {
        super(0, "Pow", Type.BINARY, 1, left, right);
    }
}
