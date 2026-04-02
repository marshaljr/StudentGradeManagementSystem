package com.sms.controller;

import com.sms.dao.CourseDAO;
import com.sms.dao.UserDAO;
import com.sms.model.*;
import com.sms.util.AlertUtil;
import com.sms.util.NavigationUtil;
import com.sms.util.SessionManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

import java.time.LocalDate;
import java.util.List;

/**
 * AdminDashboardController — Master controller for the Admin dashboard.
 *
 * Uses a single BorderPane layout; clicking sidebar buttons swaps out
 * the content in subContent VBox (similar to single-page application routing).
 */
public class AdminDashboardController {

    // ── Injected FXML fields ───────────────────────────────────────
    @FXML private Label userLabel;
    @FXML private Label pageTitle;
    @FXML private Label statStudents;
    @FXML private Label statTeachers;
    @FXML private Label statCourses;
    @FXML private Label statStudentsTrend;
    @FXML private Label statTeachersTrend;
    @FXML private Label statCoursesTrend;
    @FXML private HBox  statsRow;
    @FXML private VBox  subContent;
    @FXML private VBox  sidebarPane;
    @FXML private Button sidebarToggleBtn;
    @FXML private Button btnThemeToggle;
    @FXML private Label breadcrumbCurrent;
    @FXML private BorderPane mainRoot;  // For accessing root for dark mode styling

    // Sidebar buttons for active state tracking
    @FXML private Button btnDashboard;
    @FXML private Button btnStudents;
    @FXML private Button btnTeachers;
    @FXML private Button btnCourses;
    @FXML private Button btnReports;

    private final UserDAO   userDAO   = new UserDAO();
    private final CourseDAO courseDAO = new CourseDAO();
    private boolean sidebarExpanded = true;  // Track sidebar state
    private boolean darkModeEnabled = false;  // Track dark mode state

    @FXML
    public void initialize() {
        UserAccount user = SessionManager.getCurrentUser();
        userLabel.setText(user.getFullName() + "  |  " + user.getRoleDisplayName());
        showDashboard();
    }

    // ═══════════════════════════════════════════════════════════
    // RESPONSIVE SIDEBAR & NAVIGATION
    // ═══════════════════════════════════════════════════════════

    /**
     * Toggles sidebar visibility for mobile/tablet responsiveness
     */
    @FXML
    private void toggleSidebar() {
        sidebarExpanded = !sidebarExpanded;
        if (sidebarExpanded) {
            sidebarPane.getStyleClass().remove("sidebar-hidden");
            sidebarPane.getStyleClass().add("sidebar-expanded");
        } else {
            sidebarPane.getStyleClass().remove("sidebar-expanded");
            sidebarPane.getStyleClass().add("sidebar-hidden");
        }
    }

    /**
     * Toggles dark mode theme for the entire application
     */
    @FXML
    private void toggleDarkMode() {
        darkModeEnabled = !darkModeEnabled;
        if (mainRoot != null) {
            if (darkModeEnabled) {
                mainRoot.getStyleClass().add("dark-mode");
                btnThemeToggle.setText("☀️");  // Show sun when in dark mode
            } else {
                mainRoot.getStyleClass().remove("dark-mode");
                btnThemeToggle.setText("🌙");  // Show moon when in light mode
            }
        }
    }

    /**
     * Sets the active navigation button style and updates breadcrumb
     */
    private void setActiveNavButton(Button activeButton, String pageName) {
        // Remove active style from all buttons
        for (Button btn : new Button[]{btnDashboard, btnStudents, btnTeachers, btnCourses, btnReports}) {
            btn.getStyleClass().remove("sidebar-btn-active");
            btn.getStyleClass().add("sidebar-btn");
        }
        // Set active style on the current button
        activeButton.getStyleClass().remove("sidebar-btn");
        activeButton.getStyleClass().add("sidebar-btn-active");
        
        // Update breadcrumb
        if (breadcrumbCurrent != null) {
            breadcrumbCurrent.setText(pageName);
        }
    }

    @FXML public void handleLogout() {
        NavigationUtil.logout();
    }

    @FXML public void showDashboard() {
        setActiveNavButton(btnDashboard, "Dashboard");
        pageTitle.setText("Dashboard");
        statsRow.setVisible(true); statsRow.setManaged(true);
        refreshStats();
        subContent.getChildren().clear();
    }

