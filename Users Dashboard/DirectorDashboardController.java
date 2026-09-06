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
import javafx.scene.text.Font;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class DirectorDashboardController {

    @FXML private StackPane contentArea;
    @FXML private Button btnDashboard, btnTeachers, btnStudents, btnGuardians, btnBatches, btnFees, btnNotices, btnExams, btnResults;

    private TeacherDAO teacherDAO = new TeacherDAO();
    private StudentDAO studentDAO = new StudentDAO();
    private GuardianDAO guardianDAO = new GuardianDAO();
    private BatchDAO batchDAO = new BatchDAO();
    private FeeDAO feeDAO = new FeeDAO();
    private NoticeDAO noticeDAO = new NoticeDAO();
    private ExamDAO examDAO = new ExamDAO();

    private Button[] allNavButtons;

    @FXML
    public void initialize() {
        allNavButtons = new Button[]{btnDashboard, btnTeachers, btnStudents, btnGuardians, btnBatches, btnFees, btnNotices, btnExams, btnResults};
        showDashboard(null);
    }

    private void setActiveButton(Button active) {
        for (Button btn : allNavButtons) {
            btn.getStyleClass().remove("sidebar-btn-active");
        }
        active.getStyleClass().add("sidebar-btn-active");
    }

    // ============================================
    //  DASHBOARD (Overview)
    // ============================================
    @FXML
    public void showDashboard(ActionEvent event) {
        setActiveButton(btnDashboard);
        VBox dashboard = new VBox(25);
        dashboard.setAlignment(Pos.TOP_LEFT);

        Label title = new Label("Dashboard Overview");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #f8fafc;");

        // Stats cards
        HBox statsRow = new HBox(20);
        statsRow.setAlignment(Pos.CENTER_LEFT);

        int teacherCount = teacherDAO.getAll().size();
        int studentCount = studentDAO.getAll().size();
        int batchCount = batchDAO.getAll().size();
        int guardianCount = guardianDAO.getAll().size();
        double feesCollected = feeDAO.getTotalCollected();

        statsRow.getChildren().addAll(
                createStatCard("👨‍🏫", "Teachers", String.valueOf(teacherCount), "#3b82f6"),
                createStatCard("🎓", "Students", String.valueOf(studentCount), "#8b5cf6"),
                createStatCard("📚", "Batches", String.valueOf(batchCount), "#06b6d4"),
                createStatCard("👤", "Guardians", String.valueOf(guardianCount), "#f59e0b"),
                createStatCard("💰", "Fees Collected", String.format("৳%.0f", feesCollected), "#22c55e")
        );

        // Recent notices
        Label noticeTitle = new Label("Recent Notices");
        noticeTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #cbd5e1;");
        
        VBox noticeList = new VBox(10);
        noticeList.setStyle("-fx-background-color: rgba(30,41,59,0.7); -fx-background-radius: 12; -fx-padding: 20;");
        List<Notice> notices = noticeDAO.getAll();
        if (notices.isEmpty()) {
            Label noNotice = new Label("No notices posted yet.");
            noNotice.setStyle("-fx-text-fill: #64748b; -fx-font-size: 14px;");
            noticeList.getChildren().add(noNotice);
        } else {
            for (int i = 0; i < Math.min(5, notices.size()); i++) {
                Notice n = notices.get(i);
                HBox row = new HBox(10);
                row.setAlignment(Pos.CENTER_LEFT);
                Label icon = new Label("📢");
                icon.setStyle("-fx-font-size: 16px;");
                Label nTitle = new Label(n.getTitle());
                nTitle.setStyle("-fx-text-fill: #f8fafc; -fx-font-size: 14px; -fx-font-weight: bold;");
                Label nDate = new Label(n.getDatePosted() != null ? n.getDatePosted().toString() : "");
                nDate.setStyle("-fx-text-fill: #64748b; -fx-font-size: 12px;");
                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);
                Label nTarget = new Label(n.getTargetRole());
                nTarget.setStyle("-fx-text-fill: #3b82f6; -fx-font-size: 12px; -fx-background-color: rgba(59,130,246,0.15); -fx-padding: 2 8; -fx-background-radius: 4;");
                row.getChildren().addAll(icon, nTitle, spacer, nTarget, nDate);
                noticeList.getChildren().add(row);
            }
        }

        dashboard.getChildren().addAll(title, statsRow, noticeTitle, noticeList);
        setContent(dashboard);
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

    // ============================================
    //  TEACHERS
    // ============================================
    @FXML
    public void showTeachers(ActionEvent event) {
        setActiveButton(btnTeachers);
        VBox view = createManagementView("Manage Teachers");

        TableView<Teacher> table = new TableView<>();
        table.setStyle("-fx-background-color: transparent;");
        table.getStyleClass().add("data-table");

        TableColumn<Teacher, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("teacherId"));
        colId.setPrefWidth(60);

        TableColumn<Teacher, String> colName = new TableColumn<>("Name");
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colName.setPrefWidth(180);

        TableColumn<Teacher, String> colSubject = new TableColumn<>("Subject");
        colSubject.setCellValueFactory(new PropertyValueFactory<>("subject"));
        colSubject.setPrefWidth(150);

        TableColumn<Teacher, String> colPhone = new TableColumn<>("Phone");
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colPhone.setPrefWidth(130);

        TableColumn<Teacher, String> colEmail = new TableColumn<>("Email");
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colEmail.setPrefWidth(200);

        table.getColumns().addAll(colId, colName, colSubject, colPhone, colEmail);
        table.setItems(FXCollections.observableArrayList(teacherDAO.getAll()));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(table, Priority.ALWAYS);

        HBox actions = createActionButtons(
                e -> addTeacherDialog(table),
                e -> editTeacherDialog(table),
                e -> deleteEntity(table, "Teacher", t -> teacherDAO.delete(((Teacher) t).getTeacherId()), () -> table.setItems(FXCollections.observableArrayList(teacherDAO.getAll())))
        );

        view.getChildren().addAll(actions, table);
        setContent(view);
    }

    private void addTeacherDialog(TableView<Teacher> table) {
        Dialog<Teacher> dialog = new Dialog<>();
        dialog.setTitle("Add Teacher");
        dialog.getDialogPane().getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
        dialog.getDialogPane().setStyle("-fx-background-color: #1e293b;");

        ButtonType saveBtn = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        GridPane grid = createFormGrid();
        TextField nameField = addFormField(grid, "Name:", 0);
        TextField subjectField = addFormField(grid, "Subject:", 1);
        TextField phoneField = addFormField(grid, "Phone:", 2);
        TextField emailField = addFormField(grid, "Email:", 3);
        TextField passwordField = addFormField(grid, "Password:", 4);

        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(btn -> {
            if (btn == saveBtn) {
                Teacher t = new Teacher();
                t.setName(nameField.getText());
                t.setSubject(subjectField.getText());
                t.setPhone(phoneField.getText());
                t.setEmail(emailField.getText());
                t.setPassword(passwordField.getText());
                return t;
            }
            return null;
        });

        Optional<Teacher> result = dialog.showAndWait();
        result.ifPresent(t -> {
            if (t.getName().isEmpty() || t.getEmail().isEmpty() || t.getPassword().isEmpty()) {
                showAlert("Error", "Name, Email and Password are required.");
                return;
            }
            if (teacherDAO.insert(t)) {
                table.setItems(FXCollections.observableArrayList(teacherDAO.getAll()));
            } else {
                showAlert("Error", "Failed to add teacher. Email may already exist.");
            }
        });
    }

    private void editTeacherDialog(TableView<Teacher> table) {
        Teacher selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) { showAlert("Warning", "Please select a teacher to edit."); return; }

        Dialog<Teacher> dialog = new Dialog<>();
        dialog.setTitle("Edit Teacher");
        dialog.getDialogPane().getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
        dialog.getDialogPane().setStyle("-fx-background-color: #1e293b;");

        ButtonType saveBtn = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        GridPane grid = createFormGrid();
        TextField nameField = addFormField(grid, "Name:", 0);
        nameField.setText(selected.getName());
        TextField subjectField = addFormField(grid, "Subject:", 1);
        subjectField.setText(selected.getSubject());
        TextField phoneField = addFormField(grid, "Phone:", 2);
        phoneField.setText(selected.getPhone());
        TextField emailField = addFormField(grid, "Email:", 3);
        emailField.setText(selected.getEmail());
        TextField passwordField = addFormField(grid, "Password:", 4);
        passwordField.setText(selected.getPassword());

        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(btn -> {
            if (btn == saveBtn) {
                selected.setName(nameField.getText());
                selected.setSubject(subjectField.getText());
                selected.setPhone(phoneField.getText());
                selected.setEmail(emailField.getText());
                selected.setPassword(passwordField.getText());
                return selected;
            }
            return null;
        });

        Optional<Teacher> result = dialog.showAndWait();
        result.ifPresent(t -> {
            if (teacherDAO.update(t)) {
                table.setItems(FXCollections.observableArrayList(teacherDAO.getAll()));
            } else {
                showAlert("Error", "Failed to update teacher.");
            }
        });
    }

    // ============================================
    //  STUDENTS
    // ============================================
    @FXML
    public void showStudents(ActionEvent event) {
        setActiveButton(btnStudents);
        VBox view = createManagementView("Manage Students");

        TableView<Student> table = new TableView<>();
        table.getStyleClass().add("data-table");

        TableColumn<Student, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        colId.setPrefWidth(60);

        TableColumn<Student, String> colName = new TableColumn<>("Name");
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colName.setPrefWidth(160);

        TableColumn<Student, String> colGender = new TableColumn<>("Gender");
        colGender.setCellValueFactory(new PropertyValueFactory<>("gender"));
        colGender.setPrefWidth(80);

        TableColumn<Student, String> colPhone = new TableColumn<>("Phone");
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colPhone.setPrefWidth(120);

        TableColumn<Student, String> colEmail = new TableColumn<>("Email");
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colEmail.setPrefWidth(180);

        TableColumn<Student, Integer> colBatch = new TableColumn<>("Batch ID");
        colBatch.setCellValueFactory(new PropertyValueFactory<>("batchId"));
        colBatch.setPrefWidth(80);

        TableColumn<Student, Integer> colGuardian = new TableColumn<>("Guardian ID");
        colGuardian.setCellValueFactory(new PropertyValueFactory<>("guardianId"));
        colGuardian.setPrefWidth(90);

        table.getColumns().addAll(colId, colName, colGender, colPhone, colEmail, colBatch, colGuardian);
        table.setItems(FXCollections.observableArrayList(studentDAO.getAll()));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(table, Priority.ALWAYS);

        HBox actions = createActionButtons(
                e -> addStudentDialog(table),
                e -> editStudentDialog(table),
                e -> deleteEntity(table, "Student", t -> studentDAO.delete(((Student) t).getStudentId()), () -> table.setItems(FXCollections.observableArrayList(studentDAO.getAll())))
        );

        view.getChildren().addAll(actions, table);
        setContent(view);
    }

    private void addStudentDialog(TableView<Student> table) {
        Dialog<Student> dialog = new Dialog<>();
        dialog.setTitle("Add Student");
        dialog.getDialogPane().getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
        dialog.getDialogPane().setStyle("-fx-background-color: #1e293b;");

        ButtonType saveBtn = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        GridPane grid = createFormGrid();
        TextField nameField = addFormField(grid, "Name:", 0);
        TextField dobField = addFormField(grid, "DOB (YYYY-MM-DD):", 1);
        
        ComboBox<String> genderBox = new ComboBox<>();
        genderBox.getItems().addAll("Male", "Female", "Other");
        genderBox.setValue("Male");
        genderBox.setStyle("-fx-background-color: #0f172a; -fx-text-fill: #f8fafc;");
        Label genderLabel = new Label("Gender:");
        genderLabel.setStyle("-fx-text-fill: #cbd5e1;");
        grid.add(genderLabel, 0, 2);
        grid.add(genderBox, 1, 2);

        TextField phoneField = addFormField(grid, "Phone:", 3);
        TextField emailField = addFormField(grid, "Email:", 4);
        TextField passwordField = addFormField(grid, "Password:", 5);
        
        ComboBox<Batch> batchBox = new ComboBox<>();
        batchBox.setItems(FXCollections.observableArrayList(batchDAO.getAll()));
        batchBox.setStyle("-fx-background-color: #0f172a; -fx-text-fill: #f8fafc;");
        Label batchLabel = new Label("Batch:");
        batchLabel.setStyle("-fx-text-fill: #cbd5e1;");
        grid.add(batchLabel, 0, 6);
        grid.add(batchBox, 1, 6);

        ComboBox<Guardian> guardianBox = new ComboBox<>();
        guardianBox.setItems(FXCollections.observableArrayList(guardianDAO.getAll()));
        guardianBox.setStyle("-fx-background-color: #0f172a; -fx-text-fill: #f8fafc;");
        Label guardianLabel = new Label("Guardian:");
        guardianLabel.setStyle("-fx-text-fill: #cbd5e1;");
        grid.add(guardianLabel, 0, 7);
        grid.add(guardianBox, 1, 7);

        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(btn -> {
            if (btn == saveBtn) {
                Student s = new Student();
                s.setName(nameField.getText());
                try {
                    s.setDob(Date.valueOf(dobField.getText()));
                } catch (Exception ex) {
                    s.setDob(null);
                }
                s.setGender(genderBox.getValue());
                s.setPhone(phoneField.getText());
                s.setEmail(emailField.getText());
                s.setPassword(passwordField.getText());
                s.setBatchId(batchBox.getValue() != null ? batchBox.getValue().getBatchId() : null);
                s.setGuardianId(guardianBox.getValue() != null ? guardianBox.getValue().getGuardianId() : null);
                return s;
            }
            return null;
        });

        Optional<Student> result = dialog.showAndWait();
        result.ifPresent(s -> {
            if (s.getName().isEmpty() || s.getEmail().isEmpty() || s.getPassword().isEmpty()) {
                showAlert("Error", "Name, Email and Password are required.");
                return;
            }
            if (studentDAO.insert(s)) {
                table.setItems(FXCollections.observableArrayList(studentDAO.getAll()));
            } else {
                showAlert("Error", "Failed to add student. Email may already exist.");
            }
        });
    }

    private void editStudentDialog(TableView<Student> table) {
        Student selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) { showAlert("Warning", "Please select a student to edit."); return; }

        Dialog<Student> dialog = new Dialog<>();
        dialog.setTitle("Edit Student");
        dialog.getDialogPane().getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
        dialog.getDialogPane().setStyle("-fx-background-color: #1e293b;");

        ButtonType saveBtn = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        GridPane grid = createFormGrid();
        TextField nameField = addFormField(grid, "Name:", 0);
        nameField.setText(selected.getName());
        TextField dobField = addFormField(grid, "DOB (YYYY-MM-DD):", 1);
        dobField.setText(selected.getDob() != null ? selected.getDob().toString() : "");

        ComboBox<String> genderBox = new ComboBox<>();
        genderBox.getItems().addAll("Male", "Female", "Other");
        genderBox.setValue(selected.getGender() != null ? selected.getGender() : "Male");
        genderBox.setStyle("-fx-background-color: #0f172a; -fx-text-fill: #f8fafc;");
        Label genderLabel = new Label("Gender:");
        genderLabel.setStyle("-fx-text-fill: #cbd5e1;");
        grid.add(genderLabel, 0, 2);
        grid.add(genderBox, 1, 2);

        TextField phoneField = addFormField(grid, "Phone:", 3);
        phoneField.setText(selected.getPhone() != null ? selected.getPhone() : "");
        TextField emailField = addFormField(grid, "Email:", 4);
        emailField.setText(selected.getEmail());
        TextField passwordField = addFormField(grid, "Password:", 5);
        passwordField.setText(selected.getPassword());

        ComboBox<Batch> batchBox = new ComboBox<>();
        batchBox.setItems(FXCollections.observableArrayList(batchDAO.getAll()));
        batchBox.setStyle("-fx-background-color: #0f172a; -fx-text-fill: #f8fafc;");
        if (selected.getBatchId() != null) {
            batchBox.getItems().forEach(b -> { if (b.getBatchId() == selected.getBatchId()) batchBox.setValue(b); });
        }
        Label batchLabel = new Label("Batch:");
        batchLabel.setStyle("-fx-text-fill: #cbd5e1;");
        grid.add(batchLabel, 0, 6);
        grid.add(batchBox, 1, 6);

        ComboBox<Guardian> guardianBox = new ComboBox<>();
        guardianBox.setItems(FXCollections.observableArrayList(guardianDAO.getAll()));
        guardianBox.setStyle("-fx-background-color: #0f172a; -fx-text-fill: #f8fafc;");
        if (selected.getGuardianId() != null) {
            guardianBox.getItems().forEach(g -> { if (g.getGuardianId() == selected.getGuardianId()) guardianBox.setValue(g); });
        }
        Label guardianLabel = new Label("Guardian:");
        guardianLabel.setStyle("-fx-text-fill: #cbd5e1;");
        grid.add(guardianLabel, 0, 7);
        grid.add(guardianBox, 1, 7);

        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(btn -> {
            if (btn == saveBtn) {
                selected.setName(nameField.getText());
                try { selected.setDob(Date.valueOf(dobField.getText())); } catch (Exception ex) {}
                selected.setGender(genderBox.getValue());
                selected.setPhone(phoneField.getText());
                selected.setEmail(emailField.getText());
                selected.setPassword(passwordField.getText());
                selected.setBatchId(batchBox.getValue() != null ? batchBox.getValue().getBatchId() : null);
                selected.setGuardianId(guardianBox.getValue() != null ? guardianBox.getValue().getGuardianId() : null);
                return selected;
            }
            return null;
        });

        Optional<Student> result = dialog.showAndWait();
        result.ifPresent(s -> {
            if (studentDAO.update(s)) {
                table.setItems(FXCollections.observableArrayList(studentDAO.getAll()));
            } else {
                showAlert("Error", "Failed to update student.");
            }
        });
    }

    // ============================================
    //  GUARDIANS
    // ============================================
    @FXML
    public void showGuardians(ActionEvent event) {
        setActiveButton(btnGuardians);
        VBox view = createManagementView("Manage Guardians");

        TableView<Guardian> table = new TableView<>();
        table.getStyleClass().add("data-table");

        TableColumn<Guardian, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("guardianId"));
        colId.setPrefWidth(60);

        TableColumn<Guardian, String> colName = new TableColumn<>("Name");
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colName.setPrefWidth(180);

        TableColumn<Guardian, String> colPhone = new TableColumn<>("Phone");
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colPhone.setPrefWidth(130);

        TableColumn<Guardian, String> colEmail = new TableColumn<>("Email");
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colEmail.setPrefWidth(200);

        TableColumn<Guardian, String> colAddress = new TableColumn<>("Address");
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colAddress.setPrefWidth(200);

        table.getColumns().addAll(colId, colName, colPhone, colEmail, colAddress);
        table.setItems(FXCollections.observableArrayList(guardianDAO.getAll()));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(table, Priority.ALWAYS);

        HBox actions = createActionButtons(
                e -> addGuardianDialog(table),
                e -> editGuardianDialog(table),
                e -> deleteEntity(table, "Guardian", t -> guardianDAO.delete(((Guardian) t).getGuardianId()), () -> table.setItems(FXCollections.observableArrayList(guardianDAO.getAll())))
        );

        view.getChildren().addAll(actions, table);
        setContent(view);
    }

    private void addGuardianDialog(TableView<Guardian> table) {
        Dialog<Guardian> dialog = new Dialog<>();
        dialog.setTitle("Add Guardian");
        dialog.getDialogPane().getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
        dialog.getDialogPane().setStyle("-fx-background-color: #1e293b;");

        ButtonType saveBtn = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        GridPane grid = createFormGrid();
        TextField nameField = addFormField(grid, "Name:", 0);
        TextField phoneField = addFormField(grid, "Phone:", 1);
        TextField emailField = addFormField(grid, "Email:", 2);
        TextField passwordField = addFormField(grid, "Password:", 3);
        TextField addressField = addFormField(grid, "Address:", 4);

        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(btn -> {
            if (btn == saveBtn) {
                Guardian g = new Guardian();
                g.setName(nameField.getText());
                g.setPhone(phoneField.getText());
                g.setEmail(emailField.getText());
                g.setPassword(passwordField.getText());
                g.setAddress(addressField.getText());
                return g;
            }
            return null;
        });

        Optional<Guardian> result = dialog.showAndWait();
        result.ifPresent(g -> {
            if (g.getName().isEmpty() || g.getEmail().isEmpty() || g.getPassword().isEmpty()) {
                showAlert("Error", "Name, Email and Password are required.");
                return;
            }
            if (guardianDAO.insert(g)) {
                table.setItems(FXCollections.observableArrayList(guardianDAO.getAll()));
            } else {
                showAlert("Error", "Failed to add guardian. Email may already exist.");
            }
        });
    }

    private void editGuardianDialog(TableView<Guardian> table) {
        Guardian selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) { showAlert("Warning", "Please select a guardian to edit."); return; }

        Dialog<Guardian> dialog = new Dialog<>();
        dialog.setTitle("Edit Guardian");
        dialog.getDialogPane().getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
        dialog.getDialogPane().setStyle("-fx-background-color: #1e293b;");

        ButtonType saveBtn = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        GridPane grid = createFormGrid();
        TextField nameField = addFormField(grid, "Name:", 0);
        nameField.setText(selected.getName());
        TextField phoneField = addFormField(grid, "Phone:", 1);
        phoneField.setText(selected.getPhone() != null ? selected.getPhone() : "");
        TextField emailField = addFormField(grid, "Email:", 2);
        emailField.setText(selected.getEmail());
        TextField passwordField = addFormField(grid, "Password:", 3);
        passwordField.setText(selected.getPassword());
        TextField addressField = addFormField(grid, "Address:", 4);
        addressField.setText(selected.getAddress() != null ? selected.getAddress() : "");

        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(btn -> {
            if (btn == saveBtn) {
                selected.setName(nameField.getText());
                selected.setPhone(phoneField.getText());
                selected.setEmail(emailField.getText());
                selected.setPassword(passwordField.getText());
                selected.setAddress(addressField.getText());
                return selected;
            }
            return null;
        });

        Optional<Guardian> result = dialog.showAndWait();
        result.ifPresent(g -> {
            if (guardianDAO.update(g)) {
                table.setItems(FXCollections.observableArrayList(guardianDAO.getAll()));
            } else {
                showAlert("Error", "Failed to update guardian.");
            }
        });
    }

    // ============================================
    //  BATCHES
    // ============================================
    @FXML
    public void showBatches(ActionEvent event) {
        setActiveButton(btnBatches);
        VBox view = createManagementView("Manage Batches");

        TableView<Batch> table = new TableView<>();
        table.getStyleClass().add("data-table");

        TableColumn<Batch, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("batchId"));
        colId.setPrefWidth(60);

        TableColumn<Batch, String> colName = new TableColumn<>("Batch Name");
        colName.setCellValueFactory(new PropertyValueFactory<>("batchName"));
        colName.setPrefWidth(180);

        TableColumn<Batch, String> colSubject = new TableColumn<>("Subject");
        colSubject.setCellValueFactory(new PropertyValueFactory<>("subject"));
        colSubject.setPrefWidth(150);

        TableColumn<Batch, String> colSchedule = new TableColumn<>("Schedule");
        colSchedule.setCellValueFactory(new PropertyValueFactory<>("schedule"));
        colSchedule.setPrefWidth(200);

        TableColumn<Batch, Integer> colTeacher = new TableColumn<>("Teacher ID");
        colTeacher.setCellValueFactory(new PropertyValueFactory<>("teacherId"));
        colTeacher.setPrefWidth(100);

        table.getColumns().addAll(colId, colName, colSubject, colSchedule, colTeacher);
        table.setItems(FXCollections.observableArrayList(batchDAO.getAll()));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(table, Priority.ALWAYS);

        HBox actions = createActionButtons(
                e -> addBatchDialog(table),
                e -> editBatchDialog(table),
                e -> deleteEntity(table, "Batch", t -> batchDAO.delete(((Batch) t).getBatchId()), () -> table.setItems(FXCollections.observableArrayList(batchDAO.getAll())))
        );

        view.getChildren().addAll(actions, table);
        setContent(view);
    }

    private void addBatchDialog(TableView<Batch> table) {
        Dialog<Batch> dialog = new Dialog<>();
        dialog.setTitle("Add Batch");
        dialog.getDialogPane().getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
        dialog.getDialogPane().setStyle("-fx-background-color: #1e293b;");

        ButtonType saveBtn = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        GridPane grid = createFormGrid();
        TextField nameField = addFormField(grid, "Batch Name:", 0);
        TextField subjectField = addFormField(grid, "Subject:", 1);
        TextField scheduleField = addFormField(grid, "Schedule:", 2);

        ComboBox<Teacher> teacherBox = new ComboBox<>();
        teacherBox.setItems(FXCollections.observableArrayList(teacherDAO.getAll()));
        teacherBox.setStyle("-fx-background-color: #0f172a; -fx-text-fill: #f8fafc;");
        Label teacherLabel = new Label("Teacher:");
        teacherLabel.setStyle("-fx-text-fill: #cbd5e1;");
        grid.add(teacherLabel, 0, 3);
        grid.add(teacherBox, 1, 3);

        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(btn -> {
            if (btn == saveBtn) {
                Batch b = new Batch();
                b.setBatchName(nameField.getText());
                b.setSubject(subjectField.getText());
                b.setSchedule(scheduleField.getText());
                b.setTeacherId(teacherBox.getValue() != null ? teacherBox.getValue().getTeacherId() : null);
                return b;
            }
            return null;
        });

        Optional<Batch> result = dialog.showAndWait();
        result.ifPresent(b -> {
            if (b.getBatchName().isEmpty()) {
                showAlert("Error", "Batch Name is required.");
                return;
            }
            if (batchDAO.insert(b)) {
                table.setItems(FXCollections.observableArrayList(batchDAO.getAll()));
            } else {
                showAlert("Error", "Failed to add batch.");
            }
        });
    }

    private void editBatchDialog(TableView<Batch> table) {
        Batch selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) { showAlert("Warning", "Please select a batch to edit."); return; }

        Dialog<Batch> dialog = new Dialog<>();
        dialog.setTitle("Edit Batch");
        dialog.getDialogPane().getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
        dialog.getDialogPane().setStyle("-fx-background-color: #1e293b;");

        ButtonType saveBtn = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        GridPane grid = createFormGrid();
        TextField nameField = addFormField(grid, "Batch Name:", 0);
        nameField.setText(selected.getBatchName());
        TextField subjectField = addFormField(grid, "Subject:", 1);
        subjectField.setText(selected.getSubject() != null ? selected.getSubject() : "");
        TextField scheduleField = addFormField(grid, "Schedule:", 2);
        scheduleField.setText(selected.getSchedule() != null ? selected.getSchedule() : "");

        ComboBox<Teacher> teacherBox = new ComboBox<>();
        teacherBox.setItems(FXCollections.observableArrayList(teacherDAO.getAll()));
        teacherBox.setStyle("-fx-background-color: #0f172a; -fx-text-fill: #f8fafc;");
        if (selected.getTeacherId() != null) {
            teacherBox.getItems().forEach(t -> { if (t.getTeacherId() == selected.getTeacherId()) teacherBox.setValue(t); });
        }
        Label teacherLabel = new Label("Teacher:");
        teacherLabel.setStyle("-fx-text-fill: #cbd5e1;");
        grid.add(teacherLabel, 0, 3);
        grid.add(teacherBox, 1, 3);

        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(btn -> {
            if (btn == saveBtn) {
                selected.setBatchName(nameField.getText());
                selected.setSubject(subjectField.getText());
                selected.setSchedule(scheduleField.getText());
                selected.setTeacherId(teacherBox.getValue() != null ? teacherBox.getValue().getTeacherId() : null);
                return selected;
            }
            return null;
        });

        Optional<Batch> result = dialog.showAndWait();
        result.ifPresent(b -> {
            if (batchDAO.update(b)) {
                table.setItems(FXCollections.observableArrayList(batchDAO.getAll()));
            } else {
                showAlert("Error", "Failed to update batch.");
            }
        });
    }

    // ============================================
    //  FEES
    // ============================================
    @FXML
    public void showFees(ActionEvent event) {
        setActiveButton(btnFees);
        VBox view = createManagementView("Manage Fees");

        TableView<Fee> table = new TableView<>();
        table.getStyleClass().add("data-table");

        TableColumn<Fee, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("feeId"));
        colId.setPrefWidth(60);

        TableColumn<Fee, Integer> colStudent = new TableColumn<>("Student ID");
        colStudent.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        colStudent.setPrefWidth(90);

        TableColumn<Fee, Double> colAmount = new TableColumn<>("Amount");
        colAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        colAmount.setPrefWidth(100);

        TableColumn<Fee, java.sql.Date> colDue = new TableColumn<>("Due Date");
        colDue.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        colDue.setPrefWidth(120);

        TableColumn<Fee, java.sql.Date> colPayment = new TableColumn<>("Payment Date");
        colPayment.setCellValueFactory(new PropertyValueFactory<>("paymentDate"));
        colPayment.setPrefWidth(120);

        TableColumn<Fee, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colStatus.setPrefWidth(100);

        table.getColumns().addAll(colId, colStudent, colAmount, colDue, colPayment, colStatus);
        table.setItems(FXCollections.observableArrayList(feeDAO.getAll()));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(table, Priority.ALWAYS);

        HBox actions = createActionButtons(
                e -> addFeeDialog(table),
                e -> editFeeDialog(table),
                e -> deleteEntity(table, "Fee", t -> feeDAO.delete(((Fee) t).getFeeId()), () -> table.setItems(FXCollections.observableArrayList(feeDAO.getAll())))
        );

        view.getChildren().addAll(actions, table);
        setContent(view);
    }

    private void addFeeDialog(TableView<Fee> table) {
        Dialog<Fee> dialog = new Dialog<>();
        dialog.setTitle("Add Fee");
        dialog.getDialogPane().getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
        dialog.getDialogPane().setStyle("-fx-background-color: #1e293b;");

        ButtonType saveBtn = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        GridPane grid = createFormGrid();

        ComboBox<Student> studentBox = new ComboBox<>();
        studentBox.setItems(FXCollections.observableArrayList(studentDAO.getAll()));
        studentBox.setStyle("-fx-background-color: #0f172a; -fx-text-fill: #f8fafc;");
        Label studentLabel = new Label("Student:");
        studentLabel.setStyle("-fx-text-fill: #cbd5e1;");
        grid.add(studentLabel, 0, 0);
        grid.add(studentBox, 1, 0);

        TextField amountField = addFormField(grid, "Amount:", 1);
        TextField dueField = addFormField(grid, "Due Date (YYYY-MM-DD):", 2);

        ComboBox<String> statusBox = new ComboBox<>();
        statusBox.getItems().addAll("Unpaid", "Paid", "Partial");
        statusBox.setValue("Unpaid");
        statusBox.setStyle("-fx-background-color: #0f172a; -fx-text-fill: #f8fafc;");
        Label statusLabel = new Label("Status:");
        statusLabel.setStyle("-fx-text-fill: #cbd5e1;");
        grid.add(statusLabel, 0, 3);
        grid.add(statusBox, 1, 3);

        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(btn -> {
            if (btn == saveBtn) {
                if (studentBox.getValue() == null) {
                    showAlert("Error", "Please select a student.", "error-alert");
                    return null;
                }
                Fee f = new Fee();
                f.setStudentId(studentBox.getValue().getStudentId());
                try { f.setAmount(Double.parseDouble(amountField.getText())); } catch (Exception ex) { f.setAmount(0); }
                try { f.setDueDate(Date.valueOf(dueField.getText())); } catch (Exception ex) {}
                f.setStatus(statusBox.getValue());
                if ("Paid".equals(statusBox.getValue())) {
                    f.setPaymentDate(Date.valueOf(LocalDate.now()));
                }
                return f;
            }
            return null;
        });

        Optional<Fee> result = dialog.showAndWait();
        result.ifPresent(f -> {
            if (feeDAO.insert(f)) {
                table.setItems(FXCollections.observableArrayList(feeDAO.getAll()));
            } else {
                showAlert("Error", "Failed to add fee record.");
            }
        });
    }

    private void editFeeDialog(TableView<Fee> table) {
        Fee selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) { showAlert("Warning", "Please select a fee record to edit."); return; }

        Dialog<Fee> dialog = new Dialog<>();
        dialog.setTitle("Edit Fee");
        dialog.getDialogPane().getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
        dialog.getDialogPane().setStyle("-fx-background-color: #1e293b;");

        ButtonType saveBtn = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        GridPane grid = createFormGrid();

        Label studentLabel = new Label("Student ID: " + selected.getStudentId());
        studentLabel.setStyle("-fx-text-fill: #f8fafc;");
        grid.add(new Label("Student:") {{ setStyle("-fx-text-fill: #cbd5e1;"); }}, 0, 0);
        grid.add(studentLabel, 1, 0);

        TextField amountField = addFormField(grid, "Amount:", 1);
        amountField.setText(String.valueOf(selected.getAmount()));
        TextField dueField = addFormField(grid, "Due Date (YYYY-MM-DD):", 2);
        dueField.setText(selected.getDueDate() != null ? selected.getDueDate().toString() : "");

        ComboBox<String> statusBox = new ComboBox<>();
        statusBox.getItems().addAll("Unpaid", "Paid", "Partial");
        statusBox.setValue(selected.getStatus());
        statusBox.setStyle("-fx-background-color: #0f172a; -fx-text-fill: #f8fafc;");
        Label statusLbl = new Label("Status:");
        statusLbl.setStyle("-fx-text-fill: #cbd5e1;");
        grid.add(statusLbl, 0, 3);
        grid.add(statusBox, 1, 3);

        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(btn -> {
            if (btn == saveBtn) {
                try { selected.setAmount(Double.parseDouble(amountField.getText())); } catch (Exception ex) {}
                try { selected.setDueDate(Date.valueOf(dueField.getText())); } catch (Exception ex) {}
                selected.setStatus(statusBox.getValue());
                if ("Paid".equals(statusBox.getValue()) && selected.getPaymentDate() == null) {
                    selected.setPaymentDate(Date.valueOf(LocalDate.now()));
                }
                return selected;
            }
            return null;
        });

        Optional<Fee> result = dialog.showAndWait();
        result.ifPresent(f -> {
            if (feeDAO.update(f)) {
                table.setItems(FXCollections.observableArrayList(feeDAO.getAll()));
            } else {
                showAlert("Error", "Failed to update fee record.");
            }
        });
    }

    // ============================================
    //  NOTICES
    // ============================================
    @FXML
    public void showNotices(ActionEvent event) {
        setActiveButton(btnNotices);
        VBox view = createManagementView("Manage Notices");

        TableView<Notice> table = new TableView<>();
        table.getStyleClass().add("data-table");

        TableColumn<Notice, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("noticeId"));
        colId.setPrefWidth(60);

        TableColumn<Notice, String> colTitle = new TableColumn<>("Title");
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colTitle.setPrefWidth(250);

        TableColumn<Notice, String> colContent = new TableColumn<>("Content");
        colContent.setCellValueFactory(new PropertyValueFactory<>("content"));
        colContent.setPrefWidth(300);

        TableColumn<Notice, java.sql.Date> colDate = new TableColumn<>("Date Posted");
        colDate.setCellValueFactory(new PropertyValueFactory<>("datePosted"));
        colDate.setPrefWidth(120);

        TableColumn<Notice, String> colTarget = new TableColumn<>("Target");
        colTarget.setCellValueFactory(new PropertyValueFactory<>("targetRole"));
        colTarget.setPrefWidth(100);

        table.getColumns().addAll(colId, colTitle, colContent, colDate, colTarget);
        table.setItems(FXCollections.observableArrayList(noticeDAO.getAll()));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(table, Priority.ALWAYS);

        HBox actions = createActionButtons(
                e -> addNoticeDialog(table),
                e -> editNoticeDialog(table),
                e -> deleteEntity(table, "Notice", t -> noticeDAO.delete(((Notice) t).getNoticeId()), () -> table.setItems(FXCollections.observableArrayList(noticeDAO.getAll())))
        );

        view.getChildren().addAll(actions, table);
        setContent(view);
    }

    private void addNoticeDialog(TableView<Notice> table) {
        Dialog<Notice> dialog = new Dialog<>();
        dialog.setTitle("Post Notice");
        dialog.getDialogPane().getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
        dialog.getDialogPane().setStyle("-fx-background-color: #1e293b;");

        ButtonType saveBtn = new ButtonType("Post", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        GridPane grid = createFormGrid();
        TextField titleField = addFormField(grid, "Title:", 0);

        TextArea contentArea = new TextArea();
        contentArea.setPromptText("Notice content...");
        contentArea.setStyle("-fx-control-inner-background: #0f172a; -fx-text-fill: #f8fafc; -fx-prompt-text-fill: #64748b;");
        contentArea.setPrefRowCount(4);
        Label contentLabel = new Label("Content:");
        contentLabel.setStyle("-fx-text-fill: #cbd5e1;");
        grid.add(contentLabel, 0, 1);
        grid.add(contentArea, 1, 1);

        ComboBox<String> targetBox = new ComboBox<>();
        targetBox.getItems().addAll("All", "Teacher", "Student", "Guardian");
        targetBox.setValue("All");
        targetBox.setStyle("-fx-background-color: #0f172a; -fx-text-fill: #f8fafc;");
        Label targetLabel = new Label("Target:");
        targetLabel.setStyle("-fx-text-fill: #cbd5e1;");
        grid.add(targetLabel, 0, 2);
        grid.add(targetBox, 1, 2);

        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(btn -> {
            if (btn == saveBtn) {
                Notice n = new Notice();
                n.setTitle(titleField.getText());
                n.setContent(contentArea.getText());
                n.setDatePosted(Date.valueOf(LocalDate.now()));
                n.setTargetRole(targetBox.getValue());
                return n;
            }
            return null;
        });

        Optional<Notice> result = dialog.showAndWait();
        result.ifPresent(n -> {
            if (n.getTitle().isEmpty()) {
                showAlert("Error", "Title is required.");
                return;
            }
            if (noticeDAO.insert(n)) {
                table.setItems(FXCollections.observableArrayList(noticeDAO.getAll()));
            } else {
                showAlert("Error", "Failed to post notice.");
            }
        });
    }

    private void editNoticeDialog(TableView<Notice> table) {
        Notice selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) { showAlert("Warning", "Please select a notice to edit."); return; }

        Dialog<Notice> dialog = new Dialog<>();
        dialog.setTitle("Edit Notice");
        dialog.getDialogPane().getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
        dialog.getDialogPane().setStyle("-fx-background-color: #1e293b;");

        ButtonType saveBtn = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        GridPane grid = createFormGrid();
        TextField titleField = addFormField(grid, "Title:", 0);
        titleField.setText(selected.getTitle());

        TextArea contentArea = new TextArea(selected.getContent());
        contentArea.setStyle("-fx-control-inner-background: #0f172a; -fx-text-fill: #f8fafc;");
        contentArea.setPrefRowCount(4);
        Label contentLabel = new Label("Content:");
        contentLabel.setStyle("-fx-text-fill: #cbd5e1;");
        grid.add(contentLabel, 0, 1);
        grid.add(contentArea, 1, 1);

        ComboBox<String> targetBox = new ComboBox<>();
        targetBox.getItems().addAll("All", "Teacher", "Student", "Guardian");
        targetBox.setValue(selected.getTargetRole());
        targetBox.setStyle("-fx-background-color: #0f172a; -fx-text-fill: #f8fafc;");
        Label targetLabel = new Label("Target:");
        targetLabel.setStyle("-fx-text-fill: #cbd5e1;");
        grid.add(targetLabel, 0, 2);
        grid.add(targetBox, 1, 2);

        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(btn -> {
            if (btn == saveBtn) {
                selected.setTitle(titleField.getText());
                selected.setContent(contentArea.getText());
                selected.setTargetRole(targetBox.getValue());
                return selected;
            }
            return null;
        });

        Optional<Notice> result = dialog.showAndWait();
        result.ifPresent(n -> {
            if (noticeDAO.update(n)) {
                table.setItems(FXCollections.observableArrayList(noticeDAO.getAll()));
            } else {
                showAlert("Error", "Failed to update notice.");
            }
        });
    }

    // ============================================
    //  LOGOUT
    // ============================================
    @FXML
    public void handleLogout(ActionEvent event) {
        SceneManager.logout();
    }

    // ============================================
    //  HELPER METHODS
    // ============================================
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

    private HBox createActionButtons(javafx.event.EventHandler<ActionEvent> onAdd,
                                      javafx.event.EventHandler<ActionEvent> onEdit,
                                      javafx.event.EventHandler<ActionEvent> onDelete) {
        HBox actions = new HBox(10);
        actions.setAlignment(Pos.CENTER_LEFT);

        Button addBtn = new Button("➕ Add");
        addBtn.getStyleClass().addAll("action-btn", "add-btn");
        addBtn.setOnAction(onAdd);

        Button editBtn = new Button("✏️ Edit");
        editBtn.getStyleClass().addAll("action-btn", "edit-btn");
        editBtn.setOnAction(onEdit);

        Button deleteBtn = new Button("🗑️ Delete");
        deleteBtn.getStyleClass().addAll("action-btn", "delete-btn");
        deleteBtn.setOnAction(onDelete);

        actions.getChildren().addAll(addBtn, editBtn, deleteBtn);
        return actions;
    }

    @FunctionalInterface
    interface DeleteAction { boolean delete(Object item); }

    private <T> void deleteEntity(TableView<T> table, String entityName, DeleteAction deleteAction, Runnable refresh) {
        T selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) { showAlert("Warning", "Please select a " + entityName.toLowerCase() + " to delete."); return; }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Delete " + entityName);
        confirm.setContentText("Are you sure you want to delete this " + entityName.toLowerCase() + "?");
        confirm.getDialogPane().setStyle("-fx-background-color: #1e293b;");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (deleteAction.delete(selected)) {
                refresh.run();
            } else {
                showAlert("Error", "Failed to delete " + entityName.toLowerCase() + ". It may have related records.");
            }
        }
    }

    private GridPane createFormGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(12);
        grid.setPadding(new Insets(20));
        return grid;
    }

    private TextField addFormField(GridPane grid, String labelText, int row) {
        Label label = new Label(labelText);
        label.setStyle("-fx-text-fill: #cbd5e1; -fx-font-size: 13px;");
        TextField field = new TextField();
        field.setStyle("-fx-background-color: #0f172a; -fx-text-fill: #f8fafc; -fx-border-color: rgba(255,255,255,0.1); -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 8;");
        field.setPrefWidth(300);
        grid.add(label, 0, row);
        grid.add(field, 1, row);
        return field;
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

    @FXML
    public void showExams(ActionEvent event) {
        setActiveButton(btnExams);
        VBox view = createManagementView("Exams");

        TableView<Exam> table = new TableView<>();
        table.getStyleClass().add("data-table");

        TableColumn<Exam, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("examId"));
        colId.setPrefWidth(50);

        TableColumn<Exam, String> colName = new TableColumn<>("Exam Name");
        colName.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("examName"));

        TableColumn<Exam, String> colSubject = new TableColumn<>("Subject");
        colSubject.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("subject"));

        TableColumn<Exam, Date> colDate = new TableColumn<>("Date");
        colDate.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("examDate"));

        TableColumn<Exam, Double> colMarks = new TableColumn<>("Total Marks");
        colMarks.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("totalMarks"));

        TableColumn<Exam, Integer> colBatch = new TableColumn<>("Batch ID");
        colBatch.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("batchId"));

        table.getColumns().addAll(colId, colName, colSubject, colDate, colMarks, colBatch);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(table, Priority.ALWAYS);

        Runnable refresh = () -> table.setItems(FXCollections.observableArrayList(examDAO.getAll()));
        refresh.run();

        HBox toolbar = new HBox(10);
        toolbar.setAlignment(Pos.CENTER_LEFT);

        Button deleteBtn = new Button("🗑️ Delete");
        deleteBtn.getStyleClass().addAll("action-btn", "delete-btn");
        deleteBtn.setOnAction(e -> {
            Exam selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) { showAlert("Error", "Please select an exam to delete."); return; }
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Delete Exam");
            confirm.setHeaderText("Delete " + selected.getExamName());
            confirm.setContentText("This will also delete all results for this exam. Are you sure?");
            confirm.getDialogPane().setStyle("-fx-background-color: #1e293b;");
            confirm.showAndWait().ifPresent(bt -> {
                if (bt == ButtonType.OK) {
                    examDAO.delete(selected.getExamId());
                    refresh.run();
                }
            });
        });

        toolbar.getChildren().add(deleteBtn);
        view.getChildren().addAll(toolbar, table);
        setContent(view);
    }

    @FXML
    public void showResults(ActionEvent event) {
        setActiveButton(btnResults);
        VBox view = createManagementView("All Results");

        // Batch filter
        HBox filterBar = new HBox(12);
        filterBar.setAlignment(Pos.CENTER_LEFT);
        Label batchLbl = new Label("Filter by Batch:");
        batchLbl.setStyle("-fx-text-fill: #cbd5e1; -fx-font-size: 13px;");
        ComboBox<Batch> batchBox = new ComboBox<>();
        batchBox.setItems(FXCollections.observableArrayList(batchDAO.getAll()));
        batchBox.setPromptText("All Batches");
        batchBox.setStyle("-fx-background-color: #0f172a; -fx-text-fill: #f8fafc;");
        filterBar.getChildren().addAll(batchLbl, batchBox);

        TableView<Result> table = new TableView<>();
        table.getStyleClass().add("data-table");

        TableColumn<Result, Integer> colStudent = new TableColumn<>("Student ID");
        colStudent.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("studentId"));

        TableColumn<Result, Integer> colExam = new TableColumn<>("Exam ID");
        colExam.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("examId"));

        TableColumn<Result, Double> colMarks = new TableColumn<>("Marks Obtained");
        colMarks.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("marksObtained"));

        TableColumn<Result, String> colGrade = new TableColumn<>("Grade");
        colGrade.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("grade"));

        table.getColumns().addAll(colStudent, colExam, colMarks, colGrade);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(table, Priority.ALWAYS);

        ResultDAO resultDAO = new ResultDAO();

        // Load all results initially
        table.setItems(FXCollections.observableArrayList(resultDAO.getAll()));

        batchBox.setOnAction(e -> {
            Batch selectedBatch = batchBox.getValue();
            if (selectedBatch == null) {
                table.setItems(FXCollections.observableArrayList(resultDAO.getAll()));
            } else {
                // Filter: get students in batch, then their results
                java.util.List<Result> filtered = new java.util.ArrayList<>();
                for (Student s : studentDAO.getByBatchId(selectedBatch.getBatchId())) {
                    filtered.addAll(resultDAO.getByStudentId(s.getStudentId()));
                }
                table.setItems(FXCollections.observableArrayList(filtered));
            }
        });

        view.getChildren().addAll(filterBar, table);
        setContent(view);
    }

}
