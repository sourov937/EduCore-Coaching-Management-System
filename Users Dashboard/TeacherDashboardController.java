package com.educore.ui;

import com.educore.dao.*;
import com.educore.model.*;
import com.educore.util.SceneManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import java.util.Optional;

public class TeacherDashboardController {

    @FXML private StackPane contentArea;
    @FXML private Label teacherNameLabel;
    @FXML private Button btnDashboard, btnBatches, btnStudents, btnAttendance, btnAssignments, btnExams, btnResults;

    private Teacher currentTeacher;
    private Button[] allNavButtons;

    private BatchDAO batchDAO = new BatchDAO();
    private StudentDAO studentDAO = new StudentDAO();
    private AttendanceDAO attendanceDAO = new AttendanceDAO();
    private AssignmentDAO assignmentDAO = new AssignmentDAO();
    private ExamDAO examDAO = new ExamDAO();
    private ResultDAO resultDAO = new ResultDAO();

    @FXML
    public void initialize() {
        allNavButtons = new Button[]{btnDashboard, btnBatches, btnStudents, btnAttendance, btnAssignments, btnExams, btnResults};
    }

    public void setTeacher(Teacher teacher) {
        this.currentTeacher = teacher;
        teacherNameLabel.setText("Welcome, " + teacher.getName());
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
        VBox dashboard = createManagementView("Teacher Dashboard");
        
        int batchCount = batchDAO.getByTeacherId(currentTeacher.getTeacherId()).size();
        
        HBox statsRow = new HBox(20);
        statsRow.getChildren().addAll(
                createStatCard("📚", "Assigned Batches", String.valueOf(batchCount), "#3b82f6")
        );
        
        dashboard.getChildren().add(statsRow);
        setContent(dashboard);
    }

    @FXML
    public void showBatches(ActionEvent event) {
        setActiveButton(btnBatches);
        VBox view = createManagementView("My Batches");

        TableView<Batch> table = new TableView<>();
        table.getStyleClass().add("data-table");
        
        TableColumn<Batch, String> colName = new TableColumn<>("Batch Name");
        colName.setCellValueFactory(new PropertyValueFactory<>("batchName"));
        
        TableColumn<Batch, String> colSub = new TableColumn<>("Subject");
        colSub.setCellValueFactory(new PropertyValueFactory<>("subject"));
        
        TableColumn<Batch, String> colSch = new TableColumn<>("Schedule");
        colSch.setCellValueFactory(new PropertyValueFactory<>("schedule"));
        
        table.getColumns().addAll(colName, colSub, colSch);
        table.setItems(FXCollections.observableArrayList(batchDAO.getByTeacherId(currentTeacher.getTeacherId())));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        VBox.setVgrow(table, Priority.ALWAYS);
        view.getChildren().add(table);
        setContent(view);
    }

    @FXML
    public void showStudents(ActionEvent event) {
        setActiveButton(btnStudents);
        VBox view = createManagementView("View Students by Batch");

        ComboBox<Batch> batchBox = new ComboBox<>();
        batchBox.setItems(FXCollections.observableArrayList(batchDAO.getByTeacherId(currentTeacher.getTeacherId())));
        batchBox.setPromptText("Select a Batch");
        batchBox.setStyle("-fx-background-color: #0f172a; -fx-text-fill: #f8fafc;");

        TableView<Student> table = new TableView<>();
        table.getStyleClass().add("data-table");
        
        TableColumn<Student, String> colName = new TableColumn<>("Name");
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        
        TableColumn<Student, String> colEmail = new TableColumn<>("Email");
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        
        TableColumn<Student, String> colPhone = new TableColumn<>("Phone");
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        
        table.getColumns().addAll(colName, colEmail, colPhone);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(table, Priority.ALWAYS);
        
        batchBox.setOnAction(e -> {
            if (batchBox.getValue() != null) {
                table.setItems(FXCollections.observableArrayList(studentDAO.getByBatchId(batchBox.getValue().getBatchId())));
            }
        });

        view.getChildren().addAll(batchBox, table);
        setContent(view);
    }