    @FXML public void showStudents() {
        setActiveNavButton(btnStudents, "Students");
        pageTitle.setText("Student Management");
        statsRow.setVisible(false); statsRow.setManaged(false);
        subContent.getChildren().clear();
        subContent.getChildren().add(buildStudentPanel());
    }

    @FXML public void showTeachers() {
        setActiveNavButton(btnTeachers, "Teachers");
        pageTitle.setText("Teacher Management");
        statsRow.setVisible(false); statsRow.setManaged(false);
        subContent.getChildren().clear();
        subContent.getChildren().add(buildTeacherPanel());
    }

    @FXML public void showCourses() {
        setActiveNavButton(btnCourses, "Courses");
        pageTitle.setText("Course Management");
        statsRow.setVisible(false); statsRow.setManaged(false);
        subContent.getChildren().clear();
        subContent.getChildren().add(buildCoursePanel());
    }

    @FXML public void showReports() {
        setActiveNavButton(btnReports, "Reports");
        pageTitle.setText("Reports");
        statsRow.setVisible(false); statsRow.setManaged(false);
        subContent.getChildren().clear();
        subContent.getChildren().add(buildReportsPanel());
    }

    // ═══════════════════════════════════════════════════════════
    // STATS
    // ═══════════════════════════════════════════════════════════

    private void refreshStats() {
        int[] counts = userDAO.getDashboardCounts();
        statStudents.setText(String.valueOf(counts[0]));
        statTeachers.setText(String.valueOf(counts[1]));
        statCourses.setText(String.valueOf(counts[2]));
        
        // Add trend indicators (for demo: showing positive trends)
        // In production, compare with previous period data
        if (statStudentsTrend != null) {
            statStudentsTrend.setText("↑ 12% this semester");
            statStudentsTrend.getStyleClass().add("stat-trend-up");
        }
        if (statTeachersTrend != null) {
            statTeachersTrend.setText("↑ 5% active");
            statTeachersTrend.getStyleClass().add("stat-trend-up");
        }
        if (statCoursesTrend != null) {
            statCoursesTrend.setText("↑ 8% offered");
            statCoursesTrend.getStyleClass().add("stat-trend-up");
        }
    }

    // ═══════════════════════════════════════════════════════════
    // STUDENT PANEL
    // ═══════════════════════════════════════════════════════════

