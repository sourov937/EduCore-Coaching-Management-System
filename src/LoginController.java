package com.educore.ui;

import com.educore.dao.GuardianDAO;
import com.educore.dao.StudentDAO;
import com.educore.dao.TeacherDAO;
import com.educore.model.Guardian;
import com.educore.model.Student;
import com.educore.model.Teacher;
import com.educore.util.SceneManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class LoginController {

    @FXML
    private ComboBox<String> roleComboBox;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label messageLabel;

    @FXML
    private Button loginButton;
    
    @FXML
    private VBox loginBox;

    @FXML
    private Label dbStatusLabel;

    private TeacherDAO teacherDAO;
    private StudentDAO studentDAO;
    private GuardianDAO guardianDAO;

    @FXML
    public void initialize() {
        teacherDAO = new TeacherDAO();
        studentDAO = new StudentDAO();
        guardianDAO = new GuardianDAO();

        // Initialize roles
        roleComboBox.getItems().addAll("Director", "Teacher", "Student", "Guardian");
        roleComboBox.setValue("Director");
        
        // Add a listener to change prompt text based on role
        roleComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.equals("Director")) {
                emailField.setPromptText("admin");
            } else {
                emailField.setPromptText(newVal.toLowerCase() + "@educore.com");
            }
        });

        // Check Database Connection Async
        new Thread(() -> {
            boolean isConnected = false;
            try {
                java.sql.Connection conn = com.educore.util.DatabaseConnection.getInstance().getConnection();
                if (conn != null && !conn.isClosed()) {
                    isConnected = true;
                }
            } catch (Exception e) {
                isConnected = false;
            }
            final boolean success = isConnected;
            javafx.application.Platform.runLater(() -> {
                if (success) {
                    dbStatusLabel.setText("● Database: Connected");
                    dbStatusLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #22c55e; -fx-font-weight: bold;");
                } else {
                    dbStatusLabel.setText("● Database: Disconnected (Check MySQL & credentials)");
                    dbStatusLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #ef4444; -fx-font-weight: bold;");
                }
            });
        }).start();
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        String role = roleComboBox.getValue();
        String email = emailField.getText();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showMessage("Please enter email and password.", "error-message");
            return;
        }
        
        loginButton.setDisable(true);
        loginButton.setText("Authenticating...");

        switch (role) {
            case "Director":
                if ("admin".equals(email) && "admin123".equals(password)) {
                    SceneManager.switchScene("/fxml/DirectorDashboard.fxml", "Director Dashboard");
                } else {
                    showMessage("Invalid credentials. Please try again.", "error-message");
                }
                break;

            case "Teacher":
                Teacher teacher = teacherDAO.authenticate(email, password);
                if (teacher != null) {
                    TeacherDashboardController ctrl = SceneManager.switchSceneAndGetController(
                            "/fxml/TeacherDashboard.fxml", "Teacher Dashboard");
                    if (ctrl != null) {
                        ctrl.setTeacher(teacher);
                    }
                } else {
                    showMessage("Invalid credentials. Please try again.", "error-message");
                }
                break;

            case "Student":
                Student student = studentDAO.authenticate(email, password);
                if (student != null) {
                    StudentDashboardController ctrl2 = SceneManager.switchSceneAndGetController(
                            "/fxml/StudentDashboard.fxml", "Student Dashboard");
                    if (ctrl2 != null) {
                        ctrl2.setStudent(student);
                    }
                } else {
                    showMessage("Invalid credentials. Please try again.", "error-message");
                }
                break;

            case "Guardian":
                Guardian guardian = guardianDAO.authenticate(email, password);
                if (guardian != null) {
                    GuardianDashboardController ctrl3 = SceneManager.switchSceneAndGetController(
                            "/fxml/GuardianDashboard.fxml", "Guardian Dashboard");
                    if (ctrl3 != null) {
                        ctrl3.setGuardian(guardian);
                    }
                } else {
                    showMessage("Invalid credentials. Please try again.", "error-message");
                }
                break;
        }
        
        loginButton.setDisable(false);
        loginButton.setText("Sign In");
    }
    
    private void showMessage(String message, String styleClass) {
        messageLabel.setText(message);
        messageLabel.getStyleClass().removeAll("success-message", "error-message");
        messageLabel.getStyleClass().add(styleClass);
        messageLabel.setVisible(true);
    }
}
