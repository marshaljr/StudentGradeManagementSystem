package com.sms.controller;

import com.sms.dao.AttendanceDAO;
import com.sms.dao.CourseDAO;
import com.sms.dao.GradeDAO;
import com.sms.model.*;
import com.sms.util.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

import java.util.*;

/**
 * StudentDashboardController — View-only screens for the student role.
 *   - View enrolled courses
 *   - View grades per course with final percentage
 *   - View attendance per course
 *   - Download PDF transcript
 */
public class StudentDashboardController {

    @FXML private Label userLabel;
    @FXML private Label pageTitle;
    @FXML private VBox  subContent;
    @FXML private VBox  sidebarPane;
    @FXML private Button sidebarToggleBtn;
    @FXML private Button btnThemeToggle;
    @FXML private Label breadcrumbCurrent;
    @FXML private BorderPane mainRoot;  // For accessing root for dark mode styling

    // Sidebar buttons for active state tracking
    @FXML private Button btnDashboard;
    @FXML private Button btnCourses;
    @FXML private Button btnGrades;
    @FXML private Button btnAttendance;
    @FXML private Button btnTranscript;

    private final CourseDAO     courseDAO     = new CourseDAO();
    private final GradeDAO      gradeDAO      = new GradeDAO();
    private final AttendanceDAO attendanceDAO = new AttendanceDAO();

    private StudentUser currentStudent;
    private boolean sidebarExpanded = true;  // Track sidebar state
    private boolean darkModeEnabled = false;  // Track dark mode state

    @FXML
    public void initialize() {
        UserAccount user = SessionManager.getCurrentUser();
        userLabel.setText(user.getFullName() + "  |  Student");
        currentStudent = (StudentUser) user;
        showDashboard();
    }

