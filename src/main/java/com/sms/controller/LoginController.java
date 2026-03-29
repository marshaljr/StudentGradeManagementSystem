package com.sms.controller;

import com.sms.dao.UserDAO;
import com.sms.model.UserAccount;
import com.sms.util.AlertUtil;
import com.sms.util.NavigationUtil;
import com.sms.util.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.control.Dialog;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.GridPane;

/**
 * LoginController — Handles login screen events.
 *
 * Validates credentials, stores the user in SessionManager,
 * then navigates to the correct dashboard based on role.
 */
public class LoginController {

    @FXML private TextField     usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label         errorLabel;
    @FXML private Button        loginButton;

    private final UserDAO userDAO = new UserDAO();

    @FXML
    public void initialize() {
        errorLabel.setVisible(false);

        // Allow pressing Enter in password field to trigger login
        passwordField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) handleLogin();
        });
        usernameField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) passwordField.requestFocus();
        });
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        // ── Input validation ──────────────────────────────────────
        if (username.isEmpty()) {
            showError("Please enter your username.");
            usernameField.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            showError("Please enter your password.");
            passwordField.requestFocus();
            return;
        }

        // ── Authenticate ──────────────────────────────────────────
        loginButton.setDisable(true);
        loginButton.setText("Signing in...");

        UserAccount user = userDAO.authenticate(username, password);

        loginButton.setDisable(false);
        loginButton.setText("Sign In");

        if (user == null) {
            showError("Invalid username or password. Please try again.");
            passwordField.clear();
            passwordField.requestFocus();
            return;
        }

        // ── Success: store session and navigate ───────────────────
        SessionManager.setCurrentUser(user);
        errorLabel.setVisible(false);

        // Each UserAccount subclass knows its own dashboard path (Polymorphism)
        NavigationUtil.navigateTo(user.getDashboardPath());
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    @FXML
    private void handleForgotPassword() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Reset Password");
        dialog.setHeaderText("Enter your account details to reset password");

        GridPane form = new GridPane();
        form.setHgap(10); form.setVgap(10);
        form.setPadding(new javafx.geometry.Insets(20));

        TextField tfUser = new TextField();
        TextField tfEmail = new TextField();

        form.addRow(0, new Label("Username:"), tfUser);
        form.addRow(1, new Label("Email:"), tfEmail);

        dialog.getDialogPane().setContent(form);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        dialog.getDialogPane().getStylesheets().add(
            getClass().getResource("/css/styles.css").toExternalForm());

        dialog.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.OK) {
                String u = tfUser.getText().trim();
                String e = tfEmail.getText().trim();
                if (u.isEmpty() || e.isEmpty()) {
                    AlertUtil.showError("Error", "Please fill in all fields.");
                    return;
                }
                
                String tempPass = "password123";
                if (userDAO.resetPasswordByUsername(u, e, tempPass)) {
                    AlertUtil.showInfo("Success", "Your password has been reset to: " + tempPass);
                } else {
                    AlertUtil.showError("Failed", "Invalid username or email. Password not reset.");
                }
            }
        });
    }
}
