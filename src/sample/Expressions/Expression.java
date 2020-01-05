package sample.Expressions;

public abstract class Expression {

    public enum Type {
        LEFT_BRACKET,
        RIGHT_BRACKET,
        UNARY,
        BINARY,
        VALUE
    }

    public double val = 0;
    public String name;
    public Type type;
    public int priority;
    public int numberOfArgs;
    public Expression leftExpression = null;
    public Expression rightExpression = null;

    public Expression(double val, String name, Type type, int priority, Expression left, Expression right) {
        this.val = val;
        this.name = name;
        this.priority = priority;
        this.leftExpression = left;
        this.rightExpression = right;
        this.type = type;

        switch (type) {
            case VALUE: numberOfArgs = 0; break;
            case UNARY: numberOfArgs = 1; break;
            case BINARY: numberOfArgs = 2; break;
            case LEFT_BRACKET: numberOfArgs = 0; break;
            case RIGHT_BRACKET: numberOfArgs = 0; break;

        }
    }


    @Override
    public String toString() {
        String left = leftExpression == null ? "" : leftExpression.toString();
        String right = rightExpression == null ? "" : rightExpression.toString();
        String name = type == Type.VALUE ? ""+val : this.name+"("+left+","+right+")";
        return name;
    }

    public abstract double getVal();
    public abstract Expression getDerivative();
    public abstract Expression getIntegral();


}
