package sample.Expressions;

public class Bracket extends Expression {
    @Override
    public double getVal() {
        return 0;
    }

    @Override
    public Expression getDerivative()  {
        return null;
    }

    @Override
    public Expression getIntegral() {
        return null;
    }

    public Bracket(boolean opening) {
        super(0,")", Type.RIGHT_BRACKET, 0, null, null);
        if (opening) {
            name = "(";
            type = Type.LEFT_BRACKET;
        }
    }
}
