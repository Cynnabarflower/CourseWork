package sample.Expressions;

public class Val extends Expression {
    @Override
    public double getVal() {
        return val;
    }

    @Override
    public Expression getDerivative() {
        return new Val(0);
    }

    @Override
    public Expression getIntegral() {
        return null;
    }

    public Val(double val) {
        super(val, "Val", Type.VALUE, 0, null, null);
    }
}
