package sample.Expressions;

import java.util.ArrayList;

public class Divider extends Expression {
    public Divider() {
        super(0, ",", Type.DIVIDER, ArgumentPosition.NONE, 0, 0,null);
    }

    public Divider(Expression left, Expression right) {
        super(0, ",", Type.DIVIDER, ArgumentPosition.RIGHT, 0, 2, null, left, right);
    }

    public Divider(ArgumentPosition argumentPosition) {
        super(0, ",", Type.DIVIDER, argumentPosition, 0, 2,null);
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
            StringBuilder sb = new StringBuilder("(");
            for (var child : childExpressions) {
                sb.append(",");
                sb.append(child.toString());
            }
            sb.append(")");
            return sb.toString();
        }
    }

    @Override
    public boolean fillExpressions() {
        return true;
    }
}
