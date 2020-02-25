package sample.Expressions;

import javafx.util.Pair;

import java.util.ArrayList;

public class Bracket extends Expression {

    @Override
    public Expression getDerivative(String var)  {
        return null;
    }

    @Override
    public Expression getIntegral() {
        return null;
    }

    public Bracket(boolean opening) {
        super(0,")", Type.RIGHT_BRACKET, ArgumentPosition.NONE,0, 0,null);
        if (opening) {
            name = "(";
            type = Type.LEFT_BRACKET;
        }
    }

    public Bracket() {
        super(0,"()", Type.RIGHT_BRACKET, ArgumentPosition.RIGHT,0, 2,null);
    }


    @Override
    public String toString() {
        return name;
    }
}
