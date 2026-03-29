package com.sms.dao;

import com.sms.factory.UserFactory;
import com.sms.model.*;
import com.sms.util.DatabaseConfig;
import com.sms.util.PasswordUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * UserDAO — Data Access Object for all user operations.
 *
 * Contains all SQL queries related to users, students, teachers, admins.
 * Controllers never write SQL — they call methods here.
 *
 * ----------------------------------------------------------------
 * OOP Principle: Separation of Concerns (data layer isolated from UI)
 * ----------------------------------------------------------------
 */
public class UserDAO {

    private final Connection conn;

    public UserDAO() {
        this.conn = DatabaseConfig.getInstance().getConnection();
    }

    // ═══════════════════════════════════════════════════════════
    // AUTHENTICATION
    // ═══════════════════════════════════════════════════════════

    /**
     * Authenticates a user by username and password.
     *
     * @param username  plain username
     * @param password  plaintext password (will be hashed before comparison)
     * @return UserAccount subclass if login succeeds, null otherwise
     */
    public UserAccount authenticate(String username, String password) {
        String sql = "SELECT u.*, " +
                "a.admin_id, a.department AS department, " +
                "NULL AS teacher_id, NULL AS employee_id, NULL AS qualification, NULL AS hire_date, " +
                "NULL AS student_id, NULL AS student_number, NULL AS date_of_birth, " +
                "NULL AS gender, NULL AS address, NULL AS enrollment_year, " +
                "NULL AS program, NULL AS year_level " +
                "FROM users u LEFT JOIN admins a ON u.user_id = a.user_id " +
                "WHERE u.username = ? AND u.role = 'ADMIN' AND u.is_active = 1 " +
                "UNION ALL " +
                "SELECT u.*, " +
                "NULL, NULL, " +
                "t.teacher_id, t.employee_id, t.qualification, t.hire_date, t.department, " +
                "NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL " +
                "FROM users u LEFT JOIN teachers t ON u.user_id = t.user_id " +
                "WHERE u.username = ? AND u.role = 'TEACHER' AND u.is_active = 1 " +
                "UNION ALL " +
                "SELECT u.*, " +
                "NULL, NULL, NULL, NULL, NULL, NULL, " +
                "s.student_id, s.student_number, s.date_of_birth, " +
                "s.gender, s.address, s.enrollment_year, s.program, s.year_level " +
                "FROM users u LEFT JOIN students s ON u.user_id = s.user_id " +
                "WHERE u.username = ? AND u.role = 'STUDENT' AND u.is_active = 1";

        // Simpler approach — two queries: first check credentials, then load role data
        String checkSql = "SELECT * FROM users WHERE username = ? AND is_active = 1";
        try (PreparedStatement ps = conn.prepareStatement(checkSql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) return null;

            String storedHash = rs.getString("password");
            if (!PasswordUtil.verify(password, storedHash)) return null;

            String role   = rs.getString("role");
            int    userId = rs.getInt("user_id");
            rs.close();

            return loadUserByIdAndRole(userId, role);
        } catch (SQLException e) {
            System.err.println("[UserDAO] authenticate error: " + e.getMessage());
            return null;
        }
    }

    /**
     * Loads a full UserAccount (with role-specific data) given userId and role.
     */
    public UserAccount loadUserByIdAndRole(int userId, String role) {
        String roleSql = switch (role.toUpperCase()) {
            case "ADMIN" ->
                "SELECT u.*, a.admin_id, a.department " +
                "FROM users u JOIN admins a ON u.user_id = a.user_id " +
                "WHERE u.user_id = ?";
            case "TEACHER" ->
                "SELECT u.*, t.teacher_id, t.employee_id, t.department, " +
                "t.qualification, t.hire_date " +
                "FROM users u JOIN teachers t ON u.user_id = t.user_id " +
                "WHERE u.user_id = ?";
            case "STUDENT" ->
                "SELECT u.*, s.student_id, s.student_number, s.date_of_birth, " +
                "s.gender, s.address, s.enrollment_year, s.program, s.year_level " +
                "FROM users u JOIN students s ON u.user_id = s.user_id " +
                "WHERE u.user_id = ?";
            default -> null;
        };
        if (roleSql == null) return null;

        try (PreparedStatement ps = conn.prepareStatement(roleSql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return UserFactory.createFromResultSet(rs, role);
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] loadUser error: " + e.getMessage());
        }
        return null;
    }

