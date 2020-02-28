package sample;

import sample.Expressions.Expression;
import sample.Expressions.ExpressionFactory;

import java.util.*;

public class UserSettings {
    private ArrayList<String> defaultExpressions;

    public ArrayList<Expression> getExpressions() {
        return expressions;
    }

    public ArrayList <Expression> expressions;
    double fromX;
    double toX;
    int precision;

    public int getOptimizationLevel() {
        return optimizationLevel;
    }

    public boolean needExtraOptimization() {
        return extraOptimization;
    }

    int optimizationLevel = 1;
    boolean extraOptimization = false;

    public UserSettings(String defaultExpressions) {

        this.defaultExpressions = new ArrayList<>(Arrays.asList(defaultExpressions.split("[;\n\r]+")));
        init();
    }

    public UserSettings() {
        this.defaultExpressions = new ArrayList<>();
        init();
    }

    private void init() {
        updateExpressions();
        fromX = -10;
        toX = 10;
        precision = 100;
    }

    public void setX(double fromX, double toX) {
        this.fromX = fromX;
        this.toX = toX;
    }

    public double getFromX() {
        return fromX;
    }

    public double getToX() {
        return toX;
    }

    public void setDefaultExpressions(String defaultExpressions) {
        this.defaultExpressions = new ArrayList<>(Arrays.asList(defaultExpressions.split("[;\n\r]+")));
        updateExpressions();
    }

    public ArrayList<String> getDefaultExpressions() {
        return defaultExpressions;
    }

    public String getDefaultExpressionsString() {
        StringBuilder sb = new StringBuilder();
        for (var def : defaultExpressions) {
            sb.append(def + ";\n");
        }
        return sb.toString();
    }

    private void updateExpressions() {
        expressions = new ArrayList<>();
        for (var def : defaultExpressions) {
                ExpressionFactory.getExpressionTree(def, expressions, this);
        }
        for (var i = expressions.size()-1; i > 0; i--) {
            for (var j = 0; j < i; j++)
                if (expressions.get(i).name.equals(expressions.get(j).name)) {
                    expressions.remove(j);
                    j--;
                    i--;
                }
        }
        for (var expressionToUpdate : expressions)
            for (var updateExpression : expressions)
                if (expressionToUpdate != updateExpression)
                    expressionToUpdate.setReference(updateExpression);
/*        for (var expression : expressions) {
            if (expression.getChildren().isEmpty())
                continue;
            Expression newExpression = null;
            while (!expression.equals(newExpression)) {
                try {
                    expression = newExpression;
                    newExpression = newExpression.clone();
                    for (var child : newExpression.getChildren())
                        child.setExpressions(expressions);
                } catch (CloneNotSupportedException e) {}
            }
        }*/

      }

    public Map<String, Expression> getExpressionsMap() {
        Map<String, Expression>  map = new HashMap<>();
            for (var ex : expressions) {
                if (ex.type == Expression.Type.EQUALITY && ex.getChild(0) != null && ex.getChild(1) != null) {
                    map.put(ex.getChild(0).name, ex.getChild(1));
                }
        }
        return map;
    }


}
