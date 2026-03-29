package com.sms.util;

import com.sms.model.UserAccount;

/**
 * SessionManager — Stores the currently logged-in user for the session.
 *
 * After successful login the LoginController calls setCurrentUser().
 * Any controller can then call getCurrentUser() to get the logged-in user
 * without passing it around manually.
 */
public class SessionManager {

    private static UserAccount currentUser;

    private SessionManager() {}

    public static void setCurrentUser(UserAccount user) {
        currentUser = user;
    }

    public static UserAccount getCurrentUser() {
        return currentUser;
    }

    public static void clearSession() {
        currentUser = null;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }
}
