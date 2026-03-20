-- ============================================================
-- Student Grade Management System - Database Schema
-- MySQL 8.0+
-- ============================================================

CREATE DATABASE IF NOT EXISTS student_grade_db;
USE student_grade_db;

-- ============================================================
-- USERS table (base for all roles)
-- ============================================================
CREATE TABLE users (
    user_id     INT AUTO_INCREMENT PRIMARY KEY,
    username    VARCHAR(50) UNIQUE NOT NULL,
    password    VARCHAR(255) NOT NULL,   -- SHA-256 hashed
    role        ENUM('ADMIN','TEACHER','STUDENT') NOT NULL,
    full_name   VARCHAR(100) NOT NULL,
    email       VARCHAR(100),
    phone       VARCHAR(20),
    is_active   BOOLEAN DEFAULT TRUE,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- ADMINS table
-- ============================================================
CREATE TABLE admins (
    admin_id    INT AUTO_INCREMENT PRIMARY KEY,
    user_id     INT NOT NULL UNIQUE,
    department  VARCHAR(100),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- ============================================================
-- TEACHERS table
-- ============================================================
CREATE TABLE teachers (
    teacher_id          INT AUTO_INCREMENT PRIMARY KEY,
    user_id             INT NOT NULL UNIQUE,
    employee_id         VARCHAR(20) UNIQUE NOT NULL,
    department          VARCHAR(100),
    qualification       VARCHAR(100),
    hire_date           DATE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- ============================================================
-- STUDENTS table
-- ============================================================
CREATE TABLE students (
    student_id          INT AUTO_INCREMENT PRIMARY KEY,
    user_id             INT NOT NULL UNIQUE,
    student_number      VARCHAR(20) UNIQUE NOT NULL,
    date_of_birth       DATE,
    gender              ENUM('Male','Female','Other'),
    address             VARCHAR(255),
    enrollment_year     INT,
    program             VARCHAR(100),
    year_level          INT DEFAULT 1,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- ============================================================
-- COURSES table
-- ============================================================
CREATE TABLE courses (
    course_id       INT AUTO_INCREMENT PRIMARY KEY,
    course_code     VARCHAR(20) UNIQUE NOT NULL,
    course_name     VARCHAR(100) NOT NULL,
    description     TEXT,
    credits         INT DEFAULT 3,
    teacher_id      INT,
    semester        VARCHAR(20),
    academic_year   VARCHAR(10),
    max_students    INT DEFAULT 40,
    is_active       BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (teacher_id) REFERENCES teachers(teacher_id) ON DELETE SET NULL
);

-- ============================================================
-- ENROLLMENTS table
-- ============================================================
CREATE TABLE enrollments (
    enrollment_id   INT AUTO_INCREMENT PRIMARY KEY,
    student_id      INT NOT NULL,
    course_id       INT NOT NULL,
    enrollment_date DATE DEFAULT (CURRENT_DATE),
    status          ENUM('ACTIVE','DROPPED','COMPLETED') DEFAULT 'ACTIVE',
    UNIQUE KEY uq_enrollment (student_id, course_id),
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE,
    FOREIGN KEY (course_id)  REFERENCES courses(course_id)  ON DELETE CASCADE
);

-- ============================================================
-- ASSESSMENTS table
-- ============================================================
CREATE TABLE assessments (
    assessment_id   INT AUTO_INCREMENT PRIMARY KEY,
    course_id       INT NOT NULL,
    title           VARCHAR(100) NOT NULL,
    type            ENUM('QUIZ','ASSIGNMENT','MIDTERM','FINAL','PROJECT','LAB') NOT NULL,
    max_marks       DECIMAL(6,2) NOT NULL DEFAULT 100,
    weight          DECIMAL(5,2) NOT NULL DEFAULT 0,  -- percentage weight 0-100
    due_date        DATE,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE
);

-- ============================================================
-- GRADES table
-- ============================================================
CREATE TABLE grades (
    grade_id        INT AUTO_INCREMENT PRIMARY KEY,
    student_id      INT NOT NULL,
    assessment_id   INT NOT NULL,
    marks_obtained  DECIMAL(6,2),
    letter_grade    VARCHAR(5),
    remarks         VARCHAR(255),
    graded_by       INT,        -- teacher user_id
    graded_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uq_grade (student_id, assessment_id),
    FOREIGN KEY (student_id)    REFERENCES students(student_id)     ON DELETE CASCADE,
    FOREIGN KEY (assessment_id) REFERENCES assessments(assessment_id) ON DELETE CASCADE,
    FOREIGN KEY (graded_by)     REFERENCES users(user_id)           ON DELETE SET NULL
);

-- ============================================================
-- ATTENDANCE table
-- ============================================================
CREATE TABLE attendance (
    attendance_id   INT AUTO_INCREMENT PRIMARY KEY,
    student_id      INT NOT NULL,
    course_id       INT NOT NULL,
    att_date        DATE NOT NULL,
    status          ENUM('PRESENT','ABSENT','LATE','EXCUSED') NOT NULL DEFAULT 'PRESENT',
    remarks         VARCHAR(255),
    recorded_by     INT,        -- teacher user_id
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uq_att (student_id, course_id, att_date),
    FOREIGN KEY (student_id)  REFERENCES students(student_id) ON DELETE CASCADE,
    FOREIGN KEY (course_id)   REFERENCES courses(course_id)   ON DELETE CASCADE,
    FOREIGN KEY (recorded_by) REFERENCES users(user_id)       ON DELETE SET NULL
);

-- ============================================================
-- GRADE SCALE table
-- ============================================================
CREATE TABLE grade_scale (
    scale_id        INT AUTO_INCREMENT PRIMARY KEY,
    min_marks       DECIMAL(5,2) NOT NULL,
    max_marks       DECIMAL(5,2) NOT NULL,
    letter_grade    VARCHAR(5) NOT NULL,
    grade_point     DECIMAL(3,2) NOT NULL,
    description     VARCHAR(50)
);

-- Default grade scale data
INSERT INTO grade_scale (min_marks, max_marks, letter_grade, grade_point, description) VALUES
(90, 100, 'A+', 4.00, 'Outstanding'),
(85, 89.99, 'A',  4.00, 'Excellent'),
(80, 84.99, 'A-', 3.70, 'Very Good'),
(75, 79.99, 'B+', 3.30, 'Good'),
(70, 74.99, 'B',  3.00, 'Above Average'),
(65, 69.99, 'B-', 2.70, 'Average'),
(60, 64.99, 'C+', 2.30, 'Satisfactory'),
(55, 59.99, 'C',  2.00, 'Acceptable'),
(50, 54.99, 'C-', 1.70, 'Below Average'),
(45, 49.99, 'D',  1.00, 'Marginal Pass'),
(0,  44.99, 'F',  0.00, 'Fail');
