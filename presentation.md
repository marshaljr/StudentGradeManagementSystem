# Student Grade Management System

## Title Slide

- Student Grade Management System
- Professional Project Presentation
- Technology: Java, JavaFX, MySQL
- Architecture: OOP-based Desktop Application
- Date: March 26, 2026

---

## Introduction

- A desktop system to manage student academic records efficiently
- Supports different user roles: Admin, Teacher, Student
- Combines secure access, data management, and performance tracking
- Built to improve speed, accuracy, and organization

---

## Problem Statement

- Manual record management is slow and error-prone
- Grade calculations can be inconsistent
- Data is often scattered across files/sheets
- No centralized secure system for role-based access

---

## Objectives

- Create a secure login-based management system
- Manage student records in a structured database
- Automate grade calculations for consistency
- Build an intuitive JavaFX GUI for daily use
- Ensure reliable MySQL database integration

---

## System Architecture

- OOP-based layered desktop architecture
- Presentation Layer: JavaFX views (FXML + Controllers)
- Business Layer: models and utility services
- Data Layer: DAO classes handling SQL operations
- Storage Layer: MySQL relational database

---

## Technologies Used

- Java (core application logic)
- JavaFX (GUI development)
- MySQL (database storage)
- JDBC (database connectivity)
- Maven (build and dependency management)
- CSS + FXML (UI structure and styling)

---

## Key Features

- User login and session management
- Student profile and record management
- Grade entry and automatic grade calculation
- Attendance and course tracking
- Role-based dashboards (Admin/Teacher/Student)
- User-friendly desktop interface

---

## Database Design

- Main entities:
  - users
  - students
  - teachers
  - courses
  - grades
  - attendance
- Relational design with primary and foreign keys
- Normalized schema for consistency and reduced redundancy
- Supports fast retrieval for dashboards and reports

---

## GUI Screens (Placeholders)

- Login Screen
- Admin Dashboard
- Teacher Dashboard
- Student Dashboard
- Grade/Attendance Management Screen
- Report/Result View Screen

---

## Code Structure (OOP Concepts Used)

- Encapsulation: private fields with getters/setters in model classes
- Inheritance: user roles derived from base user account structure
- Abstraction: DAO layer hides SQL complexity
- Polymorphism: role-specific behavior in controllers/users
- Modularity: package structure (controller, dao, model, util)

---

## Challenges & Solutions

- Challenge: Role-based access control
  - Solution: Implemented session manager and access checks
- Challenge: Accurate grade computation
  - Solution: Centralized logic in grade calculator utility
- Challenge: Reliable DB operations
  - Solution: DAO pattern + reusable database config
- Challenge: Smooth screen navigation
  - Solution: Common navigation utility and FXML-based routing

---

## Conclusion

- Successfully built a practical student grade management desktop app
- Improved data accuracy and reduced manual workload
- Demonstrated strong use of Java, JavaFX, MySQL, and OOP principles
- Provides a solid base for future expansion

---

## Future Improvements

- Add analytics dashboards with charts
- Export results to PDF/Excel
- Add email/SMS notifications
- Introduce stronger authentication (OTP/2FA)
- Implement audit logs and activity tracking
- Extend to web/mobile integration via REST APIs
