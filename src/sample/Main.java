package sample;

import com.alibaba.fastjson.JSONObject;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;
import sample.Expressions.Expression;
import sample.Expressions.ExpressionFactory;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Main extends Application {

    static WebEngine webEngine = null;
    public static String inputExpression = "inputExpression";
    public static String inputExpressionValue = "inputExpressionValue";
    public static String derivativeExpression = "derivativeExpression";
    public static String derivativeExpressionValue = "derivativeExpressionValue";
    private static Map<String, UserSettings> userSettings;

    @Override
    public void start(Stage primaryStage) throws Exception{
        WebView browser = new WebView();

        webEngine = browser.getEngine();
        webEngine.load(getClass().getResource("/site/index.html").toExternalForm());
        JSObject windowObject = (JSObject) browser.getEngine().executeScript("window");
        windowObject.setMember("app", this);
        VBox root = new VBox();
        root.setPadding(new Insets(0));
        root.setSpacing(0);
        root.getChildren().addAll(browser);

        Scene scene = new Scene(root);

        primaryStage.setTitle("JavaFX WebView (o7planning.org)");
        primaryStage.setScene(scene);
        primaryStage.setWidth(1000);
        primaryStage.setHeight(400);

        primaryStage.show();
    }

    public void printLog(String s) {
        System.out.println("js: "+s);
    }


    public static void main(String[] args) {
        String[] colorsArr = {"0xff0000", "0x00ff00", "0x0000ff", "0xffff00", "0x00ffff", "0xff00ff", "0x000000"};
        new Main();
       // new MyServer();
/*        readExpressionsFromFile();
        String args2[] = {"C:\\Users\\Dmitry\\IdeaProjects\\CourseWork\\resources"};

       // launch(args);

        new http.Server(args2);*/

/*  new http.Server(args2);
  File file = new File("functions.cfg");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/


    }

    public Main() {
        //getExpressionsFromFile();
        userSettings = new HashMap<>();
        String index = "C:\\Users\\Dmitry\\IdeaProjects\\CourseWork\\resources";
        new http.Server(index, 8000);
    }

    public static UserSettings getDefaultSettings() {
        //ArrayList<Pair<String, Expression>> eqExpressions = new ArrayList<>();
        //String textExpressions[] = {};
        String stringExp = "";
        try {
            //textExpressions = new String(Main.class.getResource("/defaultExpressions.txt").openStream().readAllBytes()).split("[;\n\r]+");
            stringExp = new String(Main.class.getResource("/defaultExpressions.txt").openStream().readAllBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
/*        for (String textExpression : textExpressions) {
            try {
                ArrayList <Expression> expressions =  ExpressionFactory.getExpressionTree(textExpression, null);
                for (Expression expression : expressions)
                    if (expression.type == Expression.Type.EQUALITY && expression.leftExpression.type == Expression.Type.VAR) {
                        eqExpressions.add(new Pair<>(expression.leftExpression.name, expression.rightExpression));
                    }
            } catch (WrongExpressionException e) {
                e.printStackTrace();
            }
        }*/
        return new UserSettings(stringExp);

    }


    public static void setUserSettings(String id, String text) {
        String textExpressions[] = text.split("[;\n\r]+");
  //      ArrayList<Pair<String, Expression>> eqExpressions = new ArrayList<>();
/*        for (String textExpression : textExpressions) {
            try {
                ArrayList <Expression> expressions =  ExpressionFactory.getExpressionTree(textExpression, null);
                for (Expression expression : expressions)
                    if (expression.type == Expression.Type.EQUALITY && expression.leftExpression.type == Expression.Type.VAR) {
                        eqExpressions.add(new Pair<String, Expression>(expression.leftExpression.name, expression.rightExpression));
                    }
            } catch (WrongExpressionException e) {
                e.printStackTrace();
            }
        }*/
        userSettings.put(id, new UserSettings(text.replaceAll("[\n\r]+", "\n")));
    }

    public static UserSettings getUserSettings(String id) {
        var settings = userSettings.get(id);
        if (settings == null) {
            return getDefaultSettings();
        } else return settings;
    }

    public static void displayOutput(String id, String s) {
/*        JSObject windowObject = (JSObject)webEngine.executeScript("window");
        windowObject.setMember("resultString", s);
        windowObject.setMember("id", id);
        webEngine.executeScript("setText(id, resultString)");*/
    }


    public static void showException(String s) {
        displayOutput(inputExpression, s);
    }

    public static void displayGraph(ArrayList<Pair<Double, Double>> points, int id) {
        JSObject windowObject = (JSObject)webEngine.executeScript("window");
        windowObject.setMember("graphPoints", points);
        windowObject.setMember("graphId", id);
        webEngine.executeScript("drawGraph(graphPoints, graphId)");
       // windowObject.call("drawGraph", points);
    }

    public static void addVars(ArrayList<String> vars) {
/*        JSObject windowObject = (JSObject)webEngine.executeScript("window");
        for (String var : vars) {
            windowObject.setMember("newVar", var);
            webEngine.executeScript("addVar(newVar)");
        }*/
    }

    public static ArrayList<String> getVars(String s) {
        ArrayList<String> vars = new ArrayList<>();
        try {
        ArrayList<Expression> expressions = ExpressionFactory.getExpressionTree(s, null);
        for (var expression : expressions)
            vars.addAll(expression.getVars());
        } catch (WrongExpressionException e) {
            return null;
        }
        return  ((ArrayList<String>) vars.stream().distinct().collect(Collectors.toList()));
    }


    public static JSONObject readIt(String id, String s, String varNames) {

        ArrayList<String> vars = new ArrayList<>();
        ArrayList<Pair<String, Expression>> varValues = new ArrayList<>();
        ArrayList<String> varsFromExpressions = new ArrayList<>();
        if (!varNames.isEmpty()) {
            try {
                ArrayList<Expression> expressions = ExpressionFactory.getExpressionTree(varNames, null);
                for (Expression expression : expressions) {
                    if (expression.type == Expression.Type.EQUALITY) {
                        vars.add(expression.leftExpression.name);
                        varValues.add(new Pair<>(expression.leftExpression.name, expression.rightExpression));
                    } else {
                        vars.add(expression.name);
                    }
                }
            } catch (WrongExpressionException e) {
                showException("Incorrect var values: "+e.getMessage());
                return null;
            }
            }

        try {


            ArrayList<Expression> expressions = ExpressionFactory.getExpressionTree(s, vars);
            ArrayList<Expression> optimizedExpressions = new ArrayList<>();
            ArrayList<Expression> defaultExpressions = getUserSettings(id).expressions;
            for (var def : defaultExpressions)
                vars.addAll(def.getVars());

            for (Expression expression : expressions) {
                if (expression != null) {
                    Expression newExpression = null;
                    int iteration = 0;
                    while (true) {
                        newExpression = expression.clone();
                        for (var ex : defaultExpressions) {
                            newExpression.setExpression(ex.leftExpression.name, ex.rightExpression, false);
                        }
                        if (expression.equals(newExpression) || iteration > 100) {
                            break;
                        }
                        expression = newExpression.clone();
                        iteration++;

                    }
                    expression.setExpressions(varValues);

                    Expression der = expression.getDerivative("x").getOptimized();
                    der = ExpressionFactory.optimize(der);
                    displayOutput(inputExpression, expression.toString());
                    displayOutput(inputExpressionValue, "" + expression.getVal());
                    displayOutput(derivativeExpression, der.toString());
                    displayOutput(derivativeExpressionValue, "" + der.getVal());
                    System.out.println(expression);
                    System.out.println(der);
                    System.out.println("d/dx: " + der);
                    System.out.println("d/dx(12) " + der.getVal());
                    System.out.println("f(12) " + expression.getVal());
                    System.out.println();
                    varsFromExpressions.addAll(expression.getVars());
                    if (expression.contains(Expression.Type.EQUALITY)) {
                        expressions.remove(expression);
                    }
                } else {
                    displayOutput(inputExpression, "mistake in expression");
                }
                varsFromExpressions = ((ArrayList<String>) varsFromExpressions.stream().distinct().collect(Collectors.toList()));
                optimizedExpressions.add(expression);
            }

            String varName = varsFromExpressions.contains("x") ? "x" : varsFromExpressions.contains("y") ? "y" : varsFromExpressions.size() > 0 ? varsFromExpressions.get(0) : "";
            ArrayList<Pair<Double, Double>> points = ExpressionFactory.getPoints(optimizedExpressions, varName, -5, 5, 100, 0.01);
            varsFromExpressions.removeAll(vars);
            Map<String, Object> map = new HashMap<>();
            map.put("points", points.toArray());
            map.put("expression", "bla-bla");
            map.put("vars", varsFromExpressions.toArray());
            //  map.put("varTitles", expression.getExpressions(Expression.Type.VAR).toArray());
            return new JSONObject(map);

        } catch (Exception e) {
            displayOutput(inputExpression, e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
// Mul(Pow(12.0,Sub(12.0,1.0)),12))