# Final README: OOP and MVC Implementation

## Architecture at a Glance

This project follows MVC with JavaFX:

- View: FXML and CSS in src/main/resources/fxml and src/main/resources/css
- Controller: JavaFX controllers in src/main/java/com/sms/controller
- Model: domain classes in src/main/java/com/sms/model
- Data layer: DAO classes in src/main/java/com/sms/dao
- Support: utilities and factory in src/main/java/com/sms/util and src/main/java/com/sms/factory

## OOP Implementation (Where and How)

### 1) Abstraction + Inheritance + Polymorphism

- Base abstract class: UserAccount
  - Method: getDashboardPath() (abstract)
  - Method: getRoleDisplayName() (abstract)
- Child classes:
  - AdminUser overrides getDashboardPath() -> AdminDashboard.fxml
  - TeacherUser overrides getDashboardPath() -> TeacherDashboard.fxml
  - StudentUser overrides getDashboardPath() -> StudentDashboard.fxml
- Runtime polymorphism in LoginController.handleLogin():
  - After authentication, NavigationUtil.navigateTo(user.getDashboardPath()) is called.
  - Same call, different dashboard based on actual user object type.

### 2) Encapsulation

- UserAccount and all model classes keep fields private with getters/setters.
- Controllers never access DB fields directly; they use model objects and DAO methods.

### 3) Factory Method Pattern

- Class: UserFactory
- Method: createFromResultSet(ResultSet rs, String role)
- Internal builders: buildAdmin, buildTeacher, buildStudent
- Usage:
  - UserDAO.loadUserByIdAndRole() calls UserFactory to create correct UserAccount subtype.
- Benefit: object creation logic is centralized and role-safe.

### 4) Singleton Pattern

- Class: DatabaseConfig
- Method: getInstance()
- DAO constructors use DatabaseConfig.getInstance().getConnection()
- Benefit: one shared DB connection manager for the whole app.

### 5) Separation of Concerns via DAO Pattern

- UserDAO: authenticate, loadUserByIdAndRole, addStudent, updateStudent, addTeacher, getDashboardCounts
- CourseDAO: getAllCourses, getCoursesByTeacher, getCoursesByStudent, addCourse, updateCourse, getEnrolledStudents
- GradeDAO: getAssessmentsByCourse, saveGrade, getGradesByAssessment, getGradesByStudentAndCourse
- AttendanceDAO: getAttendanceByDate, saveBatchAttendance, getAttendanceSummary, getAttendancePercentage
- Benefit: SQL stays in DAO, UI logic stays in controllers.

## MVC Flow in This Project

### Login Flow

1. View: LoginView.fxml triggers LoginController.handleLogin().
2. Controller: LoginController validates input and calls UserDAO.authenticate(username, password).
3. Model/Data: UserDAO checks credentials, loads role data via loadUserByIdAndRole(), uses UserFactory.createFromResultSet().
4. Controller: SessionManager.setCurrentUser(user) stores session user.
5. Controller: NavigationUtil.navigateTo(user.getDashboardPath()) opens correct dashboard view.

### Teacher Grade Entry Flow

1. View event: Teacher selects course/assessment and edits marks.
2. Controller: TeacherDashboardController.buildGradeEntryPanel() handles UI events.
3. Data fetch: CourseDAO.getEnrolledStudents() and GradeDAO.getGradesByAssessment().
4. Business logic: GradeCalculator.isValidMarks() and GradeCalculator.getLetterGrade().
5. Persistence: GradeDAO.saveGrade(...) writes/updates grade.
6. View refresh: Table updates with marks, percentage, and letter grade.

### Student Grades View Flow

1. View event: Student clicks "My Grades".
2. Controller: StudentDashboardController.showGrades().
3. Data fetch: CourseDAO.getCoursesByStudent(), GradeDAO.getAssessmentsByCourse(), GradeDAO.getGradesByStudentAndCourse().
4. Business logic: GradeCalculator.calculateFinalPercentage(), getLetterGrade(), getPassFailStatus().
5. View render: per-course table + final grade summary + GPA display.

## Why This Design Works

- Easy to extend: adding a new user role mainly affects model subtype + factory + controller/view.
- Easy to maintain: SQL changes stay in DAO; UI changes stay in controllers/FXML.
- Easy to test logically: grading and GPA logic is centralized in GradeCalculator.
- Clear runtime flow: UI event -> controller -> DAO/model -> utility/business logic -> UI update.
