package com.educore.ui;

import com.educore.dao.*;
import com.educore.model.*;
import com.educore.util.SceneManager;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

import java.sql.Date;
import java.util.List;

public class GuardianDashboardController {

    @FXML private StackPane contentArea;
    @FXML private Label guardianNameLabel;
    @FXML private Button btnDashboard, btnProgress, btnFees, btnNotices;

    private Guardian currentGuardian;
    private Button[] allNavButtons;

    private StudentDAO studentDAO = new StudentDAO();
    private AttendanceDAO attendanceDAO = new AttendanceDAO();
    private ResultDAO resultDAO = new ResultDAO();
    private FeeDAO feeDAO = new FeeDAO();
    private NoticeDAO noticeDAO = new NoticeDAO();

    @FXML
    public void initialize() {
        allNavButtons = new Button[]{btnDashboard, btnProgress, btnFees, btnNotices};
    }

    public void setGuardian(Guardian guardian) {
        this.currentGuardian = guardian;
        guardianNameLabel.setText("Welcome, " + guardian.getName());
        showDashboard(null);
    }

    private void setActiveButton(Button active) {
        for (Button btn : allNavButtons) {
            btn.getStyleClass().remove("sidebar-btn-active");
        }
        active.getStyleClass().add("sidebar-btn-active");
    }

    @FXML
    public void showDashboard(ActionEvent event) {
        setActiveButton(btnDashboard);
        VBox dashboard = createManagementView("Guardian Dashboard");
        
        List<Student> myStudents = studentDAO.getByGuardianId(currentGuardian.getGuardianId());
        
        HBox statsRow = new HBox(20);
        statsRow.getChildren().addAll(
                createStatCard("🎓", "My Students", String.valueOf(myStudents.size()), "#f59e0b")
        );
        
        dashboard.getChildren().add(statsRow);
        setContent(dashboard);
    }

    @FXML
    public void showProgress(ActionEvent event) {
        setActiveButton(btnProgress);
        VBox view = createManagementView("Student Progress");

        List<Student> students = studentDAO.getByGuardianId(currentGuardian.getGuardianId());
        ComboBox<Student> studentBox = new ComboBox<>();
        studentBox.setItems(FXCollections.observableArrayList(students));
        studentBox.setPromptText("Select Student");
        studentBox.setStyle("-fx-background-color: #0f172a; -fx-text-fill: #f8fafc;");

        TabPane tabPane = new TabPane();
        tabPane.getStyleClass().add("custom-tab-pane");
        VBox.setVgrow(tabPane, Priority.ALWAYS);

        Tab attTab = new Tab("Attendance");
        attTab.setClosable(false);
        TableView<Attendance> attTable = new TableView<>();
        attTable.getStyleClass().add("data-table");
        TableColumn<Attendance, Date> colDate = new TableColumn<>("Date");
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        TableColumn<Attendance, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        attTable.getColumns().addAll(colDate, colStatus);
        attTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        attTab.setContent(attTable);

        Tab resTab = new Tab("Results");
        resTab.setClosable(false);
        TableView<Result> resTable = new TableView<>();
        resTable.getStyleClass().add("data-table");
        TableColumn<Result, String> colExamName = new TableColumn<>("Exam Name");
        colExamName.setCellValueFactory(data -> {
            Exam exam = new ExamDAO().getById(data.getValue().getExamId());
            return new javafx.beans.property.SimpleStringProperty(exam != null ? exam.getExamName() : "—");
        });

        TableColumn<Result, String> colSubject = new TableColumn<>("Subject");
        colSubject.setCellValueFactory(data -> {
            Exam exam = new ExamDAO().getById(data.getValue().getExamId());
            return new javafx.beans.property.SimpleStringProperty(exam != null && exam.getSubject() != null ? exam.getSubject() : "—");
        });

        TableColumn<Result, Double> colMarks = new TableColumn<>("Marks");
        colMarks.setCellValueFactory(new PropertyValueFactory<>("marksObtained"));

        TableColumn<Result, String> colGrade = new TableColumn<>("Grade");
        colGrade.setCellValueFactory(new PropertyValueFactory<>("grade"));

        resTable.getColumns().addAll(colExamName, colSubject, colMarks, colGrade);
        resTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        resTab.setContent(resTable);

        tabPane.getTabs().addAll(attTab, resTab);

        studentBox.setOnAction(e -> {
            Student s = studentBox.getValue();
            if (s != null) {
                attTable.setItems(FXCollections.observableArrayList(attendanceDAO.getByStudentId(s.getStudentId())));
                resTable.setItems(FXCollections.observableArrayList(resultDAO.getByStudentId(s.getStudentId())));
            }
        });

        view.getChildren().addAll(studentBox, tabPane);
        setContent(view);
    }

