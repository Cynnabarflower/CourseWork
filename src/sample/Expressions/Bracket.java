package sample.Expressions;

import javafx.util.Pair;

import java.util.ArrayList;

public class Bracket extends Expression {
    @Override
    public double getVal() {
        return 0;
    }

    @Override
    public Expression getDerivative(String var)  {
        return null;
    }

    @Override
    public Expression getIntegral() {
        return null;
    }

    public Bracket(boolean opening) {
        super(0,")", Type.RIGHT_BRACKET, ArgumentPosition.NONE,0, null, null);
        if (opening) {
            name = "(";
            type = Type.LEFT_BRACKET;
        }
    }
}
