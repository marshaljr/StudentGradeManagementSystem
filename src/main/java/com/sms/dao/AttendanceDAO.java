package com.sms.dao;

import com.sms.model.Attendance;
import com.sms.util.DatabaseConfig;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * AttendanceDAO — CRUD operations for attendance records.
 */
public class AttendanceDAO {

    private final Connection conn;

    public AttendanceDAO() {
        this.conn = DatabaseConfig.getInstance().getConnection();
    }

    // ─── Read ─────────────────────────────────────────────────────

    /** Returns attendance for all students in a course on a specific date. */
    public List<Attendance> getAttendanceByDate(int courseId, LocalDate date) {
        List<Attendance> list = new ArrayList<>();
        String sql = "SELECT a.*, u.full_name AS student_name, s.student_number, " +
                "c.course_name " +
                "FROM attendance a " +
                "JOIN students s ON a.student_id = s.student_id " +
                "JOIN users u    ON s.user_id = u.user_id " +
                "JOIN courses c  ON a.course_id = c.course_id " +
                "WHERE a.course_id = ? AND a.att_date = ? " +
                "ORDER BY u.full_name";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            ps.setDate(2, Date.valueOf(date));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapAttendance(rs));
        } catch (SQLException e) {
            System.err.println("[AttendanceDAO] getByDate: " + e.getMessage());
        }
        return list;
    }

    /** Returns all attendance records for a student in a course. */
    public List<Attendance> getAttendanceByStudentAndCourse(int studentId, int courseId) {
        List<Attendance> list = new ArrayList<>();
        String sql = "SELECT a.*, u.full_name AS student_name, s.student_number, c.course_name " +
                "FROM attendance a " +
                "JOIN students s ON a.student_id = s.student_id " +
                "JOIN users u    ON s.user_id = u.user_id " +
                "JOIN courses c  ON a.course_id = c.course_id " +
                "WHERE a.student_id = ? AND a.course_id = ? ORDER BY a.att_date DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, courseId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapAttendance(rs));
        } catch (SQLException e) {
            System.err.println("[AttendanceDAO] getByStudentAndCourse: " + e.getMessage());
        }
        return list;
    }

    /**
     * Returns an attendance summary for a student in a course.
     * Returns: map of status → count  e.g. {PRESENT→18, ABSENT→2, LATE→1}
     */
    public Map<String, Integer> getAttendanceSummary(int studentId, int courseId) {
        Map<String, Integer> summary = new LinkedHashMap<>();
        summary.put("PRESENT", 0);
        summary.put("ABSENT", 0);
        summary.put("LATE", 0);
        summary.put("EXCUSED", 0);

        String sql = "SELECT status, COUNT(*) AS cnt FROM attendance " +
                "WHERE student_id = ? AND course_id = ? GROUP BY status";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, courseId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                summary.put(rs.getString("status"), rs.getInt("cnt"));
            }
        } catch (SQLException e) {
            System.err.println("[AttendanceDAO] getSummary: " + e.getMessage());
        }
        return summary;
    }

    /**
     * Returns attendance percentage for a student in a course.
     * (PRESENT + LATE) / total * 100
     */
    public double getAttendancePercentage(int studentId, int courseId) {
        Map<String, Integer> s = getAttendanceSummary(studentId, courseId);
        int total = s.values().stream().mapToInt(Integer::intValue).sum();
        if (total == 0) return 0.0;
        int present = s.getOrDefault("PRESENT", 0) + s.getOrDefault("LATE", 0);
        return Math.round((present * 100.0 / total) * 10.0) / 10.0;
    }

    // ─── Create / Update ──────────────────────────────────────────

    /**
     * Saves attendance for a single student (upsert).
     */
    public boolean saveAttendance(Attendance a) {
        String sql = "INSERT INTO attendance (student_id, course_id, att_date, status, remarks, recorded_by) " +
                "VALUES (?,?,?,?,?,?) " +
                "ON DUPLICATE KEY UPDATE status=VALUES(status), " +
                "remarks=VALUES(remarks), recorded_by=VALUES(recorded_by)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, a.getStudentId());
            ps.setInt(2, a.getCourseId());
            ps.setDate(3, Date.valueOf(a.getAttDate()));
            ps.setString(4, a.getStatus());
            ps.setString(5, a.getRemarks());
            ps.setInt(6, a.getRecordedBy());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[AttendanceDAO] saveAttendance: " + e.getMessage());
            return false;
        }
    }

    /**
     * Saves attendance for a batch of students at once (one class session).
     */
    public boolean saveBatchAttendance(List<Attendance> records) {
        String sql = "INSERT INTO attendance (student_id, course_id, att_date, status, remarks, recorded_by) " +
                "VALUES (?,?,?,?,?,?) " +
                "ON DUPLICATE KEY UPDATE status=VALUES(status), " +
                "remarks=VALUES(remarks), recorded_by=VALUES(recorded_by)";
        try {
            conn.setAutoCommit(false);
            PreparedStatement ps = conn.prepareStatement(sql);
            for (Attendance a : records) {
                ps.setInt(1, a.getStudentId());
                ps.setInt(2, a.getCourseId());
                ps.setDate(3, Date.valueOf(a.getAttDate()));
                ps.setString(4, a.getStatus());
                ps.setString(5, a.getRemarks());
                ps.setInt(6, a.getRecordedBy());
                ps.addBatch();
            }
            ps.executeBatch();
            conn.commit();
            conn.setAutoCommit(true);
            return true;
        } catch (SQLException e) {
            System.err.println("[AttendanceDAO] saveBatch: " + e.getMessage());
            try { conn.rollback(); conn.setAutoCommit(true); } catch (SQLException ex) {}
            return false;
        }
    }

    public boolean deleteAttendance(int attendanceId) {
        String sql = "DELETE FROM attendance WHERE attendance_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, attendanceId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[AttendanceDAO] delete: " + e.getMessage());
            return false;
        }
    }

    // ─── Helper ───────────────────────────────────────────────────

    private Attendance mapAttendance(ResultSet rs) throws SQLException {
        Attendance a = new Attendance();
        a.setAttendanceId(rs.getInt("attendance_id"));
        a.setStudentId(rs.getInt("student_id"));
        a.setStudentName(rs.getString("student_name"));
        a.setStudentNumber(rs.getString("student_number"));
        a.setCourseId(rs.getInt("course_id"));
        a.setCourseName(rs.getString("course_name"));
        Date d = rs.getDate("att_date");
        if (d != null) a.setAttDate(d.toLocalDate());
        a.setStatus(rs.getString("status"));
        a.setRemarks(rs.getString("remarks"));
        a.setRecordedBy(rs.getInt("recorded_by"));
        return a;
    }
}
