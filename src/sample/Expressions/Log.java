package sample.Expressions;

public class Log extends Expression {
    @Override
    public double getVal() {
        return 0;
    }

    @Override
    public Expression getDerivative() {
        return new Div(new Val(1), new Mul(rightExpression, new Log(new Val(Math.E), leftExpression)));
    }

    @Override
    public Expression getIntegral() {
        return null;
    }

    public Log(Expression left, Expression right) {
        super(0, "Log", Type.BINARY, 1, left, right);
    }

    public Log(Expression left) {
        super(0, "Log", Type.UNARY, 1, left, null);
    }
}
