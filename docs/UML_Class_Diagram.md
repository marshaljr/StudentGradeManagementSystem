# UML Class Diagram — Student Grade Management System

## Class Hierarchy (Inheritance Tree)

```
                    ┌──────────────────────────────────┐
                    │         <<abstract>>              │
                    │          UserAccount              │
                    │ ─────────────────────────────── │
                    │ - userId      : int              │
                    │ - username    : String           │
                    │ - passwordHash: String           │
                    │ - role        : String           │
                    │ - fullName    : String           │
                    │ - email       : String           │
                    │ - phone       : String           │
                    │ - active      : boolean          │
                    │ ─────────────────────────────── │
                    │ + getDashboardPath() : String    │ ← abstract
                    │ + getRoleDisplayName(): String   │ ← abstract
                    │ + getters/setters               │
                    └──────────────────┬───────────────┘
                                       │
              ┌────────────────────────┼──────────────────────┐
              │                        │                      │
    ┌─────────┴──────┐      ┌──────────┴───────┐   ┌─────────┴──────┐
    │   AdminUser    │      │  TeacherUser      │   │  StudentUser   │
    │────────────────│      │──────────────────│   │────────────────│
    │ - adminId      │      │ - teacherId      │   │ - studentId    │
    │ - department   │      │ - employeeId     │   │ - studentNumber│
    │────────────────│      │ - department     │   │ - dateOfBirth  │
    │ getDashboard() │      │ - qualification  │   │ - gender       │
    │ getRole()      │      │ - hireDate       │   │ - program      │
    └────────────────┘      │──────────────────│   │ - yearLevel    │
                            │ getDashboard()   │   │────────────────│
                            │ getRole()        │   │ getDashboard() │
                            └──────────────────┘   │ getRole()      │
                                                   └────────────────┘
```

## Core Domain Classes

```
┌─────────────────────────────┐      ┌─────────────────────────────┐
│           Course            │      │         Assessment          │
│─────────────────────────────│      │─────────────────────────────│
│ - courseId   : int          │      │ - assessmentId : int        │
│ - courseCode : String       │      │ - courseId     : int        │
│ - courseName : String       │ 1──* │ - title        : String     │
│ - description: String       │      │ - type         : String     │
│ - credits    : int          │      │ - maxMarks     : double     │
│ - teacherId  : int          │      │ - weight       : double     │
│ - semester   : String       │      │ - dueDate      : LocalDate  │
│ - academicYear: String      │      └────────────┬────────────────┘
│ - maxStudents: int          │                   │ 1
└─────────────────────────────┘                   │
                                                   │ *
                                      ┌────────────┴────────────────┐
                                      │           Grade             │
                                      │─────────────────────────────│
                                      │ - gradeId      : int        │
                                      │ - studentId    : int        │
                                      │ - assessmentId : int        │
                                      │ - marksObtained: double     │
                                      │ - letterGrade  : String     │
                                      │ - remarks      : String     │
                                      │ - gradedAt     : LocalDateTime│
                                      └─────────────────────────────┘

┌─────────────────────────────┐
│         Attendance          │
│─────────────────────────────│
│ - attendanceId : int        │
│ - studentId    : int        │
│ - courseId     : int        │
│ - attDate      : LocalDate  │
│ - status       : String     │  ← PRESENT/ABSENT/LATE/EXCUSED
│ - remarks      : String     │
│ - recordedBy   : int        │
└─────────────────────────────┘
```

## Utility / Infrastructure Classes

```
┌─────────────────────────────────┐
│    <<Singleton>>                │
│       DatabaseConfig            │
│─────────────────────────────────│
│ - instance  : DatabaseConfig    │  ← static, only one
│ - connection: Connection        │
│─────────────────────────────────│
│ + getInstance() : DatabaseConfig│  ← returns same instance
│ + getConnection(): Connection   │
│ + closeConnection(): void       │
└─────────────────────────────────┘

┌─────────────────────────────────┐
│    <<Factory>>                  │
│        UserFactory              │
│─────────────────────────────────│
│ + createFromResultSet(          │
│     rs: ResultSet,              │
│     role: String                │
│   ) : UserAccount               │  ← returns correct subclass
└─────────────────────────────────┘

┌──────────────────────────┐   ┌──────────────────────────┐
│       UserDAO            │   │       CourseDAO          │
│──────────────────────────│   │──────────────────────────│
│ + authenticate()         │   │ + getAllCourses()        │
│ + getAllStudents()        │   │ + getCoursesByTeacher() │
│ + addStudent()           │   │ + getCoursesByStudent() │
│ + updateStudent()        │   │ + addCourse()           │
│ + deleteStudent()        │   │ + updateCourse()        │
│ + getAllTeachers()        │   │ + deleteCourse()        │
│ + addTeacher()           │   │ + enrollStudent()       │
│ + resetPassword()        │   │ + getEnrolledStudents() │
└──────────────────────────┘   └──────────────────────────┘

┌──────────────────────────┐   ┌──────────────────────────┐
│       GradeDAO           │   │     AttendanceDAO        │
│──────────────────────────│   │──────────────────────────│
│ + getAssessmentsByCourse │   │ + getAttendanceByDate() │
│ + addAssessment()        │   │ + getByStudentCourse()  │
│ + updateAssessment()     │   │ + getAttendanceSummary()│
│ + getGradesByAssessment()│   │ + getAttendancePct()    │
│ + getGradesByStudent()   │   │ + saveAttendance()      │
│ + saveGrade()            │   │ + saveBatchAttendance() │
│ + deleteGrade()          │   └──────────────────────────┘
└──────────────────────────┘
```

## MVC Architecture

```
┌────────────────────────────────────────────────────────┐
│                    VIEW (FXML)                         │
│  LoginView.fxml  │  AdminDashboard.fxml               │
│  TeacherDashboard.fxml  │  StudentDashboard.fxml      │
└────────────────────────┬───────────────────────────────┘
                         │ user events
                         ▼
┌────────────────────────────────────────────────────────┐
│                 CONTROLLER (Java)                       │
│  LoginController                                        │
│  AdminDashboardController                               │
│  TeacherDashboardController                             │
│  StudentDashboardController                             │
└────────────────────────┬───────────────────────────────┘
                         │ calls DAO methods
                         ▼
┌────────────────────────────────────────────────────────┐
│                   MODEL (Java)                          │
│  UserAccount, AdminUser, TeacherUser, StudentUser      │
│  Course, Assessment, Grade, Attendance                 │
└────────────────────────┬───────────────────────────────┘
                         │
                         ▼
┌────────────────────────────────────────────────────────┐
│                DAO Layer + Database                     │
│  UserDAO, CourseDAO, GradeDAO, AttendanceDAO           │
│  ─────────────────────────────────────────            │
│       MySQL: student_grade_db                          │
└────────────────────────────────────────────────────────┘
```

## Relationships Summary

| From       | To         | Multiplicity | Type         |
|-----------|-----------|-------------|-------------|
| UserAccount | AdminUser    | 1 : 0..1 | Inheritance  |
| UserAccount | TeacherUser  | 1 : 0..1 | Inheritance  |
| UserAccount | StudentUser  | 1 : 0..1 | Inheritance  |
| TeacherUser | Course       | 1 : many | Association  |
| Course     | Assessment   | 1 : many | Composition  |
| Assessment | Grade        | 1 : many | Association  |
| StudentUser | Grade       | 1 : many | Association  |
| StudentUser | Attendance  | 1 : many | Association  |
| Course     | Attendance   | 1 : many | Association  |
| StudentUser | Course      | many : many | via enrollments |
