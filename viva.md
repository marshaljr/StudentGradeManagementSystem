# 📚 Viva Session Guide: Student Grade Management System

## CIS096-1 – Principles of Programming and Data Structures

**Course:** OO Design & Development  
**Project:** Java 17 + JavaFX 21 + MySQL 8 Desktop Application  
**Date:** April 2026

---

## 📊 Current Status Summary

| Category                    | Score      | Status                         | Impact                           |
| --------------------------- | ---------- | ------------------------------ | -------------------------------- |
| **OO Design & Development** | 24–25/30   | ✅ Strong                      | Well-received                    |
| **GUIs & Forms**            | 19–20/30   | ⚠️ Functional but undocumented | Missing screenshots              |
| **Final Report**            | 0/30       | ❌ **MISSING**                 | **Critical gap**                 |
| **Presentation/Video**      | 0/10       | ❌ **MISSING**                 | **Critical gap**                 |
| **TOTAL**                   | **47/100** | ⚠️ Bare Pass                   | **Can improve to 70+ with docs** |

---

## ✅ What You're Doing Right

### **OOP Concepts** (Strongest Area)

1. ✅ **Abstraction + Inheritance**: `UserAccount` abstract base with `AdminUser`, `TeacherUser`, `StudentUser`
2. ✅ **Polymorphism**: `user.getDashboardPath()` dispatches type-safely in `LoginController.java`
3. ✅ **Encapsulation**: All model fields `private` with getter/setter pairs
4. ✅ **Design Patterns**:
   - Singleton: `DatabaseConfig.getInstance()`
   - Factory Method: `UserFactory.createFromResultSet()`
5. ✅ **MVC Architecture**: Clean separation—DAO layer, Controller layer, View layer

### **Database Design**

- Normalized 10-table schema with proper PKs, FKs, cascading deletes
- Good entity relationships (users → admins/teachers/students, courses, grades, attendance)

### **GUI Implementation**

- 4 working FXML screens + CSS styling
- Real data binding via DAOs
- Input validation + error handling

---

## 🎬 Demonstration Plan for Viva Session

**Total Demo Time:** 5–7 minutes (Be prepared to show this live OR pre-record it)

### **Demo Script**

#### **Part 1: Login & Role-Based Access (1 min)**

```
1. Start the app: mvn javafx:run
2. Login as ADMIN (username: admin_user, password: admin123)
   → Show "Admin Dashboard" appears
3. Logout
4. Login as TEACHER (username: teacher_user, password: teacher123)
   → Show "Teacher Dashboard" appears
   → Explain polymorphic navigation via user.getDashboardPath()
5. Logout
6. Login as STUDENT (username: student_user, password: student123)
   → Show "Student Dashboard" and "My Grades" section
```

**Talking Points:**

- "The system uses an abstract `UserAccount` class with implementations (`AdminUser`, `TeacherUser`, `StudentUser`). This demonstrates **polymorphism**—the same `getDashboardPath()` call returns different FXML files based on user type."

#### **Part 2: Grade Data Entry & Calculation (1.5 min)**

```
1. Logged in as TEACHER
2. Navigate to "Enter Grades" section
3. Select a Course (e.g., "CS101")
4. Select an Assessment (e.g., "Midterm Exam", MaxMarks: 100)
5. Enter marks for 2–3 students:
   - Student A: 85 marks → [Automatically shows: 85%, Letter Grade: A, Remarks: Excellent]
   - Student B: 72 marks → [70%, B, Good]
6. Save grades
7. Show that the database updated (SELECT query in MySQL)
```

**Talking Points:**

- "The `GradeCalculator` utility class encapsulates grade logic: `isValidMarks()`, `getLetterGrade()`, `calculateFinalPercentage()`. This separates business logic from GUI."
- "The `GradeDAO` uses **PreparedStatement** to safely insert/update grades with FK constraints to Students and Assessments."

#### **Part 3: Student Grade View (1 min)**

```
1. Logout, login as STUDENT
2. Click "My Grades"
3. Show per-course breakdown:
   - Course name, assessments completed
   - Final grade, GPA calculation
4. Explain how StudentDashboardController aggregates data from GradeDAO
```

**Talking Points:**

- "The system aggregates grades across multiple assessments per course and calculates a final GPA using weighted averages."

#### **Part 4: Database Persistence (1 min)**

```
1. Open MySQL Workbench (or MySQL CLI)
2. Show the schema:
   - users table (PK: user_id)
   - students table (FK: user_id)
   - courses table (FK: teacher_id)
   - assessments table (FK: course_id)
   - grades table (FK: student_id, assessment_id with UNIQUE constraint)
   - attendance table (FK: student_id, course_id)
3. Run query: SELECT * FROM grades WHERE student_id = 1
4. Show the saved grades match what we entered in the GUI
```

