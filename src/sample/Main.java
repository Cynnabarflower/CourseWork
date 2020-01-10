package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.util.Pair;
import netscape.javascript.JSObject;
import sample.Expressions.Expression;
import sample.Expressions.ExpressionFactory;
import sample.Expressions.Sum;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main extends Application {

    WebEngine webEngine = null;
    public String inputExpression = "inputExpression";
    public String inputExpressionValue = "inputExpressionValue";
    public String derivativeExpression = "derivativeExpression";
    public String derivativeExpressionValue = "derivativeExpressionValue";

    @Override
    public void start(Stage primaryStage) throws Exception{

        WebView browser = new WebView();

        webEngine = browser.getEngine();
        webEngine.load(getClass().getResource("/index.html").toExternalForm());
        JSObject windowObject = (JSObject) browser.getEngine().executeScript("window");
        windowObject.setMember("app", this);
        VBox root = new VBox();
        root.setPadding(new Insets(5));
        root.setSpacing(5);
        root.getChildren().addAll(browser);

        Scene scene = new Scene(root);

        primaryStage.setTitle("JavaFX WebView (o7planning.org)");
        primaryStage.setScene(scene);
        primaryStage.setWidth(450);
        primaryStage.setHeight(600);

        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
/*        File file = new File("functions.cfg");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/


    }

    public void displayOutput(String id, String s) {
        JSObject windowObject = (JSObject)webEngine.executeScript("window");
        windowObject.setMember("resultString", s);
        windowObject.setMember("id", id);
        webEngine.executeScript("setText(id, resultString)");
    }

    public void readIt(String s) {
        ArrayList<Pair<String, Double>> varValues = new ArrayList<>();
        varValues.add(new Pair<>("x", (double) 3));
        ArrayList<String> vars = new ArrayList<>();
        vars.add("x");
        vars.add("z");
        ArrayList<Double> xs = new ArrayList<>();
        ArrayList<Double> ys = new ArrayList<>();
        xs.add(1.);
/*        xs.add(2.);
        xs.add(3.);*/

/*        ys.add(1.);
        ys.add(2.);*/
        ys.add(3.);
        String in = "";
        try {

            in = s;
            ArrayList<Expression> expressions = ExpressionFactory.getExpressionTree(in, vars);
            for (Expression expression : expressions) {
                if (expression != null) {
                    expression.setValues(varValues);
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
                } else {
                    displayOutput(inputExpression, "mistake in expression");
                }
            }
        } catch (Exception e) {
            displayOutput(inputExpression, e.getMessage());
            e.printStackTrace();
        }
    }
}
// Mul(Pow(12.0,Sub(12.0,1.0)),12))