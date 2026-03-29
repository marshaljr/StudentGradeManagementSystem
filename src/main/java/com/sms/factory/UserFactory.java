package com.sms.factory;

import com.sms.model.AdminUser;
import com.sms.model.StudentUser;
import com.sms.model.TeacherUser;
import com.sms.model.UserAccount;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

/**
 * UserFactory — Factory Method pattern.
 *
 * Creates the correct UserAccount subclass based on the role string.
 * The caller doesn't need to know which subclass is being created.
 *
 * ----------------------------------------------------------------
 * OOP Principle: Factory Method (creational design pattern)
 *   - Decouples object creation from usage
 *   - Open/Closed: add new roles without changing existing code
 * ----------------------------------------------------------------
 */
public class UserFactory {

    private UserFactory() {}

    /**
     * Creates a UserAccount subclass from a JDBC ResultSet row.
     * The ResultSet must have joined users + role-specific table.
     *
     * @param rs   ResultSet positioned at the current row
     * @param role "ADMIN", "TEACHER", or "STUDENT"
     * @return the appropriate UserAccount subclass
     */
    public static UserAccount createFromResultSet(ResultSet rs, String role)
            throws SQLException {

        return switch (role.toUpperCase()) {
            case "ADMIN"   -> buildAdmin(rs);
            case "TEACHER" -> buildTeacher(rs);
            case "STUDENT" -> buildStudent(rs);
            default -> throw new IllegalArgumentException("Unknown role: " + role);
        };
    }

    // ── Private builders ──────────────────────────────────────────

    private static AdminUser buildAdmin(ResultSet rs) throws SQLException {
        AdminUser admin = new AdminUser();
        populateBase(admin, rs);
        admin.setAdminId(rs.getInt("admin_id"));
        admin.setDepartment(rs.getString("department"));
        return admin;
    }

    private static TeacherUser buildTeacher(ResultSet rs) throws SQLException {
        TeacherUser teacher = new TeacherUser();
        populateBase(teacher, rs);
        teacher.setTeacherId(rs.getInt("teacher_id"));
        teacher.setEmployeeId(rs.getString("employee_id"));
        teacher.setDepartment(rs.getString("department"));
        teacher.setQualification(rs.getString("qualification"));
        java.sql.Date hd = rs.getDate("hire_date");
        if (hd != null) teacher.setHireDate(hd.toLocalDate());
        return teacher;
    }

    private static StudentUser buildStudent(ResultSet rs) throws SQLException {
        StudentUser student = new StudentUser();
        populateBase(student, rs);
        student.setStudentId(rs.getInt("student_id"));
        student.setStudentNumber(rs.getString("student_number"));
        java.sql.Date dob = rs.getDate("date_of_birth");
        if (dob != null) student.setDateOfBirth(dob.toLocalDate());
        student.setGender(rs.getString("gender"));
        student.setAddress(rs.getString("address"));
        student.setEnrollmentYear(rs.getInt("enrollment_year"));
        student.setProgram(rs.getString("program"));
        student.setYearLevel(rs.getInt("year_level"));
        return student;
    }

    /** Populates common UserAccount fields from the users table columns. */
    private static void populateBase(UserAccount user, ResultSet rs) throws SQLException {
        user.setUserId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setPasswordHash(rs.getString("password"));
        user.setRole(rs.getString("role"));
        user.setFullName(rs.getString("full_name"));
        user.setEmail(rs.getString("email"));
        user.setPhone(rs.getString("phone"));
        user.setActive(rs.getBoolean("is_active"));
    }
}