**Talking Points:**

- "The database is normalized to 3NF: each entity has a single PK, FKs enforce referential integrity, and the `grades` table is a many-to-many join enforced with a UNIQUE constraint."

#### **Part 5: Error Handling (30s – optional)**

```
1. Attempt login with empty username → Show error dialog: "Username is required"
2. Attempt to save invalid marks (e.g., -5 or 101 out of 100)
   → Show validation error: "Marks must be between 0 and 100"
```

**Talking Points:**

- "The `AlertUtil` utility class centralizes error messaging. Controllers validate input before calling DAOs."

---

## 📝 Viva Session: Expected Questions & Answers

### **Q1: Why did you use an abstract class (`UserAccount`) instead of an interface?**

**Answer:**
"An abstract class allows shared functionality (common fields like `userId`, `username`, `passwordHash`, `role`) across all user types. Interfaces don't support abstract fields or state. Additionally, abstract classes define the contract for subclasses to override methods like `getDashboardPath()` and `getRoleDisplayName()`, which is more appropriate for a hierarchical user model."

---

### **Q2: Explain the Singleton pattern in `DatabaseConfig` and why it matters.**

**Answer:**

```
public class DatabaseConfig {
    private static DatabaseConfig instance;
    private Connection conn;

    public synchronized static DatabaseConfig getInstance() {
        if (instance == null)
            instance = new DatabaseConfig();
        return instance;
    }

    public Connection getConnection() {...}
}
```

"The Singleton pattern ensures only ONE `DatabaseConfig` object exists throughout the application's lifetime. This guarantees a single, reusable database connection pool. Without it, each DAO would open a new database connection, wasting resources. The `synchronized` keyword prevents race conditions in multi-threaded environments."

---

### **Q3: How does your system ensure data integrity in the grades table?**

**Answer:**

- "The `grades` table has a UNIQUE constraint on `(student_id, assessment_id)` to prevent duplicate grade entries."
- "Foreign keys to `students` and `assessments` ensure grades can only reference valid students and assessments."
- "The `GradeDAO.saveGrade()` method uses `INSERT ... ON DUPLICATE KEY UPDATE` to safely update existing grades."
- "Referential integrity is enforced with `ON DELETE CASCADE`—if an assessment is deleted, its grades are automatically removed."

---

### **Q4: Explain how polymorphism is used in the login flow (`LoginController`).**

**Answer:**

```
// In LoginController.handleLogin():
UserAccount user = userDAO.authenticate(username, password);
// 'user' could be AdminUser, TeacherUser, or StudentUser
NavigationUtil.navigateTo(user.getDashboardPath());
// At runtime, the correct dashboard FXML is loaded
// because each subclass overrides getDashboardPath()
```

"Polymorphism allows the **same method call** (`user.getDashboardPath()`) to execute **different code** depending on the actual runtime type (`AdminUser.getDashboardPath()` vs. `TeacherUser.getDashboardPath()`). This is **runtime polymorphism** (late binding)."

---

### **Q5: What's inside `UserFactory.createFromResultSet()`? Why use a factory?**

**Answer:**
"The Factory Method Pattern centralizes **object creation logic**. When `UserDAO.loadUserByIdAndRole()` fetches a user from the database, it calls:

```
UserAccount user = UserFactory.createFromResultSet(rs, role);
```

The factory inspects the `role` field and returns the correct subclass (`AdminUser`, `TeacherUser`, or `StudentUser`). This keeps **creation logic out of the controller** and ensures consistent initialization. If rules for creating a user change, we only update one place: `UserFactory`."

---

### **Q6: How does encapsulation prevent bugs in grade entry?**

**Answer:**
"Grade calculation is encapsulated in `GradeCalculator` utility class. Controllers and DAOs cannot directly manipulate grade logic. Instead, they call **public methods** like:

```
boolean valid = GradeCalculator.isValidMarks(marksObtained, maxMarks);
String letter = GradeCalculator.getLetterGrade(percentage, scale);
```

This prevents code duplication and bugs. If a teacher accidentally enters marks like `150/100`, the validation method returns `false`, and the grade is rejected before hitting the database."

---

### **Q7: Explain the MVC flow in grade entry (view → controller → model → data layer).**

**Answer:**

1. **View** (TeacherDashboard.fxml): Teacher selects course → assessment → enters marks
2. **Controller** (TeacherDashboardController): `handleSaveGrade()` validates input, calls DAOs
3. **Model** (Grade.java): Temporary object in memory holding `studentId`, `assessmentId`, `marksObtained`
4. **Data Layer** (GradeDAO): Executes SQL `INSERT/UPDATE` into the `grades` table
5. **Business Logic** (GradeCalculator): Converts marks to percentage + letter grade
6. **View Update**: Table refreshes with new grade data

