package com.sms.controller;

import com.sms.dao.AttendanceDAO;
import com.sms.dao.CourseDAO;
import com.sms.dao.GradeDAO;
import com.sms.model.*;
import com.sms.util.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.Clipboard;

import java.time.LocalDate;
import java.util.*;

/**
 * TeacherDashboardController — Handles all teacher-side screens:
 *   - View assigned courses
 *   - Create/manage assessments
 *   - Enter and update grades (with auto letter grade calculation)
 *   - Record daily attendance
 *   - View class performance reports
 */
public class TeacherDashboardController {

    @FXML private Label userLabel;
    @FXML private Label pageTitle;
    @FXML private VBox  subContent;

    private final CourseDAO     courseDAO     = new CourseDAO();
    private final GradeDAO      gradeDAO      = new GradeDAO();
    private final AttendanceDAO attendanceDAO = new AttendanceDAO();

    private TeacherUser currentTeacher;

    @FXML
    public void initialize() {
        UserAccount user = SessionManager.getCurrentUser();
        userLabel.setText(user.getFullName() + "  |  Teacher");
        currentTeacher = (TeacherUser) user;
        showDashboard();
    }

    @FXML public void handleLogout() { NavigationUtil.logout(); }

    @FXML public void showDashboard() {
        pageTitle.setText("My Dashboard");
        subContent.getChildren().clear();

        List<Course> courses = courseDAO.getCoursesByTeacher(currentTeacher.getTeacherId());
        Label lbl = new Label("You are teaching " + courses.size() + " course(s) this semester.");
        lbl.getStyleClass().add("hint-text");
        subContent.getChildren().add(lbl);

        for (Course c : courses) {
            VBox card = new VBox(6);
            card.getStyleClass().add("course-card");
            card.setPadding(new Insets(14));
            Label title = new Label(c.getCourseCode() + " — " + c.getCourseName());
            title.getStyleClass().add("section-title");
            Label sub = new Label(c.getSemester() + " | " + c.getAcademicYear()
                    + " | " + c.getCredits() + " credits");
            sub.getStyleClass().add("hint-text");
            card.getChildren().addAll(title, sub);
            subContent.getChildren().add(card);
        }
    }

    @FXML public void showCourses() {
        pageTitle.setText("My Courses");
        subContent.getChildren().clear();
        subContent.getChildren().add(buildCourseList());
    }

    @FXML public void showAssessments() {
        pageTitle.setText("Assessments");
        subContent.getChildren().clear();
        subContent.getChildren().add(buildAssessmentPanel());
    }

    @FXML public void showGradeEntry() {
        pageTitle.setText("Grade Entry");
        subContent.getChildren().clear();
        subContent.getChildren().add(buildGradeEntryPanel());
    }

    @FXML public void showAttendance() {
        pageTitle.setText("Attendance");
        subContent.getChildren().clear();
        subContent.getChildren().add(buildAttendancePanel());
    }

    @FXML public void showReports() {
        pageTitle.setText("Class Reports");
        subContent.getChildren().clear();
        subContent.getChildren().add(buildReportsPanel());
    }

    // ═══════════════════════════════════════════════════════════
    // COURSE LIST
    // ═══════════════════════════════════════════════════════════

    @SuppressWarnings("unchecked")
    private VBox buildCourseList() {
        VBox panel = new VBox(10);
        TableView<Course> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(350);

        TableColumn<Course, String> c1 = col("Code",        "courseCode");
        TableColumn<Course, String> c2 = col("Course Name", "courseName");
        TableColumn<Course, String> c3 = col("Credits",     "credits");
        TableColumn<Course, String> c4 = col("Semester",    "semester");
        TableColumn<Course, String> c5 = col("Year",        "academicYear");

        table.getColumns().addAll(c1, c2, c3, c4, c5);
        table.setItems(FXCollections.observableArrayList(
            courseDAO.getCoursesByTeacher(currentTeacher.getTeacherId())));
        panel.getChildren().add(table);
        return panel;
    }

    // ═══════════════════════════════════════════════════════════
    // ASSESSMENT PANEL
    // ═══════════════════════════════════════════════════════════