    @FXML
    public void showFees(ActionEvent event) {
        setActiveButton(btnFees);
        VBox view = createManagementView("Fee Status");

        TableView<Fee> table = new TableView<>();
        table.getStyleClass().add("data-table");
        
        TableColumn<Fee, Integer> colStudent = new TableColumn<>("Student ID");
        colStudent.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        
        TableColumn<Fee, Double> colAmount = new TableColumn<>("Amount");
        colAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        
        TableColumn<Fee, Date> colDue = new TableColumn<>("Due Date");
        colDue.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        
        TableColumn<Fee, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        table.getColumns().addAll(colStudent, colAmount, colDue, colStatus);
        
        List<Student> students = studentDAO.getByGuardianId(currentGuardian.getGuardianId());
        List<Fee> allFees = FXCollections.observableArrayList();
        for (Student s : students) {
            allFees.addAll(feeDAO.getByStudentId(s.getStudentId()));
        }
        
        table.setItems(FXCollections.observableArrayList(allFees));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(table, Priority.ALWAYS);
        
        view.getChildren().add(table);
        setContent(view);
    }

    @FXML
    public void showNotices(ActionEvent event) {
        setActiveButton(btnNotices);
        VBox view = createManagementView("Notices");

        TableView<Notice> table = new TableView<>();
        table.getStyleClass().add("data-table");
        
        TableColumn<Notice, String> colTitle = new TableColumn<>("Title");
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        
        TableColumn<Notice, String> colContent = new TableColumn<>("Content");
        colContent.setCellValueFactory(new PropertyValueFactory<>("content"));
        
        TableColumn<Notice, Date> colDate = new TableColumn<>("Date Posted");
        colDate.setCellValueFactory(new PropertyValueFactory<>("datePosted"));
        
        table.getColumns().addAll(colTitle, colContent, colDate);
        
        List<Notice> notices = noticeDAO.getByTargetRole("Guardian");
        
        table.setItems(FXCollections.observableArrayList(notices));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(table, Priority.ALWAYS);
        
        view.getChildren().add(table);
        setContent(view);
    }

    @FXML
    public void handleLogout(ActionEvent event) {
        SceneManager.logout();
    }

    private void setContent(javafx.scene.Node node) {
        contentArea.getChildren().clear();
        contentArea.getChildren().add(node);
    }

    private VBox createManagementView(String titleText) {
        VBox view = new VBox(15);
        view.setAlignment(Pos.TOP_LEFT);
        Label title = new Label(titleText);
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #f8fafc;");
        view.getChildren().add(title);
        return view;
    }
    
    private VBox createStatCard(String icon, String label, String value, String color) {
        VBox card = new VBox(8);
        card.setAlignment(Pos.CENTER);
        card.setPrefWidth(200);
        card.setPrefHeight(130);
        card.setStyle("-fx-background-color: rgba(30,41,59,0.7); -fx-background-radius: 16; -fx-padding: 20; -fx-border-color: " + color + "33; -fx-border-radius: 16; -fx-border-width: 1;");
        
        Label iconLbl = new Label(icon);
        iconLbl.setStyle("-fx-font-size: 28px;");
        Label valueLbl = new Label(value);
        valueLbl.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");
        Label labelLbl = new Label(label);
        labelLbl.setStyle("-fx-font-size: 13px; -fx-text-fill: #94a3b8;");
        
        card.getChildren().addAll(iconLbl, valueLbl, labelLbl);
        return card;
    }

    private void showAlert(String title, String message, String... styleClasses) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
        dialogPane.getStyleClass().add("custom-alert");
        if (styleClasses.length > 0) {
            dialogPane.getStyleClass().addAll(styleClasses);
        } else if (title.toLowerCase().contains("error")) {
            dialogPane.getStyleClass().add("error-alert");
        }
        
        alert.showAndWait();
    }
}
