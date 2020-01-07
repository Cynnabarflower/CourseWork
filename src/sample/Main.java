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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main extends Application {

    WebEngine webEngine = null;

    @Override
    public void start(Stage primaryStage) throws Exception{
        /*Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 300, 275));
        */
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

    public void readIt(String s) {
        ArrayList<Pair<String, Double>> varValues = new ArrayList<>();
        varValues.add(new Pair<>("x", (double)12));
        ArrayList<String> vars = new ArrayList<>();
        vars.add("x");
        vars.add("xx");
        vars.add("z");

        String in = "";
            try {

                in = s;
                ArrayList<Expression> expressions = ExpressionFactory.getExpressionTree(in, vars);
                for (Expression expression : expressions) {
                    if (expression != null) {
                        expression.setValues(varValues);
                        Expression der = expression.getDerivative("x");
                        System.out.println("d/dx: " + der);
                        System.out.println("d/dx(12) " + der.getVal());
                        System.out.println("f(12) " + expression.getVal());
                        System.out.println();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
}
// Mul(Pow(12.0,Sub(12.0,1.0)),12))