package com.sms;

import com.sms.util.DatabaseConfig;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Main entry point for the Student Grade Management System.
 *
 * The application follows MVC architecture:
 *  - model/   : Data classes (UserAccount, Course, Grade, etc.)
 *  - view/    : JavaFX FXML files and CSS
 *  - controller/ : JavaFX controllers wiring UI to DAO
 *  - dao/     : Data Access Objects (CRUD against MySQL)
 *  - util/    : Helpers (DB connection, hashing, navigation)
 *  - factory/ : UserFactory (Factory Method pattern)
 */
public class MainApp extends Application {

    public static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;

        // Load the Login screen
        Parent root = FXMLLoader.load(
            getClass().getResource("/fxml/LoginView.fxml")
        );

        Scene scene = new Scene(root, 900, 600);
        scene.getStylesheets().add(
            getClass().getResource("/css/styles.css").toExternalForm()
        );

        stage.setTitle("Student Grade Management System");
        stage.setScene(scene);
        stage.setMinWidth(900);
        stage.setMinHeight(600);
        stage.show();
    }

    @Override
    public void stop() {
        // Close DB connection pool on app exit
        DatabaseConfig.getInstance().closeConnection();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
