# Student Grade Management System (SMS)
<<<<<<< HEAD

### Java 17 + JavaFX 21 + MySQL 8 Desktop Application

=======
### Java 17 + JavaFX 21 + MySQL 8 Desktop Application

---

>>>>>>> dc8ddd81eefd8c1fff1c6df629e2bf26eab2a51f
## 📋 Project Overview

A fully-featured academic management desktop application built for a university OOP course project.  
It demonstrates **Encapsulation, Inheritance, Polymorphism, Abstraction, MVC Architecture, Singleton Pattern,** and **Factory Method Pattern**.

---

## 🏗️ Project Structure

```
StudentGradeSystem/
├── pom.xml                                   ← Maven build file
├── database/
│   ├── schema.sql                            ← Create tables + grade scale
<<<<<<< HEAD
=======
│   └── sample_data.sql                       ← Demo users, courses, grades
>>>>>>> dc8ddd81eefd8c1fff1c6df629e2bf26eab2a51f
└── src/main/
    ├── java/
    │   ├── module-info.java
    │   └── com/sms/
    │       ├── MainApp.java                  ← Entry point
    │       ├── model/                        ← Domain classes
    │       │   ├── UserAccount.java          ← Abstract base (Abstraction)
    │       │   ├── AdminUser.java            ← Extends UserAccount
    │       │   ├── TeacherUser.java          ← Extends UserAccount
    │       │   ├── StudentUser.java          ← Extends UserAccount
    │       │   ├── Course.java
    │       │   ├── Assessment.java
    │       │   ├── Grade.java
    │       │   └── Attendance.java
    │       ├── factory/
    │       │   └── UserFactory.java          ← Factory Method pattern
    │       ├── dao/                          ← Data Access Objects
    │       │   ├── UserDAO.java
    │       │   ├── CourseDAO.java
    │       │   ├── GradeDAO.java
    │       │   └── AttendanceDAO.java
    │       ├── controller/                   ← JavaFX Controllers (MVC)
    │       │   ├── LoginController.java
    │       │   ├── AdminDashboardController.java
    │       │   ├── TeacherDashboardController.java
    │       │   └── StudentDashboardController.java
    │       └── util/
    │           ├── DatabaseConfig.java       ← Singleton pattern
    │           ├── PasswordUtil.java         ← SHA-256 hashing
    │           ├── SessionManager.java       ← Current user session
    │           ├── NavigationUtil.java       ← Scene switching
    │           ├── AlertUtil.java            ← Reusable dialogs
    │           ├── GradeCalculator.java      ← Grade logic
    │           └── ReportGenerator.java      ← PDF + CSV reports
    └── resources/
        ├── fxml/
        │   ├── LoginView.fxml
        │   ├── AdminDashboard.fxml
        │   ├── TeacherDashboard.fxml
        │   └── StudentDashboard.fxml
        └── css/
            └── styles.css
```

---

## 🎨 OOP Design Principles

<<<<<<< HEAD
| Principle          | Where Used                                                                                         |
| ------------------ | -------------------------------------------------------------------------------------------------- |
| **Abstraction**    | `UserAccount` is abstract with `getDashboardPath()` and `getRoleDisplayName()` abstract methods    |
| **Encapsulation**  | All model fields are `private` with getters/setters                                                |
| **Inheritance**    | `AdminUser`, `TeacherUser`, `StudentUser` all extend `UserAccount`                                 |
| **Polymorphism**   | `NavigationUtil.navigateTo(user.getDashboardPath())` — each subclass returns a different FXML path |
| **Singleton**      | `DatabaseConfig.getInstance()` — one DB connection for the app lifetime                            |
| **Factory Method** | `UserFactory.createFromResultSet(rs, role)` — creates the right subclass from DB data              |
| **MVC**            | `model/` = data, `resources/fxml/` = view, `controller/` = controller                              |
=======
| Principle | Where Used |
|-----------|-----------|
| **Abstraction** | `UserAccount` is abstract with `getDashboardPath()` and `getRoleDisplayName()` abstract methods |
| **Encapsulation** | All model fields are `private` with getters/setters |
| **Inheritance** | `AdminUser`, `TeacherUser`, `StudentUser` all extend `UserAccount` |
| **Polymorphism** | `NavigationUtil.navigateTo(user.getDashboardPath())` — each subclass returns a different FXML path |
| **Singleton** | `DatabaseConfig.getInstance()` — one DB connection for the app lifetime |
| **Factory Method** | `UserFactory.createFromResultSet(rs, role)` — creates the right subclass from DB data |
| **MVC** | `model/` = data, `resources/fxml/` = view, `controller/` = controller |
>>>>>>> dc8ddd81eefd8c1fff1c6df629e2bf26eab2a51f

---

## 🛠️ Setup Instructions

### Prerequisites
<<<<<<< HEAD

=======
>>>>>>> dc8ddd81eefd8c1fff1c6df629e2bf26eab2a51f
- Java 17 or higher (JDK)
- JavaFX SDK 21
- Maven 3.8+
- MySQL 8.0+
- IntelliJ IDEA or Eclipse (recommended)

### Step 1 — Clone / unzip the project

```bash
cd StudentGradeSystem
```

### Step 2 — Set up MySQL database

```sql
-- In MySQL Workbench or terminal:
mysql -u root -p
source database/schema.sql
source database/sample_data.sql
```

### Step 3 — Configure database credentials

Edit `src/main/java/com/sms/util/DatabaseConfig.java`:

