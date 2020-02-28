package sample;

import com.alibaba.fastjson.JSONObject;
import javafx.application.Application;
import javafx.stage.Stage;
import sample.Expressions.Expression;
import sample.Expressions.ExpressionFactory;
import sample.Expressions.Val;
import sample.Expressions.Var;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class Main  {

   // static WebEngine webEngine = null;
    public static String inputExpression = "inputExpression";
    public static String inputExpressionValue = "inputExpressionValue";
    public static String derivativeExpression = "derivativeExpression";
    public static String derivativeExpressionValue = "derivativeExpressionValue";
    private static Map<String, UserSettings> userSettings;
    public static ArrayList<WrongExpressionException> warnings = new ArrayList<>();

 /*   @Override
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
    }*/

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
        new Server(index, 8000);

        Application.launch(JavaFXBrowser.class);

        if (false) {
            try {
                Desktop.getDesktop().browse(new URL("http://localhost:8000").toURI());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }

    }

    public static UserSettings getDefaultSettings() {

        String stringExp = "";
        try {
            //textExpressions = new String(Main.class.getResource("/defaultExpressions.txt").openStream().readAllBytes()).split("[;\n\r]+");
            stringExp = new String(Main.class.getResource("/defaultExpressions.txt").openStream().readAllBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }


        return new UserSettings(stringExp);

    }


    public static void setUserSettings(String id, String text, double fromX, double toX, int optimizationLevel, boolean extraOptimization) {
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
        UserSettings tempUserSettings = new UserSettings();
        tempUserSettings.setDefaultExpressions(text);
        tempUserSettings.setX(fromX, toX);
        tempUserSettings.optimizationLevel = optimizationLevel;
        tempUserSettings.extraOptimization = extraOptimization;

        userSettings.put(id, tempUserSettings);
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
        System.out.println(s);
    }


    public static void showException(String s) {

        System.err.println(s);
        displayOutput(inputExpression, s);
    }

    public static JSONObject readIt(String id, String s, String varNames) {

        ArrayList<Expression> vars = new ArrayList<>();
        ArrayList<Pair<String, Expression>> varValues = new ArrayList<>();
        ArrayList<String> varsFromExpressions = new ArrayList<>();

        UserSettings currentSettings = getUserSettings(id);
        vars.addAll(currentSettings.expressions);
       // vars.addAll(currentSettings.getExpressionsMap().keySet());
        if (!varNames.isEmpty()) {

                ArrayList<Expression> expressions = ExpressionFactory.getExpressionTree(varNames, vars, currentSettings);
                for (Expression expression : expressions) {
                    if (expression.type == Expression.Type.EQUALITY) {
                        try {
                            vars.add(expression.getChild(0).clone().addChild(expression.getChild(1).clone()));
                        } catch (CloneNotSupportedException e) {};
                        varValues.add(new Pair<>(expression.getChild(0).name, expression.getChild(1)));
                    } else {
                        vars.add(expression);
                    }
                }
            }
       // vars = ((ArrayList<String>) vars.stream().distinct().collect(Collectors.toList()));

        for (var i = vars.size()-1; i > 0; i--)
            for (var j = 0; j < i; j++)
                if (vars.get(i).name.equals(vars.get(j).name)) {
                    vars.remove(j);
                    j--;
                    i--;
                }

        try {
            ArrayList<Expression> expressions = ExpressionFactory.getExpressionTree(s, vars, currentSettings);
            for (var i = 0; i < expressions.size(); i++) {
                expressions.set(i, ExpressionFactory.optimize(ExpressionFactory.open(expressions.get(i)), currentSettings));
            }
            ArrayList<Expression> optimizedExpressions = new ArrayList<>();
            ArrayList<Expression> defaultExpressions = currentSettings.expressions;
            //vars.addAll(defaultExpressions);

            for (Expression expression : expressions) {
                if (expression != null) {

                  /*  Expression newExpression = null;
                    int iteration = 0;
                    while (true) {
                        newExpression = expression.clone();
                        for (var ex : defaultExpressions) {
                            newExpression.setExpression(ex.getChild(0).name, ex.getChild(1), true);
                        }
                        if (expression.equals(newExpression) || iteration > 100) {
                            break;
                        }
                        expression = newExpression.clone();
                        iteration++;
                    }*/
                   // expression.setExpressions(varValues);

                    //Expression der = expression.getDerivative("x").getOptimized(level);
                   // der = ExpressionFactory.optimize(der);
                    displayOutput(inputExpression, expression.toString());
                    var args = new ArrayList<Expression>();
                    args.add(new Var("x", 0.785));
                    displayOutput(inputExpressionValue, "" + expression.getVal(args));
                   // displayOutput(derivativeExpression, der.toString());
                   // displayOutput(derivativeExpressionValue, "" + der.getVal());
                    System.out.println(expression);
                   // System.out.println(der);
                  //  System.out.println("d/dx: " + der);
                   // System.out.println("d/dx(12) " + der.getVal());
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
            StringBuilder expressionString = new StringBuilder();
            for (Expression expression : optimizedExpressions) {
                expressionString.append(expression.toString());
                expressionString.append("; ");
            }

            String varName = "x";// varsFromExpressions.contains("x") ? "x" : varsFromExpressions.contains("y") ? "y" : varsFromExpressions.size() > 0 ? varsFromExpressions.get(0) : "";
            ArrayList<Pair<Double, Double>> points = ExpressionFactory.getPoints(optimizedExpressions, varName, currentSettings.fromX, currentSettings.toX, 100, 0.01);
            varsFromExpressions.removeAll(vars);
            if (!warnings.isEmpty()) {
                expressionString.append('\n');
                warnings.forEach((w) -> {
                    expressionString.append(w.toString("RU"));
                });
                warnings.clear();
            }
            Map<String, Object> map = new HashMap<>();
            map.put("points", points.toArray());
            map.put("message", expressionString.toString());
            map.put("vars", varsFromExpressions.toArray());
            //  map.put("varTitles", expression.getExpressions(Expression.Type.VAR).toArray());
            return new JSONObject(map);

        } catch (Exception e) {
            e.printStackTrace();
            var sb = new StringBuilder();
            warnings.forEach((w) -> { sb.append( w.toString("RU")); });
            warnings.clear();
            Map<String, Object> map = new HashMap<>();
            map.put("points", new int[]{});
            map.put("message", sb.toString());
            map.put("vars", varsFromExpressions.toArray());
            //  map.put("varTitles", expression.getExpressions(Expression.Type.VAR).toArray());
            return new JSONObject(map);
           // displayOutput(inputExpression, e.getMessage());

           // return null;
        }

    }
}
// Mul(Pow(12.0,Sub(12.0,1.0)),12))