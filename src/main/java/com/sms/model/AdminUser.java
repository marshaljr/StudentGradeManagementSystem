package com.sms.model;

/**
 * AdminUser — Represents a system administrator.
 *
 * ----------------------------------------------------------------
 * OOP Principle: Inheritance (extends UserAccount)
 * ----------------------------------------------------------------
 */
public class AdminUser extends UserAccount {

    private int    adminId;
    private String department;

    public AdminUser() {}

    public AdminUser(int userId, String username, String passwordHash,
                     String fullName, String email, String phone, boolean active,
                     int adminId, String department) {
        super(userId, username, passwordHash, "ADMIN", fullName, email, phone, active);
        this.adminId    = adminId;
        this.department = department;
    }

    // ── Polymorphic overrides ─────────────────────────────────────
    @Override
    public String getDashboardPath() {
        return "AdminDashboard.fxml";
    }

    @Override
    public String getRoleDisplayName() {
        return "Administrator";
    }

    // ── Getters / Setters ─────────────────────────────────────────
    public int    getAdminId()             { return adminId; }
    public void   setAdminId(int id)       { this.adminId = id; }

    public String getDepartment()          { return department; }
    public void   setDepartment(String d)  { this.department = d; }
}
