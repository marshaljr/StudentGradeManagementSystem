package com.sms.model;

/**
 * Course — Represents an academic course/subject.
 */
public class Course {

    private int     courseId;
    private String  courseCode;
    private String  courseName;
    private String  description;
    private int     credits;
    private int     teacherId;
    private String  teacherName;    // populated by JOIN queries
    private String  semester;
    private String  academicYear;
    private int     maxStudents;
    private boolean active;
    private String  gradingScale = "STANDARD";

    public Course() {}

    public Course(int courseId, String courseCode, String courseName,
                  String description, int credits, int teacherId,
                  String semester, String academicYear, int maxStudents, boolean active, String gradingScale) {
        this.courseId     = courseId;
        this.courseCode   = courseCode;
        this.courseName   = courseName;
        this.description  = description;
        this.credits      = credits;
        this.teacherId    = teacherId;
        this.semester     = semester;
        this.academicYear = academicYear;
        this.maxStudents  = maxStudents;
        this.active       = active;
        this.gradingScale = gradingScale != null ? gradingScale : "STANDARD";
    }

    // ── Getters / Setters ─────────────────────────────────────────
    public int     getCourseId()              { return courseId; }
    public void    setCourseId(int id)        { this.courseId = id; }

    public String  getCourseCode()            { return courseCode; }
    public void    setCourseCode(String c)    { this.courseCode = c; }

    public String  getCourseName()            { return courseName; }
    public void    setCourseName(String n)    { this.courseName = n; }

    public String  getDescription()           { return description; }
    public void    setDescription(String d)   { this.description = d; }

    public int     getCredits()               { return credits; }
    public void    setCredits(int c)          { this.credits = c; }

    public int     getTeacherId()             { return teacherId; }
    public void    setTeacherId(int id)       { this.teacherId = id; }

    public String  getTeacherName()           { return teacherName; }
    public void    setTeacherName(String n)   { this.teacherName = n; }

    public String  getSemester()              { return semester; }
    public void    setSemester(String s)      { this.semester = s; }

    public String  getAcademicYear()          { return academicYear; }
    public void    setAcademicYear(String y)  { this.academicYear = y; }

    public int     getMaxStudents()           { return maxStudents; }
    public void    setMaxStudents(int m)      { this.maxStudents = m; }

    public boolean isActive()                 { return active; }
    public void    setActive(boolean a)       { this.active = a; }

    public String  getGradingScale()          { return gradingScale; }
    public void    setGradingScale(String g)  { this.gradingScale = g; }

    @Override
    public String toString() { return courseCode + " - " + courseName; }
}
