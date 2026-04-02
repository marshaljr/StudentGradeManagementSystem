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
import javafx.scene.control.CheckBox;
import javafx.scene.input.KeyCode;
import javafx.scene.control.Dialog;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

/**
 * LoginController — Handles login screen events.
 *
 * Validates credentials, stores the user in SessionManager,
 * then navigates to the correct dashboard based on role.
 */
public class LoginController {

    @FXML private TextField     usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button        passwordToggleBtn;
    @FXML private Label         errorLabel;
    @FXML private Button        loginButton;
    @FXML private CheckBox      rememberMeCheckBox;

    private final UserDAO userDAO = new UserDAO();
    private boolean passwordVisible = false;
    private TextField plainPasswordField;  // For showing password in plain text

    @FXML
    public void initialize() {
        // Initialize plain text password field (hidden by default)
        plainPasswordField = new TextField();
        plainPasswordField.setStyle(passwordField.getStyle());
        plainPasswordField.setManaged(false);
        plainPasswordField.setVisible(false);

        errorLabel.setVisible(false);
        errorLabel.setStyle("-fx-text-fill: #DC2626; -fx-font-size: 12px; -fx-padding: 4 0 0 0;");

        // Allow pressing Enter in password field to trigger login
        passwordField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) handleLogin();
        });
        usernameField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) passwordField.requestFocus();
        });
        plainPasswordField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) handleLogin();
        });
    }

    /**
     * Toggles password visibility by switching between PasswordField and TextField
     */
    @FXML
    private void togglePasswordVisibility() {
        passwordVisible = !passwordVisible;

        if (passwordVisible) {
            // Show password in plain text
            plainPasswordField.setText(passwordField.getText());
            passwordToggleBtn.setText("🙈");  // Closed eye icon
            
            // Swap visibility
            passwordField.setManaged(false);
            passwordField.setVisible(false);
            plainPasswordField.setManaged(true);
            plainPasswordField.setVisible(true);
            plainPasswordField.requestFocus();
        } else {
            // Hide password again
            passwordField.setText(plainPasswordField.getText());
            passwordToggleBtn.setText("👁");  // Open eye icon
            
            // Swap visibility
            plainPasswordField.setManaged(false);
            plainPasswordField.setVisible(false);
            passwordField.setManaged(true);
            passwordField.setVisible(true);
            passwordField.requestFocus();
        }
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        // Get password from whichever field is visible
        String password = passwordVisible ? plainPasswordField.getText() : passwordField.getText();

        // ── Input validation ──────────────────────────────────────
        if (username.isEmpty()) {
            showError("❌ Please enter your username.");
            usernameField.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            showError("❌ Please enter your password.");
            (passwordVisible ? plainPasswordField : passwordField).requestFocus();
            return;
        }

        // ── Authenticate with loading state ───────────────────────
        loginButton.setDisable(true);
        loginButton.setStyle("-fx-opacity: 0.8;");
        loginButton.setText("Signing in...");

        UserAccount user = userDAO.authenticate(username, password);

        loginButton.setDisable(false);
        loginButton.setStyle("-fx-opacity: 1.0;");
        loginButton.setText("Sign In");

        if (user == null) {
            showError("❌ Invalid username or password. Please try again.");
            (passwordVisible ? plainPasswordField : passwordField).clear();
            (passwordVisible ? plainPasswordField : passwordField).requestFocus();
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
