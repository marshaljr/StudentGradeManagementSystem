package com.sms.model;

import java.time.LocalDate;

/**
 * Assessment — Represents a graded activity (quiz, exam, assignment, etc.)
 */
public class Assessment {

    private int       assessmentId;
    private int       courseId;
    private String    courseName;   // populated via JOIN
    private String    title;
    private String    type;         // QUIZ, ASSIGNMENT, MIDTERM, FINAL, PROJECT, LAB
    private double    maxMarks;
    private double    weight;       // percentage weight (0–100)
    private LocalDate dueDate;

    public Assessment() {}

    public Assessment(int assessmentId, int courseId, String title,
                      String type, double maxMarks, double weight, LocalDate dueDate) {
        this.assessmentId = assessmentId;
        this.courseId     = courseId;
        this.title        = title;
        this.type         = type;
        this.maxMarks     = maxMarks;
        this.weight       = weight;
        this.dueDate      = dueDate;
    }

    public int       getAssessmentId()               { return assessmentId; }
    public void      setAssessmentId(int id)         { this.assessmentId = id; }

    public int       getCourseId()                   { return courseId; }
    public void      setCourseId(int id)             { this.courseId = id; }

    public String    getCourseName()                 { return courseName; }
    public void      setCourseName(String n)         { this.courseName = n; }

    public String    getTitle()                      { return title; }
    public void      setTitle(String t)              { this.title = t; }

    public String    getType()                       { return type; }
    public void      setType(String t)               { this.type = t; }

    public double    getMaxMarks()                   { return maxMarks; }
    public void      setMaxMarks(double m)           { this.maxMarks = m; }

    public double    getWeight()                     { return weight; }
    public void      setWeight(double w)             { this.weight = w; }

    public LocalDate getDueDate()                    { return dueDate; }
    public void      setDueDate(LocalDate d)         { this.dueDate = d; }

    @Override
    public String toString() { return title + " (" + type + ")"; }
}
