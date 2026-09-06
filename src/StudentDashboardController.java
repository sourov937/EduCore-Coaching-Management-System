package com.educore.ui;

import com.educore.dao.*;
import com.educore.model.*;
import com.educore.util.SceneManager;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

public class StudentDashboardController {

    @FXML private StackPane contentArea;
    @FXML private Label studentNameLabel;
    @FXML private Button btnDashboard, btnAttendance, btnAssignments, btnResults, btnFees, btnNotices, btnReview;

    private Student currentStudent;
    private Button[] allNavButtons;

    private AttendanceDAO attendanceDAO = new AttendanceDAO();
    private AssignmentDAO assignmentDAO = new AssignmentDAO();
    private ResultDAO resultDAO = new ResultDAO();
    private FeeDAO feeDAO = new FeeDAO();
    private NoticeDAO noticeDAO = new NoticeDAO();
    private TeacherDAO teacherDAO = new TeacherDAO();
    private TeacherReviewDAO reviewDAO = new TeacherReviewDAO();
    private BatchDAO batchDAO = new BatchDAO();

    @FXML
    public void initialize() {
        allNavButtons = new Button[]{btnDashboard, btnAttendance, btnAssignments, btnResults, btnFees, btnNotices, btnReview};
    }

    public void setStudent(Student student) {
        this.currentStudent = student;
        studentNameLabel.setText("Welcome, " + student.getName());
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
        VBox dashboard = createManagementView("Student Dashboard");
        
        List<Fee> myFees = feeDAO.getByStudentId(currentStudent.getStudentId());
        double due = 0;
        for (Fee f : myFees) {
            if ("Unpaid".equals(f.getStatus()) || "Partial".equals(f.getStatus())) {
                due += f.getAmount();
            }
        }
        
        HBox statsRow = new HBox(20);
        statsRow.getChildren().addAll(
                createStatCard("💰", "Fees Due", String.format("৳%.0f", due), "#ef4444")
        );
        
        dashboard.getChildren().add(statsRow);
        setContent(dashboard);
    }

    @FXML
    public void showAttendance(ActionEvent event) {
        setActiveButton(btnAttendance);
        VBox view = createManagementView("My Attendance");

        TableView<Attendance> table = new TableView<>();
        table.getStyleClass().add("data-table");
        
        TableColumn<Attendance, Date> colDate = new TableColumn<>("Date");
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        
        TableColumn<Attendance, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        table.getColumns().addAll(colDate, colStatus);
        table.setItems(FXCollections.observableArrayList(attendanceDAO.getByStudentId(currentStudent.getStudentId())));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        VBox.setVgrow(table, Priority.ALWAYS);
        view.getChildren().add(table);
        setContent(view);
    }

    @FXML
    public void showAssignments(ActionEvent event) {
        setActiveButton(btnAssignments);
        VBox view = createManagementView("My Assignments");

        TableView<Assignment> table = new TableView<>();
        table.getStyleClass().add("data-table");
        
        TableColumn<Assignment, String> colTitle = new TableColumn<>("Title");
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        
        TableColumn<Assignment, String> colDesc = new TableColumn<>("Description");
        colDesc.setCellValueFactory(new PropertyValueFactory<>("description"));
        
        TableColumn<Assignment, Date> colDue = new TableColumn<>("Due Date");
        colDue.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        
        table.getColumns().addAll(colTitle, colDesc, colDue);
        
        if (currentStudent.getBatchId() != null) {
            table.setItems(FXCollections.observableArrayList(assignmentDAO.getByBatchId(currentStudent.getBatchId())));
        }
        
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(table, Priority.ALWAYS);
        
        view.getChildren().add(table);
        setContent(view);
    }
    
    @FXML
    public void showResults(ActionEvent event) {
        setActiveButton(btnResults);
        VBox view = createManagementView("My Results");

        TableView<Result> table = new TableView<>();
        table.getStyleClass().add("data-table");

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

        TableColumn<Result, Double> colMarks = new TableColumn<>("Marks Obtained");
        colMarks.setCellValueFactory(new PropertyValueFactory<>("marksObtained"));

        TableColumn<Result, String> colGrade = new TableColumn<>("Grade");
        colGrade.setCellValueFactory(new PropertyValueFactory<>("grade"));

        table.getColumns().addAll(colExamName, colSubject, colMarks, colGrade);
        table.setItems(FXCollections.observableArrayList(resultDAO.getByStudentId(currentStudent.getStudentId())));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        VBox.setVgrow(table, Priority.ALWAYS);
        view.getChildren().add(table);
        setContent(view);
    }

