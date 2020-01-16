package sample.Expressions;

public class Mul extends Expression {
    @Override
    public double getVal() {
        return leftExpression.getVal() * rightExpression.getVal();
    }

    public Mul(Expression leftExpression, Expression rightExpression) {
        super(0, "Mul", Type.FUNCTION, ArgumentPosition.LEFT_AND_RIGHT ,20, 2, leftExpression, rightExpression);
    }
    public Mul() {
        super(0, "Mul", Type.FUNCTION, ArgumentPosition.LEFT_AND_RIGHT, 20, 2,new Val(1), null);
    }

    @Override
    public Expression getDerivative(String var) {
        if (this.contains(var)) {
                /*Expression leftVal = new Val(leftExpression.getVal());
                Expression rightVal = new Val(rightExpression.getVal());*/
                Expression leftDerivative = leftExpression.getDerivative(var);
                Expression rightDerivative = rightExpression.getDerivative(var);
                Expression leftMul = new Mul(leftExpression, rightDerivative);
                Expression rightMull = new Mul(rightExpression, leftDerivative);
                return new Sum(leftMul, rightMull);
            }
        return new Val(0);
    }

    @Override
    public Expression getOptimized() throws CloneNotSupportedException{
        Expression expression = super.getOptimized();
        if (expression.type == Type.VALUE)
            return expression;
        if (expression.leftExpression.type == Type.VALUE && expression.rightExpression.type == Type.VALUE) {
            return new Val(expression.leftExpression.val * expression.rightExpression.val);
        }
        if (expression.leftExpression.type == Type.VALUE && expression.leftExpression.val == 0 ||
                expression.rightExpression.type == Type.VALUE && expression.rightExpression.val == 0) {
            return new Val(0);
        }
        if (expression.leftExpression.type == Type.VALUE && expression.leftExpression.val == 1) {
            return expression.rightExpression;
        }
        if (expression.rightExpression.type == Type.VALUE && expression.rightExpression.val == 1) {
            return expression.leftExpression;
        }
        if (expression.leftExpression.type == Type.VAR && expression.rightExpression.type == Type.VAR &&
            expression.leftExpression.name.equals(expression.rightExpression.name)){
            return new Pow(expression.leftExpression, new Val(2));
        }
        if (expression.leftExpression.type == Type.FUNCTION && expression.leftExpression.numberOfArgs == 2 && expression.leftExpression.priority > priority) {
            expression.leftExpression.setLeftExpression(new Mul(expression.leftExpression.leftExpression, rightExpression));
            expression.leftExpression.setRightExpression(new Mul(expression.leftExpression.rightExpression, rightExpression));
            return expression.leftExpression;
        }
        if (expression.rightExpression.type == Type.FUNCTION && expression.rightExpression.numberOfArgs == 2 && expression.rightExpression.priority > priority) {
            expression.rightExpression.setLeftExpression(new Mul(expression.rightExpression.leftExpression, rightExpression));
            expression.rightExpression.setRightExpression(new Mul(expression.rightExpression.rightExpression, rightExpression));
            return expression.rightExpression;
        }
        return expression;
    }

    @Override
    public Expression getIntegral() {
        return null;
    }

    @Override
    public String toString() {
        String sLeft = leftExpression.toString();
        String sRight = rightExpression.toString();
        if (leftExpression.priority > priority) {
            sLeft = "("+sLeft+")";
        }
        if (rightExpression.priority > priority) {
            sRight = "("+sRight+")";
        }
        return sLeft + "*" + sRight;
    }

    @Override
    public boolean fillExpressions() {
        if (leftExpression == null) {
            leftExpression = new Val(1);
        }
        if (rightExpression == null) {
            rightExpression = new Val(1);
        }
        return true;
    }
}
