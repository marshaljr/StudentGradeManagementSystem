package com.sms.util;

import com.sms.MainApp;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * NavigationUtil — Utility class for switching JavaFX scenes.
 *
 * All screen transitions go through this class, keeping navigation
 * logic out of individual controllers.
 */
public class NavigationUtil {

    private NavigationUtil() {}

    /**
     * Navigates the primary stage to a new FXML screen.
     *
     * @param fxmlPath relative path under /fxml/, e.g. "AdminDashboard.fxml"
     */
    public static void navigateTo(String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(
                NavigationUtil.class.getResource("/fxml/" + fxmlPath)
            );
            Scene scene = new Scene(root);
            scene.getStylesheets().add(
                NavigationUtil.class.getResource("/css/styles.css").toExternalForm()
            );
            Stage stage = MainApp.primaryStage;
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("[Nav] Failed to load " + fxmlPath + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Opens a new modal/popup window.
     *
     * @param fxmlPath  FXML file name
     * @param title     Window title
     */
    public static void openModal(String fxmlPath, String title) {
        try {
            Parent root = FXMLLoader.load(
                NavigationUtil.class.getResource("/fxml/" + fxmlPath)
            );
            Scene scene = new Scene(root);
            scene.getStylesheets().add(
                NavigationUtil.class.getResource("/css/styles.css").toExternalForm()
            );
            Stage modal = new Stage();
            modal.setTitle(title);
            modal.setScene(scene);
            modal.initOwner(MainApp.primaryStage);
            modal.showAndWait();
        } catch (IOException e) {
            System.err.println("[Nav] Modal failed: " + e.getMessage());
        }
    }

    /** Goes back to the login screen (also clears the session). */
    public static void logout() {
        SessionManager.clearSession();
        navigateTo("LoginView.fxml");
    }
}
