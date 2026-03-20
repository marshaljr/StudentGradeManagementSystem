package com.sms.model;

import java.time.LocalDate;

/**
 * Attendance — Records a student's attendance for a specific class date.
 */
public class Attendance {

    public enum Status { PRESENT, ABSENT, LATE, EXCUSED }

    private int       attendanceId;
    private int       studentId;
    private String    studentName;    // populated via JOIN
    private String    studentNumber;  // populated via JOIN
    private int       courseId;
    private String    courseName;     // populated via JOIN
    private LocalDate attDate;
    private String    status;         // PRESENT, ABSENT, LATE, EXCUSED
    private String    remarks;
    private int       recordedBy;

    public Attendance() {}

    public Attendance(int studentId, int courseId, LocalDate attDate, String status) {
        this.studentId = studentId;
        this.courseId  = courseId;
        this.attDate   = attDate;
        this.status    = status;
    }

    public int       getAttendanceId()               { return attendanceId; }
    public void      setAttendanceId(int id)         { this.attendanceId = id; }

    public int       getStudentId()                  { return studentId; }
    public void      setStudentId(int id)            { this.studentId = id; }

    public String    getStudentName()                { return studentName; }
    public void      setStudentName(String n)        { this.studentName = n; }

    public String    getStudentNumber()              { return studentNumber; }
    public void      setStudentNumber(String n)      { this.studentNumber = n; }

    public int       getCourseId()                   { return courseId; }
    public void      setCourseId(int id)             { this.courseId = id; }

    public String    getCourseName()                 { return courseName; }
    public void      setCourseName(String n)         { this.courseName = n; }

    public LocalDate getAttDate()                    { return attDate; }
    public void      setAttDate(LocalDate d)         { this.attDate = d; }

    public String    getStatus()                     { return status; }
    public void      setStatus(String s)             { this.status = s; }

    public String    getRemarks()                    { return remarks; }
    public void      setRemarks(String r)            { this.remarks = r; }

    public int       getRecordedBy()                 { return recordedBy; }
    public void      setRecordedBy(int id)           { this.recordedBy = id; }
}