    @SuppressWarnings("unchecked")
    private VBox buildAssessmentPanel() {
        VBox panel = new VBox(12);

        List<Course> courses = courseDAO.getCoursesByTeacher(currentTeacher.getTeacherId());
        ComboBox<Course> cbCourse = new ComboBox<>(FXCollections.observableArrayList(courses));
        cbCourse.setPromptText("Select Course...");
        cbCourse.setPrefWidth(300);

        Button addBtn = new Button("+ New Assessment");
        addBtn.getStyleClass().add("btn-primary");
        addBtn.setDisable(true);

        HBox toolbar = new HBox(10, new Label("Course: "), cbCourse, addBtn);

        TableView<Assessment> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(350);

        TableColumn<Assessment, String> c1 = col("Title",    "title");
        TableColumn<Assessment, String> c2 = col("Type",     "type");
        TableColumn<Assessment, String> c3 = new TableColumn<>("Max Marks");
        c3.setCellValueFactory(c -> new SimpleStringProperty(
            String.format("%.0f", c.getValue().getMaxMarks())));
        TableColumn<Assessment, String> c4 = new TableColumn<>("Weight %");
        c4.setCellValueFactory(c -> new SimpleStringProperty(
            String.format("%.1f%%", c.getValue().getWeight())));
        TableColumn<Assessment, String> c5 = col("Due Date", "dueDate");

        TableColumn<Assessment, Void> colAct = new TableColumn<>("Actions");
        colAct.setCellFactory(col -> new TableCell<>() {
            final Button editBtn = new Button("Edit");
            final Button delBtn  = new Button("Delete");
            {
                editBtn.getStyleClass().add("btn-small-secondary");
                delBtn.getStyleClass().add("btn-small-danger");
                editBtn.setOnAction(e -> {
                    Assessment a = getTableView().getItems().get(getIndex());
                    showAssessmentDialog(a, cbCourse.getValue(), table);
                });
                delBtn.setOnAction(e -> {
                    Assessment a = getTableView().getItems().get(getIndex());
                    if (AlertUtil.showConfirm("Delete", "Delete assessment: " + a.getTitle() + "?")) {
                        if (gradeDAO.deleteAssessment(a.getAssessmentId())) {
                            table.getItems().remove(a);
                        }
                    }
                });
            }
            @Override protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                if (empty) { setGraphic(null); return; }
                setGraphic(new HBox(6, editBtn, delBtn));
            }
        });

        table.getColumns().addAll(c1, c2, c3, c4, c5, colAct);

        cbCourse.setOnAction(e -> {
            Course sel = cbCourse.getValue();
            if (sel != null) {
                addBtn.setDisable(false);
                table.setItems(FXCollections.observableArrayList(
                    gradeDAO.getAssessmentsByCourse(sel.getCourseId())));
            }
        });

        addBtn.setOnAction(e -> showAssessmentDialog(null, cbCourse.getValue(), table));

