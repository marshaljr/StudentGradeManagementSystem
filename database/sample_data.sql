-- ============================================================
-- Sample Data for Student Grade Management System
-- Run schema.sql first, then this file
-- ============================================================
USE student_grade_db;

<<<<<<< HEAD
-- Disable foreign key checks temporarily
SET FOREIGN_KEY_CHECKS=0;

-- Clear existing data (if re-running the script)
DELETE FROM attendance;
DELETE FROM grades;
DELETE FROM assessments;
DELETE FROM enrollments;
DELETE FROM courses;
DELETE FROM students;
DELETE FROM teachers;
DELETE FROM admins;
DELETE FROM users;

-- Reset auto-increment values
ALTER TABLE users AUTO_INCREMENT = 1;
ALTER TABLE admins AUTO_INCREMENT = 1;
ALTER TABLE teachers AUTO_INCREMENT = 1;
ALTER TABLE students AUTO_INCREMENT = 1;
ALTER TABLE courses AUTO_INCREMENT = 1;
ALTER TABLE enrollments AUTO_INCREMENT = 1;
ALTER TABLE assessments AUTO_INCREMENT = 1;
ALTER TABLE grades AUTO_INCREMENT = 1;
ALTER TABLE attendance AUTO_INCREMENT = 1;

-- Re-enable foreign key checks
SET FOREIGN_KEY_CHECKS=1;

=======
>>>>>>> dc8ddd81eefd8c1fff1c6df629e2bf26eab2a51f
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

<<<<<<< HEAD
=======
-- ENROLLMENTS
INSERT INTO enrollments (student_id, course_id) VALUES
(1, 1),(1, 2),(1, 3),(1, 4),
(2, 1),(2, 2),(2, 3),
(3, 3),(3, 4),
(4, 1),(4, 4),
(5, 4);

>>>>>>> dc8ddd81eefd8c1fff1c6df629e2bf26eab2a51f
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

<<<<<<< HEAD
-- ============================================================
-- ADDITIONAL DATA TO REACH 400+ RECORDS
-- ============================================================

-- Additional Admins (3 more, total 5)
INSERT INTO users (username, password, role, full_name, email, phone) VALUES
('admin2', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'ADMIN', 'Michael Admin', 'admin2@school.edu', '555-0003'),
('admin3', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'ADMIN', 'Patricia Supervisor', 'admin3@school.edu', '555-0004'),
('admin4', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'ADMIN', 'James Manager', 'admin4@school.edu', '555-0005');

