package sample;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class JavaFXBrowser extends Application {

    @Override
    public void start(Stage primaryStage) {

        primaryStage.setTitle("JavaFX WebView Example");
        WebView webView = new WebView();
        webView.getEngine().load("http://localhost:8000");
        VBox vBox = new VBox(webView);
        Scene scene = new Scene(vBox, 960, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

}