        panel.getChildren().addAll(toolbar, table);
        return panel;
    }

    private void showAssessmentDialog(Assessment existing, Course course, TableView<Assessment> table) {
        if (course == null) return;
        Dialog<ButtonType> dlg = new Dialog<>();
        dlg.setTitle(existing == null ? "New Assessment" : "Edit Assessment");
        GridPane form = new GridPane();
        form.setHgap(12); form.setVgap(10); form.setPadding(new Insets(20));

        TextField tfTitle  = new TextField(existing != null ? existing.getTitle() : "");
        ComboBox<String> cbType = new ComboBox<>(FXCollections.observableArrayList(
            "QUIZ","ASSIGNMENT","MIDTERM","FINAL","PROJECT","LAB"));
        if (existing != null) cbType.setValue(existing.getType());
        TextField tfMax    = new TextField(existing != null ? String.valueOf(existing.getMaxMarks()) : "100");
        TextField tfWeight = new TextField(existing != null ? String.valueOf(existing.getWeight())   : "20");
        DatePicker dpDue   = new DatePicker(existing != null ? existing.getDueDate() : LocalDate.now());

        form.addRow(0, new Label("Title:"),      tfTitle);
        form.addRow(1, new Label("Type:"),       cbType);
        form.addRow(2, new Label("Max Marks:"),  tfMax);
        form.addRow(3, new Label("Weight (%):"), tfWeight);
        form.addRow(4, new Label("Due Date:"),   dpDue);

        dlg.getDialogPane().setContent(form);
        dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dlg.getDialogPane().getStylesheets().add(
            getClass().getResource("/css/styles.css").toExternalForm());

        dlg.showAndWait().ifPresent(bt -> {
            if (bt != ButtonType.OK) return;
            Assessment a = existing != null ? existing : new Assessment();
            a.setCourseId(course.getCourseId());
            a.setTitle(tfTitle.getText().trim());
            a.setType(cbType.getValue() != null ? cbType.getValue() : "ASSIGNMENT");
            try { a.setMaxMarks(Double.parseDouble(tfMax.getText())); }
            catch (Exception ex) { a.setMaxMarks(100); }
            try { a.setWeight(Double.parseDouble(tfWeight.getText())); }
            catch (Exception ex) { a.setWeight(20); }
            a.setDueDate(dpDue.getValue());

            boolean ok = existing == null
                    ? gradeDAO.addAssessment(a)
                    : gradeDAO.updateAssessment(a);
            if (ok) {
                AlertUtil.showInfo("Saved", "Assessment saved.");
                table.setItems(FXCollections.observableArrayList(
                    gradeDAO.getAssessmentsByCourse(course.getCourseId())));
            } else {
                AlertUtil.showError("Error", "Could not save assessment.");
            }
        });
    }

    // ═══════════════════════════════════════════════════════════
    // GRADE ENTRY PANEL
    // ═══════════════════════════════════════════════════════════

    @SuppressWarnings("unchecked")
    private VBox buildGradeEntryPanel() {
        VBox panel = new VBox(12);

        List<Course> courses = courseDAO.getCoursesByTeacher(currentTeacher.getTeacherId());
        ComboBox<Course>      cbCourse     = new ComboBox<>(FXCollections.observableArrayList(courses));
        ComboBox<Assessment>  cbAssessment = new ComboBox<>();
        cbCourse.setPromptText("Select Course..."); cbCourse.setPrefWidth(260);
        cbAssessment.setPromptText("Select Assessment..."); cbAssessment.setPrefWidth(260);

        HBox selRow = new HBox(10,
            new Label("Course:"), cbCourse,
            new Label("Assessment:"), cbAssessment);

        Label infoLabel = new Label("Max Marks: —    Weight: —");
        infoLabel.getStyleClass().add("hint-text");

        // Grade table with editable Marks column
        TableView<Grade> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(350);
        table.setEditable(true);

        TableColumn<Grade, String> cNo   = col("Student No.", "studentNumber");
        cNo.setPrefWidth(120);
        
        table.getSelectionModel().setCellSelectionEnabled(true);
        table.setOnKeyPressed(event -> {
            if (event.isShortcutDown() && event.getCode() == KeyCode.V) {
                pasteFromClipboard(table, cbAssessment.getValue(), cbCourse.getValue());
            }
        });
        TableColumn<Grade, String> cName = col("Student Name","studentName");
        cName.setPrefWidth(180);
        TableColumn<Grade, String> cMarks = new TableColumn<>("Marks");
        cMarks.setPrefWidth(100);
        cMarks.setCellValueFactory(c ->
            new SimpleStringProperty(c.getValue().getMarksObtained() >= 0
                ? String.format("%.1f", c.getValue().getMarksObtained()) : ""));
        cMarks.setCellFactory(javafx.scene.control.cell.TextFieldTableCell.forTableColumn());
        cMarks.setEditable(true);
        cMarks.setOnEditCommit(e -> {
            Grade g = e.getRowValue();
            Assessment asmnt = cbAssessment.getValue();
            if (asmnt == null) return;
            try {
                double val = Double.parseDouble(e.getNewValue().trim());
                if (!GradeCalculator.isValidMarks(val, asmnt.getMaxMarks())) {
                    AlertUtil.showError("Invalid", "Marks must be between 0 and " + asmnt.getMaxMarks());
                    table.refresh();
                    return;
                }
                g.setMarksObtained(val);
                gradeDAO.saveGrade(g, asmnt.getMaxMarks(), cbCourse.getValue().getGradingScale(), currentTeacher.getUserId());
                // Refresh letter grade display
                double pct = (val / asmnt.getMaxMarks()) * 100.0;
                g.setLetterGrade(GradeCalculator.getLetterGrade(pct, cbCourse.getValue().getGradingScale()));
                table.refresh();
            } catch (NumberFormatException ex) {
                AlertUtil.showError("Invalid", "Please enter a numeric marks value.");
                table.refresh();
            }
        });

        TableColumn<Grade, String> cLetter = col("Grade", "letterGrade");
        cLetter.setPrefWidth(70);
        TableColumn<Grade, String> cPct = new TableColumn<>("Percentage");
        cPct.setCellValueFactory(c -> {
            Grade g = c.getValue();
            Assessment a = cbAssessment.getValue();
            if (a == null || g.getMarksObtained() < 0) return new SimpleStringProperty("-");
            double pct = (g.getMarksObtained() / a.getMaxMarks()) * 100.0;
            return new SimpleStringProperty(String.format("%.1f%%", pct));
        });

        table.getColumns().addAll(cNo, cName, cMarks, cLetter, cPct);

        Button btnSaveAll = new Button("Save All Grades");
        btnSaveAll.getStyleClass().add("btn-primary");
        btnSaveAll.setDisable(true);

        Button btnCalculate = new Button("Calculate Final Grades");
        btnCalculate.getStyleClass().add("btn-secondary");
        btnCalculate.setDisable(true);

        // Course selection loads assessments
        cbCourse.setOnAction(e -> {
            Course sel = cbCourse.getValue();
            if (sel != null) {
                cbAssessment.setItems(FXCollections.observableArrayList(
                    gradeDAO.getAssessmentsByCourse(sel.getCourseId())));
                cbAssessment.getSelectionModel().clearSelection();
                table.getItems().clear();
                infoLabel.setText("Max Marks: —    Weight: —");
            }
        });

        // Assessment selection loads enrolled students + existing grades
        cbAssessment.setOnAction(e -> {
            Assessment sel = cbAssessment.getValue();
            Course cSel = cbCourse.getValue();
            if (sel == null || cSel == null) return;

            infoLabel.setText("Max Marks: " + sel.getMaxMarks()
                    + "    Weight: " + sel.getWeight() + "%");

            List<StudentUser> students = courseDAO.getEnrolledStudents(cSel.getCourseId());
            List<Grade> existing = gradeDAO.getGradesByAssessment(sel.getAssessmentId());
            Map<Integer, Grade> existingMap = new HashMap<>();
            for (Grade g : existing) existingMap.put(g.getStudentId(), g);

            ObservableList<Grade> rows = FXCollections.observableArrayList();
            for (StudentUser s : students) {
                Grade g = existingMap.getOrDefault(s.getStudentId(), new Grade());
                if (g.getStudentId() == 0) {
                    g.setStudentId(s.getStudentId());
                    g.setStudentName(s.getFullName());
                    g.setStudentNumber(s.getStudentNumber());
                    g.setAssessmentId(sel.getAssessmentId());
                    g.setMarksObtained(-1);
                }
                rows.add(g);
            }
            table.setItems(rows);
            btnSaveAll.setDisable(false);
            btnCalculate.setDisable(false);
        });

        btnSaveAll.setOnAction(e -> {
            Assessment sel = cbAssessment.getValue();
            if (sel == null) return;
            int saved = 0, skipped = 0;
            for (Grade g : table.getItems()) {
                if (g.getMarksObtained() < 0) { skipped++; continue; }
                gradeDAO.saveGrade(g, sel.getMaxMarks(), cbCourse.getValue().getGradingScale(), currentTeacher.getUserId());
                saved++;
            }
            AlertUtil.showInfo("Saved", "Saved " + saved + " grade(s). Skipped " + skipped + " ungraded.");
            table.refresh();
        });

        btnCalculate.setOnAction(e -> {
            Course sel = cbCourse.getValue();
            if (sel == null) return;
            
            showFinalGradesDialog(sel);
        });

        panel.getChildren().addAll(selRow, infoLabel, table, new HBox(10, btnSaveAll, btnCalculate));
        return panel;
    }

    private void pasteFromClipboard(TableView<Grade> table, Assessment asmnt, Course course) {
        if (asmnt == null || course == null) return;
        String content = Clipboard.getSystemClipboard().getString();
        if (content == null || content.isEmpty()) return;

        TablePosition<?, ?> pos = table.getFocusModel().getFocusedCell();
        if (pos == null) return;
        
        int startRow = pos.getRow();
        String[] rows = content.split("\n");
        int saved = 0;
        int maxRows = table.getItems().size();

        for (int i = 0; i < rows.length; i++) {
            if (startRow + i >= maxRows) break;
            String[] cols = rows[i].trim().split("\t");
            if (cols.length == 0 || cols[0].isEmpty()) continue;
            
            try {
                double val = Double.parseDouble(cols[0]);
                if (GradeCalculator.isValidMarks(val, asmnt.getMaxMarks())) {
                    Grade g = table.getItems().get(startRow + i);
                    g.setMarksObtained(val);
                    gradeDAO.saveGrade(g, asmnt.getMaxMarks(), course.getGradingScale(), currentTeacher.getUserId());
                    
                    double pct = (val / asmnt.getMaxMarks()) * 100.0;
                    g.setLetterGrade(GradeCalculator.getLetterGrade(pct, course.getGradingScale()));
                    saved++;
                }
            } catch (NumberFormatException ex) {
                // Ignore non-numeric pastes
            }
        }
        
        if (saved > 0) {
            table.refresh();
            AlertUtil.showInfo("Pasted", "Successfully pasted and saved " + saved + " grade(s).");
        }
    }
    
    @SuppressWarnings("unchecked")
    private void showFinalGradesDialog(Course course) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Final Grades & Status for " + course.getCourseCode());
        
        TableView<StudentUser> finalTable = new TableView<>();
        finalTable.setPrefSize(500, 350);
        
        TableColumn<StudentUser, String> cNo = col("Student No.", "studentNumber");
        TableColumn<StudentUser, String> cName = col("Name", "fullName");
        
        TableColumn<StudentUser, String> cFinal = new TableColumn<>("Final %");
        TableColumn<StudentUser, String> cGrade = new TableColumn<>("Grade");
        TableColumn<StudentUser, String> cStatus = new TableColumn<>("Status");
        
        List<Assessment> assessments = gradeDAO.getAssessmentsByCourse(course.getCourseId());
        Map<Integer, Assessment> aMap = new HashMap<>();
        for (Assessment a : assessments) aMap.put(a.getAssessmentId(), a);
        
        cFinal.setCellValueFactory(c -> {
            StudentUser s = c.getValue();
            List<Grade> grades = gradeDAO.getGradesByStudentAndCourse(s.getStudentId(), course.getCourseId());
            double pct = GradeCalculator.calculateFinalPercentage(grades, aMap);
            return new SimpleStringProperty(pct >= 0 ? String.format("%.1f%%", pct) : "-");
        });
        
        cGrade.setCellValueFactory(c -> {
            StudentUser s = c.getValue();
            List<Grade> grades = gradeDAO.getGradesByStudentAndCourse(s.getStudentId(), course.getCourseId());
            double pct = GradeCalculator.calculateFinalPercentage(grades, aMap);
            return new SimpleStringProperty(pct >= 0 ? GradeCalculator.getLetterGrade(pct, course.getGradingScale()) : "-");
        });
        
        cStatus.setCellValueFactory(c -> {
            StudentUser s = c.getValue();
            List<Grade> grades = gradeDAO.getGradesByStudentAndCourse(s.getStudentId(), course.getCourseId());
            double pct = GradeCalculator.calculateFinalPercentage(grades, aMap);
            return new SimpleStringProperty(pct >= 0 ? GradeCalculator.getPassFailStatus(pct, course.getGradingScale()) : "-");
        });
        
        finalTable.getColumns().addAll(cNo, cName, cFinal, cGrade, cStatus);
        
        List<StudentUser> students = courseDAO.getEnrolledStudents(course.getCourseId());
        finalTable.setItems(FXCollections.observableArrayList(students));
        
        VBox dbContent = new VBox(finalTable);
        dialog.getDialogPane().setContent(dbContent);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.getDialogPane().getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
        dialog.showAndWait();
    }

    // ═══════════════════════════════════════════════════════════
    // ATTENDANCE PANEL
    // ═══════════════════════════════════════════════════════════

    @SuppressWarnings("unchecked")
    private VBox buildAttendancePanel() {
        VBox panel = new VBox(12);

        List<Course> courses = courseDAO.getCoursesByTeacher(currentTeacher.getTeacherId());
        ComboBox<Course> cbCourse = new ComboBox<>(FXCollections.observableArrayList(courses));
        cbCourse.setPromptText("Select Course...");
        cbCourse.setPrefWidth(260);

        DatePicker dpDate = new DatePicker(LocalDate.now());
        Button btnLoad    = new Button("Load Students");
        btnLoad.getStyleClass().add("btn-secondary");

        HBox toolbar = new HBox(10,
            new Label("Course:"), cbCourse,
            new Label("Date:"),   dpDate,
            btnLoad);

        // Attendance table with editable Status column
        TableView<Attendance> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(350);

        TableColumn<Attendance, String> cNo   = col("Student No.", "studentNumber");
        TableColumn<Attendance, String> cName = col("Name", "studentName");

        TableColumn<Attendance, String> cStatus = new TableColumn<>("Status");
        cStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        cStatus.setCellFactory(col -> new TableCell<>() {
            final ComboBox<String> cb = new ComboBox<>(FXCollections.observableArrayList(
                "PRESENT", "ABSENT", "LATE", "EXCUSED"));
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) { setGraphic(null); return; }
                cb.setValue(item != null ? item : "PRESENT");
                cb.setOnAction(e -> {
                    Attendance a = getTableView().getItems().get(getIndex());
                    a.setStatus(cb.getValue());
                });
                setGraphic(cb);
            }
        });

        TableColumn<Attendance, String> cRemarks = new TableColumn<>("Remarks");
        cRemarks.setCellValueFactory(new PropertyValueFactory<>("remarks"));

        table.getColumns().addAll(cNo, cName, cStatus, cRemarks);

        Button btnSave = new Button("Save Attendance");
        btnSave.getStyleClass().add("btn-primary");
        btnSave.setDisable(true);

        btnLoad.setOnAction(e -> {
            Course cSel = cbCourse.getValue();
            LocalDate date = dpDate.getValue();
            if (cSel == null || date == null) {
                AlertUtil.showWarning("Select", "Please select a course and date."); return;
            }
            List<StudentUser> students = courseDAO.getEnrolledStudents(cSel.getCourseId());
            List<Attendance>  existing = attendanceDAO.getAttendanceByDate(cSel.getCourseId(), date);
            Map<Integer, Attendance> map = new HashMap<>();
            for (Attendance a : existing) map.put(a.getStudentId(), a);

            ObservableList<Attendance> rows = FXCollections.observableArrayList();
            for (StudentUser s : students) {
                Attendance a = map.getOrDefault(s.getStudentId(), new Attendance());
                if (a.getStudentId() == 0) {
                    a.setStudentId(s.getStudentId());
                    a.setStudentName(s.getFullName());
                    a.setStudentNumber(s.getStudentNumber());
                    a.setCourseId(cSel.getCourseId());
                    a.setAttDate(date);
                    a.setStatus("PRESENT");
                    a.setRecordedBy(currentTeacher.getUserId());
                }
                rows.add(a);
            }
            table.setItems(rows);
            btnSave.setDisable(false);
        });

        btnSave.setOnAction(e -> {
            List<Attendance> records = table.getItems();
            if (attendanceDAO.saveBatchAttendance(records)) {
                AlertUtil.showInfo("Saved", "Attendance saved for " + records.size() + " student(s).");
            } else {
                AlertUtil.showError("Error", "Could not save attendance.");
            }
        });

        panel.getChildren().addAll(toolbar, table, btnSave);
        return panel;
    }

    // ═══════════════════════════════════════════════════════════
    // CLASS REPORTS PANEL
    // ═══════════════════════════════════════════════════════════

    @SuppressWarnings("unchecked")
    private VBox buildReportsPanel() {
        VBox panel = new VBox(12);

        List<Course> courses = courseDAO.getCoursesByTeacher(currentTeacher.getTeacherId());
        ComboBox<Course> cbCourse = new ComboBox<>(FXCollections.observableArrayList(courses));
        cbCourse.setPromptText("Select Course..."); cbCourse.setPrefWidth(260);
        Button btnLoad = new Button("Load Report");
        btnLoad.getStyleClass().add("btn-secondary");

        HBox toolbar = new HBox(10, new Label("Course:"), cbCourse, btnLoad);

        // Summary table: Student, Final%, Grade, Attendance%
        TableView<StudentUser> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(360);

        TableColumn<StudentUser, String> cNo   = col("Student No.", "studentNumber");
        TableColumn<StudentUser, String> cName = col("Name", "fullName");

        TableColumn<StudentUser, String> cFinal = new TableColumn<>("Final %");
        cFinal.setCellValueFactory(c -> {
            Course sel = cbCourse.getValue();
            if (sel == null) return new SimpleStringProperty("-");
            StudentUser s = c.getValue();
            List<Grade> grades = gradeDAO.getGradesByStudentAndCourse(s.getStudentId(), sel.getCourseId());
            List<Assessment> assessments = gradeDAO.getAssessmentsByCourse(sel.getCourseId());
            Map<Integer, Assessment> aMap = new HashMap<>();
            for (Assessment a : assessments) aMap.put(a.getAssessmentId(), a);
            double pct = GradeCalculator.calculateFinalPercentage(grades, aMap);
            return new SimpleStringProperty(pct >= 0 ? String.format("%.1f%%", pct) : "-");
        });

        TableColumn<StudentUser, String> cGrade = new TableColumn<>("Grade");
        cGrade.setCellValueFactory(c -> {
            Course sel = cbCourse.getValue();
            if (sel == null) return new SimpleStringProperty("-");
            StudentUser s = c.getValue();
            List<Grade> grades = gradeDAO.getGradesByStudentAndCourse(s.getStudentId(), sel.getCourseId());
            List<Assessment> assessments = gradeDAO.getAssessmentsByCourse(sel.getCourseId());
            Map<Integer, Assessment> aMap = new HashMap<>();
            for (Assessment a : assessments) aMap.put(a.getAssessmentId(), a);
            double pct = GradeCalculator.calculateFinalPercentage(grades, aMap);
            return new SimpleStringProperty(pct >= 0 ? GradeCalculator.getLetterGrade(pct, sel.getGradingScale()) : "-");
        });

        TableColumn<StudentUser, String> cStatus = new TableColumn<>("Status");
        cStatus.setCellValueFactory(c -> {
            Course sel = cbCourse.getValue();
            if (sel == null) return new SimpleStringProperty("-");
            StudentUser s = c.getValue();
            List<Grade> grades = gradeDAO.getGradesByStudentAndCourse(s.getStudentId(), sel.getCourseId());
            List<Assessment> assessments = gradeDAO.getAssessmentsByCourse(sel.getCourseId());
            Map<Integer, Assessment> aMap = new HashMap<>();
            for (Assessment a : assessments) aMap.put(a.getAssessmentId(), a);
            double pct = GradeCalculator.calculateFinalPercentage(grades, aMap);
            return new SimpleStringProperty(pct >= 0 ? GradeCalculator.getPassFailStatus(pct, sel.getGradingScale()) : "-");
        });

        TableColumn<StudentUser, String> cAtt = new TableColumn<>("Attendance %");
        cAtt.setCellValueFactory(c -> {
            Course sel = cbCourse.getValue();
            if (sel == null) return new SimpleStringProperty("-");
            double attPct = attendanceDAO.getAttendancePercentage(
                c.getValue().getStudentId(), sel.getCourseId());
            return new SimpleStringProperty(String.format("%.1f%%", attPct));
        });

        table.getColumns().addAll(cNo, cName, cFinal, cGrade, cStatus, cAtt);

        btnLoad.setOnAction(e -> {
            Course sel = cbCourse.getValue();
            if (sel == null) return;
            table.setItems(FXCollections.observableArrayList(
                courseDAO.getEnrolledStudents(sel.getCourseId())));
        });

        // Export CSV button
        Button btnCSV = new Button("Export Grades CSV");
        btnCSV.getStyleClass().add("btn-secondary");
        btnCSV.setOnAction(e -> {
            Course sel = cbCourse.getValue();
            if (sel == null) { AlertUtil.showWarning("Select","Please select a course."); return; }
            javafx.stage.FileChooser fc = new javafx.stage.FileChooser();
            fc.setInitialFileName(sel.getCourseCode() + "_grades.csv");
            fc.getExtensionFilters().add(new javafx.stage.FileChooser.ExtensionFilter("CSV","*.csv"));
            java.io.File file = fc.showSaveDialog(com.sms.MainApp.primaryStage);
            if (file != null) {
                ReportGenerator rg = new ReportGenerator();
                if (rg.exportGradesToCSV(sel, file.getAbsolutePath())) {
                    AlertUtil.showInfo("Exported", "CSV saved to:\n" + file.getAbsolutePath());
                }
            }
        });

        panel.getChildren().addAll(toolbar, table, btnCSV);
        return panel;
    }

    // ─── Generic column builder ────────────────────────────────────
    private <T> TableColumn<T, String> col(String title, String property) {
        TableColumn<T, String> col = new TableColumn<>(title);
        col.setCellValueFactory(new PropertyValueFactory<>(property));
        return col;
    }
}