-- Additional Teachers (17 more, total 20)
INSERT INTO users (username, password, role, full_name, email, phone) VALUES
('t.miller',    'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'TEACHER', 'Dr. David Miller',       'd.miller@school.edu', '555-0014'),
('t.davis',     'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'TEACHER', 'Prof. Elizabeth Davis',  'e.davis@school.edu', '555-0015'),
('t.wilson',    'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'TEACHER', 'Mr. Christopher Wilson', 'c.wilson@school.edu', '555-0016'),
('t.moore',     'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'TEACHER', 'Dr. Lisa Moore',         'l.moore@school.edu', '555-0017'),
('t.taylor',    'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'TEACHER', 'Prof. Daniel Taylor',   'd.taylor@school.edu', '555-0018'),
('t.anderson',  'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'TEACHER', 'Mr. Kevin Anderson',    'k.anderson@school.edu', '555-0019'),
('t.thomas',    'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'TEACHER', 'Dr. Rebecca Thomas',    'r.thomas@school.edu', '555-0020'),
('t.jackson',   'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'TEACHER', 'Prof. Michelle Jackson', 'm.jackson@school.edu', '555-0021'),
('t.white',     'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'TEACHER', 'Mr. Joseph White',      'j.white@school.edu', '555-0022'),
('t.harris',    'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'TEACHER', 'Dr. Linda Harris',      'l.harris@school.edu', '555-0023'),
('t.martin',    'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'TEACHER', 'Prof. Steven Martin',   's.martin@school.edu', '555-0024'),
('t.clark',     'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'TEACHER', 'Mr. Paul Clark',        'p.clark@school.edu', '555-0025'),
('t.lewis',     'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'TEACHER', 'Dr. Jennifer Lewis',    'j.lewis@school.edu', '555-0026'),
('t.walker',    'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'TEACHER', 'Prof. Nancy Walker',    'n.walker@school.edu', '555-0027'),
('t.hall',      'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'TEACHER', 'Mr. Mark Hall',         'm.hall@school.edu', '555-0028'),
('t.young',     'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'TEACHER', 'Dr. Barbara Young',     'b.young@school.edu', '555-0029'),
('t.thompson',  'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'TEACHER', 'Prof. Richard Thompson', 'r.thompson@school.edu', '555-0030');

-- 100 Student Users
INSERT INTO users (username, password, role, full_name, email, phone) VALUES
('s.student001', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'STUDENT', 'Student One', 's001@student.edu', '555-0101'),
('s.student002', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'STUDENT', 'Student Two', 's002@student.edu', '555-0102'),
('s.student003', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'STUDENT', 'Student Three', 's003@student.edu', '555-0103'),
('s.student004', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'STUDENT', 'Student Four', 's004@student.edu', '555-0104'),
('s.student005', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'STUDENT', 'Student Five', 's005@student.edu', '555-0105'),
('s.student006', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'STUDENT', 'Student Six', 's006@student.edu', '555-0106'),
('s.student007', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'STUDENT', 'Student Seven', 's007@student.edu', '555-0107'),
('s.student008', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'STUDENT', 'Student Eight', 's008@student.edu', '555-0108'),
('s.student009', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'STUDENT', 'Student Nine', 's009@student.edu', '555-0109'),
('s.student010', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'STUDENT', 'Student Ten', 's010@student.edu', '555-0110'),
('s.student011', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'STUDENT', 'Student Eleven', 's011@student.edu', '555-0111'),
('s.student012', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'STUDENT', 'Student Twelve', 's012@student.edu', '555-0112'),
('s.student013', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'STUDENT', 'Student Thirteen', 's013@student.edu', '555-0113'),
('s.student014', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'STUDENT', 'Student Fourteen', 's014@student.edu', '555-0114'),
('s.student015', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'STUDENT', 'Student Fifteen', 's015@student.edu', '555-0115'),
('s.student016', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'STUDENT', 'Student Sixteen', 's016@student.edu', '555-0116'),
('s.student017', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'STUDENT', 'Student Seventeen', 's017@student.edu', '555-0117'),
('s.student018', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'STUDENT', 'Student Eighteen', 's018@student.edu', '555-0118'),
('s.student019', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'STUDENT', 'Student Nineteen', 's019@student.edu', '555-0119'),
('s.student020', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'STUDENT', 'Student Twenty', 's020@student.edu', '555-0120'),
('s.student021', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'STUDENT', 'Student TwentyOne', 's021@student.edu', '555-0121'),
('s.student022', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'STUDENT', 'Student TwentyTwo', 's022@student.edu', '555-0122'),
('s.student023', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'STUDENT', 'Student TwentyThree', 's023@student.edu', '555-0123'),
('s.student024', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'STUDENT', 'Student TwentyFour', 's024@student.edu', '555-0124'),
('s.student025', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'STUDENT', 'Student TwentyFive', 's025@student.edu', '555-0125'),
('s.student026', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'STUDENT', 'Student TwentySix', 's026@student.edu', '555-0126'),
('s.student027', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'STUDENT', 'Student TwentySeven', 's027@student.edu', '555-0127'),
('s.student028', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'STUDENT', 'Student TwentyEight', 's028@student.edu', '555-0128'),
('s.student029', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'STUDENT', 'Student TwentyNine', 's029@student.edu', '555-0129'),
('s.student030', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'STUDENT', 'Student Thirty', 's030@student.edu', '555-0130'),
('s.student031', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'STUDENT', 'Student ThirtyOne', 's031@student.edu', '555-0131'),
('s.student032', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'STUDENT', 'Student ThirtyTwo', 's032@student.edu', '555-0132'),
('s.student033', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'STUDENT', 'Student ThirtyThree', 's033@student.edu', '555-0133'),
('s.student034', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'STUDENT', 'Student ThirtyFour', 's034@student.edu', '555-0134'),
('s.student035', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'STUDENT', 'Student ThirtyFive', 's035@student.edu', '555-0135'),
('s.student036', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'STUDENT', 'Student ThirtySix', 's036@student.edu', '555-0136'),
('s.student037', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'STUDENT', 'Student ThirtySeven', 's037@student.edu', '555-0137'),
('s.student038', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'STUDENT', 'Student ThirtyEight', 's038@student.edu', '555-0138'),
('s.student039', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'STUDENT', 'Student ThirtyNine', 's039@student.edu', '555-0139'),
('s.student040', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'STUDENT', 'Student Forty', 's040@student.edu', '555-0140'),
('s.student041', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'STUDENT', 'Student FortyOne', 's041@student.edu', '555-0141'),
('s.student042', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'STUDENT', 'Student FortyTwo', 's042@student.edu', '555-0142'),
('s.student043', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'STUDENT', 'Student FortyThree', 's043@student.edu', '555-0143'),
('s.student044', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'STUDENT', 'Student FortyFour', 's044@student.edu', '555-0144'),
('s.student045', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'STUDENT', 'Student FortyFive', 's045@student.edu', '555-0145'),
('s.student046', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'STUDENT', 'Student FortySix', 's046@student.edu', '555-0146'),
('s.student047', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'STUDENT', 'Student FortySeven', 's047@student.edu', '555-0147'),
('s.student048', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'STUDENT', 'Student FortyEight', 's048@student.edu', '555-0148'),
('s.student049', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'STUDENT', 'Student FortyNine', 's049@student.edu', '555-0149'),
('s.student050', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'STUDENT', 'Student Fifty', 's050@student.edu', '555-0150'),
('s.student051', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'STUDENT', 'Student FiftyOne', 's051@student.edu', '555-0151'),
('s.student052', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'STUDENT', 'Student FiftyTwo', 's052@student.edu', '555-0152'),
('s.student053', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'STUDENT', 'Student FiftyThree', 's053@student.edu', '555-0153'),
('s.student054', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'STUDENT', 'Student FiftyFour', 's054@student.edu', '555-0154'),
('s.student055', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'STUDENT', 'Student FiftyFive', 's055@student.edu', '555-0155'),
('s.student056', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'STUDENT', 'Student FiftySix', 's056@student.edu', '555-0156'),
('s.student057', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'STUDENT', 'Student FiftySeven', 's057@student.edu', '555-0157'),
('s.student058', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'STUDENT', 'Student FiftyEight', 's058@student.edu', '555-0158'),
('s.student059', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'STUDENT', 'Student FiftyNine', 's059@student.edu', '555-0159'),
('s.student060', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'STUDENT', 'Student Sixty', 's060@student.edu', '555-0160'),
('s.student061', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'STUDENT', 'Student SixtyOne', 's061@student.edu', '555-0161'),
('s.student062', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'STUDENT', 'Student SixtyTwo', 's062@student.edu', '555-0162'),
('s.student063', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'STUDENT', 'Student SixtyThree', 's063@student.edu', '555-0163'),
('s.student064', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'STUDENT', 'Student SixtyFour', 's064@student.edu', '555-0164'),
('s.student065', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'STUDENT', 'Student SixtyFive', 's065@student.edu', '555-0165'),
('s.student066', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'STUDENT', 'Student SixtySix', 's066@student.edu', '555-0166'),
('s.student067', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'STUDENT', 'Student SixtySeven', 's067@student.edu', '555-0167'),
('s.student068', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'STUDENT', 'Student SixtyEight', 's068@student.edu', '555-0168'),
('s.student069', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'STUDENT', 'Student SixtyNine', 's069@student.edu', '555-0169'),
('s.student070', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'STUDENT', 'Student Seventy', 's070@student.edu', '555-0170'),
('s.student071', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'STUDENT', 'Student SeventyOne', 's071@student.edu', '555-0171'),
('s.student072', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'STUDENT', 'Student SeventyTwo', 's072@student.edu', '555-0172'),
('s.student073', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'STUDENT', 'Student SeventyThree', 's073@student.edu', '555-0173'),
('s.student074', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'STUDENT', 'Student SeventyFour', 's074@student.edu', '555-0174'),
('s.student075', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'STUDENT', 'Student SeventyFive', 's075@student.edu', '555-0175'),
('s.student076', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'STUDENT', 'Student SeventySix', 's076@student.edu', '555-0176'),
('s.student077', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'STUDENT', 'Student SeventySeven', 's077@student.edu', '555-0177'),
('s.student078', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'STUDENT', 'Student SeventyEight', 's078@student.edu', '555-0178'),
('s.student079', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'STUDENT', 'Student SeventyNine', 's079@student.edu', '555-0179'),
('s.student080', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'STUDENT', 'Student Eighty', 's080@student.edu', '555-0180'),
('s.student081', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'STUDENT', 'Student EightyOne', 's081@student.edu', '555-0181'),
('s.student082', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'STUDENT', 'Student EightyTwo', 's082@student.edu', '555-0182'),
('s.student083', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'STUDENT', 'Student EightyThree', 's083@student.edu', '555-0183'),
('s.student084', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'STUDENT', 'Student EightyFour', 's084@student.edu', '555-0184'),
('s.student085', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'STUDENT', 'Student EightyFive', 's085@student.edu', '555-0185'),
('s.student086', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'STUDENT', 'Student EightySix', 's086@student.edu', '555-0186'),
('s.student087', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'STUDENT', 'Student EightySeven', 's087@student.edu', '555-0187'),
('s.student088', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'STUDENT', 'Student EightyEight', 's088@student.edu', '555-0188'),
('s.student089', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'STUDENT', 'Student EightyNine', 's089@student.edu', '555-0189'),
('s.student090', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'STUDENT', 'Student Ninety', 's090@student.edu', '555-0190'),
('s.student091', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'STUDENT', 'Student NinetyOne', 's091@student.edu', '555-0191'),
('s.student092', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'STUDENT', 'Student NinetyTwo', 's092@student.edu', '555-0192'),
('s.student093', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'STUDENT', 'Student NinetyThree', 's093@student.edu', '555-0193'),
('s.student094', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'STUDENT', 'Student NinetyFour', 's094@student.edu', '555-0194'),
('s.student095', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'STUDENT', 'Student NinetyFive', 's095@student.edu', '555-0195'),
('s.student096', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'STUDENT', 'Student NinetySix', 's096@student.edu', '555-0196'),
('s.student097', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'STUDENT', 'Student NinetySeven', 's097@student.edu', '555-0197'),
('s.student098', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'STUDENT', 'Student NinetyEight', 's098@student.edu', '555-0198'),
('s.student099', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'STUDENT', 'Student NinetyNine', 's099@student.edu', '555-0199'),
('s.student100', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'STUDENT', 'Student HundredOne', 's100@student.edu', '555-0200');

-- Additional Admin records
INSERT INTO admins (user_id, department) VALUES
(11, 'IT Support'),
(12, 'Finance'),
(13, 'Human Resources');

-- Additional Teacher records
INSERT INTO teachers (user_id, employee_id, department, qualification, hire_date) VALUES
(14, 'EMP-004', 'Physics', 'PhD Physics', '2020-09-01'),
(15, 'EMP-005', 'Chemistry', 'PhD Chemistry', '2021-01-15'),
(16, 'EMP-006', 'Biology', 'MA Biology', '2021-06-01'),
(17, 'EMP-007', 'History', 'PhD History', '2019-08-15'),
(18, 'EMP-008', 'Art', 'MA Art', '2020-02-01'),
(19, 'EMP-009', 'Music', 'MA Music', '2020-07-20'),
(20, 'EMP-010', 'PE', 'MA Sports Science', '2018-09-01'),
(21, 'EMP-011', 'Business', 'MBA', '2019-03-15'),
(22, 'EMP-012', 'Economics', 'PhD Economics', '2020-01-01'),
(23, 'EMP-013', 'Psychology', 'MA Psychology', '2021-09-01'),
(24, 'EMP-014', 'Sociology', 'MA Sociology', '2020-08-15'),
(25, 'EMP-015', 'Philosophy', 'PhD Philosophy', '2019-02-01'),
(26, 'EMP-016', 'Geography', 'MA Geography', '2021-04-01'),
(27, 'EMP-017', 'Law', 'JD', '2020-06-01'),
(28, 'EMP-018', 'Medicine', 'MD', '2018-08-01'),
(29, 'EMP-019', 'Engineering', 'PhD Engineering', '2019-11-01'),
(30, 'EMP-020', 'Architecture', 'MA Architecture', '2021-03-15');

-- 100 Student records
INSERT INTO students (user_id, student_number, date_of_birth, gender, address, enrollment_year, program, year_level) VALUES
(11, 'STU-2023-101', '2002-01-15', 'Male', '100 Campus Ave', 2023, 'BS Computer Science', 2),
(12, 'STU-2023-102', '2002-02-20', 'Female', '101 Tech Blvd', 2023, 'BS Computer Science', 2),
(13, 'STU-2023-103', '2002-03-10', 'Male', '102 Code Lane', 2023, 'BS Computer Science', 2),
(14, 'STU-2023-104', '2002-04-05', 'Female', '103 Data Drive', 2023, 'BS Data Science', 2),
(15, 'STU-2023-105', '2002-05-12', 'Male', '104 Logic Loop', 2023, 'BS Computer Science', 2),
(16, 'STU-2023-106', '2002-06-18', 'Female', '105 Algorithm Ave', 2023, 'BS Mathematics', 2),
(17, 'STU-2023-107', '2002-07-22', 'Male', '106 Byte Blvd', 2023, 'BS Computer Science', 2),
(18, 'STU-2023-108', '2002-08-30', 'Female', '107 Function Fwy', 2023, 'BS Computer Science', 2),
(19, 'STU-2023-109', '2002-09-14', 'Male', '108 Variable View', 2023, 'BS Physics', 2),
(20, 'STU-2023-110', '2002-10-25', 'Female', '109 Method Manor', 2023, 'BS Chemistry', 2),
(21, 'STU-2023-111', '2002-11-08', 'Male', '110 Class Court', 2023, 'BS Computer Science', 2),
(22, 'STU-2023-112', '2002-12-16', 'Female', '111 Object Oval', 2023, 'BS Computer Science', 2),
(23, 'STU-2023-113', '2003-01-20', 'Male', '112 Pointer Plaza', 2023, 'BS Mathematics', 2),
(24, 'STU-2023-114', '2003-02-14', 'Female', '113 Array Alley', 2023, 'BS Computer Science', 2),
(25, 'STU-2023-115', '2003-03-22', 'Male', '114 String Street', 2023, 'BS Data Science', 2),
(26, 'STU-2023-116', '2003-04-10', 'Female', '115 Vector Valley', 2023, 'BS Mathematics', 2),
(27, 'STU-2023-117', '2003-05-15', 'Male', '116 Stack Stack', 2023, 'BS Computer Science', 2),
(28, 'STU-2023-118', '2003-06-21', 'Female', '117 Queue Quad', 2023, 'BS Computer Science', 2),
(29, 'STU-2023-119', '2003-07-11', 'Male', '118 Hash Hall', 2023, 'BS Physics', 2),
(30, 'STU-2023-120', '2003-08-19', 'Female', '119 Node Nook', 2023, 'BS Biology', 2),
(31, 'STU-2022-121', '2001-01-10', 'Male', '120 Graph Garden', 2022, 'BS Computer Science', 3),
(32, 'STU-2022-122', '2001-02-18', 'Female', '121 Tree Trail', 2022, 'BS Computer Science', 3),
(33, 'STU-2022-123', '2001-03-25', 'Male', '122 Search Street', 2022, 'BS Mathematics', 3),
(34, 'STU-2022-124', '2001-04-30', 'Female', '123 Sort Square', 2022, 'BS Computer Science', 3),
(35, 'STU-2022-125', '2001-05-12', 'Male', '124 Loop Lane', 2022, 'BS Data Science', 3),
(36, 'STU-2022-126', '2001-06-20', 'Female', '125 Branch Boulevard', 2022, 'BS Mathematics', 3),
(37, 'STU-2022-127', '2001-07-08', 'Male', '126 Merge Manor', 2022, 'BS Computer Science', 3),
(38, 'STU-2022-128', '2001-08-14', 'Female', '127 Divide Drive', 2022, 'BS Computer Science', 3),
(39, 'STU-2022-129', '2001-09-22', 'Male', '128 Conquer Court', 2022, 'BS Physics', 3),
(40, 'STU-2022-130', '2001-10-30', 'Female', '129 Dynamic Depot', 2022, 'BS Chemistry', 3),
(41, 'STU-2022-131', '2001-11-18', 'Male', '130 Program Park', 2022, 'BS Computer Science', 3),
(42, 'STU-2022-132', '2001-12-26', 'Female', '131 Debug District', 2022, 'BS Computer Science', 3),
(43, 'STU-2022-133', '2002-01-05', 'Male', '132 Test Track', 2022, 'BS Mathematics', 3),
(44, 'STU-2022-134', '2002-02-13', 'Female', '133 Deploy Drive', 2022, 'BS Computer Science', 3),
(45, 'STU-2022-135', '2002-03-21', 'Male', '134 Release Road', 2022, 'BS Data Science', 3),
(46, 'STU-2022-136', '2002-04-29', 'Female', '135 Build Boulevard', 2022, 'BS Mathematics', 3),
(47, 'STU-2022-137', '2002-05-07', 'Male', '136 Compile Court', 2022, 'BS Computer Science', 3),
(48, 'STU-2022-138', '2002-06-15', 'Female', '137 Optimize Oval', 2022, 'BS Computer Science', 3),
(49, 'STU-2022-139', '2002-07-23', 'Male', '138 Cache Crest', 2022, 'BS Physics', 3),
(50, 'STU-2022-140', '2002-08-31', 'Female', '139 Memory Meadow', 2022, 'BS Hardware', 3),
(51, 'STU-2021-141', '2000-01-12', 'Male', '140 Register Range', 2021, 'BS Computer Science', 4),
(52, 'STU-2021-142', '2000-02-20', 'Female', '141 Bit Basin', 2021, 'BS Computer Science', 4),
(53, 'STU-2021-143', '2000-03-28', 'Male', '142 Byte Beach', 2021, 'BS Mathematics', 4),
(54, 'STU-2021-144', '2000-04-05', 'Female', '143 Word Wharf', 2021, 'BS Computer Science', 4),
(55, 'STU-2021-145', '2000-05-13', 'Male', '144 Pixel Plaza', 2021, 'BS Data Science', 4),
(56, 'STU-2021-146', '2000-06-21', 'Female', '145 Color Court', 2021, 'BS Mathematics', 4),
(57, 'STU-2021-147', '2000-07-29', 'Male', '146 Format Field', 2021, 'BS Computer Science', 4),
(58, 'STU-2021-148', '2000-08-06', 'Female', '147 Protocol Park', 2021, 'BS Computer Science', 4),
(59, 'STU-2021-149', '2000-09-14', 'Male', '148 Port Parquet', 2021, 'BS Physics', 4),
(60, 'STU-2021-150', '2000-10-22', 'Female', '149 Socket Street', 2021, 'BS Chemistry', 4),
(61, 'STU-2021-151', '2000-11-30', 'Male', '150 Server Suite', 2021, 'BS Computer Science', 4),
(62, 'STU-2021-152', '2000-12-08', 'Female', '151 Client Corner', 2021, 'BS Computer Science', 4),
(63, 'STU-2021-153', '2001-01-16', 'Male', '152 Request Row', 2021, 'BS Mathematics', 4),
(64, 'STU-2021-154', '2001-02-24', 'Female', '153 Response Rd', 2021, 'BS Computer Science', 4),
(65, 'STU-2021-155', '2001-03-03', 'Male', '154 Status Street', 2021, 'BS Data Science', 4),
(66, 'STU-2021-156', '2001-04-11', 'Female', '155 Error Estate', 2021, 'BS Mathematics', 4),
(67, 'STU-2021-157', '2001-05-19', 'Male', '156 Exception Exc', 2021, 'BS Computer Science', 4),
(68, 'STU-2021-158', '2001-06-27', 'Female', '157 Throw Terrace', 2021, 'BS Computer Science', 4),
(69, 'STU-2021-159', '2001-07-05', 'Male', '158 Catch Cove', 2021, 'BS Physics', 4),
(70, 'STU-2021-160', '2001-08-13', 'Female', '159 Finally Field', 2021, 'BS Chemistry', 4),
(71, 'STU-2020-161', '1999-01-17', 'Male', '160 Try Trail', 2020, 'BS Computer Science', 4),
(72, 'STU-2020-162', '1999-02-25', 'Female', '161 Catch Court', 2020, 'BS Computer Science', 4),
(73, 'STU-2020-163', '1999-03-05', 'Male', '162 Finally Fwy', 2020, 'BS Mathematics', 4),
(74, 'STU-2020-164', '1999-04-13', 'Female', '163 Synchronized St', 2020, 'BS Computer Science', 4),
(75, 'STU-2020-165', '1999-05-21', 'Male', '164 Volatile Valley', 2020, 'BS Data Science', 4),
(76, 'STU-2020-166', '1999-06-29', 'Female', '165 Transient Trl', 2020, 'BS Mathematics', 4),
(77, 'STU-2020-167', '1999-07-07', 'Male', '166 Strictfp Street', 2020, 'BS Computer Science', 4),
(78, 'STU-2020-168', '1999-08-15', 'Female', '167 Native Nook', 2020, 'BS Computer Science', 4),
(79, 'STU-2020-169', '1999-09-23', 'Male', '168 Interface Inlet', 2020, 'BS Physics', 4),
(80, 'STU-2020-170', '1999-10-31', 'Female', '169 Abstract Arch', 2020, 'BS Chemistry', 4),
(81, 'STU-2020-171', '1999-11-08', 'Male', '170 Final Forum', 2020, 'BS Computer Science', 4),
(82, 'STU-2020-172', '1999-12-16', 'Female', '171 Static Site', 2020, 'BS Computer Science', 4),
(83, 'STU-2020-173', '2000-01-24', 'Male', '172 Public Plaza', 2020, 'BS Mathematics', 4),
(84, 'STU-2020-174', '2000-02-01', 'Female', '173 Private Passage', 2020, 'BS Computer Science', 4),
(85, 'STU-2020-175', '2000-03-11', 'Male', '174 Protected Park', 2020, 'BS Data Science', 4),
(86, 'STU-2020-176', '2000-04-19', 'Female', '175 Package Palace', 2020, 'BS Mathematics', 4),
(87, 'STU-2020-177', '2000-05-27', 'Male', '176 Import Island', 2020, 'BS Computer Science', 4),
(88, 'STU-2020-178', '2000-06-04', 'Female', '177 Export Estate', 2020, 'BS Computer Science', 4),
(89, 'STU-2020-179', '2000-07-12', 'Male', '178 Extends Extent', 2020, 'BS Physics', 4),
(90, 'STU-2020-180', '2000-08-20', 'Female', '179 Implements Inn', 2020, 'BS Chemistry', 4),
(91, 'STU-2023-181', '2002-01-09', 'Male', '180 Super Lake', 2023, 'BS Computer Science', 2),
(92, 'STU-2023-182', '2002-02-17', 'Female', '181 This Theater', 2023, 'BS Computer Science', 2),
(93, 'STU-2023-183', '2002-03-27', 'Male', '182 New Niche', 2023, 'BS Mathematics', 2),
(94, 'STU-2023-184', '2002-04-04', 'Female', '183 Instance Inn', 2023, 'BS Computer Science', 2),
(95, 'STU-2023-185', '2002-05-12', 'Male', '184 Type Trail', 2023, 'BS Data Science', 2),
(96, 'STU-2023-186', '2002-06-20', 'Female', '185 Cast Coast', 2023, 'BS Mathematics', 2),
(97, 'STU-2023-187', '2002-07-28', 'Male', '186 Instanceof Island', 2023, 'BS Computer Science', 2),
(98, 'STU-2023-188', '2002-08-05', 'Female', '187 Return Road', 2023, 'BS Computer Science', 2),
(99, 'STU-2023-189', '2002-09-13', 'Male', '188 Break Beach', 2023, 'BS Physics', 2),
(100, 'STU-2023-190', '2002-10-21', 'Female', '189 Continue Court', 2023, 'BS Chemistry', 2),
(101, 'STU-2023-191', '2002-11-29', 'Male', '190 Switch Street', 2023, 'BS Computer Science', 2),
(102, 'STU-2023-192', '2002-12-07', 'Female', '191 Case Cue', 2023, 'BS Computer Science', 2),
(103, 'STU-2023-193', '2003-01-15', 'Male', '192 Default Drive', 2023, 'BS Mathematics', 2),
(104, 'STU-2023-194', '2003-02-23', 'Female', '193 For Forum', 2023, 'BS Computer Science', 2),
(105, 'STU-2023-195', '2003-03-02', 'Male', '194 While Wharf', 2023, 'BS Data Science', 2),
(106, 'STU-2023-196', '2003-04-10', 'Female', '195 Do Dover', 2023, 'BS Mathematics', 2),
(107, 'STU-2023-197', '2003-05-18', 'Male', '196 If Island', 2023, 'BS Computer Science', 2),
(108, 'STU-2023-198', '2003-06-26', 'Female', '197 Else Excel', 2023, 'BS Computer Science', 2),
(109, 'STU-2023-199', '2003-07-03', 'Male', '198 Else If Inlet', 2023, 'BS Physics', 2),
(110, 'STU-2023-200', '2003-08-11', 'Female', '199 Ternary Trail', 2023, 'BS Chemistry', 2);

-- Additional 40+ COURSES
INSERT INTO courses (course_code, course_name, description, credits, teacher_id, semester, academic_year) VALUES
('CS102', 'Web Development', 'HTML, CSS, JavaScript, and web frameworks', 3, 2, 'First', '2024-2025'),
('CS103', 'Mobile Development', 'iOS and Android development', 3, 3, 'First', '2024-2025'),
('CS202', 'Object Oriented Programming', 'Classes, inheritance, polymorphism', 3, 4, 'Second', '2024-2025'),
('CS203', 'Software Engineering', 'Design patterns, SOLID principles', 3, 5, 'Second', '2024-2025'),
('CS302', 'Operating Systems', 'Process management, memory management', 4, 6, 'First', '2024-2025'),
('CS303', 'Networks and Security', 'TCP/IP, encryption, firewalls', 3, 7, 'Second', '2024-2025'),
('MATH102', 'Linear Algebra', 'Matrices, vectors, eigenvalues', 4, 8, 'Second', '2024-2025'),
('MATH201', 'Discrete Mathematics', 'Sets, logic, graph theory', 3, 9, 'First', '2024-2025'),
('MATH202', 'Probability and Statistics', 'Random variables, hypothesis testing', 4, 8, 'Second', '2024-2025'),
('PHYS101', 'Mechanics', 'Motion, forces, energy', 4, 10, 'First', '2024-2025'),
('PHYS102', 'Electricity and Magnetism', 'Circuits, fields, waves', 4, 10, 'Second', '2024-2025'),
('CHEM101', 'General Chemistry', 'Atoms, bonding, reactions', 4, 11, 'First', '2024-2025'),
('CHEM102', 'Organic Chemistry', 'Hydrocarbons, functional groups', 4, 11, 'Second', '2024-2025'),
('BIO101', 'General Biology', 'Cells, genetics, evolution', 4, 12, 'First', '2024-2025'),
('BIO102', 'Ecology', 'Ecosystems, populations, communities', 3, 12, 'Second', '2024-2025'),
('ENG102', 'Literature Analysis', 'Novel interpretation and criticism', 3, 3, 'Second', '2024-2025'),
('ENG201', 'Creative Writing', 'Fiction, poetry, drama', 3, 3, 'First', '2024-2025'),
('HIST101', 'Ancient History', 'Civilizations and empires', 3, 13, 'First', '2024-2025'),
('HIST102', 'Modern History', 'Industrial revolution to present', 3, 13, 'Second', '2024-2025'),
('ART101', 'Art History', 'Movements and styles', 3, 14, 'First', '2024-2025'),
('ART102', 'Studio Art', 'Drawing, painting, sculpture', 3, 14, 'Second', '2024-2025'),
('MUS101', 'Music Theory', 'Scales, chords, composition', 3, 15, 'First', '2024-2025'),
('MUS102', 'Music History', 'Classical to contemporary', 3, 15, 'Second', '2024-2025'),
('PE101', 'Physical Education', 'Sports and fitness', 2, 16, 'First', '2024-2025'),
('BUS101', 'Business Fundamentals', 'Economics, accounting, management', 3, 17, 'First', '2024-2025'),
('BUS102', 'Marketing Basics', 'Consumer behavior, campaigns', 3, 17, 'Second', '2024-2025'),
('ECON101', 'Microeconomics', 'Supply, demand, markets', 3, 18, 'First', '2024-2025'),
('ECON102', 'Macroeconomics', 'GDP, inflation, unemployment', 3, 18, 'Second', '2024-2025'),
('PSY101', 'Introduction to Psychology', 'Behavior, cognition, development', 3, 19, 'First', '2024-2025'),
('PSY102', 'Social Psychology', 'Groups, influence, attitudes', 3, 19, 'Second', '2024-2025'),
('SOC101', 'Sociology Basics', 'Culture, socialization, institutions', 3, 20, 'First', '2024-2025'),
('SOC102', 'Social Issues', 'Inequality, justice, change', 3, 20, 'Second', '2024-2025'),
('PHIL101', 'Introduction to Philosophy', 'Logic, ethics, metaphysics', 3, 1, 'First', '2024-2025'),
('PHIL102', 'Epistemology', 'Knowledge, truth, belief', 3, 1, 'Second', '2024-2025'),
('GEO101', 'Physical Geography', 'Climate, landforms, ecosystems', 3, 2, 'First', '2024-2025'),
('GEO102', 'Human Geography', 'Culture, politics, development', 3, 2, 'Second', '2024-2025'),
('LAW101', 'Legal Foundations', 'Constitution, contracts, torts', 3, 3, 'First', '2024-2025'),
('LAW102', 'Criminal Law', 'Crimes, punishment, justice', 3, 3, 'Second', '2024-2025'),
('MED101', 'Medical Basics', 'Anatomy, physiology, pathology', 4, 4, 'First', '2024-2025'),
('MED102', 'Pharmacology', 'Drugs, mechanisms, side effects', 3, 4, 'Second', '2024-2025'),
('ENGF101', 'Engineering Fundamentals', 'Materials, mechanics, systems', 4, 5, 'First', '2024-2025'),
('ENG202', 'Mechanical Engineering', 'Thermodynamics, mechanics, design', 4, 5, 'Second', '2024-2025'),
('ARCH101', 'Architecture Basics', 'Design, materials, styles', 3, 6, 'First', '2024-2025'),
('ARCH102', 'Urban Planning', 'City design, zoning, infrastructure', 3, 6, 'Second', '2024-2025'),
('DS101', 'Data Science Introduction', 'Python, pandas, visualization', 3, 2, 'First', '2024-2025');

-- Additional ENROLLMENTS (150+ records)
INSERT INTO enrollments (student_id, course_id) VALUES
(1, 1),(1, 2),(1, 3),(1, 6),(1, 7),
(2, 1),(2, 2),(2, 4),(2, 8),(2, 9),
(3, 3),(3, 5),(3, 8),(3, 10),(3, 11),
(4, 1),(4, 4),(4, 6),(4, 12),(4, 13),
(5, 4),(5, 7),(5, 9),(5, 14),(5, 15),
(6, 1),(6, 2),(6, 3),(6, 8),
(7, 2),(7, 4),(7, 10),(7, 12),
(8, 3),(8, 5),(8, 11),(8, 13),
(9, 1),(9, 7),(9, 9),(9, 14),
(10, 4),(10, 6),(10, 12),(10, 15),
(11, 1),(11, 2),(11, 3),(11, 4),(11, 5),
(12, 2),(12, 4),(12, 6),(12, 8),(12, 10),
(13, 3),(13, 5),(13, 7),(13, 9),(13, 11),
(14, 1),(14, 4),(14, 8),(14, 12),(14, 13),
(15, 2),(15, 5),(15, 9),(15, 14),(15, 15),
(16, 1),(16, 3),(16, 10),(16, 13),
(17, 2),(17, 4),(17, 11),(17, 14),
(18, 3),(18, 6),(18, 12),(18, 15),
(19, 1),(19, 7),(19, 9),
(20, 4),(20, 8),(20, 13),
(21, 1),(21, 2),(21, 3),(21, 6),(21, 7),
(22, 2),(22, 4),(22, 8),(22, 10),(22, 12),
(23, 3),(23, 5),(23, 9),(23, 11),(23, 14),
(24, 1),(24, 6),(24, 13),(24, 15),
(25, 4),(25, 7),(25, 9),(25, 14),
(26, 1),(26, 2),(26, 5),(26, 8),(26, 12),
(27, 3),(27, 4),(27, 10),(27, 13),(27, 15),
(28, 2),(28, 6),(28, 7),(28, 11),(28, 14),
(29, 1),(29, 5),(29, 9),
(30, 4),(30, 8),(30, 12),
(31, 1),(31, 2),(31, 3),(31, 6),(31, 7),
(32, 2),(32, 4),(32, 8),(32, 10),(32, 12),
(33, 3),(33, 5),(33, 9),(33, 11),(33, 14),
(34, 1),(34, 6),(34, 13),(34, 15),
(35, 4),(35, 7),(35, 9),(35, 14),
(36, 1),(36, 2),(36, 5),(36, 8),(36, 12),
(37, 3),(37, 4),(37, 10),(37, 13),(37, 15),
(38, 2),(38, 6),(38, 7),(38, 11),(38, 14),
(39, 1),(39, 5),(39, 9),
(40, 4),(40, 8),(40, 12),
(41, 1),(41, 2),(41, 3),(41, 4),(41, 5),
(42, 2),(42, 6),(42, 7),(42, 8),(42, 9),
(43, 3),(43, 10),(43, 11),(43, 12),(43, 13),
(44, 4),(44, 14),(44, 15),(44, 16),(44, 17),
(45, 1),(45, 5),(45, 9),(45, 18),(45, 19),
(46, 2),(46, 6),(46, 10),(46, 20),(46, 21),
(47, 3),(47, 7),(47, 11),(47, 22),(47, 23),
(48, 4),(48, 8),(48, 12),(48, 24),(48, 25),
(49, 1),(49, 9),(49, 14),
(50, 2),(50, 10),(50, 15),
(51, 1),(51, 2),(51, 3),(51, 4),(51, 5),
(52, 6),(52, 7),(52, 8),(52, 9),(52, 10),
(53, 11),(53, 12),(53, 13),(53, 14),(53, 15),
(54, 16),(54, 17),(54, 18),(54, 19),(54, 20),
(55, 1),(55, 5),(55, 9),(55, 21),(55, 25),
(56, 2),(56, 6),(56, 10),(56, 22),(56, 26),
(57, 3),(57, 7),(57, 11),(57, 23),(57, 27),
(58, 4),(58, 8),(58, 12),(58, 24),(58, 28),
(59, 1),(59, 13),(59, 29),
(60, 2),(60, 14),(60, 30),
(61, 3),(61, 5),(61, 15),(61, 31),(61, 32),
(62, 4),(62, 6),(62, 16),(62, 33),(62, 34),
(63, 7),(63, 8),(63, 17),(63, 35),(63, 36),
(64, 9),(64, 10),(64, 18),(64, 37),(64, 38),
(65, 11),(65, 12),(65, 19),(65, 39),
(66, 13),(66, 14),(66, 20),(66, 40),
(67, 1),(67, 2),(67, 21),(67, 22),
(68, 3),(68, 4),(68, 23),(68, 24),
(69, 5),(69, 25),
(70, 6),(70, 26),
(71, 1),(71, 2),(71, 3),(71, 4),(71, 5),
(72, 6),(72, 7),(72, 8),(72, 9),(72, 10),
(73, 11),(73, 12),(73, 13),(73, 14),(73, 15),
(74, 16),(74, 17),(74, 18),(74, 19),(74, 20),
(75, 1),(75, 21),(75, 22),(75, 25),
(76, 2),(76, 23),(76, 24),(76, 26),
(77, 3),(77, 27),(77, 28),
(78, 4),(78, 29),(78, 30),
(79, 5),(79, 31),
(80, 6),(80, 32),
(81, 1),(81, 2),(81, 7),(81, 8),(81, 9),
(82, 3),(82, 4),(82, 10),(82, 11),(82, 12),
(83, 5),(83, 6),(83, 13),(83, 14),(83, 15),
(84, 7),(84, 8),(84, 16),(84, 17),(84, 18),
(85, 9),(85, 10),(85, 19),(85, 20),
(86, 11),(86, 12),(86, 21),(86, 22),
(87, 1),(87, 13),(87, 23),
(88, 2),(88, 14),(88, 24),
(89, 3),(89, 25),
(90, 4),(90, 26),
(91, 1),(91, 2),(91, 3),(91, 4),
(92, 5),(92, 6),(92, 7),(92, 8),
(93, 9),(93, 10),(93, 11),(93, 12),
(94, 13),(94, 14),(94, 15),(94, 16),
(95, 17),(95, 18),(95, 19),(95, 20),
(96, 21),(96, 22),(96, 23),(96, 24),
(97, 25),(97, 26),(97, 27),(97, 28),
(98, 29),(98, 30),(98, 31),(98, 32),
(99, 33),(99, 34),
(100, 35),(100, 36);

-- Additional GRADES (80+ records)
INSERT INTO grades (student_id, assessment_id, marks_obtained, graded_by) VALUES
(1, 1, 17, 3),(2, 1, 15, 3),(3, 1, 18, 3),(4, 1, 16, 3),(5, 1, 19, 3),
(6, 1, 14, 3),(7, 1, 17, 3),(8, 1, 16, 3),(9, 1, 15, 3),(10, 1, 18, 3),
(1, 2, 44, 3),(2, 2, 38, 3),(3, 2, 48, 3),(4, 2, 42, 3),(5, 2, 45, 3),
(6, 2, 40, 3),(7, 2, 46, 3),(8, 2, 43, 3),(9, 2, 39, 3),(10, 2, 47, 3),
(11, 1, 16, 3),(12, 1, 17, 3),(13, 1, 15, 3),(14, 1, 18, 3),(15, 1, 17, 3),
(11, 2, 41, 3),(12, 2, 44, 3),(13, 2, 39, 3),(14, 2, 46, 3),(15, 2, 43, 3),
(16, 1, 14, 3),(17, 1, 19, 3),(18, 1, 16, 3),(19, 1, 15, 3),(20, 1, 17, 3),
(16, 2, 36, 3),(17, 2, 49, 3),(18, 2, 42, 3),(19, 2, 38, 3),(20, 2, 44, 3),
(21, 1, 18, 3),(22, 1, 16, 3),(23, 1, 17, 3),(24, 1, 15, 3),(25, 1, 19, 3),
(21, 2, 45, 3),(22, 2, 41, 3),(23, 2, 47, 3),(24, 2, 40, 3),(25, 2, 48, 3),
(26, 1, 17, 3),(27, 1, 18, 3),(28, 1, 16, 3),(29, 1, 14, 3),(30, 1, 19, 3),
(26, 2, 43, 3),(27, 2, 46, 3),(28, 2, 42, 3),(29, 2, 37, 3),(30, 2, 49, 3),
(31, 1, 15, 3),(32, 1, 17, 3),(33, 1, 18, 3),(34, 1, 16, 3),(35, 1, 14, 3),
(31, 2, 39, 3),(32, 2, 44, 3),(33, 2, 48, 3),(34, 2, 41, 3),(35, 2, 36, 3),
(36, 1, 19, 3),(37, 1, 16, 3),(38, 1, 17, 3),(39, 1, 15, 3),(40, 1, 18, 3),
(36, 2, 47, 3),(37, 2, 42, 3),(38, 2, 45, 3),(39, 2, 38, 3),(40, 2, 46, 3),
(41, 1, 16, 3),(42, 1, 18, 3),(43, 1, 14, 3),(44, 1, 19, 3),(45, 1, 17, 3),
(41, 2, 41, 3),(42, 2, 47, 3),(43, 2, 36, 3),(44, 2, 49, 3),(45, 2, 44, 3),
(46, 1, 15, 3),(47, 1, 17, 3),(48, 1, 16, 3),(49, 1, 18, 3),(50, 1, 14, 3),
(46, 2, 38, 3),(47, 2, 45, 3),(48, 2, 42, 3),(49, 2, 47, 3),(50, 2, 35, 3);

-- Additional ATTENDANCE (100+ records)
INSERT INTO attendance (student_id, course_id, att_date, status, recorded_by) VALUES
(1, 1, '2024-11-04', 'PRESENT', 3),(2, 1, '2024-11-04', 'PRESENT', 3),(3, 1, '2024-11-04', 'ABSENT', 3),(4, 1, '2024-11-04', 'PRESENT', 3),(5, 1, '2024-11-04', 'LATE', 3),
(6, 1, '2024-11-04', 'PRESENT', 3),(7, 1, '2024-11-04', 'PRESENT', 3),(8, 1, '2024-11-04', 'ABSENT', 3),(9, 1, '2024-11-04', 'PRESENT', 3),(10, 1, '2024-11-04', 'PRESENT', 3),
(11, 1, '2024-11-06', 'PRESENT', 3),(12, 1, '2024-11-06', 'LATE', 3),(13, 1, '2024-11-06', 'PRESENT', 3),(14, 1, '2024-11-06', 'PRESENT', 3),(15, 1, '2024-11-06', 'ABSENT', 3),
(16, 1, '2024-11-06', 'PRESENT', 3),(17, 1, '2024-11-06', 'PRESENT', 3),(18, 1, '2024-11-06', 'PRESENT', 3),(19, 1, '2024-11-06', 'LATE', 3),(20, 1, '2024-11-06', 'PRESENT', 3),
(21, 1, '2024-11-08', 'PRESENT', 3),(22, 1, '2024-11-08', 'PRESENT', 3),(23, 1, '2024-11-08', 'PRESENT', 3),(24, 1, '2024-11-08', 'ABSENT', 3),(25, 1, '2024-11-08', 'PRESENT', 3),
(26, 1, '2024-11-08', 'LATE', 3),(27, 1, '2024-11-08', 'PRESENT', 3),(28, 1, '2024-11-08', 'PRESENT', 3),(29, 1, '2024-11-08', 'PRESENT', 3),(30, 1, '2024-11-08', 'ABSENT', 3),
(31, 1, '2024-11-11', 'PRESENT', 3),(32, 1, '2024-11-11', 'PRESENT', 3),(33, 1, '2024-11-11', 'LATE', 3),(34, 1, '2024-11-11', 'PRESENT', 3),(35, 1, '2024-11-11', 'PRESENT', 3),
(36, 1, '2024-11-11', 'ABSENT', 3),(37, 1, '2024-11-11', 'PRESENT', 3),(38, 1, '2024-11-11', 'PRESENT', 3),(39, 1, '2024-11-11', 'PRESENT', 3),(40, 1, '2024-11-11', 'LATE', 3),
(41, 1, '2024-11-13', 'PRESENT', 3),(42, 1, '2024-11-13', 'ABSENT', 3),(43, 1, '2024-11-13', 'PRESENT', 3),(44, 1, '2024-11-13', 'PRESENT', 3),(45, 1, '2024-11-13', 'PRESENT', 3),
(46, 1, '2024-11-13', 'LATE', 3),(47, 1, '2024-11-13', 'PRESENT', 3),(48, 1, '2024-11-13', 'PRESENT', 3),(49, 1, '2024-11-13', 'ABSENT', 3),(50, 1, '2024-11-13', 'PRESENT', 3),
(1, 6, '2024-11-04', 'PRESENT', 3),(2, 6, '2024-11-04', 'ABSENT', 3),(3, 6, '2024-11-04', 'PRESENT', 3),(4, 6, '2024-11-04', 'LATE', 3),(5, 6, '2024-11-04', 'PRESENT', 3),
(6, 6, '2024-11-04', 'PRESENT', 3),(7, 6, '2024-11-04', 'PRESENT', 3),(8, 6, '2024-11-04', 'ABSENT', 3),(9, 6, '2024-11-04', 'PRESENT', 3),(10, 6, '2024-11-04', 'PRESENT', 3),
(51, 1, '2024-11-15', 'PRESENT', 3),(52, 1, '2024-11-15', 'PRESENT', 3),(53, 1, '2024-11-15', 'PRESENT', 3),(54, 1, '2024-11-15', 'ABSENT', 3),(55, 1, '2024-11-15', 'PRESENT', 3),
(56, 1, '2024-11-15', 'LATE', 3),(57, 1, '2024-11-15', 'PRESENT', 3),(58, 1, '2024-11-15', 'PRESENT', 3),(59, 1, '2024-11-15', 'PRESENT', 3),(60, 1, '2024-11-15', 'ABSENT', 3),
(61, 1, '2024-11-17', 'PRESENT', 3),(62, 1, '2024-11-17', 'PRESENT', 3),(63, 1, '2024-11-17', 'LATE', 3),(64, 1, '2024-11-17', 'PRESENT', 3),(65, 1, '2024-11-17', 'PRESENT', 3),
(66, 1, '2024-11-17', 'ABSENT', 3),(67, 1, '2024-11-17', 'PRESENT', 3),(68, 1, '2024-11-17', 'PRESENT', 3),(69, 1, '2024-11-17', 'PRESENT', 3),(70, 1, '2024-11-17', 'LATE', 3);
=======
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
>>>>>>> dc8ddd81eefd8c1fff1c6df629e2bf26eab2a51f
