package sample;

import sample.Expressions.Expression;
import sample.Expressions.ExpressionFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserSettings {
    private String defaultExpressions;
    ArrayList <Expression> expressions;

    public UserSettings(String defaultExpressions) {
        this.defaultExpressions = defaultExpressions;
        updateExpressions();
    }

    public void setDefaultExpressions(String defaultExpressions) {
        this.defaultExpressions = defaultExpressions;
        updateExpressions();
    }

    public String getDefaultExpressions() {
        return defaultExpressions;
    }

    private void updateExpressions() {
        expressions = new ArrayList<>();
        String[] defs = defaultExpressions.split("[;\n\r]+");
        for (var def : defs) {
            try {
                expressions.addAll(ExpressionFactory.getExpressionTree(def, null));
            } catch (WrongExpressionException e) {
                e.printStackTrace();
            }
        }
    }

    public Map<String, Expression> getExpressionsMap() {
        Map<String, Expression>  map = new HashMap<>();
            for (var ex : expressions) {
                if (ex.type == Expression.Type.EQUALITY && ex.leftExpression != null && ex.rightExpression != null) {
                    map.put(ex.leftExpression.name, ex.rightExpression);
                }
        }
        return map;
    }


}