---

### **Q8: Why is the `grades` table's UNIQUE constraint important?**

**Answer:**
"A student should have exactly ONE grade per assessment. The UNIQUE constraint on `(student_id, assessment_id)` enforces this at the database level. If a teacher tries to enter two grades for the same student on the same assessment, the second `INSERT` fails with a constraint violation, preventing data corruption."

---

### **Q9: How does attendance tracking work? Show me the DAO method.**

**Answer:**
"The `AttendanceDAO.saveBatchAttendance()` method:

```
public void saveBatchAttendance(int courseId, LocalDate date,
                                  Map<Integer, String> attendanceMap)
```

- Takes a course, date, and a map of `(studentId → status)`
- Inserts/updates records in the `attendance` table
- The `attendance` table has a UNIQUE constraint on `(student_id, date, course_id)` to prevent duplicate attendance records
- `getAttendancePercentage()` then queries the database to calculate `(present_count / total_classes) * 100`"

---

### **Q10: What challenges did you face, and how did you solve them?**

**Answer (prepare 2–3 realistic examples):**

| Challenge                             | Solution                                                                                                                           |
| ------------------------------------- | ---------------------------------------------------------------------------------------------------------------------------------- |
| **Module system compilation error**   | Added `requires transitive java.sql` in `module-info.java` to export SQL classes to other modules. Fixed Git merge conflict.       |
| **Password security**                 | Implemented `PasswordUtil.hashPassword()` using SHA-256 hashing instead of storing plain text.                                     |
| **Grade calculation inconsistencies** | Centralized all grading math in `GradeCalculator` to prevent bugs. Use `BigDecimal` for percentage to avoid floating-point errors. |
| **Database connection leaks**         | Use try-with-resources in DAOs: `try (Connection conn = DatabaseConfig.getInstance().getConnection())` to auto-close connections.  |

---

## 📋 Pre-Viva Checklist

- [ ] **Final Report** created (.pdf, 8–12 pages with images)
- [ ] **Presentation slides** (10–15 slides, saved as .pptx)
- [ ] **5+ screenshots** of working GUI in `/docs/screenshots/`
- [ ] **README updated** with screenshots embedded
- [ ] **Demo script prepared** (run mvn javafx:run, login, show grade entry, show DB)
- [ ] **Viva Q&A prepared** (practice answering the 10 questions above)
- [ ] **Code walkthrough ready** (be able to show inheritance, polymorphism in files)
- [ ] **Database schema** ready to show (MySQL Workbench open with ER diagram)
- [ ] **Test data prepared** (ensure sample_data.sql is loaded so demo feels smooth)

---

## 🎯 Expected Score Improvement

| Item                     | Current    | With Documentation |
| ------------------------ | ---------- | ------------------ |
| OO Design (Section A)    | 24/30      | 28–30/30           |
| GUI (Section B)          | 19/30      | 25–27/30           |
| Final Report (Section C) | 0/30       | **24–28/30** ⬆️    |
| Presentation (Section D) | 0/10       | **8–10/10** ⬆️     |
| **TOTAL**                | **47/100** | **80–95/100** ⬆️   |

**With professional documentation + clear viva performance, you can move from "Bare Pass" (47) to "Very Good" (80–85+).**

---

## 🚀 Quick Reference: Key Concepts to Master

### **Inheritance Hierarchy**

```
UserAccount (abstract)
├── AdminUser
├── TeacherUser
└── StudentUser
```

### **Design Patterns Used**

- **Singleton**: DatabaseConfig
- **Factory Method**: UserFactory
- **MVC**: View (FXML) → Controller → DAO → Model

### **Key Data Structures**

- **Grades**: Student grades per assessment with letter grade calculation
- **Attendance**: Date-based tracking per student per course
- **Courses**: Taught by teachers, enrolled by students

### **OOP Pillars Demonstrated**

1. **Encapsulation**: Private fields + getter/setter methods
2. **Inheritance**: User subclasses extend abstract UserAccount
3. **Polymorphism**: Runtime dispatch of `getDashboardPath()`
4. **Abstraction**: Abstract methods force subclasses to implement behavior

---

## 📞 Last-Minute Tips

1. **Know your code** — Be able to navigate to any class in 10 seconds
2. **Practice saying it** — Record yourself answering the 10 Q&A responses
3. **Show confidence** — Speak clearly about OOP concepts (inheritance, polymorphism)
4. **Live demo matters** — Test your demo 2–3 times before the viva
5. **Have MySQL ready** — Keep Workbench open with schema visible during Q&A
6. **Reference the report** — Mention your Final Report when discussing design decisions

Good luck! 🎓