    @FXML public void handleLogout() { NavigationUtil.logout(); }

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
        for (Button btn : new Button[]{btnDashboard, btnCourses, btnGrades, btnAttendance, btnTranscript}) {
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

    @FXML public void showDashboard() {
        setActiveNavButton(btnDashboard, "Dashboard");
        pageTitle.setText("My Dashboard");
        subContent.getChildren().clear();

        VBox info = new VBox(8);
        info.getStyleClass().add("course-card");
        info.setPadding(new Insets(16));

        Label name  = new Label("👋  Welcome, " + currentStudent.getFullName());
        name.getStyleClass().add("section-title");
        Label no    = new Label("Student Number: " + currentStudent.getStudentNumber());
        Label prog  = new Label("Program: " + nullSafe(currentStudent.getProgram()));
        Label yr    = new Label("Year Level: " + currentStudent.getYearLevel());

        no.getStyleClass().add("hint-text");
        prog.getStyleClass().add("hint-text");
        yr.getStyleClass().add("hint-text");

        info.getChildren().addAll(name, no, prog, yr);
        subContent.getChildren().add(info);

        // Quick summary: course count, GPA estimate
        List<Course> courses = courseDAO.getCoursesByStudent(currentStudent.getStudentId());
        Label summary = new Label("You are enrolled in " + courses.size() + " course(s) this semester.");
        summary.getStyleClass().add("hint-text");
        subContent.getChildren().add(summary);
    }

    @FXML public void showCourses() {
        setActiveNavButton(btnCourses, "My Courses");
        pageTitle.setText("My Courses");
        subContent.getChildren().clear();

        List<Course> courses = courseDAO.getCoursesByStudent(currentStudent.getStudentId());
        if (courses.isEmpty()) {
            subContent.getChildren().add(new Label("You are not enrolled in any courses yet."));
            return;
        }
        for (Course c : courses) {
            VBox card = new VBox(5);
            card.getStyleClass().add("course-card");
            card.setPadding(new Insets(14));
            Label title   = new Label(c.getCourseCode() + " — " + c.getCourseName());
            title.getStyleClass().add("section-title");
            Label teacher = new Label("Teacher: " + nullSafe(c.getTeacherName()));
            Label det     = new Label(c.getSemester() + " | " + c.getCredits() + " credits");
            teacher.getStyleClass().add("hint-text");
            det.getStyleClass().add("hint-text");
            card.getChildren().addAll(title, teacher, det);
            subContent.getChildren().add(card);
        }
    }

    @FXML public void showGrades() {
        setActiveNavButton(btnGrades, "My Grades");
        pageTitle.setText("My Grades");
        subContent.getChildren().clear();

        List<Course> courses = courseDAO.getCoursesByStudent(currentStudent.getStudentId());
        if (courses.isEmpty()) {
            subContent.getChildren().add(new Label("No courses found."));
            return;
        }

        double totalGP      = 0;
        int    totalCredits = 0;

        for (Course course : courses) {
            Label courseLabel = new Label(course.getCourseCode() + " — " + course.getCourseName());
            courseLabel.getStyleClass().add("section-title");

            List<Assessment> assessments = gradeDAO.getAssessmentsByCourse(course.getCourseId());
            List<Grade>      grades      = gradeDAO.getGradesByStudentAndCourse(
                    currentStudent.getStudentId(), course.getCourseId());

            Map<Integer, Grade>      gMap = new HashMap<>();
            Map<Integer, Assessment> aMap = new HashMap<>();
            for (Grade g : grades)      gMap.put(g.getAssessmentId(), g);
            for (Assessment a : assessments) aMap.put(a.getAssessmentId(), a);

            TableView<Assessment> table = new TableView<>();
            table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            table.setMaxHeight(200);

            TableColumn<Assessment, String> c1 = new TableColumn<>("Assessment");
            c1.setCellValueFactory(new PropertyValueFactory<>("title"));
            TableColumn<Assessment, String> c2 = new TableColumn<>("Type");
            c2.setCellValueFactory(new PropertyValueFactory<>("type"));
            TableColumn<Assessment, String> c3 = new TableColumn<>("Max");
            c3.setCellValueFactory(c ->
                new SimpleStringProperty(String.format("%.0f", c.getValue().getMaxMarks())));
            TableColumn<Assessment, String> c4 = new TableColumn<>("Marks");
            c4.setCellValueFactory(c -> {
                Grade g = gMap.get(c.getValue().getAssessmentId());
                return new SimpleStringProperty(g != null
                    ? String.format("%.1f", g.getMarksObtained()) : "—");
            });
            TableColumn<Assessment, String> c5 = new TableColumn<>("Grade");
            c5.setCellValueFactory(c -> {
                Grade g = gMap.get(c.getValue().getAssessmentId());
                return new SimpleStringProperty(g != null ? nullSafe(g.getLetterGrade()) : "—");
            });
            TableColumn<Assessment, String> c6 = new TableColumn<>("Weight");
            c6.setCellValueFactory(c ->
                new SimpleStringProperty(String.format("%.1f%%", c.getValue().getWeight())));

            table.getColumns().addAll(c1, c2, c3, c4, c5, c6);
            table.setItems(FXCollections.observableArrayList(assessments));

            // Final percentage row
            double pct    = GradeCalculator.calculateFinalPercentage(grades, aMap);
            String letter = pct >= 0 ? GradeCalculator.getLetterGrade(pct, course.getGradingScale()) : "—";
            String status = pct >= 0 ? " (" + GradeCalculator.getPassFailStatus(pct, course.getGradingScale()) + ")" : "";
            String gp     = pct >= 0 ? String.format("%.2f", GradeCalculator.getGradePoint(letter)) : "—";
            Label finalLbl = new Label(
                "Final:  " + (pct >= 0 ? String.format("%.1f%%", pct) : "—")
                + "   |   Letter: " + letter + status
                + "   |   Grade Points: " + gp);
            finalLbl.getStyleClass().add("final-grade-label");

            if (pct >= 0) {
                totalGP      += GradeCalculator.getGradePoint(letter) * course.getCredits();
                totalCredits += course.getCredits();
            }

            subContent.getChildren().addAll(courseLabel, table, finalLbl, new Separator());
        }

        // GPA footer
        if (totalCredits > 0) {
            double gpa = Math.round((totalGP / totalCredits) * 100.0) / 100.0;
            Label gpaLabel = new Label("📊  Cumulative GPA: " + String.format("%.2f", gpa) + " / 4.00");
            gpaLabel.getStyleClass().add("section-title");
            subContent.getChildren().add(gpaLabel);
        }
    }

    @FXML public void showAttendance() {
        setActiveNavButton(btnAttendance, "Attendance");
        pageTitle.setText("My Attendance");
        subContent.getChildren().clear();

        List<Course> courses = courseDAO.getCoursesByStudent(currentStudent.getStudentId());
        for (Course course : courses) {
            Label courseLabel = new Label(course.getCourseCode() + " — " + course.getCourseName());
            courseLabel.getStyleClass().add("section-title");

            Map<String, Integer> summary = attendanceDAO.getAttendanceSummary(
                    currentStudent.getStudentId(), course.getCourseId());
            double pct = attendanceDAO.getAttendancePercentage(
                    currentStudent.getStudentId(), course.getCourseId());

            HBox summaryRow = new HBox(20);
            summaryRow.getChildren().addAll(
                badge("✅ Present",  String.valueOf(summary.getOrDefault("PRESENT", 0)), "badge-green"),
                badge("❌ Absent",   String.valueOf(summary.getOrDefault("ABSENT",  0)), "badge-red"),
                badge("⏰ Late",    String.valueOf(summary.getOrDefault("LATE",    0)), "badge-orange"),
                badge("📋 Excused", String.valueOf(summary.getOrDefault("EXCUSED", 0)), "badge-gray"),
                badge("📊 Attendance", String.format("%.1f%%", pct),                    "badge-blue")
            );

            // Detailed attendance table
            TableView<Attendance> table = new TableView<>();
            table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            table.setMaxHeight(200);

            TableColumn<Attendance, String> cDate   = new TableColumn<>("Date");
            cDate.setCellValueFactory(new PropertyValueFactory<>("attDate"));
            TableColumn<Attendance, String> cStatus = new TableColumn<>("Status");
            cStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
            TableColumn<Attendance, String> cRem    = new TableColumn<>("Remarks");
            cRem.setCellValueFactory(new PropertyValueFactory<>("remarks"));

            table.getColumns().addAll(cDate, cStatus, cRem);
            table.setItems(FXCollections.observableArrayList(
                attendanceDAO.getAttendanceByStudentAndCourse(
                    currentStudent.getStudentId(), course.getCourseId())));

            subContent.getChildren().addAll(courseLabel, summaryRow, table, new Separator());
        }
    }

    @FXML public void showTranscript() {
        setActiveNavButton(btnTranscript, "Transcript");
        pageTitle.setText("Download Transcript");
        subContent.getChildren().clear();

        Label desc = new Label("Click the button below to generate and download your official PDF transcript.");
        desc.setWrapText(true);
        desc.getStyleClass().add("hint-text");

        Button btnPDF = new Button("📄  Download PDF Transcript");
        btnPDF.getStyleClass().add("btn-primary");
        btnPDF.setOnAction(e -> {
            javafx.stage.FileChooser fc = new javafx.stage.FileChooser();
            fc.setTitle("Save Transcript");
            fc.setInitialFileName(currentStudent.getStudentNumber() + "_transcript.pdf");
            fc.getExtensionFilters().add(
                new javafx.stage.FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
            java.io.File file = fc.showSaveDialog(com.sms.MainApp.primaryStage);
            if (file != null) {
                ReportGenerator rg = new ReportGenerator();
                if (rg.generateTranscript(currentStudent, file.getAbsolutePath())) {
                    AlertUtil.showInfo("Done", "Transcript saved!\n" + file.getAbsolutePath());
                } else {
                    AlertUtil.showError("Error", "Could not generate transcript.");
                }
            }
        });

        subContent.getChildren().addAll(desc, btnPDF);
    }

    // ─── Helper: badge widget ──────────────────────────────────────
    private VBox badge(String label, String value, String style) {
        VBox b = new VBox(2);
        b.getStyleClass().addAll("badge", style);
        b.setPadding(new Insets(10, 16, 10, 16));
        Label lbl = new Label(value);  lbl.getStyleClass().add("badge-value");
        Label sub = new Label(label);  sub.getStyleClass().add("badge-label");
        b.getChildren().addAll(lbl, sub);
        return b;
    }

    private String nullSafe(String s) { return s != null ? s : ""; }
}