    @FXML
    public void showAttendance(ActionEvent event) {
        setActiveButton(btnAttendance);
        VBox view = createManagementView("Record Attendance");

        HBox topBar = new HBox(15);
        topBar.setAlignment(Pos.CENTER_LEFT);
        
        ComboBox<Batch> batchBox = new ComboBox<>();
        batchBox.setItems(FXCollections.observableArrayList(batchDAO.getByTeacherId(currentTeacher.getTeacherId())));
        batchBox.setPromptText("Select a Batch");
        
        DatePicker datePicker = new DatePicker(LocalDate.now());
        
        Button loadBtn = new Button("Load Students");
        loadBtn.getStyleClass().add("action-btn");
        
        topBar.getChildren().addAll(new Label("Batch:"), batchBox, new Label("Date:"), datePicker, loadBtn);
        
        TableView<StudentAttendanceWrapper> table = new TableView<>();
        table.getStyleClass().add("data-table");
        
        TableColumn<StudentAttendanceWrapper, String> colName = new TableColumn<>("Student Name");
        colName.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        colName.setPrefWidth(200);
        
        TableColumn<StudentAttendanceWrapper, ComboBox<String>> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("statusCombo"));
        colStatus.setCellFactory(col -> new TableCell<StudentAttendanceWrapper, ComboBox<String>>() {
            @Override
            protected void updateItem(ComboBox<String> item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    setGraphic(item);
                }
            }
        });
        colStatus.setPrefWidth(150);
        
        table.getColumns().addAll(colName, colStatus);
        VBox.setVgrow(table, Priority.ALWAYS);
        
        Button saveBtn = new Button("💾 Save Attendance");
        saveBtn.getStyleClass().addAll("action-btn", "add-btn");
        saveBtn.setDisable(true);
        
        loadBtn.setOnAction(e -> {
            if (batchBox.getValue() == null) {
                showAlert("Select Batch", "Please select a batch first.");
                return;
            }
            List<Student> students = studentDAO.getByBatchId(batchBox.getValue().getBatchId());
            ObservableList<StudentAttendanceWrapper> data = FXCollections.observableArrayList();
            for (Student s : students) {
                data.add(new StudentAttendanceWrapper(s, datePicker.getValue()));
            }
            table.setItems(data);
            saveBtn.setDisable(data.isEmpty());
        });
        
        saveBtn.setOnAction(e -> {
            int batchId = batchBox.getValue().getBatchId();
            Date date = Date.valueOf(datePicker.getValue());
            for (StudentAttendanceWrapper wrapper : table.getItems()) {
                Attendance att = new Attendance();
                att.setStudentId(wrapper.getStudentId());
                att.setBatchId(batchId);
                att.setDate(date);
                att.setStatus(wrapper.getStatusCombo().getValue());
                attendanceDAO.insert(att);
            }
            showAlert("Success", "Attendance saved successfully!");
            table.getItems().clear();
            saveBtn.setDisable(true);
        });

        view.getChildren().addAll(topBar, table, saveBtn);
        setContent(view);
    }
    
    public class StudentAttendanceWrapper {
        private Student student;
        private ComboBox<String> statusCombo;
        public StudentAttendanceWrapper(Student s, LocalDate d) {
            this.student = s;
            this.statusCombo = new ComboBox<>();
            this.statusCombo.getItems().addAll("Present", "Absent", "Late");
            this.statusCombo.setValue("Present");
            this.statusCombo.setStyle("-fx-background-color: #0f172a; -fx-text-fill: white;");
        }
        public String getStudentName() { return student.getName(); }
        public int getStudentId() { return student.getStudentId(); }
        public ComboBox<String> getStatusCombo() { return statusCombo; }
    }

    @FXML
    public void showAssignments(ActionEvent event) {
        setActiveButton(btnAssignments);
        VBox view = createManagementView("Manage Assignments");

        TableView<Assignment> table = new TableView<>();
        table.getStyleClass().add("data-table");
        
        TableColumn<Assignment, String> colTitle = new TableColumn<>("Title");
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        
        TableColumn<Assignment, Date> colDue = new TableColumn<>("Due Date");
        colDue.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        
        TableColumn<Assignment, Integer> colBatch = new TableColumn<>("Batch ID");
        colBatch.setCellValueFactory(new PropertyValueFactory<>("batchId"));
        
        table.getColumns().addAll(colTitle, colDue, colBatch);
        table.setItems(FXCollections.observableArrayList(assignmentDAO.getByTeacherId(currentTeacher.getTeacherId())));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(table, Priority.ALWAYS);
        
        Button addBtn = new Button("➕ Add Assignment");
        addBtn.getStyleClass().addAll("action-btn", "add-btn");
        addBtn.setOnAction(e -> {
            Dialog<Assignment> dialog = new Dialog<>();
            dialog.setTitle("Add Assignment");
            dialog.getDialogPane().getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
            dialog.getDialogPane().setStyle("-fx-background-color: #1e293b;");

            ButtonType saveBtn = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

            GridPane grid = new GridPane();
            grid.setHgap(10); grid.setVgap(10); grid.setPadding(new Insets(20));
            
            TextField titleF = new TextField();
            DatePicker dueP = new DatePicker(LocalDate.now());
            ComboBox<Batch> batchB = new ComboBox<>();
            batchB.setItems(FXCollections.observableArrayList(batchDAO.getByTeacherId(currentTeacher.getTeacherId())));
            TextArea desc = new TextArea();
            desc.setPrefRowCount(3);
            
            grid.add(new Label("Title:"), 0, 0); grid.add(titleF, 1, 0);
            grid.add(new Label("Due Date:"), 0, 1); grid.add(dueP, 1, 1);
            grid.add(new Label("Batch:"), 0, 2); grid.add(batchB, 1, 2);
            grid.add(new Label("Description:"), 0, 3); grid.add(desc, 1, 3);
            
            dialog.getDialogPane().setContent(grid);
            dialog.setResultConverter(btn -> {
                if (btn == saveBtn && batchB.getValue() != null) {
                    Assignment a = new Assignment();
                    a.setTitle(titleF.getText());
                    a.setDescription(desc.getText());
                    a.setDueDate(Date.valueOf(dueP.getValue()));
                    a.setBatchId(batchB.getValue().getBatchId());
                    a.setTeacherId(currentTeacher.getTeacherId());
                    return a;
                }
                return null;
            });
            
            dialog.showAndWait().ifPresent(a -> {
                if (assignmentDAO.insert(a)) table.setItems(FXCollections.observableArrayList(assignmentDAO.getByTeacherId(currentTeacher.getTeacherId())));
            });
        });

        view.getChildren().addAll(addBtn, table);
        setContent(view);
    }

    @FXML
    public void showExams(ActionEvent event) {
        setActiveButton(btnExams);
        VBox view = createManagementView("Manage Exams");

        TableView<Exam> table = new TableView<>();
        table.getStyleClass().add("data-table");

        TableColumn<Exam, String> colName = new TableColumn<>("Exam Name");
        colName.setCellValueFactory(new PropertyValueFactory<>("examName"));

        TableColumn<Exam, String> colSubject = new TableColumn<>("Subject");
        colSubject.setCellValueFactory(new PropertyValueFactory<>("subject"));

        TableColumn<Exam, Date> colDate = new TableColumn<>("Date");
        colDate.setCellValueFactory(new PropertyValueFactory<>("examDate"));

        TableColumn<Exam, Double> colMarks = new TableColumn<>("Total Marks");
        colMarks.setCellValueFactory(new PropertyValueFactory<>("totalMarks"));

        table.getColumns().addAll(colName, colSubject, colDate, colMarks);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(table, Priority.ALWAYS);

        // Load exams for teacher's batches
        Runnable refresh = () -> {
            java.util.List<Exam> allExams = new java.util.ArrayList<>();
            for (Batch b : batchDAO.getByTeacherId(currentTeacher.getTeacherId())) {
                allExams.addAll(examDAO.getByBatchId(b.getBatchId()));
            }
            table.setItems(FXCollections.observableArrayList(allExams));
        };
        refresh.run();

        Button addBtn = new Button("➕ Add Exam");
        addBtn.getStyleClass().addAll("action-btn", "add-btn");
        addBtn.setOnAction(e -> {
            Dialog<Exam> dialog = new Dialog<>();
            dialog.setTitle("Create Exam");
            dialog.getDialogPane().getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
            dialog.getDialogPane().setStyle("-fx-background-color: #1e293b;");

            ButtonType saveBtn = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

            GridPane grid = new GridPane();
            grid.setHgap(10); grid.setVgap(10); grid.setPadding(new Insets(20));

            TextField nameF = new TextField(); nameF.setPromptText("e.g. Mid-Term Exam");
            TextField subjectF = new TextField(); subjectF.setPromptText("e.g. Mathematics");
            DatePicker datePicker = new DatePicker(LocalDate.now());
            TextField marksF = new TextField("100");
            ComboBox<Batch> batchB = new ComboBox<>();
            batchB.setItems(FXCollections.observableArrayList(batchDAO.getByTeacherId(currentTeacher.getTeacherId())));

            // Style fields
            String fieldStyle = "-fx-background-color: #0f172a; -fx-text-fill: #f8fafc; -fx-border-color: rgba(255,255,255,0.1); -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 8;";
            nameF.setStyle(fieldStyle); subjectF.setStyle(fieldStyle); marksF.setStyle(fieldStyle);
            for (Label lbl : new Label[]{new Label("Name:"), new Label("Subject:"), new Label("Date:"), new Label("Total Marks:"), new Label("Batch:")}) {
                lbl.setStyle("-fx-text-fill: #cbd5e1;");
            }

            grid.add(new Label("Name:"), 0, 0);        grid.add(nameF, 1, 0);
            grid.add(new Label("Subject:"), 0, 1);     grid.add(subjectF, 1, 1);
            grid.add(new Label("Date:"), 0, 2);        grid.add(datePicker, 1, 2);
            grid.add(new Label("Total Marks:"), 0, 3); grid.add(marksF, 1, 3);
            grid.add(new Label("Batch:"), 0, 4);       grid.add(batchB, 1, 4);

            dialog.getDialogPane().setContent(grid);
            dialog.setResultConverter(btn -> {
                if (btn == saveBtn) {
                    if (nameF.getText().isBlank() || batchB.getValue() == null) {
                        showAlert("Error", "Please fill in the exam name and select a batch.");
                        return null;
                    }
                    Exam ex = new Exam();
                    ex.setExamName(nameF.getText().trim());
                    ex.setSubject(subjectF.getText().trim());
                    ex.setExamDate(Date.valueOf(datePicker.getValue()));
                    try { ex.setTotalMarks(Double.parseDouble(marksF.getText())); } catch (Exception ex2) { ex.setTotalMarks(100); }
                    ex.setBatchId(batchB.getValue().getBatchId());
                    return ex;
                }
                return null;
            });

            dialog.showAndWait().ifPresent(ex -> {
                if (examDAO.insert(ex)) {
                    refresh.run();
                    showAlert("Success", "Exam \"" + ex.getExamName() + "\" created!");
                } else {
                    showAlert("Error", "Failed to create exam.");
                }
            });
        });

        view.getChildren().addAll(addBtn, table);
        setContent(view);
    }

    @FXML
    public void showResults(ActionEvent event) {
        setActiveButton(btnResults);
        VBox view = createManagementView("Upload Exam Results");
        
        HBox topBar = new HBox(15);
        topBar.setAlignment(Pos.CENTER_LEFT);
        
        ComboBox<Exam> examBox = new ComboBox<>();
        // In a real app we'd fetch exams by teacher's batches. Let's just fetch all exams for now and filter by teacher if needed.
        examBox.setItems(FXCollections.observableArrayList(examDAO.getAll())); 
        examBox.setPromptText("Select Exam");
        
        Button loadBtn = new Button("Load Students");
        loadBtn.getStyleClass().add("action-btn");
        topBar.getChildren().addAll(new Label("Exam:"), examBox, loadBtn);
        
        TableView<ResultWrapper> table = new TableView<>();
        table.getStyleClass().add("data-table");
        
        TableColumn<ResultWrapper, String> colName = new TableColumn<>("Student Name");
        colName.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        
        TableColumn<ResultWrapper, TextField> colMarks = new TableColumn<>("Marks");
        colMarks.setCellValueFactory(new PropertyValueFactory<>("marksField"));
        colMarks.setCellFactory(col -> new TableCell<ResultWrapper, TextField>() {
            @Override
            protected void updateItem(TextField item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    setGraphic(item);
                }
            }
        });
        
        table.getColumns().addAll(colName, colMarks);
        VBox.setVgrow(table, Priority.ALWAYS);
        
        Button saveBtn = new Button("💾 Save Results");
        saveBtn.getStyleClass().addAll("action-btn", "add-btn");
        saveBtn.setDisable(true);
        
        loadBtn.setOnAction(e -> {
            if (examBox.getValue() == null) return;
            if (examBox.getValue().getBatchId() == null) { showAlert("Error", "Exam has no batch attached."); return; }
            
            List<Student> students = studentDAO.getByBatchId(examBox.getValue().getBatchId());
            ObservableList<ResultWrapper> data = FXCollections.observableArrayList();
            for (Student s : students) data.add(new ResultWrapper(s));
            table.setItems(data);
            saveBtn.setDisable(data.isEmpty());
        });
        
        saveBtn.setOnAction(e -> {
            int examId = examBox.getValue().getExamId();
            for (ResultWrapper w : table.getItems()) {
                Result r = new Result();
                r.setStudentId(w.getStudentId());
                r.setExamId(examId);
                try { r.setMarksObtained(Double.parseDouble(w.getMarksField().getText())); } catch (Exception ex) { r.setMarksObtained(0); }
                resultDAO.insert(r);
            }
            showAlert("Success", "Results uploaded successfully.");
            table.getItems().clear();
            saveBtn.setDisable(true);
        });
        
        view.getChildren().addAll(topBar, table, saveBtn);
        setContent(view);
    }
    
    public class ResultWrapper {
        private Student student;
        private TextField marksField;
        public ResultWrapper(Student s) {
            this.student = s;
            this.marksField = new TextField("0");
            this.marksField.setStyle("-fx-background-color: #0f172a; -fx-text-fill: white;");
        }
        public String getStudentName() { return student.getName(); }
        public int getStudentId() { return student.getStudentId(); }
        public TextField getMarksField() { return marksField; }
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