    // ═══════════════════════════════════════════════════════════
    // STUDENT CRUD
    // ═══════════════════════════════════════════════════════════

    /** Returns all students with their user and student details. */
    public List<StudentUser> getAllStudents() {
        List<StudentUser> list = new ArrayList<>();
        String sql = "SELECT u.*, s.student_id, s.student_number, s.date_of_birth, " +
                "s.gender, s.address, s.enrollment_year, s.program, s.year_level " +
                "FROM users u JOIN students s ON u.user_id = s.user_id " +
                "WHERE u.role = 'STUDENT' ORDER BY s.student_number";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add((StudentUser) UserFactory.createFromResultSet(rs, "STUDENT"));
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] getAllStudents: " + e.getMessage());
        }
        return list;
    }

    /**
     * Adds a new student: inserts into users then students tables (transaction).
     */
    public boolean addStudent(StudentUser s, String plainPassword) {
        String userSql = "INSERT INTO users (username, password, role, full_name, email, phone) " +
                "VALUES (?, ?, 'STUDENT', ?, ?, ?)";
        String stuSql  = "INSERT INTO students (user_id, student_number, date_of_birth, " +
                "gender, address, enrollment_year, program, year_level) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            conn.setAutoCommit(false);

            PreparedStatement ps1 = conn.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS);
            ps1.setString(1, s.getUsername());
            ps1.setString(2, PasswordUtil.hash(plainPassword));
            ps1.setString(3, s.getFullName());
            ps1.setString(4, s.getEmail());
            ps1.setString(5, s.getPhone());
            ps1.executeUpdate();

            ResultSet keys = ps1.getGeneratedKeys();
            if (!keys.next()) throw new SQLException("No user_id generated");
            int newUserId = keys.getInt(1);

            PreparedStatement ps2 = conn.prepareStatement(stuSql);
            ps2.setInt(1, newUserId);
            ps2.setString(2, s.getStudentNumber());
            ps2.setDate(3, s.getDateOfBirth() != null ? Date.valueOf(s.getDateOfBirth()) : null);
            ps2.setString(4, s.getGender());
            ps2.setString(5, s.getAddress());
            ps2.setInt(6, s.getEnrollmentYear());
            ps2.setString(7, s.getProgram());
            ps2.setInt(8, s.getYearLevel());
            ps2.executeUpdate();

            conn.commit();
            conn.setAutoCommit(true);
            return true;
        } catch (SQLException e) {
            System.err.println("[UserDAO] addStudent: " + e.getMessage());
            try { conn.rollback(); conn.setAutoCommit(true); } catch (SQLException ex) {}
            return false;
        }
    }

    /** Updates student and user record. */
    public boolean updateStudent(StudentUser s) {
        String userSql = "UPDATE users SET full_name=?, email=?, phone=?, is_active=? WHERE user_id=?";
        String stuSql  = "UPDATE students SET student_number=?, date_of_birth=?, gender=?, " +
                "address=?, enrollment_year=?, program=?, year_level=? WHERE student_id=?";
        try {
            conn.setAutoCommit(false);

            PreparedStatement ps1 = conn.prepareStatement(userSql);
            ps1.setString(1, s.getFullName());
            ps1.setString(2, s.getEmail());
            ps1.setString(3, s.getPhone());
            ps1.setBoolean(4, s.isActive());
            ps1.setInt(5, s.getUserId());
            ps1.executeUpdate();

            PreparedStatement ps2 = conn.prepareStatement(stuSql);
            ps2.setString(1, s.getStudentNumber());
            ps2.setDate(2, s.getDateOfBirth() != null ? Date.valueOf(s.getDateOfBirth()) : null);
            ps2.setString(3, s.getGender());
            ps2.setString(4, s.getAddress());
            ps2.setInt(5, s.getEnrollmentYear());
            ps2.setString(6, s.getProgram());
            ps2.setInt(7, s.getYearLevel());
            ps2.setInt(8, s.getStudentId());
            ps2.executeUpdate();

            conn.commit();
            conn.setAutoCommit(true);
            return true;
        } catch (SQLException e) {
            System.err.println("[UserDAO] updateStudent: " + e.getMessage());
            try { conn.rollback(); conn.setAutoCommit(true); } catch (SQLException ex) {}
            return false;
        }
    }

    /** Deletes a student (cascades to enrollments, grades, attendance via FK). */
    public boolean deleteStudent(int userId) {
        String sql = "DELETE FROM users WHERE user_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[UserDAO] deleteStudent: " + e.getMessage());
            return false;
        }
    }

    // ═══════════════════════════════════════════════════════════
    // TEACHER CRUD
    // ═══════════════════════════════════════════════════════════

    public List<TeacherUser> getAllTeachers() {
        List<TeacherUser> list = new ArrayList<>();
        String sql = "SELECT u.*, t.teacher_id, t.employee_id, t.department, " +
                "t.qualification, t.hire_date " +
                "FROM users u JOIN teachers t ON u.user_id = t.user_id " +
                "WHERE u.role = 'TEACHER' ORDER BY u.full_name";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add((TeacherUser) UserFactory.createFromResultSet(rs, "TEACHER"));
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] getAllTeachers: " + e.getMessage());
        }
        return list;
    }

    public boolean addTeacher(TeacherUser t, String plainPassword) {
        String userSql = "INSERT INTO users (username, password, role, full_name, email, phone) " +
                "VALUES (?, ?, 'TEACHER', ?, ?, ?)";
        String tchSql  = "INSERT INTO teachers (user_id, employee_id, department, qualification, hire_date) " +
                "VALUES (?, ?, ?, ?, ?)";
        try {
            conn.setAutoCommit(false);
            PreparedStatement ps1 = conn.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS);
            ps1.setString(1, t.getUsername());
            ps1.setString(2, PasswordUtil.hash(plainPassword));
            ps1.setString(3, t.getFullName());
            ps1.setString(4, t.getEmail());
            ps1.setString(5, t.getPhone());
            ps1.executeUpdate();
            ResultSet keys = ps1.getGeneratedKeys();
            if (!keys.next()) throw new SQLException("No user_id generated");
            int newUserId = keys.getInt(1);

            PreparedStatement ps2 = conn.prepareStatement(tchSql);
            ps2.setInt(1, newUserId);
            ps2.setString(2, t.getEmployeeId());
            ps2.setString(3, t.getDepartment());
            ps2.setString(4, t.getQualification());
            ps2.setDate(5, t.getHireDate() != null ? Date.valueOf(t.getHireDate()) : null);
            ps2.executeUpdate();

            conn.commit(); conn.setAutoCommit(true);
            return true;
        } catch (SQLException e) {
            System.err.println("[UserDAO] addTeacher: " + e.getMessage());
            try { conn.rollback(); conn.setAutoCommit(true); } catch (SQLException ex) {}
            return false;
        }
    }

    /** Resets a user's password. */
    public boolean resetPassword(int userId, String newPlainPassword) {
        String sql = "UPDATE users SET password = ? WHERE user_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, PasswordUtil.hash(newPlainPassword));
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[UserDAO] resetPassword: " + e.getMessage());
            return false;
        }
    }

    /** Resets a user's password based on username and email for the "Forgot Password" feature. */
    public boolean resetPasswordByUsername(String username, String email, String newPlainPassword) {
        String sql = "UPDATE users SET password = ? WHERE username = ? AND email = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, PasswordUtil.hash(newPlainPassword));
            ps.setString(2, username);
            ps.setString(3, email);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[UserDAO] resetPasswordByUsername: " + e.getMessage());
            return false;
        }
    }

    /** Returns count of students, teachers, and courses for the admin dashboard. */
    public int[] getDashboardCounts() {
        int[] counts = new int[3]; // [students, teachers, courses]
        try (Statement st = conn.createStatement()) {
            ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM students");
            if (rs.next()) counts[0] = rs.getInt(1);
            rs = st.executeQuery("SELECT COUNT(*) FROM teachers");
            if (rs.next()) counts[1] = rs.getInt(1);
            rs = st.executeQuery("SELECT COUNT(*) FROM courses WHERE is_active = 1");
            if (rs.next()) counts[2] = rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("[UserDAO] getDashboardCounts: " + e.getMessage());
        }
        return counts;
    }
}
