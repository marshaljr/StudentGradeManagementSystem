package com.sms.model;

import java.time.LocalDateTime;

/**
 * Grade — Stores marks obtained by a student in an assessment.
 */
public class Grade {

    private int           gradeId;
    private int           studentId;
    private String        studentName;    // populated via JOIN
    private String        studentNumber;  // populated via JOIN
    private int           assessmentId;
    private String        assessmentTitle;// populated via JOIN
    private double        marksObtained;  // -1 means "not yet graded"
    private String        letterGrade;
    private String        remarks;
    private int           gradedBy;
    private LocalDateTime gradedAt;

    public Grade() {}

    public Grade(int studentId, int assessmentId, double marksObtained) {
        this.studentId      = studentId;
        this.assessmentId   = assessmentId;
        this.marksObtained  = marksObtained;
    }

    public int           getGradeId()                { return gradeId; }
    public void          setGradeId(int id)          { this.gradeId = id; }

    public int           getStudentId()              { return studentId; }
    public void          setStudentId(int id)        { this.studentId = id; }

    public String        getStudentName()            { return studentName; }
    public void          setStudentName(String n)    { this.studentName = n; }

    public String        getStudentNumber()          { return studentNumber; }
    public void          setStudentNumber(String n)  { this.studentNumber = n; }

    public int           getAssessmentId()           { return assessmentId; }
    public void          setAssessmentId(int id)     { this.assessmentId = id; }

    public String        getAssessmentTitle()        { return assessmentTitle; }
    public void          setAssessmentTitle(String t){ this.assessmentTitle = t; }

    public double        getMarksObtained()          { return marksObtained; }
    public void          setMarksObtained(double m)  { this.marksObtained = m; }

    public String        getLetterGrade()            { return letterGrade; }
    public void          setLetterGrade(String g)    { this.letterGrade = g; }

    public String        getRemarks()                { return remarks; }
    public void          setRemarks(String r)        { this.remarks = r; }

    public int           getGradedBy()               { return gradedBy; }
    public void          setGradedBy(int id)         { this.gradedBy = id; }

    public LocalDateTime getGradedAt()               { return gradedAt; }
    public void          setGradedAt(LocalDateTime d){ this.gradedAt = d; }
}
