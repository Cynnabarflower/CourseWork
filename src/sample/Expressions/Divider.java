package sample.Expressions;

public class Divider extends Expression {
    public Divider() {
        super(0, ",", Type.DIVIDER, ArgumentPosition.NONE, 0, 0,null, null);
    }

    public Divider(Expression left, Expression right) {
        super(0, ",", Type.DIVIDER, ArgumentPosition.RIGHT, 0, 2, left, right);
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

    @Override
    public String toString() {
        if(argumentPosition == ArgumentPosition.NONE) {
            return ",";
        } else {
            return "{"+leftExpression+", "+rightExpression+"}";
        }
    }
}
