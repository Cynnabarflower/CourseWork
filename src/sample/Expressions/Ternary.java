package sample.Expressions;

import java.util.ArrayList;

public class Ternary extends Expression {
    public Ternary(Expression condition, Expression positive, Expression negative) {
        super(0, "Ternary", Type.FUNCTION, ArgumentPosition.RIGHT, 10, 3, null, condition, positive, negative);
    }
    public Ternary() {
        super(0, "Ternary", Type.FUNCTION, ArgumentPosition.RIGHT, 10, 3, null);
    }

    @Override
    public double getVal(ArrayList<Expression> args) {
        if (childExpressions.isEmpty())
            return 0;
        if (getChild(0).getVal(args) > 0)
            return getChild(1).getVal(args);
        return getChild(2).getVal(args);
    }

}
