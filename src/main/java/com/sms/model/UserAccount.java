package com.sms.model;

/**
 * UserAccount — Abstract base class for all system users.
 *
 * ----------------------------------------------------------------
 * OOP Principles demonstrated:
 *   - Abstraction   : abstract class with abstract method getDashboardPath()
 *   - Encapsulation : private fields with getters/setters
 *   - Inheritance   : AdminUser, TeacherUser, StudentUser extend this class
 * ----------------------------------------------------------------
 */
public abstract class UserAccount {

    // ── Private fields (Encapsulation) ────────────────────────────
    private int    userId;
    private String username;
    private String passwordHash;
    private String role;        // "ADMIN", "TEACHER", "STUDENT"
    private String fullName;
    private String email;
    private String phone;
    private boolean active;

    // ── Constructor ───────────────────────────────────────────────
    public UserAccount(int userId, String username, String passwordHash,
                       String role, String fullName, String email,
                       String phone, boolean active) {
        this.userId       = userId;
        this.username     = username;
        this.passwordHash = passwordHash;
        this.role         = role;
        this.fullName     = fullName;
        this.email        = email;
        this.phone        = phone;
        this.active       = active;
    }

    /** No-arg constructor for DAO use. */
    public UserAccount() {}

    // ── Abstract method (Abstraction) ─────────────────────────────
    /**
     * Each subclass must specify which FXML dashboard it uses.
     * This is polymorphic — different users navigate to different screens.
     *
     * @return FXML filename, e.g. "AdminDashboard.fxml"
     */
    public abstract String getDashboardPath();

    /**
     * Returns a display string for the role (Polymorphism example).
     *
     * @return human-readable role name
     */
    public abstract String getRoleDisplayName();

    // ── Getters and Setters (Encapsulation) ───────────────────────
    public int    getUserId()        { return userId; }
    public void   setUserId(int id)  { this.userId = id; }

    public String getUsername()             { return username; }
    public void   setUsername(String u)     { this.username = u; }

    public String getPasswordHash()             { return passwordHash; }
    public void   setPasswordHash(String hash)  { this.passwordHash = hash; }

    public String getRole()           { return role; }
    public void   setRole(String r)   { this.role = r; }

    public String getFullName()           { return fullName; }
    public void   setFullName(String n)   { this.fullName = n; }

    public String getEmail()          { return email; }
    public void   setEmail(String e)  { this.email = e; }

    public String getPhone()          { return phone; }
    public void   setPhone(String p)  { this.phone = p; }

    public boolean isActive()            { return active; }
    public void    setActive(boolean a)  { this.active = a; }

    @Override
    public String toString() {
        return fullName + " (" + username + ")";
    }
}
