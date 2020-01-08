package sample.Expressions;

public class Divider extends Expression {
    public Divider() {
        super(0, ",", Type.DIVIDER, ArgumentPosition.NONE, 0, 0,null, null);
    }

    @Override
    public double getVal() {
        return 0;
    }

    @Override
    public Expression getDerivative(String var) {
        return null;
    }

    @Override
    public Expression getIntegral() {
        return null;
    }
}
