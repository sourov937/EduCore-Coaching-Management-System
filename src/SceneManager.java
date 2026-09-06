package com.educore.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Utility class for switching between scenes in the application.
 */
public class SceneManager {

    private static Stage primaryStage;

    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * Switches the current scene to the specified FXML file.
     * @param fxmlPath The path to the FXML file (e.g., "/fxml/DirectorDashboard.fxml")
     * @param title The title for the window
     */
    public static void switchScene(String fxmlPath, String title) {
        try {
            java.net.URL fxmlUrl = SceneManager.class.getResource(fxmlPath);
            if (fxmlUrl == null) {
                System.err.println("ERROR: Could not find FXML file: " + fxmlPath);
                return;
            }
            Parent root = FXMLLoader.load(fxmlUrl);
            Scene scene = new Scene(root);

            java.net.URL cssUrl = SceneManager.class.getResource("/css/style.css");
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            }

            primaryStage.setTitle("EduCore - " + title);
            primaryStage.setScene(scene);
            primaryStage.setResizable(true);
            primaryStage.setMaximized(true);
            primaryStage.show();
        } catch (IOException e) {
            System.err.println("ERROR: Failed to load scene: " + fxmlPath);
            e.printStackTrace();
        }
    }

    /**
     * Switches scene and returns the controller for the loaded FXML.
     */
    public static <T> T switchSceneAndGetController(String fxmlPath, String title) {
        try {
            java.net.URL fxmlUrl = SceneManager.class.getResource(fxmlPath);
            if (fxmlUrl == null) {
                System.err.println("ERROR: Could not find FXML file: " + fxmlPath);
                return null;
            }
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();
            Scene scene = new Scene(root);

            java.net.URL cssUrl = SceneManager.class.getResource("/css/style.css");
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            }

            primaryStage.setTitle("EduCore - " + title);
            primaryStage.setScene(scene);
            primaryStage.setResizable(true);
            primaryStage.setMaximized(true);
            primaryStage.show();
            return loader.getController();
        } catch (IOException e) {
            System.err.println("ERROR: Failed to load scene: " + fxmlPath);
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Switches back to the login screen.
     */
    public static void logout() {
        try {
            java.net.URL fxmlUrl = SceneManager.class.getResource("/fxml/Login.fxml");
            if (fxmlUrl == null) return;
            Parent root = FXMLLoader.load(fxmlUrl);
            Scene scene = new Scene(root);

            java.net.URL cssUrl = SceneManager.class.getResource("/css/style.css");
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            }

            primaryStage.setTitle("EduCore - Coaching Management System");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.setMaximized(false);
            primaryStage.setWidth(600);
            primaryStage.setHeight(700);
            primaryStage.centerOnScreen();
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
