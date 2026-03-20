-- ============================================================
-- Sample Data for Student Grade Management System
-- Run schema.sql first, then this file
-- ============================================================
USE student_grade_db;

-- ============================================================
-- USERS  (passwords are SHA-256 of "password123")
-- SHA-256("password123") = ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f
-- ============================================================
INSERT INTO users (username, password, role, full_name, email, phone) VALUES
-- Admins
('admin',      'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'ADMIN',   'System Administrator', 'admin@school.edu',     '555-0001'),
('registrar',  'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'ADMIN',   'Jane Registrar',       'registrar@school.edu', '555-0002'),
-- Teachers
('t.smith',    'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'TEACHER', 'Dr. John Smith',       'j.smith@school.edu',   '555-0011'),
('t.johnson',  'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'TEACHER', 'Prof. Sarah Johnson',  's.johnson@school.edu', '555-0012'),
('t.williams', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'TEACHER', 'Mr. Robert Williams',  'r.williams@school.edu','555-0013'),
-- Students
('s.alice',    'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'STUDENT', 'Alice Brown',          'alice@student.edu',    '555-0021'),
('s.bob',      'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'STUDENT', 'Bob Davis',            'bob@student.edu',      '555-0022'),
('s.carol',    'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'STUDENT', 'Carol Evans',          'carol@student.edu',    '555-0023'),
('s.david',    'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'STUDENT', 'David Foster',         'david@student.edu',    '555-0024'),
('s.emma',     'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'STUDENT', 'Emma Garcia',          'emma@student.edu',     '555-0025');

-- ADMINS
INSERT INTO admins (user_id, department) VALUES
(1, 'Administration'),
(2, 'Registrar Office');

-- TEACHERS
INSERT INTO teachers (user_id, employee_id, department, qualification, hire_date) VALUES
(3, 'EMP-001', 'Computer Science', 'PhD Computer Science', '2018-08-01'),
(4, 'EMP-002', 'Mathematics',      'PhD Mathematics',      '2019-01-15'),
(5, 'EMP-003', 'English',          'MA English Literature','2020-06-01');

-- STUDENTS
INSERT INTO students (user_id, student_number, date_of_birth, gender, address, enrollment_year, program, year_level) VALUES
(6,  'STU-2023-001', '2002-03-15', 'Female', '123 Main St',   2023, 'BS Computer Science', 2),
(7,  'STU-2023-002', '2001-07-22', 'Male',   '456 Oak Ave',   2023, 'BS Computer Science', 2),
(8,  'STU-2022-003', '2003-11-08', 'Female', '789 Pine Rd',   2022, 'BS Mathematics',      3),
(9,  'STU-2023-004', '2002-05-30', 'Male',   '321 Elm St',    2023, 'BS Computer Science', 2),
(10, 'STU-2022-005', '2001-09-14', 'Female', '654 Maple Ave', 2022, 'BS English',          3);

-- COURSES
INSERT INTO courses (course_code, course_name, description, credits, teacher_id, semester, academic_year) VALUES
('CS101', 'Introduction to Programming',   'Basic programming concepts using Java',    3, 1, 'First',  '2024-2025'),
('CS201', 'Data Structures & Algorithms',  'Arrays, lists, trees, sorting algorithms', 3, 1, 'First',  '2024-2025'),
('MATH101','Calculus I',                   'Limits, derivatives, and integrals',       4, 2, 'First',  '2024-2025'),
('ENG101', 'English Composition',          'Academic writing and communication',       3, 3, 'First',  '2024-2025'),
('CS301', 'Database Management Systems',   'Relational databases and SQL',             3, 1, 'Second', '2024-2025');

-- ENROLLMENTS
INSERT INTO enrollments (student_id, course_id) VALUES
(1, 1),(1, 2),(1, 3),(1, 4),
(2, 1),(2, 2),(2, 3),
(3, 3),(3, 4),
(4, 1),(4, 4),
(5, 4);

-- ASSESSMENTS for CS101
INSERT INTO assessments (course_id, title, type, max_marks, weight, due_date) VALUES
(1, 'Quiz 1',        'QUIZ',       20, 10, '2024-09-15'),
(1, 'Assignment 1',  'ASSIGNMENT', 50, 15, '2024-09-30'),
(1, 'Midterm Exam',  'MIDTERM',   100, 30, '2024-10-20'),
(1, 'Assignment 2',  'ASSIGNMENT', 50, 15, '2024-11-10'),
(1, 'Final Exam',    'FINAL',     100, 30, '2024-12-10'),
-- ASSESSMENTS for CS201
(2, 'Quiz 1',        'QUIZ',       20, 10, '2024-09-20'),
(2, 'Assignment 1',  'ASSIGNMENT', 50, 15, '2024-10-05'),
(2, 'Midterm Exam',  'MIDTERM',   100, 30, '2024-10-25'),
(2, 'Final Exam',    'FINAL',     100, 45, '2024-12-12'),
-- ASSESSMENTS for MATH101
(3, 'Quiz 1',        'QUIZ',       25, 10, '2024-09-18'),
(3, 'Midterm Exam',  'MIDTERM',   100, 40, '2024-10-22'),
(3, 'Final Exam',    'FINAL',     100, 50, '2024-12-15');

-- GRADES for student 1 (Alice) in CS101
INSERT INTO grades (student_id, assessment_id, marks_obtained, graded_by) VALUES
(1, 1, 17, 3),(1, 2, 44, 3),(1, 3, 82, 3),(1, 4, 46, 3),(1, 5, 88, 3),
-- Alice in CS201
(1, 6, 18, 3),(1, 7, 43, 3),(1, 8, 78, 3),(1, 9, 85, 3),
-- Bob in CS101
(2, 1, 15, 3),(2, 2, 38, 3),(2, 3, 70, 3),(2, 4, 42, 3),(2, 5, 75, 3);

-- ATTENDANCE samples (last 5 class days)
INSERT INTO attendance (student_id, course_id, att_date, status, recorded_by) VALUES
(1,1,'2024-11-04','PRESENT',3),(1,1,'2024-11-06','PRESENT',3),(1,1,'2024-11-08','LATE',3),
(1,1,'2024-11-11','PRESENT',3),(1,1,'2024-11-13','PRESENT',3),
(2,1,'2024-11-04','PRESENT',3),(2,1,'2024-11-06','ABSENT',3),(2,1,'2024-11-08','PRESENT',3),
(2,1,'2024-11-11','PRESENT',3),(2,1,'2024-11-13','ABSENT',3),
(3,3,'2024-11-04','PRESENT',4),(3,3,'2024-11-06','PRESENT',4),(3,3,'2024-11-08','PRESENT',4),
(4,1,'2024-11-04','ABSENT',3),(4,1,'2024-11-06','PRESENT',3),(4,1,'2024-11-08','PRESENT',3);