<<<<<<< HEAD
=======
```java
private static final String URL      = "jdbc:mysql://localhost:3306/student_grade_db?...";
private static final String USERNAME = "root";        // your MySQL username
private static final String PASSWORD = "your_password"; // your MySQL password
```

>>>>>>> dc8ddd81eefd8c1fff1c6df629e2bf26eab2a51f
### Step 4 — Build with Maven

```bash
mvn clean package
```

### Step 5 — Run the application

```bash
<<<<<<< HEAD

```

Or run the fat JAR:

=======
mvn javafx:run
```
Or run the fat JAR:
>>>>>>> dc8ddd81eefd8c1fff1c6df629e2bf26eab2a51f
```bash
java --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml \
     -jar target/StudentGradeSystem-1.0.0.jar
```

---

## 🔐 Demo Login Credentials

<<<<<<< HEAD
| Role    | Username  | Password    |
| ------- | --------- | ----------- |
| Admin   | admin     | password123 |
| Teacher | t.smith   | password123 |
| Teacher | t.johnson | password123 |
| Student | s.alice   | password123 |
| Student | s.bob     | password123 |
=======
| Role    | Username   | Password    |
|---------|-----------|-------------|
| Admin   | admin      | password123 |
| Teacher | t.smith    | password123 |
| Teacher | t.johnson  | password123 |
| Student | s.alice    | password123 |
| Student | s.bob      | password123 |
>>>>>>> dc8ddd81eefd8c1fff1c6df629e2bf26eab2a51f

---

## 📊 Features by Role

### Admin
<<<<<<< HEAD

=======
>>>>>>> dc8ddd81eefd8c1fff1c6df629e2bf26eab2a51f
- View dashboard with student/teacher/course counts
- Add, edit, delete students
- Add, edit, delete courses
- Add teachers
- Generate PDF transcripts for any student
- Export course grades and attendance as CSV

### Teacher
<<<<<<< HEAD

=======
>>>>>>> dc8ddd81eefd8c1fff1c6df629e2bf26eab2a51f
- View assigned courses
- Create/edit/delete assessments (Quiz, Midterm, Final, etc.)
- Enter and update grades — letter grade is auto-calculated
- Record daily attendance (batch save)
- View class performance report
- Export grades as CSV

### Student
<<<<<<< HEAD

=======
>>>>>>> dc8ddd81eefd8c1fff1c6df629e2bf26eab2a51f
- View enrolled courses
- View grades per assessment with final percentage and letter grade
- View GPA (cumulative)
- View attendance summary per course
<<<<<<< HEAD

---

| 80-84 | A- | 3.70 |
| 70-74 | B | 3.00 |
| 65-69 | B- | 2.70 |
| 60-64 | C+ | 2.30 |
| 55-59 | C | 2.00 |
| 50-54 | C- | 1.70 |
| 45-49 | D | 1.00 |

**Final Percentage Formula:**

=======
- Download PDF transcript

---

## 🔢 Grade Scale

| Percentage | Letter | GPA Points |
|-----------|--------|-----------|
| 90-100    | A+     | 4.00 |
| 85-89     | A      | 4.00 |
| 80-84     | A-     | 3.70 |
| 75-79     | B+     | 3.30 |
| 70-74     | B      | 3.00 |
| 65-69     | B-     | 2.70 |
| 60-64     | C+     | 2.30 |
| 55-59     | C      | 2.00 |
| 50-54     | C-     | 1.70 |
| 45-49     | D      | 1.00 |
| 0-44      | F      | 0.00 |

**Final Percentage Formula:**
>>>>>>> dc8ddd81eefd8c1fff1c6df629e2bf26eab2a51f
```
Final % = Σ( (marksObtained / maxMarks) × weight ) / Σ(weight)  × 100
```

---

## 🗄️ Database Schema Summary

```
users         → base table for all accounts
admins        → admin-specific data (FK → users)
teachers      → teacher-specific data (FK → users)
students      → student-specific data (FK → users)
courses       → course catalogue (FK → teachers)
enrollments   → student ↔ course many-to-many
assessments   → quizzes/exams per course
grades        → marks per student per assessment
attendance    → attendance records per student per course per date
grade_scale   → configurable letter grade thresholds
```

---

## 📦 Dependencies

<<<<<<< HEAD
| Library                | Version | Purpose        |
| ---------------------- | ------- | -------------- |
| JavaFX Controls + FXML | 21.0.1  | UI framework   |
| MySQL Connector/J      | 8.2.0   | JDBC driver    |
| Apache PDFBox          | 3.0.1   | PDF generation |
| Apache Commons CSV     | 1.10.0  | CSV export     |
=======
| Library | Version | Purpose |
|---------|---------|---------|
| JavaFX Controls + FXML | 21.0.1 | UI framework |
| MySQL Connector/J | 8.2.0 | JDBC driver |
| Apache PDFBox | 3.0.1 | PDF generation |
| Apache Commons CSV | 1.10.0 | CSV export |
>>>>>>> dc8ddd81eefd8c1fff1c6df629e2bf26eab2a51f

---

## 📝 Notes for University Submission

- All passwords are stored as **SHA-256 hashes** (never plaintext)
- All DB operations use **PreparedStatement** (prevents SQL injection)
- Transactions with **rollback** are used for multi-table operations
- All `Connection` and `ResultSet` objects are closed via **try-with-resources**
- The app gracefully handles `null` database values
