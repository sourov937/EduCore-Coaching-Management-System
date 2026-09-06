package com.educore.ui;

import com.educore.util.SceneManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            System.out.println("Starting JavaFX Application...");

            SceneManager.setPrimaryStage(primaryStage);

            java.net.URL fxmlUrl = getClass().getResource("/fxml/Login.fxml");
            if (fxmlUrl == null) {
                System.err.println("CRITICAL ERROR: Could not find /fxml/Login.fxml in resources!");
                return;
            }
            Parent root = FXMLLoader.load(fxmlUrl);
            Scene scene = new Scene(root);

            java.net.URL cssUrl = getClass().getResource("/css/style.css");
            if (cssUrl == null) {
                System.err.println("WARNING: Could not find /css/style.css in resources!");
            } else {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            }

            primaryStage.setTitle("EduCore - Coaching Management System");
            primaryStage.setScene(scene);
            primaryStage.setWidth(600);
            primaryStage.setHeight(700);
            primaryStage.setResizable(false);
            primaryStage.centerOnScreen();
            primaryStage.show();
            System.out.println("JavaFX Window should now be visible!");
        } catch (Exception e) {
            System.err.println("CRITICAL ERROR during JavaFX startup:");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