    @FXML
    public void showFees(ActionEvent event) {
        setActiveButton(btnFees);
        VBox view = createManagementView("My Fee Status");

        TableView<Fee> table = new TableView<>();
        table.getStyleClass().add("data-table");
        
        TableColumn<Fee, Double> colAmount = new TableColumn<>("Amount");
        colAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        
        TableColumn<Fee, Date> colDue = new TableColumn<>("Due Date");
        colDue.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        
        TableColumn<Fee, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        table.getColumns().addAll(colAmount, colDue, colStatus);
        table.setItems(FXCollections.observableArrayList(feeDAO.getByStudentId(currentStudent.getStudentId())));
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
        
        List<Notice> notices = noticeDAO.getByTargetRole("Student");
        // getByTargetRole already fetches 'All' notices
        
        table.setItems(FXCollections.observableArrayList(notices));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        VBox.setVgrow(table, Priority.ALWAYS);
        view.getChildren().add(table);
        setContent(view);
    }

    @FXML
    public void showReviews(ActionEvent event) {
        setActiveButton(btnReview);
        VBox view = createManagementView("Review Teachers");

        HBox form = new HBox(15);
        form.setAlignment(Pos.CENTER_LEFT);
        
        ComboBox<Teacher> teacherBox = new ComboBox<>();
        // Ideally only fetch teachers teaching this student's batch. For simplicity, fetching all.
        teacherBox.setItems(FXCollections.observableArrayList(teacherDAO.getAll()));
        teacherBox.setPromptText("Select Teacher");
        
        ComboBox<Integer> ratingBox = new ComboBox<>();
        ratingBox.getItems().addAll(1, 2, 3, 4, 5);
        ratingBox.setPromptText("Rating");
        
        TextField commentField = new TextField();
        commentField.setPromptText("Write a comment...");
        commentField.setPrefWidth(300);
        
        Button submitBtn = new Button("Submit Review");
        submitBtn.getStyleClass().addAll("action-btn", "add-btn");
        
        form.getChildren().addAll(teacherBox, ratingBox, commentField, submitBtn);
        
        TableView<TeacherReview> table = new TableView<>();
        table.getStyleClass().add("data-table");
        
        TableColumn<TeacherReview, Integer> colTid = new TableColumn<>("Teacher ID");
        colTid.setCellValueFactory(new PropertyValueFactory<>("teacherId"));
        
        TableColumn<TeacherReview, Integer> colRat = new TableColumn<>("Rating");
        colRat.setCellValueFactory(new PropertyValueFactory<>("rating"));
        
        TableColumn<TeacherReview, String> colCom = new TableColumn<>("Comment");
        colCom.setCellValueFactory(new PropertyValueFactory<>("comment"));
        
        table.getColumns().addAll(colTid, colRat, colCom);
        table.setItems(FXCollections.observableArrayList(reviewDAO.getByStudentId(currentStudent.getStudentId())));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        submitBtn.setOnAction(e -> {
            if (teacherBox.getValue() != null && ratingBox.getValue() != null) {
                TeacherReview tr = new TeacherReview();
                tr.setStudentId(currentStudent.getStudentId());
                tr.setTeacherId(teacherBox.getValue().getTeacherId());
                tr.setRating(ratingBox.getValue());
                tr.setComment(commentField.getText());
                tr.setDateSubmitted(Date.valueOf(LocalDate.now()));
                
                if (reviewDAO.insert(tr)) {
                    showAlert("Success", "Review submitted!");
                    table.setItems(FXCollections.observableArrayList(reviewDAO.getByStudentId(currentStudent.getStudentId())));
                    commentField.clear();
                }
            } else {
                showAlert("Error", "Please select a teacher and a rating.");
            }
        });
        
        VBox.setVgrow(table, Priority.ALWAYS);
        view.getChildren().addAll(form, table);
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
