package com.sms.model;

import java.time.LocalDate;

/**
 * TeacherUser — Represents a teacher / faculty member.
 *
 * OOP Principle: Inheritance (extends UserAccount)
 */
public class TeacherUser extends UserAccount {

    private int       teacherId;
    private String    employeeId;
    private String    department;
    private String    qualification;
    private LocalDate hireDate;

    public TeacherUser() {}

    public TeacherUser(int userId, String username, String passwordHash,
                       String fullName, String email, String phone, boolean active,
                       int teacherId, String employeeId, String department,
                       String qualification, LocalDate hireDate) {
        super(userId, username, passwordHash, "TEACHER", fullName, email, phone, active);
        this.teacherId     = teacherId;
        this.employeeId    = employeeId;
        this.department    = department;
        this.qualification = qualification;
        this.hireDate      = hireDate;
    }

    @Override
    public String getDashboardPath() { return "TeacherDashboard.fxml"; }

    @Override
    public String getRoleDisplayName() { return "Teacher"; }

    public int       getTeacherId()               { return teacherId; }
    public void      setTeacherId(int id)          { this.teacherId = id; }

    public String    getEmployeeId()               { return employeeId; }
    public void      setEmployeeId(String id)      { this.employeeId = id; }

    public String    getDepartment()               { return department; }
    public void      setDepartment(String d)       { this.department = d; }

    public String    getQualification()            { return qualification; }
    public void      setQualification(String q)    { this.qualification = q; }

    public LocalDate getHireDate()                 { return hireDate; }
    public void      setHireDate(LocalDate d)      { this.hireDate = d; }
}
