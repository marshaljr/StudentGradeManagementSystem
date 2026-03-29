package com.sms.dao;

import com.sms.model.Assessment;
import com.sms.model.Grade;
import com.sms.util.DatabaseConfig;
import com.sms.util.GradeCalculator;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * GradeDAO — CRUD operations for assessments and grades.
 */
public class GradeDAO {

    private final Connection conn;

    public GradeDAO() {
        this.conn = DatabaseConfig.getInstance().getConnection();
    }

    // ═══════════════════════════════════════════════════════════
    // ASSESSMENT CRUD
    // ═══════════════════════════════════════════════════════════

    public List<Assessment> getAssessmentsByCourse(int courseId) {
        List<Assessment> list = new ArrayList<>();
        String sql = "SELECT a.*, c.course_name FROM assessments a " +
                "JOIN courses c ON a.course_id = c.course_id " +
                "WHERE a.course_id = ? ORDER BY a.due_date";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapAssessment(rs));
        } catch (SQLException e) {
            System.err.println("[GradeDAO] getAssessmentsByCourse: " + e.getMessage());
        }
        return list;
    }

    public Assessment getAssessmentById(int assessmentId) {
        String sql = "SELECT a.*, c.course_name FROM assessments a " +
                "JOIN courses c ON a.course_id = c.course_id " +
                "WHERE a.assessment_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, assessmentId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapAssessment(rs);
        } catch (SQLException e) {
            System.err.println("[GradeDAO] getAssessmentById: " + e.getMessage());
        }
        return null;
    }

    public boolean addAssessment(Assessment a) {
        String sql = "INSERT INTO assessments (course_id, title, type, max_marks, weight, due_date) " +
                "VALUES (?,?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, a.getCourseId());
            ps.setString(2, a.getTitle());
            ps.setString(3, a.getType());
            ps.setDouble(4, a.getMaxMarks());
            ps.setDouble(5, a.getWeight());
            ps.setDate(6, a.getDueDate() != null ? Date.valueOf(a.getDueDate()) : null);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[GradeDAO] addAssessment: " + e.getMessage());
            return false;
        }
    }

    public boolean updateAssessment(Assessment a) {
        String sql = "UPDATE assessments SET title=?, type=?, max_marks=?, weight=?, due_date=? " +
                "WHERE assessment_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, a.getTitle());
            ps.setString(2, a.getType());
            ps.setDouble(3, a.getMaxMarks());
            ps.setDouble(4, a.getWeight());
            ps.setDate(5, a.getDueDate() != null ? Date.valueOf(a.getDueDate()) : null);
            ps.setInt(6, a.getAssessmentId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[GradeDAO] updateAssessment: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteAssessment(int assessmentId) {
        String sql = "DELETE FROM assessments WHERE assessment_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, assessmentId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[GradeDAO] deleteAssessment: " + e.getMessage());
            return false;
        }
    }

    // ═══════════════════════════════════════════════════════════
    // GRADE CRUD
    // ═══════════════════════════════════════════════════════════

    /** Returns all grades for one assessment (for teacher grade-entry screen). */
    public List<Grade> getGradesByAssessment(int assessmentId) {
        List<Grade> list = new ArrayList<>();
        String sql = "SELECT g.*, u.full_name AS student_name, s.student_number, " +
                "a.title AS assessment_title " +
                "FROM grades g " +
                "JOIN students s ON g.student_id = s.student_id " +
                "JOIN users u    ON s.user_id = u.user_id " +
                "JOIN assessments a ON g.assessment_id = a.assessment_id " +
                "WHERE g.assessment_id = ? ORDER BY u.full_name";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, assessmentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapGrade(rs));
        } catch (SQLException e) {
            System.err.println("[GradeDAO] getGradesByAssessment: " + e.getMessage());
        }
        return list;
    }

    /** Returns all grades for a student in a course. */
    public List<Grade> getGradesByStudentAndCourse(int studentId, int courseId) {
        List<Grade> list = new ArrayList<>();
        String sql = "SELECT g.*, u.full_name AS student_name, s.student_number, " +
                "a.title AS assessment_title " +
                "FROM grades g " +
                "JOIN assessments a ON g.assessment_id = a.assessment_id " +
                "JOIN students s ON g.student_id = s.student_id " +
                "JOIN users u ON s.user_id = u.user_id " +
                "WHERE g.student_id = ? AND a.course_id = ? ORDER BY a.due_date";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, courseId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapGrade(rs));
        } catch (SQLException e) {
            System.err.println("[GradeDAO] getGradesByStudentAndCourse: " + e.getMessage());
        }
        return list;
    }

    /** Returns all grades for a student across all courses. */
    public List<Grade> getAllGradesByStudent(int studentId) {
        List<Grade> list = new ArrayList<>();
        String sql = "SELECT g.*, u.full_name AS student_name, s.student_number, " +
                "a.title AS assessment_title " +
                "FROM grades g " +
                "JOIN assessments a ON g.assessment_id = a.assessment_id " +
                "JOIN students s ON g.student_id = s.student_id " +
                "JOIN users u ON s.user_id = u.user_id " +
                "WHERE g.student_id = ? ORDER BY a.course_id, a.due_date";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapGrade(rs));
        } catch (SQLException e) {
            System.err.println("[GradeDAO] getAllGradesByStudent: " + e.getMessage());
        }
        return list;
    }

    /**
     * Saves or updates a grade (upsert using INSERT ... ON DUPLICATE KEY UPDATE).
     * Automatically calculates and sets the letter grade before saving using the specified scale.
     */
    public boolean saveGrade(Grade g, double maxMarks, String gradingScale, int gradedByUserId) {
        // Auto-calculate letter grade
        double pct = (g.getMarksObtained() / maxMarks) * 100.0;
        g.setLetterGrade(GradeCalculator.getLetterGrade(pct, gradingScale));

        String sql = "INSERT INTO grades (student_id, assessment_id, marks_obtained, " +
                "letter_grade, remarks, graded_by) VALUES (?,?,?,?,?,?) " +
                "ON DUPLICATE KEY UPDATE marks_obtained=VALUES(marks_obtained), " +
                "letter_grade=VALUES(letter_grade), remarks=VALUES(remarks), " +
                "graded_by=VALUES(graded_by), graded_at=CURRENT_TIMESTAMP";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, g.getStudentId());
            ps.setInt(2, g.getAssessmentId());
            ps.setDouble(3, g.getMarksObtained());
            ps.setString(4, g.getLetterGrade());
            ps.setString(5, g.getRemarks());
            ps.setInt(6, gradedByUserId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[GradeDAO] saveGrade: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteGrade(int gradeId) {
        String sql = "DELETE FROM grades WHERE grade_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, gradeId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[GradeDAO] deleteGrade: " + e.getMessage());
            return false;
        }
    }

    // ─── Helpers ──────────────────────────────────────────────────

    private Assessment mapAssessment(ResultSet rs) throws SQLException {
        Assessment a = new Assessment();
        a.setAssessmentId(rs.getInt("assessment_id"));
        a.setCourseId(rs.getInt("course_id"));
        a.setCourseName(rs.getString("course_name"));
        a.setTitle(rs.getString("title"));
        a.setType(rs.getString("type"));
        a.setMaxMarks(rs.getDouble("max_marks"));
        a.setWeight(rs.getDouble("weight"));
        Date d = rs.getDate("due_date");
        if (d != null) a.setDueDate(d.toLocalDate());
        return a;
    }

    private Grade mapGrade(ResultSet rs) throws SQLException {
        Grade g = new Grade();
        g.setGradeId(rs.getInt("grade_id"));
        g.setStudentId(rs.getInt("student_id"));
        g.setStudentName(rs.getString("student_name"));
        g.setStudentNumber(rs.getString("student_number"));
        g.setAssessmentId(rs.getInt("assessment_id"));
        g.setAssessmentTitle(rs.getString("assessment_title"));
        g.setMarksObtained(rs.getDouble("marks_obtained"));
        g.setLetterGrade(rs.getString("letter_grade"));
        g.setRemarks(rs.getString("remarks"));
        g.setGradedBy(rs.getInt("graded_by"));
        Timestamp ts = rs.getTimestamp("graded_at");
        if (ts != null) g.setGradedAt(ts.toLocalDateTime());
        return g;
    }
}
