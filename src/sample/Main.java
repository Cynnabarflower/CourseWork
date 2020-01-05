package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sample.Expressions.Expression;
import sample.Expressions.ExpressionFactory;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
    }


    public static void main(String[] args) {
        //launch(args);
        try {
            Expression expression = ExpressionFactory.getExpressionTree("w*w");
            Expression der = expression.getDerivative('w');
            System.out.println(der);
            System.out.println(der.getVal());
            System.out.println(expression.getVal());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
