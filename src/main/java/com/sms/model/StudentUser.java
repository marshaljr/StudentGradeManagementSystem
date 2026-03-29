package com.sms.model;

import java.time.LocalDate;

/**
 * StudentUser — Represents a student.
 *
 * OOP Principle: Inheritance (extends UserAccount)
 */
public class StudentUser extends UserAccount {

    private int       studentId;
    private String    studentNumber;
    private LocalDate dateOfBirth;
    private String    gender;
    private String    address;
    private int       enrollmentYear;
    private String    program;
    private int       yearLevel;

    public StudentUser() {}

    public StudentUser(int userId, String username, String passwordHash,
                       String fullName, String email, String phone, boolean active,
                       int studentId, String studentNumber, LocalDate dateOfBirth,
                       String gender, String address, int enrollmentYear,
                       String program, int yearLevel) {
        super(userId, username, passwordHash, "STUDENT", fullName, email, phone, active);
        this.studentId      = studentId;
        this.studentNumber  = studentNumber;
        this.dateOfBirth    = dateOfBirth;
        this.gender         = gender;
        this.address        = address;
        this.enrollmentYear = enrollmentYear;
        this.program        = program;
        this.yearLevel      = yearLevel;
    }

    @Override
    public String getDashboardPath() { return "StudentDashboard.fxml"; }

    @Override
    public String getRoleDisplayName() { return "Student"; }

    public int       getStudentId()                { return studentId; }
    public void      setStudentId(int id)          { this.studentId = id; }

    public String    getStudentNumber()            { return studentNumber; }
    public void      setStudentNumber(String n)    { this.studentNumber = n; }

    public LocalDate getDateOfBirth()              { return dateOfBirth; }
    public void      setDateOfBirth(LocalDate d)   { this.dateOfBirth = d; }

    public String    getGender()                   { return gender; }
    public void      setGender(String g)           { this.gender = g; }

    public String    getAddress()                  { return address; }
    public void      setAddress(String a)          { this.address = a; }

    public int       getEnrollmentYear()           { return enrollmentYear; }
    public void      setEnrollmentYear(int y)      { this.enrollmentYear = y; }

    public String    getProgram()                  { return program; }
    public void      setProgram(String p)          { this.program = p; }

    public int       getYearLevel()                { return yearLevel; }
    public void      setYearLevel(int y)           { this.yearLevel = y; }
}
