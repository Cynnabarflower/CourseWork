package sample;

import javafx.scene.canvas.Canvas;
import sample.Expressions.Expression;

import java.util.ArrayList;

public class GraphBuilder {
    ArrayList<Expression> expressions;

    public GraphBuilder(ArrayList<Expression> expressions, Canvas canvas) {
        this.expressions = expressions;

    }
}
