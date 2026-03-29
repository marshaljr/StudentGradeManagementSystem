# Student Grade Management System - Comprehensive Project Analysis

## 📋 Table of Contents

1. [Data Structures](#data-structures)
2. [OOP Concepts & Design Patterns](#oop-concepts--design-patterns)
3. [Architecture & Workflow](#architecture--workflow)
4. [Package Structure](#package-structure)
5. [Database Design & Operations](#database-design--operations)
6. [User Access Control](#user-access-control)
7. [Controllers & Event Handlers](#controllers--event-handlers)
8. [Utility Classes](#utility-classes)
9. [Integration & Communication Flow](#integration--communication-flow)
10. [Detailed User Workflows: Input-Process-Output](#detailed-user-workflows-input-process-output)

---

## 1. Data Structures

### 1.1 User Hierarchy (Inheritance-Based)

#### Base Class: `UserAccount` (Abstract)

```
Encapsulation: private fields (userId, username, passwordHash, role, etc.)
Abstract Methods:
  - getDashboardPath() → returns FXML filename for role-specific screen
  - getRoleDisplayName() → returns human-readable role name
```

#### User Subclasses:

1. **AdminUser** (extends UserAccount)
   - `adminId`: Unique admin identifier
   - `department`: Admin's department
   - Dashboard: `AdminDashboard.fxml`
   - Capabilities: User management, course management, reports

2. **TeacherUser** (extends UserAccount)
   - `teacherId`: Unique teacher identifier
   - `employeeId`: Employee ID
   - `department`: Department assignment
   - `qualification`: Academic qualification
   - `hireDate`: Employment date
   - Dashboard: `TeacherDashboard.fxml`
   - Capabilities: Grade entry, attendance recording, course management

3. **StudentUser** (extends UserAccount)
   - `studentId`: Unique student identifier
   - `studentNumber`: Registration number
   - `dateOfBirth`: Birth date
   - `gender`: Gender (Male/Female/Other)
   - `address`: Contact address
   - `enrollmentYear`: Year of enrollment
   - `program`: Program/Major
   - `yearLevel`: Current year level
   - Dashboard: `StudentDashboard.fxml`
   - Capabilities: View grades, attendance, personal records

### 1.2 Academic Data Structures

#### **Course**

- `courseId`: Primary key
- `courseCode`: Unique course identifier (e.g., "CS101")
- `courseName`: Course title
- `description`: Course description
- `credits`: Credit hours (default: 3)
- `teacherId`: Foreign key → Teachers
- `semester`: Semester offered
- `academicYear`: Academic year
- `maxStudents`: Maximum capacity (default: 40)
- `gradingScale`: Grading system (STANDARD/STRICT/GENEROUS)
- `active`: Active/Inactive status

#### **Assessment**

- `assessmentId`: Primary key
- `courseId`: Foreign key → Courses
- `title`: Assessment name
- `type`: ENUM (QUIZ, ASSIGNMENT, MIDTERM, FINAL, PROJECT, LAB)
- `maxMarks`: Out of total marks (e.g., 100)
- `weight`: Percentage weight in final grade (0-100)
- `dueDate`: Submission/completion date

#### **Grade**

- `gradeId`: Primary key
- `studentId`: Foreign key → Students
- `assessmentId`: Foreign key → Assessments
- `marksObtained`: Marks scored (-1 = not yet graded)
- `letterGrade`: Letter representation (A+, A, B+, ..., F)
- `remarks`: Teacher comments
- `gradedBy`: Foreign key → Users (which teacher graded)
- `gradedAt`: Timestamp

#### **Attendance**

- `attendanceId`: Primary key
- `studentId`: Foreign key → Students
- `courseId`: Foreign key → Courses
- `attDate`: Attendance date
- `status`: ENUM (PRESENT, ABSENT, LATE, EXCUSED)
- `remarks`: Comments
- `recordedBy`: Foreign key → Users (which teacher recorded)

### 1.3 Supporting Data Structures

#### **Enrollment**

- Links students to courses
- Tracks enrollment status (ACTIVE, DROPPED, COMPLETED)
- UNIQUE constraint: (student_id, course_id)

#### **GradeScale**

- Defines letter grade ranges for different grading scales
- Bridge between numeric marks and letter grades

---

## 2. OOP Concepts & Design Patterns

### 2.1 Encapsulation

**Definition**: Bundling data (fields) with methods, hiding internal details.

**Implementation**:

- All model classes use **private fields** with public **getters/setters**
- Example: `UserAccount` class

  ```java
  private int userId;
  private String username;
  // ... other fields

  public int getUserId() { return userId; }
  public void setUserId(int id) { this.userId = id; }
  ```

- Controllers cannot directly manipulate DAO queries; they call DAO methods
- Database connection details hidden in `DatabaseConfig`

### 2.2 Inheritance

**Definition**: Subclasses inherit properties/behaviors from parent class.

**Implementation**:

- **UserAccount** (parent) → AdminUser, TeacherUser, StudentUser (children)
- Shared attributes (userId, username, fullName, email, phone, active)
- Each subclass adds role-specific fields
- All inherit common behavior (getters/setters, toString())
- Each subclass implements abstract methods differently:
  ```java
  AdminUser.getDashboardPath() → "AdminDashboard.fxml"
  TeacherUser.getDashboardPath() → "TeacherDashboard.fxml"
  StudentUser.getDashboardPath() → "StudentDashboard.fxml"
  ```

### 2.3 Abstraction

**Definition**: Hiding complexity by defining abstract methods/classes.

**Implementation**:

- **UserAccount** is abstract with abstract methods:
  - `abstract String getDashboardPath()`
  - `abstract String getRoleDisplayName()`
- Subclasses MUST implement these; cannot instantiate UserAccount directly
- Abstracts the concept of "a user dashboard" without knowing exact implementation
- DAO classes abstract database operations from controllers

### 2.4 Polymorphism

**Definition**: Same method name, different implementations based on type.

**Implementation**:

- Polymorphic behavior in login:

  ```java
  UserAccount user = userDAO.authenticate(username, password);
  // Returns AdminUser, TeacherUser, or StudentUser

  // Polymorphic call — correct dashboard opens regardless of actual type:
  NavigationUtil.navigateTo(user.getDashboardPath());
  ```

- `SessionManager.setCurrentUser(UserAccount)` can store any subclass
- Controllers retrieve user via `SessionManager.getCurrentUser()` without knowing exact type
- `UserFactory.createFromResultSet()` uses a switch statement to return correct subclass

### 2.5 Design Patterns

#### **Singleton Pattern** (DatabaseConfig)

- **Purpose**: Ensure only ONE database connection exists for entire app lifetime
- **Implementation**:

  ```java
  public class DatabaseConfig {
      private static DatabaseConfig instance;
      private Connection connection;

      private DatabaseConfig() { /* private constructor */ }

      public static synchronized DatabaseConfig getInstance() {
          if (instance == null) instance = new DatabaseConfig();
          return instance;
      }

      public Connection getConnection() { /* returns or recreates connection */ }
  }
  ```

- All DAOs use: `DatabaseConfig.getInstance().getConnection()`
- Thread-safe via synchronized keyword
- Ensures consistent, reusable connection

#### **Factory Method Pattern** (UserFactory)

- **Purpose**: Decouple object creation from usage
- **Implementation**:
  ```java
  public static UserAccount createFromResultSet(ResultSet rs, String role) {
      return switch(role.toUpperCase()) {
          case "ADMIN" → buildAdmin(rs);
          case "TEACHER" → buildTeacher(rs);
          case "STUDENT" → buildStudent(rs);
      };
  }
  ```
- UserDAO calls `UserFactory.createFromResultSet(rs, role)`
- Caller doesn't know/care which subclass is created
- Open/Closed principle: Add new roles by adding new cases, no other code changes

#### **DAO (Data Access Object) Pattern**

- **Purpose**: Isolate database operations from business logic
- **Implementation**:
  - `UserDAO`, `CourseDAO`, `GradeDAO`, `AttendanceDAO`
  - Each DAO = CRUD operations for one entity
  - Controllers call DAO methods (e.g., `userDAO.addStudent(student)`)
  - DAO handles SQL, connection management, ResultSet mapping
  - Fully decoupled: change DB from MySQL to PostgreSQL? Only update DAOs

#### **MVC Architecture**

- **Model** (`model/`): Data classes (UserAccount, Course, Grade, etc.)
- **View** (`resources/fxml/`, `resources/css/`): FXML layouts and CSS styling
- **Controller** (`controller/`): Event handlers, business logic wiring

---

## 3. Architecture & Workflow

### 3.1 Application Startup Flow

```
1. MainApp.start(Stage)
   ├─ Load LoginView.fxml
   ├─ Display login screen
   └─ Wait for user input

2. User enters credentials
   └─ LoginController.handleLogin() triggered

3. LoginController
   ├─ Validates input (non-empty username/password)
   ├─ Calls userDAO.authenticate(username, password)
   │  └─ DAO queries users table
   │  └─ Verifies password hash via PasswordUtil
   │  └─ Returns UserAccount (admin/teacher/student) or null
   ├─ On success:
   │  ├─ SessionManager.setCurrentUser(user)
   │  ├─ Gets dashboard path via polymorphism: user.getDashboardPath()
   │  ├─ Calls NavigationUtil.navigateTo(dashboardPath)
   │  │  └─ FXMLLoader loads correct FXML
   │  │  └─ Scene switches to new dashboard
   │  └─ Dashboard-specific controller.initialize() called
   └─ On failure: Display error, keep on login screen
```

### 3.2 Dashboard Navigation Pattern

Each dashboard (Admin/Teacher/Student) uses **single-page application (SPA)** pattern:

```
BorderPane (root)
├─ Top: Header with logout button
├─ Left: Sidebar with navigation links
└─ Center: BorderPane
   ├─ Top: Page title
   ├─ Center: subContent (VBox)
   │  └─ Dynamically swapped panels:
   │     ├─ Dashboard panel (stats, summary)
   │     ├─ Student management panel
   │     ├─ Grade management panel
   │     ├─ Attendance panel
   │     └─ Reports panel
   └─ Bottom: Footer

When user clicks sidebar button:
1. Controller method called (e.g., handleShowStudents())
2. Clear subContent VBox
3. Build appropriate panel (buildStudentPanel())
4. Add panel to subContent
5. UI updates immediately (no scene switch)
```

### 3.3 Grade Calculation Workflow

```
GradeCalculator (Utility Class)

Input:
  └─ List<Grade> + Map<Integer, Assessment>

Algorithm:
  1. For each grade:
     ├─ Check if graded (marksObtained >= 0)
     ├─ Calculate: percentage = (marksObtained / maxMarks) * 100
     ├─ Calculate: weightedScore = percentage * weight
     └─ Accumulate sums

  2. finalPercentage = weightedSum / totalWeight * 100

  3. Convert to letter grade: getLetterGrade(percentage, scale)
     ├─ STANDARD scale: 90+→A+, 85+→A, 80+→A-, ... 45+→D, <45→F
     ├─ STRICT scale: 95+→A+, 90+→A, 85+→A-, ... 50+→D, <50→F
     ├─ GENEROUS scale: 85+→A+, 80+→A, 75+→A-, ... 40+→D, <40→F
     └─ Return letter grade

Example:
  Assessment 1 (weight=30%): marks=80/100 = 80% → 80*0.3 = 24
  Assessment 2 (weight=20%): marks=90/100 = 90% → 90*0.2 = 18
  Assessment 3 (weight=50%): marks=70/100 = 70% → 70*0.5 = 35
  ─────────────────────────────────────────────────────────────
  Final = (24+18+35)/(30+20+50)*100 = 77/100 * 100 = 77% = B-
```

---

## 4. Package Structure

```
com.sms/
├── MainApp.java (Entry point)
│
├── model/
│   ├── UserAccount.java (abstract base)
│   ├── AdminUser.java
│   ├── TeacherUser.java
│   ├── StudentUser.java
│   ├── Course.java
│   ├── Assessment.java
│   ├── Grade.java
│   ├── Attendance.java
│   └── [other POJOs]
│
├── controller/
│   ├── LoginController.java (login screen)
│   ├── AdminDashboardController.java
│   ├── TeacherDashboardController.java
│   ├── StudentDashboardController.java
│   └── [specialized controllers]
│
├── dao/
│   ├── UserDAO.java (users, admins, teachers, students)
│   ├── CourseDAO.java (courses, enrollments)
│   ├── GradeDAO.java (assessments, grades)
│   ├── AttendanceDAO.java (attendance records)
│   └── [other DAO classes]
│
├── factory/
│   └── UserFactory.java (creates user subclasses from ResultSet)
│
├── util/
│   ├── DatabaseConfig.java (Singleton, JDBC connection)
│   ├── SessionManager.java (stores current user)
│   ├── NavigationUtil.java (scene switching)
│   ├── GradeCalculator.java (percentage, letter grade logic)
│   ├── PasswordUtil.java (hashing verification)
│   ├── AlertUtil.java (JavaFX dialogs)
│   ├── ReportGenerator.java (PDF/Excel export)
│   └── [other utilities]
│
└── module-info.java (Java 9+ module system)
    ├── requires javafx.controls, javafx.fxml
    ├── requires java.sql
    ├── requires org.apache.pdfbox, org.apache.commons.csv
    ├── opens com.sms, com.sms.controller, com.sms.model (to JavaFX)
    └── exports com.sms.* (public APIs)

resources/
├── fxml/
│   ├── LoginView.fxml
│   ├── AdminDashboard.fxml
│   ├── TeacherDashboard.fxml
│   ├── StudentDashboard.fxml
│   └── [other screens]
│
└── css/
    └── styles.css (styling for all screens)

database/
├── schema.sql (CREATE TABLE statements)
└── sample_data.sql (test data)
```

---

## 5. Database Design & Operations

### 5.1 Database Schema Overview

```sql
Database: student_grade_db

Tables (with relationships):

users (base table)
  ├─ user_id (PK, auto-increment)
  ├─ username (UNIQUE, NOT NULL)
  ├─ password (SHA-256 hash)
  ├─ role (ENUM: ADMIN, TEACHER, STUDENT)
  ├─ full_name, email, phone
  ├─ is_active (boolean)
  └─ created_at (timestamp)

admins (FK: users.user_id)
  ├─ admin_id (PK)
  └─ department

teachers (FK: users.user_id)
  ├─ teacher_id (PK)
  ├─ employee_id (UNIQUE)
  ├─ department
  ├─ qualification
  └─ hire_date

students (FK: users.user_id)
  ├─ student_id (PK)
  ├─ student_number (UNIQUE)
  ├─ date_of_birth
  ├─ gender (ENUM: Male, Female, Other)
  ├─ address
  ├─ enrollment_year
  ├─ program
  └─ year_level

courses (FK: teachers.teacher_id)
  ├─ course_id (PK)
  ├─ course_code (UNIQUE)
  ├─ course_name
  ├─ credits
  ├─ semester, academic_year
  ├─ max_students
  └─ is_active

enrollments (FK: students.student_id, FK: courses.course_id)
  ├─ enrollment_id (PK)
  ├─ student_id
  ├─ course_id
  ├─ status (ENUM: ACTIVE, DROPPED, COMPLETED)
  └─ UNIQUE(student_id, course_id)

assessments (FK: courses.course_id)
  ├─ assessment_id (PK)
  ├─ course_id
  ├─ title
  ├─ type (ENUM: QUIZ, ASSIGNMENT, MIDTERM, FINAL, PROJECT, LAB)
  ├─ max_marks
  ├─ weight (0-100%)
  └─ due_date

grades (FK: students.student_id, assessments.assessment_id, users.user_id)
  ├─ grade_id (PK)
  ├─ student_id
  ├─ assessment_id
  ├─ marks_obtained
  ├─ letter_grade
  ├─ remarks
  ├─ graded_by (teacher user_id)
  ├─ graded_at (timestamp)
  └─ UNIQUE(student_id, assessment_id)

attendance (FK: students.student_id, courses.course_id, users.user_id)
  ├─ attendance_id (PK)
  ├─ student_id
  ├─ course_id
  ├─ att_date
  ├─ status (ENUM: PRESENT, ABSENT, LATE, EXCUSED)
  ├─ remarks
  ├─ recorded_by (teacher user_id)
  └─ UNIQUE(student_id, course_id, att_date)

grade_scale
  ├─ scale_id (PK)
  ├─ min_marks, max_marks
  ├─ letter_grade
  └─ scale_name (STANDARD, STRICT, GENEROUS)
```

### 5.2 CRUD Operations for Each Entity

#### **USER CRUD** (UserDAO)

**CREATE (Add User)**

```java
public boolean addStudent(StudentUser s, String plainPassword) {
  1. Hash password: hashedPass = PasswordUtil.hash(plainPassword)
  2. Insert into users table:
     INSERT INTO users (username, password, role, full_name, email, phone)
     VALUES (?, ?, 'STUDENT', ?, ?, ?)
  3. Get generated user_id
  4. Insert into students table:
     INSERT INTO students (user_id, student_number, dob, ...)
     VALUES (?, ?, ?, ...)
  5. Return success/failure
}

Similar for addTeacher(), addAdmin()
```

**READ (Authenticate Login)**

```java
public UserAccount authenticate(String username, String password) {
  1. Query: SELECT * FROM users WHERE username = ? AND is_active = 1
  2. If found:
     - Get stored password hash
     - Verify: PasswordUtil.verify(plainPassword, storedHash)
     - If valid:
       a. Get role from result
       b. Call: loadUserByIdAndRole(userId, role)
       c. Load role-specific data (admin, teacher, or student)
       d. Use UserFactory to create correct subclass
       e. Return UserAccount subclass
  3. If invalid or not found: return null
}

public UserAccount loadUserByIdAndRole(int userId, String role) {
  1. Based on role, execute different SQL:
     ADMIN:   SELECT u.*, a.* FROM users u JOIN admins a ON ...
     TEACHER: SELECT u.*, t.* FROM users u JOIN teachers t ON ...
     STUDENT: SELECT u.*, s.* FROM users u JOIN students s ON ...
  2. Map ResultSet to UserAccount via UserFactory.createFromResultSet()
  3. Return populated UserAccount subclass
}
```

**UPDATE (Modify User)**

```java
public boolean updateUser(UserAccount user) {
  1. Update users table: UPDATE users SET full_name=?, email=?, phone=?, ...
  2. If role-specific data (e.g., student year level):
     UPDATE students SET year_level=?, program=?, ...
  3. Return success/failure
}
```

**DELETE (Remove User)**

```java
public boolean deactivateUser(int userId) {
  1. Set flag: UPDATE users SET is_active = FALSE WHERE user_id = ?
  2. (OR use cascading delete if needed: DELETE FROM users WHERE user_id = ?)
  3. All dependent records (admins, students, teachers) auto-deleted via CASCADE
  4. Return success/failure
}
```

#### **GRADE CRUD** (GradeDAO)

**CREATE (Enter Grade)**

```java
public boolean addGrade(Grade g) {
  INSERT INTO grades (student_id, assessment_id, marks_obtained, letter_grade, remarks, graded_by)
  VALUES (?, ?, ?, ?, ?, ?)
}
```

**READ (Retrieve Grades)**

```java
public List<Grade> getGradesByAssessment(int assessmentId) {
  SELECT g.*, u.full_name, s.student_number, a.title
  FROM grades g
  JOIN students s ON g.student_id = s.student_id
  JOIN users u ON s.user_id = u.user_id
  JOIN assessments a ON g.assessment_id = a.assessment_id
  WHERE g.assessment_id = ?
  ORDER BY u.full_name
}

public List<Grade> getGradesByStudent(int studentId, int courseId) {
  SELECT g.*, a.title, a.max_marks, a.weight
  FROM grades g
  JOIN assessments a ON g.assessment_id = a.assessment_id
  WHERE g.student_id = ? AND a.course_id = ?
}
```

**UPDATE (Modify Grade)**

```java
public boolean updateGrade(Grade g) {
  UPDATE grades
  SET marks_obtained=?, letter_grade=?, remarks=?, graded_by=?
  WHERE grade_id = ?
}
```

**DELETE (Remove Grade)**

```java
public boolean deleteGrade(int gradeId) {
  DELETE FROM grades WHERE grade_id = ?
}
```

#### **ATTENDANCE CRUD** (AttendanceDAO)

**CREATE (Record Attendance)**

```java
public boolean markAttendance(Attendance a) {
  INSERT INTO attendance (student_id, course_id, att_date, status, remarks, recorded_by)
  VALUES (?, ?, ?, ?, ?, ?)
  ON DUPLICATE KEY UPDATE status=?, remarks=?  -- update if exists
}
```

**READ (Retrieve Attendance)**

```java
public List<Attendance> getAttendanceByDate(int courseId, LocalDate date) {
  SELECT a.*, u.full_name, s.student_number
  FROM attendance a
  JOIN students s ON a.student_id = s.student_id
  JOIN users u ON s.user_id = u.user_id
  WHERE a.course_id = ? AND a.att_date = ?
}

public double getAttendancePercentage(int studentId, int courseId) {
  1. Get summary: SELECT status, COUNT(*) FROM attendance WHERE student_id=? GROUP BY status
  2. Calculate: (PRESENT + LATE) / total * 100
  3. Return percentage
}
```

**UPDATE (Modify Attendance)**

```java
public boolean updateAttendance(Attendance a) {
  UPDATE attendance SET status=?, remarks=? WHERE attendance_id = ?
}
```

### 5.3 Database Connection Flow

```
Application Start
  └─ DatabaseConfig.getInstance()
     ├─ First time: private constructor creates connection
     │  ├─ Class.forName("com.mysql.cj.jdbc.Driver") → loads MySQL driver
     │  ├─ DriverManager.getConnection(URL, username, password)
     │  └─ Connection stored in static instance
     └─ Subsequent calls: return existing instance

DAO Usage
  └─ new UserDAO()
     └─ this.conn = DatabaseConfig.getInstance().getConnection()
        └─ Returns the SINGLETON database connection

On Any Query
  ├─ PreparedStatement ps = conn.prepareStatement(sql)
  ├─ ps.setInt(1, value), ps.setString(2, value) ... (parameterized)
  ├─ ResultSet rs = ps.executeQuery()
  ├─ while(rs.next()) { map ResultSet to model object }
  └─ rs.close(), ps.close()

On Application Exit (MainApp.stop())
  └─ DatabaseConfig.getInstance().closeConnection()
     └─ If connection not closed: connection.close()
```

**Key Features**:

- **Singleton ensures one connection** throughout app lifetime
- **Parameterized queries prevent SQL injection** (? placeholders)
- **try-with-resources** auto-closes statements
- **Connection pooling not needed** for desktop app (single user)

---

## 6. User Access Control

### 6.1 Session Management (SessionManager)

```java
public class SessionManager {
    private static UserAccount currentUser;  // holds logged-in user

    public static void setCurrentUser(UserAccount user) {
        currentUser = user;  // called after successful login
    }

    public static UserAccount getCurrentUser() {
        return currentUser;  // any controller can retrieve
    }

    public static boolean isLoggedIn() {
        return currentUser != null;  // check if user is authenticated
    }

    public static void clearSession() {
        currentUser = null;  // called on logout
    }
}
```

**Usage in Controllers**:

```java
// In AdminDashboardController.initialize()
UserAccount user = SessionManager.getCurrentUser();
userLabel.setText(user.getFullName() + " | " + user.getRoleDisplayName());

// On logout
NavigationUtil.logout();
  └─ SessionManager.clearSession()
  └─ NavigationUtil.navigateTo("LoginView.fxml")
```

### 6.2 Role-Based Access Control (RBAC)

| Role        | Permissions               | Dashboard        | Can Access                                                                                                                                                         |
| ----------- | ------------------------- | ---------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| **ADMIN**   | Full system control       | AdminDashboard   | <ul><li>Manage users (create/edit/deactivate)</li><li>Manage courses</li><li>View all grades/attendance</li><li>Generate reports</li><li>System settings</li></ul> |
| **TEACHER** | Course & grade management | TeacherDashboard | <ul><li>View assigned courses</li><li>Enter grades for own courses</li><li>Record attendance</li><li>View assigned students</li><li>Export reports</li></ul>       |
| **STUDENT** | View own records          | StudentDashboard | <ul><li>View own grades</li><li>View own attendance</li><li>View personal info</li><li>Download transcripts</li></ul>                                              |

### 6.3 Access Pattern Implementation

```
Login Screen (No Authentication Required)
↓
LoginController.handleLogin()
  ├─ UserDAO.authenticate(username, password)
  │  └─ Query: users table where username=? and is_active=1
  │  └─ Verify password hash
  │  └─ Return UserAccount (with role embedded)
  ├─ On Success: SessionManager.setCurrentUser(user)
  ├─ Get dashboard via polymorphism: user.getDashboardPath()
  └─ Navigate to appropriate dashboard

Admin Dashboard
  ├─ @FXML methods check: SessionManager.getCurrentUser() instanceof AdminUser
  ├─ If user not admin → show error (shouldn't happen if navigation correct)
  └─ Access: UserDAO, CourseDAO, GradeDAO (full access)

Teacher Dashboard
  ├─ @FXML methods check: SessionManager.getCurrentUser() instanceof TeacherUser
  ├─ Access restricted by:
  │  ├─ Only see courses where teacherId = getCurrentUser().getTeacherId()
  │  ├─ Only modify grades for own courses
  │  └─ graded_by field auto-set to current teacher
  └─ Access: CourseDAO, GradeDAO, AttendanceDAO (filtered)

Student Dashboard
  ├─ @FXML methods check: SessionManager.getCurrentUser() instanceof StudentUser
  ├─ Access restricted by:
  │  ├─ Only see grades where studentId = getCurrentUser().getStudentId()
  │  ├─ Only see attendance for own enrollments
  │  └─ Read-only access (no grade/attendance modification)
  └─ Access: GradeDAO, AttendanceDAO (read-only filtered queries)

Logout
  ├─ NavigationUtil.logout()
  │  ├─ SessionManager.clearSession() → currentUser = null
  │  └─ Navigate back to LoginView.fxml
  └─ User must login again
```

---

## 7. Controllers & Event Handlers

### 7.1 LoginController

**Responsibilities**:

- Validate username/password input
- Call UserDAO.authenticate()
- Store user in SessionManager on success
- Navigate to appropriate dashboard
- Handle "Forgot Password" dialog

**Key Methods**:

```java
@FXML
private void handleLogin() {
    // 1. Input validation
    if (username.isEmpty() || password.isEmpty()) {
        showError("Fill all fields");
        return;
    }

    // 2. Authenticate
    UserAccount user = userDAO.authenticate(username, password);
    if (user == null) {
        showError("Invalid credentials");
        return;
    }

    // 3. Store session
    SessionManager.setCurrentUser(user);

    // 4. Navigate (polymorphic)
    NavigationUtil.navigateTo(user.getDashboardPath());
        // Returns "AdminDashboard.fxml" or "TeacherDashboard.fxml" etc.
}

private void showError(String message) {
    errorLabel.setText(message);
    errorLabel.setVisible(true);
}
```

**User Interactions**:

- Click "Sign In" button → handleLogin()
- Press Enter in password field → handleLogin() (convenience)
- Click "Forgot Password" → handleForgotPassword() → dialog

### 7.2 AdminDashboardController

**Responsibilities**:

- Display admin dashboard with stats
- Navigate between admin functions
- Perform CRUD operations (users, courses)
- Generate reports

**Key Methods**:

```java
@FXML
public void initialize() {
    // Get current user from session
    UserAccount user = SessionManager.getCurrentUser();
    userLabel.setText(user.getFullName() + " | " + user.getRoleDisplayName());
    showDashboard();  // show stats initially
}

// Navigation methods (sidebar buttons call these)
@FXML public void showStudents() {
    pageTitle.setText("Student Management");
    subContent.getChildren().clear();
    subContent.getChildren().add(buildStudentPanel());
}

// Build dynamic panels
private VBox buildStudentPanel() {
    VBox panel = new VBox(12);

    // Toolbar with search + add button
    HBox toolbar = new HBox(10);
    TextField searchField = new TextField();
    Button addBtn = new Button("+ Add Student");
    addBtn.setOnAction(e -> handleAddStudent());

    // TableView with columns
    TableView<StudentUser> table = new TableView<>();
    table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

    TableColumn<StudentUser, String> colNumber = new TableColumn<>("Student No.");
    colNumber.setCellValueFactory(new PropertyValueFactory<>("studentNumber"));

    TableColumn<StudentUser, String> colName = new TableColumn<>("Full Name");
    colName.setCellValueFactory(new PropertyValueFactory<>("fullName"));

    // ... more columns

    // Populate table
    List<StudentUser> students = userDAO.getAllStudents();
    table.setItems(FXCollections.observableArrayList(students));

    panel.getChildren().addAll(toolbar, table);
    return panel;
}

@FXML private void handleAddStudent() {
    // Open dialog or form for adding student
    StudentUser newStudent = new StudentUser();
    // ... collect input from user
    // Call: userDAO.addStudent(newStudent, plainPassword)
}

@FXML public void handleLogout() {
    NavigationUtil.logout();  // clears session, goes to login
}
```

**UI Pattern**:

- Single BorderPane with sidebar navigation
- Clicking sidebar button → calls controller method → rebuilds center VBox
- No scene switching (SPA-like behavior improves responsiveness)

### 7.3 TeacherDashboardController

**Similar pattern to AdminDashboard** but with teacher-specific panels:

- **My Courses**: List courses taught by current teacher
- **Grade Entry**: Select course/assessment → input grades for students
- **Attendance**: Record attendance for each course/date
- **My Students**: View list of students in my courses

**Key Difference**:

- All queries filtered by current teacher:
  ```java
  TeacherUser teacher = (TeacherUser) SessionManager.getCurrentUser();
  List<Course> courses = courseDAO.getCoursesByTeacher(teacher.getTeacherId());
  ```
- Grade-entry auto-sets `graded_by` to current teacher
- Can only modify grades for own courses

### 7.4 StudentDashboardController

**Read-only access** to personal data:

- **My Grades**: View grades only for courses enrolled in
- **My Attendance**: View attendance summary for each course
- **My Info**: Display personal student details
- **Transcript**: PDF view of grades

**Key Difference**:

- No add/edit/delete buttons
- All queries filtered by current student:
  ```java
  StudentUser student = (StudentUser) SessionManager.getCurrentUser();
  List<Grade> grades = gradeDAO.getGradesByStudent(student.getStudentId(), courseId);
  ```

---

## 8. Utility Classes

### 8.1 DatabaseConfig (Singleton)

**Purpose**: Manage single database connection for entire app

```java
public class DatabaseConfig {
    private static DatabaseConfig instance;
    private Connection connection;

    private DatabaseConfig() {
        // Initialize JDBC connection in constructor
        Class.forName("com.mysql.cj.jdbc.Driver");
        this.connection = DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/student_grade_db?useSSL=false&serverTimezone=UTC",
            "root", "root"
        );
    }

    public static synchronized DatabaseConfig getInstance() {
        if (instance == null) instance = new DatabaseConfig();
        return instance;
    }

    public Connection getConnection() {
        // Auto-reconnect if closed
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(...);
        }
        return connection;
    }

    public void closeConnection() {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
```

### 8.2 SessionManager (Static Storage)

**Purpose**: Store currently logged-in user for access across controllers

```java
public class SessionManager {
    private static UserAccount currentUser;

    public static void setCurrentUser(UserAccount user) {
        currentUser = user;
    }

    public static UserAccount getCurrentUser() {
        return currentUser;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    public static void clearSession() {
        currentUser = null;
    }
}
```

**Usage**: Replaces need to pass user object between controllers

### 8.3 NavigationUtil (FXML Scene Switching)

**Purpose**: Centralize scene loading and switching logic

```java
public class NavigationUtil {
    public static void navigateTo(String fxmlPath) {
        // Load FXML
        Parent root = FXMLLoader.load(
            NavigationUtil.class.getResource("/fxml/" + fxmlPath)
        );

        // Create scene with CSS
        Scene scene = new Scene(root);
        scene.getStylesheets().add(
            NavigationUtil.class.getResource("/css/styles.css").toExternalForm()
        );

        // Switch scene on main stage
        Stage stage = MainApp.primaryStage;
        stage.setScene(scene);
        stage.show();
    }

    public static void openModal(String fxmlPath, String title) {
        // Similar, but creates new Stage (modal window)
    }

    public static void logout() {
        SessionManager.clearSession();
        navigateTo("LoginView.fxml");
    }
}
```

### 8.4 GradeCalculator (Business Logic)

**Purpose**: Calculate final grades independent of database

```java
public class GradeCalculator {
    public static double calculateFinalPercentage(
            List<Grade> grades,
            Map<Integer, Assessment> assessments) {

        double weightedSum = 0;
        double totalWeight = 0;

        for (Grade g : grades) {
            if (g.getMarksObtained() < 0) continue;  // skip ungraded

            Assessment a = assessments.get(g.getAssessmentId());
            double percentage = (g.getMarksObtained() / a.getMaxMarks()) * 100;
            weightedSum += percentage * a.getWeight();
            totalWeight += a.getWeight();
        }

        return totalWeight > 0 ? weightedSum / totalWeight : -1;
    }

    public static String getLetterGrade(double percentage, String scale) {
        return switch(scale) {
            case "STRICT" -> (percentage >= 95) ? "A+" : (percentage >= 90) ? "A" : ...
            case "GENEROUS" -> (percentage >= 85) ? "A+" : ...
            default -> (percentage >= 90) ? "A+" : ...  // STANDARD
        };
    }

    public static double getGradePoint(String letterGrade) {
        // Convert A+ → 4.0, A → 4.0, A- → 3.7, B+ → 3.3, etc.
        return switch(letterGrade) {
            case "A+" -> 4.0;
            case "A" -> 4.0;
            case "A-" -> 3.7;
            case "B+" -> 3.3;
            // ... more cases
            default -> 0.0;  // F
        };
    }
}
```

### 8.5 AlertUtil (JavaFX Dialogs)

**Purpose**: Provide consistent alert styles across app

```java
public class AlertUtil {
    public static void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static boolean showConfirm(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
}
```

### 8.6 PasswordUtil

**Purpose**: Hash and verify passwords securely

```java
public class PasswordUtil {
    public static String hash(String plainPassword) {
        // Use SHA-256 or bcrypt
        return DigestUtils.sha256Hex(plainPassword);
    }

    public static boolean verify(String plainPassword, String storedHash) {
        return hash(plainPassword).equals(storedHash);
    }
}
```

### 8.7 Other Utilities

- **ReportGenerator**: PDF/Excel export of grades, transcripts
- **ReportGenerator**: (already mentioned)

---

## 9. Integration & Communication Flow

### 9.1 Complete User Login-to-Dashboard Flow

```
┌─ Start Application
│  └─ MainApp.start(primaryStage)
│     └─ FXMLLoader.load("LoginView.fxml")
│        └─ LoginController.initialize()
│           └─ Display login form
│
├─ User enters credentials & clicks "Sign In"
│  └─ LoginController.handleLogin()
│
│  1. Input Validation
│     └─ Check: username/password not empty
│
│  2. Authentication
│     └─ Call: UserDAO.authenticate(username, password)
│        ├─ SQLException caught → return null
│        ├─ Query users table: SELECT * FROM users WHERE username=? AND is_active=1
│        ├─ Check if found
│        ├─ Verify password: PasswordUtil.verify(plainPass, storedHash)
│        └─ Call: loadUserByIdAndRole(userId, role)
│           ├─ Execute role-specific SQL join
│           ├─ Fetch ResultSet with user + role-specific data
│           └─ Return: UserFactory.createFromResultSet(rs, role)
│              ├─ switch(role)
│              ├─ Create AdminUser / TeacherUser / StudentUser
│              └─ Populate all fields from ResultSet
│
│  3. Session Management
│     └─ SessionManager.setCurrentUser(userAccount)
│
│  4. Navigation
│     └─ Get dashboard path (polymorphic):
│        ├─ AdminUser.getDashboardPath() → "AdminDashboard.fxml"
│        ├─ TeacherUser.getDashboardPath() → "TeacherDashboard.fxml"
│        └─ StudentUser.getDashboardPath() → "StudentDashboard.fxml"
│
│     └─ NavigationUtil.navigateTo(fxmlPath)
│        ├─ FXMLLoader.load("/fxml/" + fxmlPath)
│        ├─ Create Scene with CSS
│        ├─ primaryStage.setScene(newScene)
│        └─ primaryStage.show()
│
└─ Dashboard Controller initializes
   ├─ get CurrentUser: SessionManager.getCurrentUser()
   ├─ Display user info in header
   ├─ Load initial data (e.g., stats)
   │  └─ Call appropriate DAO methods
   └─ Display dashboard content
```

### 9.2 Teacher Grade Entry Flow

```
┌─ Teacher clicks "Grade Entry" in dashboard
│  └─ TeacherDashboardController.showGradeEntry()
│
├─ UI displays:
│  ├─ Dropdown: Select Course (filtered by teacherId)
│  ├─ Dropdown: Select Assessment
│  └─ TableView with students and grade input fields
│
├─ User selects course and assessment
│  └─ TeacherDashboardController.onCourseSelected()
│     ├─ Call: courseDAO.getCourseById(courseId)
│     ├─ Call: gradeDAO.getAssessmentsByCourse(courseId)
│     ├─ Call: gradeDAO.getGradesByAssessment(assessmentId)
│     └─ Populate TableView
│
├─ Teacher enters marks for students
│  ├─ User types mark in TableCell
│  └─ On cell focus lost:
│     └─ Validate mark (0 to maxMarks)
│        ├─ If invalid: show error
│        └─ If valid: calculate letter grade
│           └─ GradeCalculator.getLetterGrade(percentage, scale)
│
├─ Teacher clicks "Save Grades"
│  └─ TeacherDashboardController.handleSaveGrades()
│     ├─ Get current teacher: SessionManager.getCurrentUser()
│     ├─ For each grade:
│     │  ├─ Call: gradeDAO.addGrade() or gradeDAO.updateGrade()
│     │  ├─ Set graded_by to current teacher id
│     │  ├─ Set graded_at to current timestamp
│     │  └─ DB executes: INSERT/UPDATE grades table
│     └─ Show success alert
```

### 9.3 Student View Grades Flow

```
┌─ Student clicks "My Grades" in dashboard
│  └─ StudentDashboardController.showGrades()
│
├─ UI displays course selection
│  ├─ Get current student: SessionManager.getCurrentUser()
│  ├─ Call: courseDAO.getCoursesForStudent(studentId)
│     └─ Query: SELECT c.* FROM courses c
│              JOIN enrollments e ON c.course_id = e.course_id
│              WHERE e.student_id = ? AND e.status = 'ACTIVE'
│  └─ Populate dropdown
│
├─ Student selects course
│  └─ StudentDashboardController.onCourseSelected(courseId)
│
├─ Display assessments & grades
│  ├─ Call: gradeDAO.getGradesByStudent(studentId, courseId)
│  ├─ Call: gradeDAO.getAssessmentsByCourse(courseId)
│  ├─ Populate TableView:
│  │  ├─ Assessment Title | Marks Obtained | Max Marks | Percentage | Letter Grade
│  │  ├─ QUIZ 1: 18 / 20 = 90% = A
│  │  ├─ MIDTERM: 35 / 50 = 70% = B-
│  │  └─ FINAL: 42 / 50 = 84% = A-
│  └─ Calculate final grade
│     ├─ Collect all grade objects
│     ├─ Create assessment map
│     ├─ Call: GradeCalculator.calculateFinalPercentage(grades, assessments)
│     ├─ Get course grading scale
│     ├─ Call: GradeCalculator.getLetterGrade(finalPercent, scale)
│     └─ Display: Final Grade = 79% = B-
│
└─ Display complete grade summary
```

---

## 10. Detailed User Workflows: Input-Process-Output

This section documents the complete data flow for every major user interaction from login through report export, showing exactly what users input, how the system processes it, and what output is displayed.

### 10.1 LOGIN WORKFLOW

#### **STEP 1: User Visits Application**

**INPUT**: Application start

```
├─ MainApp.start(Stage)
├─ Loads LoginView.fxml from resources
└─ Displays login screen
```

**PROCESS**:

```
├─ LoginController.initialize()
│  ├─ Create new UserDAO()
│  ├─ Initialize JavaFX components (@FXML fields)
│  ├─ Bind event handlers:
│  │  ├─ usernameField.onKeyPressed → navigate to password (Enter)
│  │  ├─ passwordField.onKeyPressed → handleLogin() (Enter)
│  │  └─ loginButton.onAction → handleLogin()
│  └─ Set errorLabel visibility = false
└─ Wait for user input
```

**OUTPUT**:

```
✓ Login Form displayed with:
  ├─ Username text field (prompt: "Username")
  ├─ Password field (masked)
  ├─ "Sign In" button
  ├─ "Forgot Password?" link
  └─ Error label (hidden initially)
```

---

#### **STEP 2: User Enters Credentials**

**INPUT**: User types username and password, clicks "Sign In"

```
├─ usernameField text: "john.doe" ← User enters
├─ passwordField text: "myPassword123" ← User enters
└─ loginButton clicked ← User action
```

**PROCESS**:

```
└─ LoginController.handleLogin()
   ├─ INPUT VALIDATION:
   │  ├─ username.trim() → "john.doe" (remove whitespace)
   │  ├─ if (username.isEmpty())? → YES
   │  │   ├─ showError("Please enter your username.")
   │  │   ├─ usernameField.requestFocus()
   │  │   └─ RETURN (stop here)
   │  └─ if (password.isEmpty())? → NO, continue
   │
   ├─ DISABLE LOGIN BUTTON (prevent double-click):
   │  ├─ loginButton.setDisable(true)
   │  └─ loginButton.setText("Signing in...")
   │
   ├─ CALL DATABASE AUTHENTICATION:
   │  └─ userDAO.authenticate("john.doe", "myPassword123")
   │
   │     ├─ In UserDAO.authenticate():
   │     │  ├─ PREPARE SQL:
   │     │  │  └─ "SELECT * FROM users WHERE username = ? AND is_active = 1"
   │     │  │
   │     │  ├─ SET PARAMETERS:
   │     │  │  └─ ps.setString(1, "john.doe")
   │     │  │
   │     │  ├─ EXECUTE QUERY:
   │     │  │  └─ ResultSet rs = ps.executeQuery()
   │     │  │
   │     │  ├─ DATABASE RETURNS:
   │     │  │  └─ 1 row with:
   │     │  │     ├─ user_id: 5
   │     │  │     ├─ username: "john.doe"
   │     │  │     ├─ password: "a1e8f3d4c2b... (SHA-256 hash)"
   │     │  │     ├─ role: "TEACHER"
   │     │  │     ├─ full_name: "John Doe"
   │     │  │     ├─ email: "john@school.edu"
   │     │  │     ├─ phone: "555-1234"
   │     │  │     └─ is_active: 1
   │     │  │
   │     │  ├─ VERIFY PASSWORD:
   │     │  │  ├─ storedHash = rs.getString("password")
   │     │  │  │  └─ "a1e8f3d4c2b... (from DB)"
   │     │  │  │
   │     │  │  ├─ plainPassword = "myPassword123" (from user input)
   │     │  │  │
   │     │  │  ├─ Call PasswordUtil.verify(plainPassword, storedHash)
   │     │  │  │  ├─ PasswordUtil.hash("myPassword123")
   │     │  │  │  │  ├─ MessageDigest.getInstance("SHA-256")
   │     │  │  │  │  ├─ digest.digest(plainPassword.getBytes())
   │     │  │  │  │  ├─ Convert to hex: "a1e8f3d4c2b..."
   │     │  │  │  │  └─ RETURN computed hash
   │     │  │  │  │
   │     │  │  │  └─ Compare: "a1e8f3d4c2b...".equals("a1e8f3d4c2b...")
   │     │  │  │     └─ TRUE (password matches!)
   │     │  │  │
   │     │  │  └─ RETURN true (password verified)
   │     │  │
   │     │  ├─ GET USER ROLE:
   │     │  │  └─ role = rs.getString("role") → "TEACHER"
   │     │  │
   │     │  ├─ LOAD ROLE-SPECIFIC DATA:
   │     │  │  └─ loadUserByIdAndRole(5, "TEACHER")
   │     │  │     ├─ Execute role-specific SQL:
   │     │  │     │  └─ SELECT u.*, t.teacher_id, t.employee_id, ...
   │     │  │     │     FROM users u
   │     │  │     │     JOIN teachers t ON u.user_id = t.user_id
   │     │  │     │     WHERE u.user_id = 5
   │     │  │     │
   │     │  │     ├─ DATABASE RETURNS (joined row):
   │     │  │     │  ├─ user_id: 5, username: "john.doe", ...
   │     │  │     │  ├─ teacher_id: 12
   │     │  │     │  ├─ employee_id: "T00567"
   │     │  │     │  ├─ department: "Mathematics"
   │     │  │     │  ├─ qualification: "M.Sc. Mathematics"
   │     │  │     │  └─ hire_date: "2020-08-15"
   │     │  │     │
   │     │  │     ├─ CREATE USER SUBCLASS:
   │     │  │     │  └─ UserFactory.createFromResultSet(rs, "TEACHER")
   │     │  │     │     ├─ switch("TEACHER") → case "TEACHER":
   │     │  │     │     ├─ buildTeacher(rs)
   │     │  │     │     │  ├─ TeacherUser teacher = new TeacherUser()
   │     │  │     │     │  ├─ populateBase(teacher, rs)
   │     │  │     │     │  │  └─ Set: userId, username, passwordHash, role, fullName, email, phone, active
   │     │  │     │     │  ├─ Set teacher-specific fields:
   │     │  │     │     │  │  ├─ teacher.setTeacherId(12)
   │     │  │     │     │  │  ├─ teacher.setEmployeeId("T00567")
   │     │  │     │     │  │  ├─ teacher.setDepartment("Mathematics")
   │     │  │     │     │  │  ├─ teacher.setQualification("M.Sc. Mathematics")
   │     │  │     │     │  │  └─ teacher.setHireDate(LocalDate.parse("2020-08-15"))
   │     │  │     │     │  └─ RETURN teacher (TeacherUser object)
   │     │  │     │     └─ Type: TeacherUser
   │     │  │     │
   │     │  │     └─ RETURN teacher (fully populated TeacherUser)
   │     │  │
   │     │  └─ RETURN teacher (UserAccount reference)
   │     │
   │     └─ Returns: TeacherUser object (or null if auth fails)
   │
   ├─ RE-ENABLE LOGIN BUTTON:
   │  ├─ loginButton.setDisable(false)
   │  └─ loginButton.setText("Sign In")
   │
   ├─ CHECK AUTHENTICATION RESULT:
   │  ├─ if (user == null)? → NO (success!)
   │  └─ Continue to session management
   │
   ├─ SESSION MANAGEMENT:
   │  └─ SessionManager.setCurrentUser(user)
   │     └─ Static field currentUser = teacher (TeacherUser object)
   │        (Now available app-wide via SessionManager.getCurrentUser())
   │
   ├─ CLEAR ERROR DISPLAY:
   │  └─ errorLabel.setVisible(false)
   │
   ├─ NAVIGATE TO DASHBOARD:
   │  ├─ user.getDashboardPath() (polymorphic call)
   │  │  └─ TeacherUser.getDashboardPath()
   │  │     └─ RETURN "TeacherDashboard.fxml"
   │  │
   │  └─ NavigationUtil.navigateTo("TeacherDashboard.fxml")
   │     ├─ FXMLLoader.load("/fxml/TeacherDashboard.fxml")
   │     ├─ Create Scene with CSS styling
   │     ├─ primaryStage.setScene(newScene)
   │     └─ primaryStage.show()
   │
   └─ TeacherDashboardController.initialize() is called automatically
      └─ (See STEP 3)
```

**OUTPUT**:

```
✓ Successful Login:
  ├─ TeacherDashboard loads
  ├─ Header displays: "John Doe | Teacher"
  ├─ Sidebar shows navigation options:
  │  ├─ Dashboard
  │  ├─ My Courses
  │  ├─ Grade Entry
  │  └─ Attendance
  ├─ Center panel shows dashboard overview
  └─ Logout button visible in header

✗ Failed Login (password wrong):
  ├─ Error message: "Invalid username or password. Please try again."
  ├─ errorLabel becomes visible
  ├─ passwordField is cleared
  ├─ passwordField receives focus
  ├─ User remains on LoginView
  └─ Can retry login
```

---

### 10.2 ADMIN MANAGES STUDENTS WORKFLOW

#### **STEP 1: Admin Views Student List**

**INPUT**: Admin clicks "Student Management" in sidebar

```
└─ AdminDashboardController.showStudents() called
```

**PROCESS**:

```
└─ showStudents()
   ├─ pageTitle.setText("Student Management")
   ├─ statsRow visibility = false (hide dashboard stats)
   ├─ Clear existing content: subContent.getChildren().clear()
   ├─ Build student panel: buildStudentPanel()
   │
   └─ In buildStudentPanel():
      ├─ Create VBox (main container)
      ├─ Create toolbar HBox:
      │  ├─ TextField for search (prompt: "Search students...")
      │  └─ Button "+ Add Student"
      │
      ├─ Create TableView<StudentUser>:
      │  ├─ Column: Student No. ← studentNumber field
      │  ├─ Column: Full Name ← fullName field
      │  ├─ Column: Program ← program field
      │  ├─ Column: Year ← yearLevel field
      │  ├─ Column: Email ← email field
      │  ├─ Column: Status ← active flag (Active/Inactive)
      │  └─ Column: Actions (View, Edit, Delete buttons)
      │
      ├─ LOAD STUDENT DATA:
      │  └─ userDAO.getAllStudents()
      │     ├─ PREPARE SQL:
      │     │  └─ SELECT u.*, s.* FROM users u
      │     │     JOIN students s ON u.user_id = s.user_id
      │     │     WHERE u.role = 'STUDENT'
      │     │     ORDER BY s.student_number
      │     │
      │     ├─ EXECUTE & FETCH:
      │     │  └─ Database returns 150 student rows:
      │     │     ├─ Row 1: STU001, Alice Johnson, 2024, 2, alice@school.edu, Active
      │     │     ├─ Row 2: STU002, Bob Smith, CSC, 2, bob@school.edu, Active
      │     │     ├─ Row 3: STU003, Charlie Brown, CSC, 1, charlie@school.edu, Inactive
      │     │     └─ ... (150 total)
      │     │
      │     ├─ MAP TO OBJECTS:
      │     │  └─ For each row:
      │     │     └─ Create StudentUser object with all fields populated
      │     │
      │     └─ RETURN List<StudentUser> (150 objects in memory)
      │
      ├─ POPULATE TABLE:
      │  └─ table.setItems(FXCollections.observableArrayList(students))
      │     └─ TableView now displays all 150 students
      │        (Only visible rows rendered due to virtualization)
      │
      └─ Add panel to dashboard
         └─ subContent.getChildren().add(studentPanel)
```

**OUTPUT**:

```
✓ Student Management Panel displayed:

  ┌─ Toolbar ─────────────────────────────────────────┐
  │ [Search students...] [+ Add Student Button]       │
  └───────────────────────────────────────────────────┘

  ┌─ TableView ───────────────────────────────────────┐
  │ St# │ Name              │ Program  │ Yr │ Email   │
  ├─────┼───────────────────┼──────────┼────┼─────────┤
  │STU0│ Alice Johnson     │ CSC      │ 2  │ alice@..│ [View] [Edit] [Delete]
  │STU0│ Bob Smith         │ CSC      │ 2  │ bob@... │ [View] [Edit] [Delete]
  │STU0│ Charlie Brown     │ CSC      │ 1  │ charlie │ [View] [Edit] [Delete]
  │ ... │ (more rows)       │          │    │         │
  └───────────────────────────────────────────────────┘

  Status: 150 students loaded
```

---

#### **STEP 2: Admin Searches for Student**

**INPUT**: Admin types "alice" in search field

```
└─ searchField text: "alice"
```

**PROCESS**:

```
└─ searchField.textProperty().addListener((obs, oldVal, newVal) → {
   ├─ Get search term: "alice" (case-insensitive)
   ├─ Filter table rows:
   │  ├─ For each StudentUser in table:
   │  │  ├─ Check if: fullName.toLowerCase().contains("alice")
   │  │  │     or studentNumber.contains("alice")
   │  │  │     or email.contains("alice")
   │  │  │
   │  │  ├─ If match: keep row visible
   │  │  └─ If no match: hide row
   │  │
   │  └─ Result: Only rows containing "alice" shown
   │
   └─ Table re-renders with filtered data
})
```

**OUTPUT**:

```
✓ Filtered Results:

  ┌─ TableView (Filtered) ─────────────────────────────┐
  │ St# │ Name              │ Program  │ Yr │ Email   │
  ├─────┼───────────────────┼──────────┼────┼─────────┤
  │STU0│ Alice Johnson     │ CSC      │ 2  │ alice@..│ [View] [Edit] [Delete]
  └───────────────────────────────────────────────────┘

  Status: 1 student matches "alice"
```

---

#### **STEP 3: Admin Clicks "Edit" for a Student**

**INPUT**: Admin clicks [Edit] button for "Alice Johnson"

```
└─ editBtn.setOnAction(e → handleEditStudent(alice))
```

**PROCESS**:

```
└─ handleEditStudent(StudentUser selectedStudent)
   ├─ selectedStudent = Alice Johnson (StudentUser object)
   ├─ Open modal dialog with edit form:
   │
   ├─ COLLECT CURRENT DATA:
   │  ├─ Full Name: Alice Johnson
   │  ├─ Student Number: STU001
   │  ├─ Email: alice@school.edu
   │  ├─ Phone: 555-1234
   │  ├─ Program: CSC
   │  ├─ Year Level: 2
   │  └─ Gender: Female
   │
   ├─ DISPLAY FORM FIELDS (pre-populated):
   │  ├─ TextField fullName: "Alice Johnson"
   │  ├─ TextField email: "alice@school.edu"
   │  ├─ TextField phone: "555-1234"
   │  ├─ ComboBox program: ["CSC", "MATH", "ENG", ...] (selected: "CSC")
   │  ├─ ComboBox yearLevel: [1, 2, 3, 4] (selected: 2)
   │  ├─ ComboBox gender: ["Male", "Female", "Other"] (selected: "Female")
   │  ├─ Button "Save Changes"
   │  └─ Button "Cancel"
   │
   └─ Wait for user input
```

**OUTPUT**:

```
✓ Edit Student Modal displayed:

┌─ Edit Student ─────────────────────────┐
│ Student Number: STU001 (read-only)     │
│                                         │
│ Full Name:                              │
│ [Alice Johnson___________________]      │
│                                         │
│ Email:                                  │
│ [alice@school.edu________________]      │
│                                         │
│ Phone:                                  │
│ [555-1234___________________]            │
│                                         │
│ Program:    [CSC ▼]                     │
│ Year Level: [2 ▼]                       │
│ Gender:     [Female ▼]                  │
│                                         │
│ [Save Changes] [Cancel]                 │
└─────────────────────────────────────────┘
```

---

#### **STEP 4: Admin Modifies and Saves Student**

**INPUT**: Admin changes Year Level from 2 to 3, clicks "Save Changes"

```
├─ yearLevelCombo.setValue(3) ← User selects
└─ saveBtn.setOnAction(e → handleSaveStudent()) ← User clicks
```

**PROCESS**:

```
└─ handleSaveStudent()
   ├─ COLLECT FORM VALUES:
   │  ├─ fullName = "Alice Johnson" (unchanged)
   │  ├─ email = "alice@school.edu" (unchanged)
   │  ├─ phone = "555-1234" (unchanged)
   │  ├─ program = "CSC" (unchanged)
   │  ├─ yearLevel = 3 ← CHANGED
   │  └─ gender = "Female" (unchanged)
   │
   ├─ INPUT VALIDATION:
   │  ├─ if (fullName.trim().isEmpty()) → NO
   │  ├─ if (email.trim().isEmpty()) → NO
   │  ├─ if (yearLevel == null) → NO
   │  └─ All validations pass ✓
   │
   ├─ UPDATE OBJECT:
   │  └─ selectedStudent.setYearLevel(3)
   │     └─ studentUser.yearLevel = 3
   │
   ├─ DATABASE UPDATE:
   │  └─ userDAO.updateUser(selectedStudent)
   │     ├─ PREPARE SQL:
   │     │  └─ UPDATE students SET year_level = ? WHERE student_id = ?
   │     │
   │     ├─ SET PARAMETERS:
   │     │  ├─ ps.setInt(1, 3) ← year_level
   │     │  └─ ps.setInt(2, 1) ← student_id for Alice
   │     │
   │     ├─ EXECUTE:
   │     │  └─ Database executes UPDATE
   │     │     └─ 1 row affected ✓
   │     │
   │     └─ RETURN true
   │
   ├─ REFRESH TABLE:
   │  ├─ studentList = userDAO.getAllStudents() ← Fetch fresh data
   │  ├─ table.setItems(new updated list)
   │  └─ TableView re-renders
   │
   ├─ SHOW SUCCESS:
   │  └─ AlertUtil.showInfo("Success", "Student updated successfully!")
   │
   └─ Close modal dialog
```

**OUTPUT**:

```
✓ Success Alert displayed:
  "Student updated successfully!"

✓ TableView refreshed:
  STU001 │ Alice Johnson │ CSC │ 3 │ alice@... ← Year changed from 2 to 3

✓ Modal closes, returns to Student Management panel
```

---

### 10.3 TEACHER ENTERS GRADES WORKFLOW

#### **STEP 1: Teacher Views Grade Entry Interface**

**INPUT**: Teacher clicks "Grade Entry" in sidebar

```
└─ TeacherDashboardController.showGradeEntry() called
```

**PROCESS**:

```
└─ showGradeEntry()
   ├─ pageTitle.setText("Grade Entry")
   ├─ Clear content: subContent.getChildren().clear()
   ├─ Create grade entry panel:
   │
   ├─ Get current teacher:
   │  └─ TeacherUser teacher = (TeacherUser) SessionManager.getCurrentUser()
   │     └─ teacher.getTeacherId() → 12
   │
   ├─ LOAD TEACHER'S COURSES:
   │  └─ courseDAO.getCoursesByTeacher(12)
   │     ├─ PREPARE SQL:
   │     │  └─ SELECT c.* FROM courses c
   │     │     WHERE c.teacher_id = ? AND c.is_active = 1
   │     │
   │     ├─ SET PARAMETER:
   │     │  └─ ps.setInt(1, 12)
   │     │
   │     ├─ EXECUTE & FETCH:
   │     │  └─ Database returns 3 courses for teacher ID 12:
   │     │     ├─ Course 1: CS101 - Programming Fundamentals
   │     │     ├─ Course 2: CS201 - Data Structures
   │     │     └─ Course 3: CS301 - Algorithms
   │     │
   │     └─ RETURN List<Course> (3 courses)
   │
   ├─ POPULATE COURSE DROPDOWN:
   │  └─ courseCombo.setItems(courses)
   │     └─ Dropdown now shows:
   │        ├─ [Select Course ▼]
   │        ├─ CS101 - Programming Fundamentals
   │        ├─ CS201 - Data Structures
   │        └─ CS301 - Algorithms
   │
   └─ Wait for teacher to select course
```

**OUTPUT**:

```
✓ Grade Entry Panel displayed:

┌─ Grade Entry ──────────────────────────────┐
│                                             │
│ Select Course:                              │
│ [CS101 - Programming Fundamentals ▼]        │
│                                             │
│ Select Assessment:                          │
│ [No assessments yet - select course first]  │
│                                             │
│ ┌─ Grade Table ──────────────────────────┐ │
│ │ (Wait for course & assessment selection)  │
│ └────────────────────────────────────────┘ │
│                                             │
└─────────────────────────────────────────────┘
```

---

#### **STEP 2: Teacher Selects a Course and Assessment**

**INPUT**: Teacher selects "CS101 - Programming Fundamentals" from dropdown

```
└─ courseCombo.setValue(CS101 course object)
```

**PROCESS**:

```
├─ courseCombo.valueProperty().addListener((obs, oldVal, newVal) → {
│  ├─ selectedCourse = CS101 (Course object)
│  │
│  ├─ LOAD ASSESSMENTS FOR THIS COURSE:
│  │  └─ gradeDAO.getAssessmentsByCourse(1) ← CS101 = course_id 1
│  │     ├─ PREPARE SQL:
│  │     │  └─ SELECT a.* FROM assessments a
│  │     │     JOIN courses c ON a.course_id = c.course_id
│  │     │     WHERE a.course_id = ? ORDER BY a.due_date
│  │     │
│  │     ├─ SET PARAMETER:
│  │     │  └─ ps.setInt(1, 1)
│  │     │
│  │     ├─ EXECUTE & FETCH:
│  │     │  └─ Database returns 4 assessments:
│  │     │     ├─ QUIZ (max_marks: 20, weight: 10%)
│  │     │     ├─ ASSIGNMENT (max_marks: 30, weight: 20%)
│  │     │     ├─ MIDTERM (max_marks: 50, weight: 30%)
│  │     │     └─ FINAL (max_marks: 100, weight: 40%)
│  │     │
│  │     └─ RETURN List<Assessment> (4 assessments)
│  │
│  ├─ POPULATE ASSESSMENT DROPDOWN:
│  │  └─ assessmentCombo.setItems(assessments)
│  │     └─ Dropdown now shows:
│  │        ├─ [Select Assessment ▼]
│  │        ├─ QUIZ (20 marks) - 10%
│  │        ├─ ASSIGNMENT (30 marks) - 20%
│  │        ├─ MIDTERM (50 marks) - 30%
│  │        └─ FINAL (100 marks) - 40%
│  │
│  └─ Wait for assessment selection
│})

└─ User selects "MIDTERM (50 marks) - 30%"
   └─ assessmentCombo.setValue(MIDTERM assessment)
      │
      ├─ LOAD STUDENTS IN COURSE:
      │  └─ courseDAO.getEnrolledStudents(1) ← CS101 = course_id 1
      │     ├─ PREPARE SQL:
      │     │  └─ SELECT u.*, s.* FROM enrollments e
      │     │     JOIN students s ON e.student_id = s.student_id
      │     │     JOIN users u ON s.user_id = u.user_id
      │     │     WHERE e.course_id = ? AND e.status = 'ACTIVE'
      │     │
      │     ├─ SET PARAMETER:
      │     │  └─ ps.setInt(1, 1)
      │     │
      │     ├─ EXECUTE & FETCH:
      │     │  └─ Database returns 45 enrolled students:
      │     │     ├─ STU001, Alice Johnson
      │     │     ├─ STU002, Bob Smith
      │     │     ├─ STU003, Charlie Brown
      │     │     └─ ... (45 total)
      │     │
      │     └─ RETURN List<StudentUser> (45 students)
      │
      ├─ LOAD EXISTING GRADES FOR THIS ASSESSMENT:
      │  └─ gradeDAO.getGradesByAssessment(101) ← MIDTERM = assessment_id 101
      │     ├─ PREPARE SQL:
      │     │  └─ SELECT g.*, u.full_name, s.student_number FROM grades g
      │     │     JOIN students s ON g.student_id = s.student_id
      │     │     JOIN users u ON s.user_id = u.user_id
      │     │     WHERE g.assessment_id = ? ORDER BY u.full_name
      │     │
      │     ├─ SET PARAMETER:
      │     │  └─ ps.setInt(1, 101) ← MIDTERM assessment_id
      │     │
      │     ├─ EXECUTE & FETCH:
      │     │  └─ Database returns existing grades:
      │     │     ├─ Grade for STU001 (Alice): 42/50 marks
      │     │     ├─ Grade for STU002 (Bob): 38/50 marks
      │     │     ├─ Grade for STU003 (Charlie): null (not yet graded)
      │     │     └─ ...
      │     │
      │     └─ RETURN Map<StudentId, Grade>
      │
      ├─ BUILD GRADE TABLE:
      │  ├─ Create TableView with columns:
      │  │  ├─ Student Number
      │  │  ├─ Student Name
      │  │  ├─ Marks Obtained (EDITABLE TextCell)
      │  │  ├─ Out of (read-only): 50
      │  │  ├─ Percentage (calculated, read-only)
      │  │  └─ Letter Grade (calculated, read-only)
      │  │
      │  └─ Populate rows:
      │     ├─ Row 1: STU001, Alice Johnson, [42], 50, 84.0%, A-
      │     ├─ Row 2: STU002, Bob Smith, [38], 50, 76.0%, B
      │     ├─ Row 3: STU003, Charlie Brown, [], 50, -, N/A
      │     └─ ... (45 rows total)
      │
      └─ Display table
```

**OUTPUT**:

```
✓ Grade Entry Table displayed:

┌─ Grade Entry for MIDTERM (CS101) ──────────────────────────┐
│                                                              │
│ Course: CS101 - Programming Fundamentals                   │
│ Assessment: MIDTERM (50 marks, weight: 30%)                │
│                                                              │
│ ┌─ Grades Table ────────────────────────────────────────┐ │
│ │ St# │ Name            │ Marks │ /50 │ %    │ Grade  │ │
│ ├─────┼─────────────────┼───────┼─────┼──────┼────────┤ │
│ │STU0│ Alice Johnson   │[42.0] │ 50  │ 84.0 │ A-     │ │
│ │STU0│ Bob Smith       │[38.0] │ 50  │ 76.0 │ B      │ │
│ │STU0│ Charlie Brown   │[    ] │ 50  │ -    │ N/A    │ │ (empty = not graded)
│ │ ... │ (44 more rows)  │       │     │      │        │ │
│ └────────────────────────────────────────────────────────┘ │
│                                                              │
│ [Save All Grades] [Cancel]                                  │
└──────────────────────────────────────────────────────────────┘
```

---

#### **STEP 3: Teacher Enters a Grade**

**INPUT**: Teacher clicks on "Marks" cell for "Charlie Brown", enters "45"

```
├─ cellEdit event triggered on row 3, marks column
├─ oldValue = null or ""
└─ newValue = "45" ← User types
```

**PROCESS**:

```
└─ TableCell.onCommit(EditEvent) {
   ├─ newValue = "45" (String from user)
   │
   ├─ INPUT VALIDATION:
   │  ├─ Try to parse: Integer.parseInt("45") → 45 (success)
   │  ├─ Check range: 45 <= 50 (max_marks) → YES
   │  ├─ Check range: 45 >= 0 → YES
   │  └─ Validation passes ✓
   │
   ├─ CALCULATE PERCENTAGE:
   │  ├─ marksObtained = 45
   │  ├─ maxMarks = 50
   │  ├─ percentage = (45 / 50) * 100 = 90%
   │  └─ Store: 90.0
   │
   ├─ CALCULATE LETTER GRADE:
   │  ├─ GradeCalculator.getLetterGrade(90.0, "STANDARD")
   │  ├─ Check: 90.0 >= 90? → YES
   │  └─ RETURN "A+" ← Letter grade
   │
   ├─ UPDATE TABLE CELLS:
   │  ├─ Cell[3][3] (Marks): display "45.0"
   │  ├─ Cell[3][4] (Percentage): display "90.0%"
   │  └─ Cell[3][5] (Letter): display "A+"
   │
   └─ Mark grade as "dirty" (unsaved)
}
```

**OUTPUT**:

```
✓ Grade entry accepted and calculated:

│ Row 3: │ STU003 │ Charlie Brown │[45.0] │ 50 │ 90.0% │ A+ │ ← Updated!

(Cell changes from empty to filled value with auto-calculated percentage and letter grade)
```

---

#### **STEP 4: Teacher Saves All Grades**

**INPUT**: Teacher clicks "Save All Grades" button

```
└─ saveBtn.setOnAction(e → handleSaveAllGrades())
```

**PROCESS**:

```
└─ handleSaveAllGrades()
   ├─ Get current teacher:
   │  └─ TeacherUser teacher = (TeacherUser) SessionManager.getCurrentUser()
   │     └─ teacher.getTeacherId() → 12
   │     └─ teacher.getUserId() → 5
   │
   ├─ Get selected assessment:
   │  └─ Assessment assessment = assessmentCombo.getValue()
   │     └─ assessment.getAssessmentId() → 101
   │
   ├─ Get all grades from table:
   │  └─ List<Grade> allGrades = table.getItems() ← 45 rows
   │
   ├─ FOR EACH GRADE IN TABLE:
   │  ├─ Iteration 1: Alice Johnson (STU001)
   │  │  ├─ marks = 42.0 (was already in DB)
   │  │  ├─ If CHANGED: call UPDATE
   │  │  │  └─ gradeDAO.updateGrade(grade)
   │  │  │     ├─ PREPARE SQL:
   │  │  │     │  └─ UPDATE grades
   │  │  │     │     SET marks_obtained=?, letter_grade=?, graded_by=?, graded_at=NOW()
   │  │  │     │     WHERE grade_id=?
   │  │  │     │
   │  │  │     ├─ SET PARAMETERS:
   │  │  │     │  ├─ ps.setDouble(1, 42.0)
   │  │  │     │  ├─ ps.setString(2, "A-")
   │  │  │     │  ├─ ps.setInt(3, 5) ← graded_by (current teacher user_id)
   │  │  │     │  └─ ps.setInt(4, gradeId)
   │  │  │     │
   │  │  │     ├─ EXECUTE:
   │  │  │     │  └─ UPDATE query executed
   │  │  │     │     └─ 1 row affected ✓
   │  │  │     │
   │  │  │     └─ RETURN true
   │  │  │
   │  │  └─ If NOT CHANGED: skip (no DB operation)
   │  │
   │  ├─ Iteration 2: Bob Smith (STU002)
   │  │  ├─ marks = 38.0 (was already in DB)
   │  │  └─ (skip if unchanged)
   │  │
   │  ├─ Iteration 3: Charlie Brown (STU003)
   │  │  ├─ marks = 45.0 ← NEWLY ENTERED
   │  │  ├─ This is NEW (no grade_id yet):
   │  │  │  └─ Call INSERT instead:
   │  │  │     └─ gradeDAO.addGrade(newGrade)
   │  │  │        ├─ PREPARE SQL:
   │  │  │        │  └─ INSERT INTO grades
   │  │  │        │     (student_id, assessment_id, marks_obtained, letter_grade, graded_by, graded_at)
   │  │  │        │     VALUES (?, ?, ?, ?, ?, NOW())
   │  │  │        │
   │  │  │        ├─ SET PARAMETERS:
   │  │  │        │  ├─ ps.setInt(1, 3) ← student_id for Charlie (STU003)
   │  │  │        │  ├─ ps.setInt(2, 101) ← assessment_id (MIDTERM)
   │  │  │        │  ├─ ps.setDouble(3, 45.0) ← marks_obtained
   │  │  │        │  ├─ ps.setString(4, "A+") ← letter_grade
   │  │  │        │  └─ ps.setInt(5, 5) ← graded_by (current teacher user_id)
   │  │  │        │
   │  │  │        ├─ EXECUTE:
   │  │  │        │  └─ INSERT executed
   │  │  │        │     └─ 1 row inserted ✓
   │  │  │        │     └─ grade_id auto-generated (e.g., 205)
   │  │  │        │
   │  │  │        └─ RETURN true
   │  │  │
   │  │  └─ Grade now in database
   │  │
   │  └─ ... (repeat for remaining 42 grades)
   │
   ├─ SHOW SUCCESS ALERT:
   │  └─ AlertUtil.showInfo("Success", "All grades saved successfully!")
   │
   ├─ CLEAR DIRTY FLAGS:
   │  └─ All rows marked as "saved"
   │
   └─ Transaction complete
```

**OUTPUT**:

```
✓ Success Alert:
  "All grades saved successfully!"

✓ Database updated:
  INSERT INTO grades: 1 new grade (Charlie Brown)
  UPDATE grades: 44 modified/unchanged grades
  Database now has 45 total grades for MIDTERM assessment

✓ UI state:
  │ Row 3: │ STU003 │ Charlie Brown │[45.0] │ 50 │ 90.0% │ A+ │ ← Now in database!
```

---

### 10.4 EXPORTING STUDENT REPORTS WORKFLOW

#### **STEP 1: Admin Selects Report Type and Student**

**INPUT**: Admin clicks "Reports" in sidebar

```
└─ AdminDashboardController.showReports() called
```

**PROCESS**:

```
└─ showReports()
   ├─ pageTitle.setText("Reports")
   ├─ Create reports panel:
   │
   ├─ BUILD REPORT OPTIONS:
   │  ├─ ComboBox "Report Type":
   │  │  ├─ Student Transcript (PDF)
   │  │  ├─ Grade Report (CSV)
   │  │  ├─ Attendance Report (CSV)
   │  │  └─ Class Performance (PDF)
   │  │
   │  ├─ ComboBox "Select Student":
   │  │  ├─ Load all students: userDAO.getAllStudents() ← 150 students
   │  │  └─ Display in dropdown
   │  │
   │  └─ Button "Generate Report"
   │
   └─ Wait for selections
```

**OUTPUT**:

```
✓ Reports Panel displayed:

┌─ Generate Reports ─────────────────────────┐
│                                             │
│ Report Type:                                │
│ [Student Transcript (PDF) ▼]                │
│                                             │
│ Select Student:                             │
│ [Alice Johnson ▼]                           │
│                                             │
│ Report Format:                              │
│ ◉ PDF                                       │
│ ○ CSV                                       │
│                                             │
│ [Generate Report] [Cancel]                  │
└─────────────────────────────────────────────┘
```

---

#### **STEP 2: Admin Selects Student and Generates Report**

**INPUT**: Admin selects:

- Report Type: "Student Transcript (PDF)"
- Student: "Alice Johnson"
- Clicks "Generate Report"

```
└─ generateBtn.setOnAction(e → handleGenerateReport()) called
```

**PROCESS**:

```
└─ handleGenerateReport()
   ├─ GET SELECTIONS:
   │  ├─ reportType = "Student Transcript (PDF)"
   │  ├─ selectedStudent = Alice Johnson (StudentUser object)
   │  │  └─ studentId = 1, studentNumber = "STU001"
   │  └─ format = "PDF"
   │
   ├─ COLLECT STUDENT GRADES:
   │  ├─ Get courses for student:
   │  │  └─ courseDAO.getCoursesByStudent(1)
   │  │     ├─ Query: SELECT c.* FROM courses c
   │  │     │         JOIN enrollments e ON c.course_id = e.course_id
   │  │     │         WHERE e.student_id = 1 AND e.status = 'ACTIVE'
   │  │     │
   │  │     ├─ Returns 4 courses:
   │  │     │  ├─ CS101 - Programming Fundamentals (3 credits)
   │  │     │  ├─ CS201 - Data Structures (3 credits)
   │  │     │  ├─ ENG102 - English Composition (3 credits)
   │  │     │  └─ MATH101 - Calculus I (4 credits)
   │  │     │
   │  │     └─ Store in list
   │  │
   │  ├─ FOR EACH COURSE:
   │  │  │
   │  │  ├─ Course 1: CS101 (course_id = 1)
   │  │  │  ├─ Get grades for this course:
   │  │  │  │  └─ gradeDAO.getGradesByStudentAndCourse(1, 1)
   │  │  │  │     ├─ Query: SELECT g.*, a.title, a.max_marks, a.weight
   │  │  │  │     │         FROM grades g
   │  │  │  │     │         JOIN assessments a ON g.assessment_id = a.assessment_id
   │  │  │  │     │         WHERE g.student_id = 1 AND a.course_id = 1
   │  │  │  │     │
   │  │  │  │     └─ Returns 4 grades:
   │  │  │  │        ├─ QUIZ: 18/20 marks (weight 10%)
   │  │  │  │        ├─ ASSIGNMENT: 28/30 marks (weight 20%)
   │  │  │  │        ├─ MIDTERM: 42/50 marks (weight 30%)
   │  │  │  │        └─ FINAL: 88/100 marks (weight 40%)
   │  │  │  │
   │  │  │  ├─ Get assessments for this course:
   │  │  │  │  └─ gradeDAO.getAssessmentsByCourse(1)
   │  │  │  │     └─ Returns same 4 assessments with max_marks and weight
   │  │  │  │
   │  │  │  ├─ CALCULATE FINAL GRADE FOR THIS COURSE:
   │  │  │  │  ├─ GradeCalculator.calculateFinalPercentage(grades, assessmentMap)
   │  │  │  │  │
   │  │  │  │  ├─ Calculation:
   │  │  │  │  │  ├─ Q1: (18/20)*100 = 90% × 0.10 (weight) = 9.0
   │  │  │  │  │  ├─ A1: (28/30)*100 = 93.3% × 0.20 (weight) = 18.67
   │  │  │  │  │  ├─ M1: (42/50)*100 = 84% × 0.30 (weight) = 25.2
   │  │  │  │  │  ├─ F1: (88/100)*100 = 88% × 0.40 (weight) = 35.2
   │  │  │  │  │  └─ Sum: (9.0 + 18.67 + 25.2 + 35.2) / (10+20+30+40) * 100
   │  │  │  │  │     = 88.07 / 100 * 100 = 88.07%
   │  │  │  │  │
   │  │  │  │  ├─ GradeCalculator.getLetterGrade(88.07, "STANDARD")
   │  │  │  │  │  └─ Check: 88.07 >= 85? → YES
   │  │  │  │  │  └─ RETURN "A" ← Letter grade
   │  │  │  │  │
   │  │  │  │  ├─ GradeCalculator.getGradePoint("A")
   │  │  │  │  │  └─ RETURN 4.0 ← GPA points
   │  │  │  │  │
   │  │  │  │  └─ Final for CS101: 88.07%, A, 4.0 GPA
   │  │  │  │
   │  │  │  └─ Store: courseGrades.put(1, {finalPercent: 88.07, letterGrade: "A", gpa: 4.0})
   │  │  │
   │  │  ├─ Course 2: CS201 → Final: 82.5%, B+, 3.3 GPA
   │  │  ├─ Course 3: ENG102 → Final: 91.2%, A, 4.0 GPA
   │  │  └─ Course 4: MATH101 → Final: 77.8%, B-, 2.7 GPA
   │  │
   │  └─ Calculate cumulative GPA:
   │     ├─ Total GPA points: (4.0×3 + 3.3×3 + 4.0×3 + 2.7×4) = 37.9
   │     ├─ Total credits: 3+3+3+4 = 13
   │     ├─ Cumulative GPA: 37.9 / 13 = 2.92
   │     └─ Store: cumulativeGPA = 2.92
   │
   ├─ FORMAT: STUDENT TRANSCRIPT (PDF)
   │  └─ ReportGenerator.generateTranscript(student, filePath)
   │     │
   │     ├─ Create PDDocument (blank PDF)
   │     ├─ Add PDPage (LETTER size)
   │     ├─ Add content using PDPageContentStream:
   │     │
   │     ├─ ── HEADER ──
   │     │  ├─ Title: "Springfield Academy" (font: Helvetica Bold, 18pt)
   │     │  ├─ Subtitle: "Official Academic Transcript" (font: Helvetica, 11pt)
   │     │  └─ Horizontal line separator
   │     │
   │     ├─ ── STUDENT INFO ──
   │     │  ├─ Student Name:     Alice Johnson
   │     │  ├─ Student ID:       STU001
   │     │  ├─ Program:          Computer Science
   │     │  ├─ Date Issued:      March 26, 2026
   │     │  └─ Horizontal line separator
   │     │
   │     ├─ ── GRADES TABLE HEADER ──
   │     │  └─ Columns: Course | Credits | Final % | Grade | GPA Points | Status
   │     │
   │     ├─ ── GRADES ROWS ──
   │     │  ├─ CS101        │ 3 │ 88.1% │ A  │ 4.00 │ PASSED
   │     │  ├─ CS201        │ 3 │ 82.5% │ B+ │ 3.30 │ PASSED
   │     │  ├─ ENG102       │ 3 │ 91.2% │ A  │ 4.00 │ PASSED
   │     │  └─ MATH101      │ 4 │ 77.8% │ B- │ 2.70 │ PASSED
   │     │     ─────────────────────────────────
   │     │     TOTALS:     13   (Cumulative GPA: 2.92)
   │     │
   │     ├─ ── FOOTER ──
   │     │  ├─ Generated Date: 2026-03-26
   │     │  ├─ Official Seal placeholder
   │     │  └─ "This is an official document"
   │     │
   │     ├─ Save PDF:
   │     │  ├─ filePath = "/Users/marshal/Downloads/STU001_Transcript.pdf"
   │     │  └─ doc.save(filePath)
   │     │
   │     └─ RETURN true (success)
   │
   ├─ OPEN FILE SAVE DIALOG:
   │  ├─ FileChooser dialog opens
   │  ├─ Initial directory: user's Downloads folder
   │  ├─ Initial filename: "STU001_Transcript.pdf"
   │  ├─ File type filter: PDF Files (*.pdf)
   │  └─ User clicks "Save"
   │
   ├─ SAVE PDF:
   │  ├─ Selected filepath = "/Users/marshal/Downloads/STU001_Transcript.pdf"
   │  ├─ PDF file written to disk
   │  └─ File size: ~50 KB
   │
   ├─ SHOW SUCCESS:
   │  └─ AlertUtil.showInfo("Report Generated",
   │                        "Transcript saved to: /Downloads/STU001_Transcript.pdf")
   │
   └─ Report ready for download/email
```

**OUTPUT**:

```
✓ PDF Transcript Generated:

┌─────────────────────────────────────────────────────────┐
│                  Springfield Academy                    │
│           Official Academic Transcript                  │
├─────────────────────────────────────────────────────────┤
│                                                          │
│ Student Name:   Alice Johnson                           │
│ Student ID:     STU001                                  │
│ Program:        Computer Science                        │
│ Date Issued:    March 26, 2026                          │
│                                                          │
├─────────────────────────────────────────────────────────┤
│ Course          Credits  Final %  Grade  GPA    Status  │
├─────────────────────────────────────────────────────────┤
│ CS101           3        88.1%    A      4.00   PASSED  │
│ CS201           3        82.5%    B+     3.30   PASSED  │
│ ENG102          3        91.2%    A      4.00   PASSED  │
│ MATH101         4        77.8%    B-     2.70   PASSED  │
├─────────────────────────────────────────────────────────┤
│ TOTALS:         13                     GPA: 2.92        │
├─────────────────────────────────────────────────────────┤
│ Generated: 2026-03-26                                   │
│ This is an official document of Springfield Academy     │
└─────────────────────────────────────────────────────────┘

✓ File saved: STU001_Transcript.pdf (50 KB)
✓ Location: /Users/marshal/Downloads/
```

---

#### **STEP 3: Export Multiple Students as CSV**

**INPUT**: Admin selects:

- Report Type: "Grade Report (CSV)"
- Filter: "All Students in CS101"
- Clicks "Generate Report"

```
└─ handleGenerateReport() called with CSV format
```

**PROCESS**:

```
└─ handleGenerateReport()
   ├─ GET SELECTIONS:
   │  ├─ reportType = "Grade Report (CSV)"
   │  ├─ courseFilter = "CS101"
   │  └─ format = "CSV"
   │
   ├─ COLLECT DATA:
   │  ├─ Get students in CS101:
   │  │  └─ courseDAO.getEnrolledStudents(1)
   │  │     └─ Returns 45 students
   │  │
   │  ├─ FOR EACH STUDENT:
   │  │  ├─ Get student grades for CS101:
   │  │  │  ├─ gradeDAO.getGradesByStudentAndCourse(studentId, 1)
   │  │  │  └─ Get assessments and calculate final grade
   │  │  │
   │  │  └─ Collect: {studentId, studentNumber, fullName, finalPercent, letterGrade, status}
   │  │
   │  └─ Create List<ReportRow> with 45 students
   │
   ├─ FORMAT: CSV FILE
   │  └─ ReportGenerator.generateGradeCSV(courseId, studentList, filePath)
   │     │
   │     ├─ Create CSVFormat (with headers):
   │     │  └─ Headers: "Student ID", "Student Number", "Full Name", "Final %", "Letter Grade", "Status"
   │     │
   │     ├─ Using Apache Commons CSV:
   │     │  ├─ FileWriter fw = new FileWriter(filePath)
   │     │  └─ CSVPrinter csvPrinter = new CSVPrinter(fw, CSVFormat.DEFAULT.withHeader(...))
   │     │
   │     ├─ WRITE DATA:
   │     │  │
   │     │  ├─ Header row:
   │     │  │  ├─ "Student ID", "Student Number", "Full Name", "Final %", "Letter Grade", "Status"
   │     │  │
   │     │  ├─ Data rows (45 total):
   │     │  │  ├─ Row 1: "1", "STU001", "Alice Johnson", "88.1", "A", "PASSED"
   │     │  │  ├─ Row 2: "2", "STU002", "Bob Smith", "76.5", "B", "PASSED"
   │     │  │  ├─ Row 3: "3", "STU003", "Charlie Brown", "65.3", "D", "PASSED"
   │     │  │  ├─ Row 4: "4", "STU004", "Diana Prince", "92.7", "A+", "PASSED"
   │     │  │  └─ ... (41 more rows)
   │     │  │
   │     │  └─ Each row is comma-separated, properly escaped
   │     │
   │     ├─ Close file:
   │     │  └─ csvPrinter.flush()
   │     │  └─ fw.close()
   │     │
   │     └─ RETURN true (success)
   │
   ├─ OPEN FILE SAVE DIALOG:
   │  ├─ FileChooser dialog
   │  ├─ Initial filename: "CS101_Grades.csv"
   │  └─ User clicks "Save"
   │
   ├─ SAVE CSV:
   │  ├─ filePath = "/Users/marshal/Downloads/CS101_Grades.csv"
   │  ├─ CSV file written to disk
   │  └─ File size: ~8 KB (45 students)
   │
   ├─ SHOW SUCCESS:
   │  └─ AlertUtil.showInfo("Report Generated",
   │                        "Grade report saved to: CS101_Grades.csv")
   │
   └─ Report ready for spreadsheet import
```

**OUTPUT**:

```
✓ CSV File Generated and Saved:

Filename: CS101_Grades.csv
Location: /Users/marshal/Downloads/

Contents (when opened in Excel/Sheets):
┌─────────────┬─────────────────┬──────────────────┬───────┬──────────┬────────┐
│ Student ID  │ Student Number  │ Full Name        │ Fin%  │ Letter G │ Status │
├─────────────┼─────────────────┼──────────────────┼───────┼──────────┼────────┤
│ 1           │ STU001          │ Alice Johnson    │ 88.1  │ A        │ PASSED │
│ 2           │ STU002          │ Bob Smith        │ 76.5  │ B        │ PASSED │
│ 3           │ STU003          │ Charlie Brown    │ 65.3  │ D        │ PASSED │
│ 4           │ STU004          │ Diana Prince     │ 92.7  │ A+       │ PASSED │
│ ...         │ ...             │ ...              │ ...   │ ...      │ ...    │
│ 45          │ STU045          │ Zara Williams    │ 81.4  │ B+       │ PASSED │
└─────────────┴─────────────────┴──────────────────┴───────┴──────────┴────────┘

✓ File ready for:
  ├─ Download to user's computer
  ├─ Import into Excel/Google Sheets
  ├─ Email to department
  ├─ Print for records
  └─ Archive/backup
```

---

### 10.5 LOGOUT WORKFLOW

**INPUT**: Teacher clicks "Logout" button in header

```
└─ logoutBtn.setOnAction(e → handleLogout()) called
```

**PROCESS**:

```
└─ handleLogout()
   ├─ Call: NavigationUtil.logout()
   │  │
   │  ├─ Clear session:
   │  │  └─ SessionManager.clearSession()
   │  │     └─ currentUser = null ← Static field reset
   │  │
   │  └─ Navigate to login:
   │     └─ NavigationUtil.navigateTo("LoginView.fxml")
   │        ├─ Load LoginView.fxml
   │        ├─ Create new Scene
   │        ├─ primaryStage.setScene(scene)
   │        └─ primaryStage.show()
   │
   └─ Logout complete
```

**OUTPUT**:

```
✓ Logout successful:
  ├─ TeacherDashboard disappears
  ├─ LoginView displayed again
  ├─ Username/password fields cleared
  ├─ Error labels hidden
  └─ User can login again (session cleared)
```

---

## 11. Key Architectural Decisions

| Decision                              | Rationale                                                                                |
| ------------------------------------- | ---------------------------------------------------------------------------------------- |
| **Abstract Base Class (UserAccount)** | Different user types share core data but have different behaviors (different dashboards) |
| **Factory Pattern (UserFactory)**     | Isolate object creation logic; easy to add new user types                                |
| **Singleton (DatabaseConfig)**        | Ensure single database connection in desktop app; avoid connection leaks                 |
| **DAO Pattern**                       | Isolate SQL from UI logic; easy to swap database frameworks                              |
| **SessionManager**                    | Eliminate need to pass user object through multiple controllers                          |
| **NavigationUtil**                    | Centralize FXML loading logic; consistent styling                                        |
| **SPA Dashboard Pattern**             | Smooth navigation without scene switching; better UX                                     |
| **Weighted Grade Calculation**        | Flexible grading: different assessment types have different importance                   |
| **Parameterized SQL Queries**         | Prevent SQL injection attacks                                                            |
| **Java 9+ Modules**                   | Encapsulation of packages; explicit dependencies                                         |

---

## 12. Security Considerations

1. **Password Hashing**: All passwords stored as SHA-256 (consider bcrypt for production)
2. **Parameterized Queries**: Prevent SQL injection via `?` placeholders
3. **Session Management**: User stored in memory; cleared on logout
4. **Role-Based Access**: Filters applied in DAO layer; teachers can't access other teachers' courses
5. **Active Flag**: Users marked inactive rather than deleted; maintains referential integrity
6. **InputValidation**: Controllers validate before calling DAOs

---

## 13. Performance Considerations

- **Single Connection Pool**: Works for single-user desktop app; would need HikariCP for multi-user
- **Lazy Loading**: Grades only loaded when student navigates to that course (not on dashboard startup)
- **Indexed Columns**: Primary keys and ForeignKeys auto-indexed; consider adding on (teacher_id, course_id)
- **Prepared Statements**: Reuse compiled SQL; faster for repeated queries
- **TableView Virtualization**: Only renders visible rows (handles large student lists efficiently)

---

## Summary

The **Student Grade Management System** demonstrates professional Java application architecture:

✅ **OOP**: Inheritance (user hierarchy), Polymorphism (dashboard routing), Encapsulation (private fields), Abstraction (abstract UserAccount)

✅ **Design Patterns**: Singleton (DB config), Factory (user creation), DAO (data layer), MVC (model/view/controller)

✅ **Database**: Normalized relational schema, proper foreign keys, CRUD operations in dedicated DAO classes

✅ **Security**: Hashed passwords, parameterized queries, session management, role-based access

✅ **Maintainability**: Separation of concerns, reusable utilities, consistent error handling

✅ **User Experience**: Role-specific dashboards, intuitive navigation, proper validation and feedback
