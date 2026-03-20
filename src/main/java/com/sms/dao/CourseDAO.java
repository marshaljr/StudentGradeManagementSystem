package com.sms.dao;

import com.sms.model.Course;
import com.sms.model.StudentUser;
import com.sms.util.DatabaseConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * CourseDAO — CRUD operations for courses and enrollments.
 */
public class CourseDAO {

    private final Connection conn;

    public CourseDAO() {
        this.conn = DatabaseConfig.getInstance().getConnection();
    }

    // ─── Read ─────────────────────────────────────────────────────

    public List<Course> getAllCourses() {
        List<Course> list = new ArrayList<>();
        String sql = "SELECT c.*, CONCAT(u.full_name) AS teacher_name " +
                "FROM courses c " +
                "LEFT JOIN teachers t ON c.teacher_id = t.teacher_id " +
                "LEFT JOIN users u    ON t.user_id = u.user_id " +
                "ORDER BY c.course_code";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapCourse(rs));
        } catch (SQLException e) {
            System.err.println("[CourseDAO] getAllCourses: " + e.getMessage());
        }
        return list;
    }

    /** Returns only the courses assigned to a specific teacher. */
    public List<Course> getCoursesByTeacher(int teacherId) {
        List<Course> list = new ArrayList<>();
        String sql = "SELECT c.*, u.full_name AS teacher_name " +
                "FROM courses c " +
                "LEFT JOIN teachers t ON c.teacher_id = t.teacher_id " +
                "LEFT JOIN users u    ON t.user_id = u.user_id " +
                "WHERE c.teacher_id = ? AND c.is_active = 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, teacherId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapCourse(rs));
        } catch (SQLException e) {
            System.err.println("[CourseDAO] getCoursesByTeacher: " + e.getMessage());
        }
        return list;
    }

    /** Returns courses a student is enrolled in. */
    public List<Course> getCoursesByStudent(int studentId) {
        List<Course> list = new ArrayList<>();
        String sql = "SELECT c.*, u.full_name AS teacher_name " +
                "FROM courses c " +
                "JOIN enrollments e ON c.course_id = e.course_id " +
                "LEFT JOIN teachers t ON c.teacher_id = t.teacher_id " +
                "LEFT JOIN users u    ON t.user_id = u.user_id " +
                "WHERE e.student_id = ? AND e.status = 'ACTIVE'";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapCourse(rs));
        } catch (SQLException e) {
            System.err.println("[CourseDAO] getCoursesByStudent: " + e.getMessage());
        }
        return list;
    }

    // ─── Create ───────────────────────────────────────────────────

    public boolean addCourse(Course c) {
        String sql = "INSERT INTO courses (course_code, course_name, description, credits, " +
                "teacher_id, semester, academic_year, max_students, grading_scale) VALUES (?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getCourseCode());
            ps.setString(2, c.getCourseName());
            ps.setString(3, c.getDescription());
            ps.setInt(4, c.getCredits());
            if (c.getTeacherId() > 0) ps.setInt(5, c.getTeacherId());
            else ps.setNull(5, Types.INTEGER);
            ps.setString(6, c.getSemester());
            ps.setString(7, c.getAcademicYear());
            ps.setInt(8, c.getMaxStudents());
            ps.setString(9, c.getGradingScale());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[CourseDAO] addCourse: " + e.getMessage());
            return false;
        }
    }

    // ─── Update ───────────────────────────────────────────────────

    public boolean updateCourse(Course c) {
        String sql = "UPDATE courses SET course_code=?, course_name=?, description=?, " +
                "credits=?, teacher_id=?, semester=?, academic_year=?, " +
                "max_students=?, is_active=?, grading_scale=? WHERE course_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getCourseCode());
            ps.setString(2, c.getCourseName());
            ps.setString(3, c.getDescription());
            ps.setInt(4, c.getCredits());
            if (c.getTeacherId() > 0) ps.setInt(5, c.getTeacherId());
            else ps.setNull(5, Types.INTEGER);
            ps.setString(6, c.getSemester());
            ps.setString(7, c.getAcademicYear());
            ps.setInt(8, c.getMaxStudents());
            ps.setBoolean(9, c.isActive());
            ps.setString(10, c.getGradingScale());
            ps.setInt(11, c.getCourseId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[CourseDAO] updateCourse: " + e.getMessage());
            return false;
        }
    }

    // ─── Delete ───────────────────────────────────────────────────

    public boolean deleteCourse(int courseId) {
        String sql = "DELETE FROM courses WHERE course_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[CourseDAO] deleteCourse: " + e.getMessage());
            return false;
        }
    }

    // ─── Enrollments ──────────────────────────────────────────────

    public boolean enrollStudent(int studentId, int courseId) {
        String sql = "INSERT IGNORE INTO enrollments (student_id, course_id) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, courseId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[CourseDAO] enrollStudent: " + e.getMessage());
            return false;
        }
    }

    public boolean dropEnrollment(int studentId, int courseId) {
        String sql = "UPDATE enrollments SET status='DROPPED' WHERE student_id=? AND course_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId); ps.setInt(2, courseId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[CourseDAO] dropEnrollment: " + e.getMessage());
            return false;
        }
    }

    /** Returns all students enrolled in a course. */
    public List<StudentUser> getEnrolledStudents(int courseId) {
        List<StudentUser> list = new ArrayList<>();
        String sql = "SELECT u.user_id, u.username, u.password, u.role, u.full_name, " +
                "u.email, u.phone, u.is_active, " +
                "s.student_id, s.student_number, s.date_of_birth, s.gender, " +
                "s.address, s.enrollment_year, s.program, s.year_level " +
                "FROM enrollments e " +
                "JOIN students s ON e.student_id = s.student_id " +
                "JOIN users u    ON s.user_id = u.user_id " +
                "WHERE e.course_id = ? AND e.status = 'ACTIVE' " +
                "ORDER BY u.full_name";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                StudentUser st = new StudentUser();
                st.setUserId(rs.getInt("user_id"));
                st.setUsername(rs.getString("username"));
                st.setFullName(rs.getString("full_name"));
                st.setEmail(rs.getString("email"));
                st.setPhone(rs.getString("phone"));
                st.setActive(rs.getBoolean("is_active"));
                st.setStudentId(rs.getInt("student_id"));
                st.setStudentNumber(rs.getString("student_number"));
                st.setProgram(rs.getString("program"));
                st.setYearLevel(rs.getInt("year_level"));
                java.sql.Date dob = rs.getDate("date_of_birth");
                if (dob != null) st.setDateOfBirth(dob.toLocalDate());
                st.setGender(rs.getString("gender"));
                list.add(st);
            }
        } catch (SQLException e) {
            System.err.println("[CourseDAO] getEnrolledStudents: " + e.getMessage());
        }
        return list;
    }

    // ─── Helpers ──────────────────────────────────────────────────

    private Course mapCourse(ResultSet rs) throws SQLException {
        Course c = new Course();
        c.setCourseId(rs.getInt("course_id"));
        c.setCourseCode(rs.getString("course_code"));
        c.setCourseName(rs.getString("course_name"));
        c.setDescription(rs.getString("description"));
        c.setCredits(rs.getInt("credits"));
        c.setTeacherId(rs.getInt("teacher_id"));
        c.setTeacherName(rs.getString("teacher_name"));
        c.setSemester(rs.getString("semester"));
        c.setAcademicYear(rs.getString("academic_year"));
        c.setMaxStudents(rs.getInt("max_students"));
        c.setActive(rs.getBoolean("is_active"));
        
        try {
            String scale = rs.getString("grading_scale");
            if (scale != null) c.setGradingScale(scale);
        } catch (SQLException ignore) {
            // column might not exist if db schema not perfectly synced
        }
        
        return c;
    }
}
