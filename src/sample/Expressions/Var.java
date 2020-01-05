package sample.Expressions;

public class Var extends Expression {
    public Var(double val, String name) {
        super(val, name, Type.VALUE, 0, null, null);
    }

    @Override
    public double getVal() {
        return val;
    }

    @Override
    public Expression getDerivative()  {
        return new Val(1);
    }

    @Override
    public Expression getIntegral() {
        return null;
    }
}
