package sample.Expressions;

public class Sub extends Expression {

    @Override
    public double getVal() {
        return leftExpression.getVal() - rightExpression.getVal();
    }

    @Override
    public Expression getDerivative(String var) {
        if (this.contains(var)) {
            return new Sub(leftExpression.getDerivative(var), rightExpression.getDerivative(var));
        }
        return new Val(0);
    }
    @Override
    public Expression getIntegral() {
        return null;
    }

    public Sub(Expression left, Expression right) {
        super(0, "Sub", Type.BINARY, ArgumentPosition.LEFT_AND_RIGHT,2, left, right);
    }
}