    @SuppressWarnings("unchecked")
    private VBox buildStudentPanel() {
        VBox panel = new VBox(12);

        // Search bar + Add button row
        HBox toolbar = new HBox(10);
        TextField searchField = new TextField();
        searchField.setPromptText("Search students...");
        searchField.setPrefWidth(250);
        Button addBtn = new Button("+ Add Student");
        addBtn.getStyleClass().add("btn-primary");
        toolbar.getChildren().addAll(searchField, addBtn);

        // TableView
        TableView<StudentUser> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(400);

        TableColumn<StudentUser, String> colNum  = new TableColumn<>("Student No.");
        colNum.setCellValueFactory(new PropertyValueFactory<>("studentNumber"));

        TableColumn<StudentUser, String> colName = new TableColumn<>("Full Name");
        colName.setCellValueFactory(new PropertyValueFactory<>("fullName"));

        TableColumn<StudentUser, String> colProg = new TableColumn<>("Program");
        colProg.setCellValueFactory(new PropertyValueFactory<>("program"));

        TableColumn<StudentUser, String> colYr   = new TableColumn<>("Year");
        colYr.setCellValueFactory(c ->
            new SimpleStringProperty(String.valueOf(c.getValue().getYearLevel())));

        TableColumn<StudentUser, String> colEmail = new TableColumn<>("Email");
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));

        TableColumn<StudentUser, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(c ->
            new SimpleStringProperty(c.getValue().isActive() ? "Active" : "Inactive"));

        TableColumn<StudentUser, Void> colActions = new TableColumn<>("Actions");
        colActions.setCellFactory(col -> new TableCell<>() {
            final Button viewBtn   = new Button("View");
            final Button editBtn   = new Button("Edit");
            final Button deleteBtn = new Button("Delete");
            {
                viewBtn.getStyleClass().add("btn-small-secondary");
                editBtn.getStyleClass().add("btn-small-secondary");
                deleteBtn.getStyleClass().add("btn-small-danger");
                viewBtn.setOnAction(e -> {
                    StudentUser s = getTableView().getItems().get(getIndex());
                    showStudentDialog(s, table, true);
                });
                editBtn.setOnAction(e -> {
                    StudentUser s = getTableView().getItems().get(getIndex());
                    showStudentDialog(s, table, false);
                });
                deleteBtn.setOnAction(e -> {
                    StudentUser s = getTableView().getItems().get(getIndex());
                    if (AlertUtil.showConfirm("Delete Student",
                            "Delete " + s.getFullName() + "? This cannot be undone.")) {
                        if (userDAO.deleteStudent(s.getUserId())) {
                            table.getItems().remove(s);
                            AlertUtil.showInfo("Deleted", "Student removed successfully.");
                        } else {
                            AlertUtil.showError("Error", "Could not delete student.");
                        }
                    }
                });
            }
            @Override protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                if (empty) { setGraphic(null); return; }
                HBox box = new HBox(6, viewBtn, editBtn, deleteBtn);
                setGraphic(box);
            }
        });

        table.getColumns().addAll(colNum, colName, colProg, colYr, colEmail, colStatus, colActions);

        List<StudentUser> students = userDAO.getAllStudents();
        table.setItems(FXCollections.observableArrayList(students));

        // Search filter
        searchField.textProperty().addListener((obs, old, txt) -> {
            if (txt.isBlank()) {
                table.setItems(FXCollections.observableArrayList(students));
            } else {
                String lower = txt.toLowerCase();
                table.setItems(FXCollections.observableArrayList(
                    students.stream().filter(s ->
                        s.getFullName().toLowerCase().contains(lower) ||
                        s.getStudentNumber().toLowerCase().contains(lower)
                    ).toList()
                ));
            }
        });

        addBtn.setOnAction(e -> showStudentDialog(null, table, false));

        panel.getChildren().addAll(toolbar, table);
        return panel;
    }

    /** Shows Add/Edit student dialog. Pass null for new student. */
    private void showStudentDialog(StudentUser existing, TableView<StudentUser> table, boolean isViewOnly) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(isViewOnly ? "View Student" : (existing == null ? "Add New Student" : "Edit Student"));
        dialog.setHeaderText(null);

        GridPane form = new GridPane();
        form.setHgap(12); form.setVgap(10);
        form.setPadding(new Insets(20));

        TextField tfFullName   = new TextField(existing != null ? existing.getFullName()     : "");
        TextField tfUsername   = new TextField(existing != null ? existing.getUsername()      : "");
        TextField tfStuNo      = new TextField(existing != null ? existing.getStudentNumber() : "");
        TextField tfEmail      = new TextField(existing != null ? existing.getEmail()         : "");
        TextField tfPhone      = new TextField(existing != null ? existing.getPhone()         : "");
        TextField tfProgram    = new TextField(existing != null ? existing.getProgram()       : "");
        TextField tfPassword   = new TextField(existing == null ? "password123" : "");
        ComboBox<String> cbGender = new ComboBox<>(
            FXCollections.observableArrayList("Male","Female","Other"));
        if (existing != null) cbGender.setValue(existing.getGender());

        if (existing != null || isViewOnly) tfUsername.setDisable(true);
        tfStuNo.setDisable(existing != null || isViewOnly);
        
        tfFullName.setDisable(isViewOnly);
        tfEmail.setDisable(isViewOnly);
        tfPhone.setDisable(isViewOnly);
        tfProgram.setDisable(isViewOnly);
        cbGender.setDisable(isViewOnly);

        form.addRow(0, new Label("Full Name:"),   tfFullName);
        form.addRow(1, new Label("Username:"),    tfUsername);
        if (existing == null)
        form.addRow(2, new Label("Password:"),    tfPassword);
        form.addRow(3, new Label("Student No:"),  tfStuNo);
        form.addRow(4, new Label("Email:"),       tfEmail);
        form.addRow(5, new Label("Phone:"),       tfPhone);
        form.addRow(6, new Label("Program:"),     tfProgram);
        form.addRow(7, new Label("Gender:"),      cbGender);

        dialog.getDialogPane().setContent(form);
        if (isViewOnly) {
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CLOSE);
        } else {
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        }
        dialog.getDialogPane().getStylesheets().add(
            getClass().getResource("/css/styles.css").toExternalForm());

        dialog.showAndWait().ifPresent(bt -> {
            if (bt != ButtonType.OK) return;
            if (tfFullName.getText().isBlank() || tfStuNo.getText().isBlank()) {
                AlertUtil.showError("Validation", "Full Name and Student Number are required.");
                return;
            }
            if (existing == null) {
                StudentUser s = new StudentUser();
                s.setFullName(tfFullName.getText().trim());
                s.setUsername(tfUsername.getText().trim());
                s.setStudentNumber(tfStuNo.getText().trim());
                s.setEmail(tfEmail.getText().trim());
                s.setPhone(tfPhone.getText().trim());
                s.setProgram(tfProgram.getText().trim());
                s.setGender(cbGender.getValue());
                s.setEnrollmentYear(LocalDate.now().getYear());
                s.setYearLevel(1);
                s.setActive(true);
                if (userDAO.addStudent(s, tfPassword.getText())) {
                    AlertUtil.showInfo("Success", "Student added successfully.");
                    table.setItems(FXCollections.observableArrayList(userDAO.getAllStudents()));
                } else {
                    AlertUtil.showError("Error", "Could not add student. Username or Student Number may already exist.");
                }
            } else {
                existing.setFullName(tfFullName.getText().trim());
                existing.setStudentNumber(tfStuNo.getText().trim());
                existing.setEmail(tfEmail.getText().trim());
                existing.setPhone(tfPhone.getText().trim());
                existing.setProgram(tfProgram.getText().trim());
                existing.setGender(cbGender.getValue());
                if (userDAO.updateStudent(existing)) {
                    AlertUtil.showInfo("Success", "Student updated successfully.");
                    table.setItems(FXCollections.observableArrayList(userDAO.getAllStudents()));
                } else {
                    AlertUtil.showError("Error", "Could not update student.");
                }
            }
        });
    }

    // ═══════════════════════════════════════════════════════════
    // TEACHER PANEL
    // ═══════════════════════════════════════════════════════════

    @SuppressWarnings("unchecked")
    private VBox buildTeacherPanel() {
        VBox panel = new VBox(12);

        HBox toolbar = new HBox(10);
        Button addBtn = new Button("+ Add Teacher");
        addBtn.getStyleClass().add("btn-primary");
        toolbar.getChildren().add(addBtn);

        TableView<TeacherUser> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(400);

        TableColumn<TeacherUser, String> colEmp  = new TableColumn<>("Employee ID");
        colEmp.setCellValueFactory(new PropertyValueFactory<>("employeeId"));
        TableColumn<TeacherUser, String> colName = new TableColumn<>("Full Name");
        colName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        TableColumn<TeacherUser, String> colDept = new TableColumn<>("Department");
        colDept.setCellValueFactory(new PropertyValueFactory<>("department"));
        TableColumn<TeacherUser, String> colQual = new TableColumn<>("Qualification");
        colQual.setCellValueFactory(new PropertyValueFactory<>("qualification"));
        TableColumn<TeacherUser, String> colEmail= new TableColumn<>("Email");
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));

        table.getColumns().addAll(colEmp, colName, colDept, colQual, colEmail);
        table.setItems(FXCollections.observableArrayList(userDAO.getAllTeachers()));

        addBtn.setOnAction(e -> showTeacherDialog(table));

        panel.getChildren().addAll(toolbar, table);
        return panel;
    }

    private void showTeacherDialog(TableView<TeacherUser> table) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add New Teacher");
        GridPane form = new GridPane();
        form.setHgap(12); form.setVgap(10); form.setPadding(new Insets(20));

        TextField tfName   = new TextField();
        TextField tfUser   = new TextField();
        TextField tfPass   = new TextField("password123");
        TextField tfEmpId  = new TextField();
        TextField tfDept   = new TextField();
        TextField tfQual   = new TextField();
        TextField tfEmail  = new TextField();

        form.addRow(0, new Label("Full Name:"),    tfName);
        form.addRow(1, new Label("Username:"),     tfUser);
        form.addRow(2, new Label("Password:"),     tfPass);
        form.addRow(3, new Label("Employee ID:"),  tfEmpId);
        form.addRow(4, new Label("Department:"),   tfDept);
        form.addRow(5, new Label("Qualification:"),tfQual);
        form.addRow(6, new Label("Email:"),        tfEmail);

        dialog.getDialogPane().setContent(form);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.getDialogPane().getStylesheets().add(
            getClass().getResource("/css/styles.css").toExternalForm());

        dialog.showAndWait().ifPresent(bt -> {
            if (bt != ButtonType.OK) return;
            TeacherUser t = new TeacherUser();
            t.setFullName(tfName.getText().trim());
            t.setUsername(tfUser.getText().trim());
            t.setEmployeeId(tfEmpId.getText().trim());
            t.setDepartment(tfDept.getText().trim());
            t.setQualification(tfQual.getText().trim());
            t.setEmail(tfEmail.getText().trim());
            t.setHireDate(LocalDate.now());
            if (userDAO.addTeacher(t, tfPass.getText())) {
                AlertUtil.showInfo("Success", "Teacher added.");
                table.setItems(FXCollections.observableArrayList(userDAO.getAllTeachers()));
            } else {
                AlertUtil.showError("Error", "Could not add teacher.");
            }
        });
    }

    // ═══════════════════════════════════════════════════════════
    // COURSE PANEL
    // ═══════════════════════════════════════════════════════════

    @SuppressWarnings("unchecked")
    private VBox buildCoursePanel() {
        VBox panel = new VBox(12);

        HBox toolbar = new HBox(10);
        Button addBtn = new Button("+ Add Course");
        addBtn.getStyleClass().add("btn-primary");
        toolbar.getChildren().add(addBtn);

        TableView<Course> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(400);

        TableColumn<Course, String> colCode = new TableColumn<>("Code");
        colCode.setCellValueFactory(new PropertyValueFactory<>("courseCode"));
        TableColumn<Course, String> colName = new TableColumn<>("Course Name");
        colName.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        TableColumn<Course, String> colCred = new TableColumn<>("Credits");
        colCred.setCellValueFactory(c ->
            new SimpleStringProperty(String.valueOf(c.getValue().getCredits())));
        TableColumn<Course, String> colTeach= new TableColumn<>("Teacher");
        colTeach.setCellValueFactory(new PropertyValueFactory<>("teacherName"));
        TableColumn<Course, String> colSem  = new TableColumn<>("Semester");
        colSem.setCellValueFactory(new PropertyValueFactory<>("semester"));
        TableColumn<Course, String> colStatus= new TableColumn<>("Status");
        colStatus.setCellValueFactory(c ->
            new SimpleStringProperty(c.getValue().isActive() ? "Active" : "Inactive"));

        TableColumn<Course, Void> colActions = new TableColumn<>("Actions");
        colActions.setCellFactory(col -> new TableCell<>() {
            final Button viewBtn   = new Button("View");
            final Button editBtn   = new Button("Edit");
            final Button deleteBtn = new Button("Delete");
            {
                viewBtn.getStyleClass().add("btn-small-secondary");
                editBtn.getStyleClass().add("btn-small-secondary");
                deleteBtn.getStyleClass().add("btn-small-danger");
                viewBtn.setOnAction(e -> {
                    Course c = getTableView().getItems().get(getIndex());
                    showCourseDialog(c, table, true);
                });
                editBtn.setOnAction(e -> {
                    Course c = getTableView().getItems().get(getIndex());
                    showCourseDialog(c, table, false);
                });
                deleteBtn.setOnAction(e -> {
                    Course c = getTableView().getItems().get(getIndex());
                    if (AlertUtil.showConfirm("Delete Course",
                            "Delete " + c.getCourseName() + "?")) {
                        if (courseDAO.deleteCourse(c.getCourseId())) {
                            table.getItems().remove(c);
                        } else {
                            AlertUtil.showError("Error", "Could not delete course.");
                        }
                    }
                });
            }
            @Override protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                if (empty) { setGraphic(null); return; }
                setGraphic(new HBox(6, viewBtn, editBtn, deleteBtn));
            }
        });

        table.getColumns().addAll(colCode, colName, colCred, colTeach, colSem, colStatus, colActions);
        table.setItems(FXCollections.observableArrayList(courseDAO.getAllCourses()));

        addBtn.setOnAction(e -> showCourseDialog(null, table, false));

        panel.getChildren().addAll(toolbar, table);
        return panel;
    }

    private void showCourseDialog(Course existing, TableView<Course> table, boolean isViewOnly) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(isViewOnly ? "View Course" : (existing == null ? "Add Course" : "Edit Course"));
        GridPane form = new GridPane();
        form.setHgap(12); form.setVgap(10); form.setPadding(new Insets(20));

        TextField tfCode   = new TextField(existing != null ? existing.getCourseCode()   : "");
        TextField tfName   = new TextField(existing != null ? existing.getCourseName()   : "");
        TextField tfDesc   = new TextField(existing != null ? existing.getDescription()  : "");
        TextField tfCred   = new TextField(existing != null ? String.valueOf(existing.getCredits()) : "3");
        TextField tfSem    = new TextField(existing != null ? existing.getSemester()     : "");
        TextField tfYear   = new TextField(existing != null ? existing.getAcademicYear() : "2024-2025");
        TextField tfMax    = new TextField(existing != null ? String.valueOf(existing.getMaxStudents()) : "40");

        List<TeacherUser> teachers = userDAO.getAllTeachers();
        ComboBox<TeacherUser> cbTeacher = new ComboBox<>(
            FXCollections.observableArrayList(teachers));
        if (existing != null) {
            teachers.stream().filter(t -> t.getTeacherId() == existing.getTeacherId())
                    .findFirst().ifPresent(cbTeacher::setValue);
        }

        tfCode.setDisable(existing != null || isViewOnly);
        tfName.setDisable(isViewOnly);
        tfDesc.setDisable(isViewOnly);
        tfCred.setDisable(isViewOnly);
        tfSem.setDisable(isViewOnly);
        tfYear.setDisable(isViewOnly);
        tfMax.setDisable(isViewOnly);
        cbTeacher.setDisable(isViewOnly);

        ComboBox<String> cbScale = new ComboBox<>(FXCollections.observableArrayList("STANDARD", "STRICT", "GENEROUS"));
        cbScale.setValue(existing != null ? existing.getGradingScale() : "STANDARD");
        cbScale.setDisable(isViewOnly);

        form.addRow(0, new Label("Course Code:"),  tfCode);
        form.addRow(1, new Label("Course Name:"),  tfName);
        form.addRow(2, new Label("Description:"),  tfDesc);
        form.addRow(3, new Label("Credits:"),      tfCred);
        form.addRow(4, new Label("Teacher:"),      cbTeacher);
        form.addRow(5, new Label("Semester:"),     tfSem);
        form.addRow(6, new Label("Academic Year:"),tfYear);
        form.addRow(7, new Label("Max Students:"), tfMax);
        form.addRow(8, new Label("Grading Scale:"),cbScale);

        dialog.getDialogPane().setContent(form);
        if (isViewOnly) {
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CLOSE);
        } else {
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        }
        dialog.getDialogPane().getStylesheets().add(
            getClass().getResource("/css/styles.css").toExternalForm());

        dialog.showAndWait().ifPresent(bt -> {
            if (bt != ButtonType.OK) return;
            Course c = existing != null ? existing : new Course();
            c.setCourseCode(tfCode.getText().trim());
            c.setCourseName(tfName.getText().trim());
            c.setDescription(tfDesc.getText().trim());
            try { c.setCredits(Integer.parseInt(tfCred.getText().trim())); }
            catch (NumberFormatException ex) { c.setCredits(3); }
            TeacherUser sel = cbTeacher.getValue();
            c.setTeacherId(sel != null ? sel.getTeacherId() : 0);
            c.setSemester(tfSem.getText().trim());
            c.setAcademicYear(tfYear.getText().trim());
            try { c.setMaxStudents(Integer.parseInt(tfMax.getText().trim())); }
            catch (NumberFormatException ex) { c.setMaxStudents(40); }
            c.setGradingScale(cbScale.getValue());
            c.setActive(true);

            boolean ok = existing == null ? courseDAO.addCourse(c) : courseDAO.updateCourse(c);
            if (ok) {
                AlertUtil.showInfo("Success", "Course saved.");
                table.setItems(FXCollections.observableArrayList(courseDAO.getAllCourses()));
            } else {
                AlertUtil.showError("Error", "Could not save course.");
            }
        });
    }

    // ═══════════════════════════════════════════════════════════
    // REPORTS PANEL
    // ═══════════════════════════════════════════════════════════

    private VBox buildReportsPanel() {
        VBox panel = new VBox(16);
        Label info = new Label("Select a student to generate their transcript as PDF, " +
                "or select a course to export grades or attendance as CSV.");
        info.setWrapText(true);
        info.getStyleClass().add("hint-text");

        // Student transcript section
        Label lbl1 = new Label("Student Transcript (PDF)");
        lbl1.getStyleClass().add("section-title");

        List<StudentUser> students = userDAO.getAllStudents();
        ComboBox<StudentUser> cbStudent = new ComboBox<>(
            FXCollections.observableArrayList(students));
        cbStudent.setPromptText("Select Student...");
        cbStudent.setPrefWidth(280);

        Button btnPDF = new Button("Generate PDF Transcript");
        btnPDF.getStyleClass().add("btn-primary");
        btnPDF.setOnAction(e -> {
            StudentUser s = cbStudent.getValue();
            if (s == null) { AlertUtil.showWarning("Select Student", "Please select a student."); return; }
            javafx.stage.FileChooser fc = new javafx.stage.FileChooser();
            fc.setTitle("Save Transcript");
            fc.setInitialFileName(s.getStudentNumber() + "_transcript.pdf");
            fc.getExtensionFilters().add(new javafx.stage.FileChooser.ExtensionFilter("PDF","*.pdf"));
            java.io.File file = fc.showSaveDialog(com.sms.MainApp.primaryStage);
            if (file != null) {
                com.sms.util.ReportGenerator rg = new com.sms.util.ReportGenerator();
                if (rg.generateTranscript(s, file.getAbsolutePath())) {
                    AlertUtil.showInfo("Done", "Transcript saved to:\n" + file.getAbsolutePath());
                } else {
                    AlertUtil.showError("Error", "Failed to generate PDF.");
                }
            }
        });

        // Course grades CSV section
        Label lbl2 = new Label("Course Grades Export (CSV)");
        lbl2.getStyleClass().add("section-title");

        List<Course> courses = courseDAO.getAllCourses();
        ComboBox<Course> cbCourse = new ComboBox<>(
            FXCollections.observableArrayList(courses));
        cbCourse.setPromptText("Select Course...");
        cbCourse.setPrefWidth(280);

        Button btnCSV = new Button("Export Grades as CSV");
        btnCSV.getStyleClass().add("btn-secondary");
        btnCSV.setOnAction(e -> {
            Course c = cbCourse.getValue();
            if (c == null) { AlertUtil.showWarning("Select Course","Please select a course."); return; }
            javafx.stage.FileChooser fc = new javafx.stage.FileChooser();
            fc.setTitle("Save CSV");
            fc.setInitialFileName(c.getCourseCode() + "_grades.csv");
            fc.getExtensionFilters().add(new javafx.stage.FileChooser.ExtensionFilter("CSV","*.csv"));
            java.io.File file = fc.showSaveDialog(com.sms.MainApp.primaryStage);
            if (file != null) {
                com.sms.util.ReportGenerator rg = new com.sms.util.ReportGenerator();
                if (rg.exportGradesToCSV(c, file.getAbsolutePath())) {
                    AlertUtil.showInfo("Done", "Grades exported to:\n" + file.getAbsolutePath());
                } else {
                    AlertUtil.showError("Error", "Failed to export CSV.");
                }
            }
        });

        Button btnAttCSV = new Button("Export Attendance as CSV");
        btnAttCSV.getStyleClass().add("btn-secondary");
        btnAttCSV.setOnAction(e -> {
            Course c = cbCourse.getValue();
            if (c == null) { AlertUtil.showWarning("Select Course","Please select a course."); return; }
            javafx.stage.FileChooser fc = new javafx.stage.FileChooser();
            fc.setTitle("Save Attendance CSV");
            fc.setInitialFileName(c.getCourseCode() + "_attendance.csv");
            fc.getExtensionFilters().add(new javafx.stage.FileChooser.ExtensionFilter("CSV","*.csv"));
            java.io.File file = fc.showSaveDialog(com.sms.MainApp.primaryStage);
            if (file != null) {
                com.sms.util.ReportGenerator rg = new com.sms.util.ReportGenerator();
                if (rg.exportAttendanceToCSV(c, file.getAbsolutePath())) {
                    AlertUtil.showInfo("Done", "Attendance exported.");
                } else {
                    AlertUtil.showError("Error", "Failed to export.");
                }
            }
        });

        HBox row1 = new HBox(10, cbStudent, btnPDF);
        HBox row2 = new HBox(10, cbCourse, btnCSV, btnAttCSV);

        panel.getChildren().addAll(info, lbl1, row1, new Separator(), lbl2, row2);
        return panel;
    }
}
