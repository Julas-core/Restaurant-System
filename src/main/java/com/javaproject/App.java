package com.javaproject;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

import com.javaproject.db.DbBootstrap;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;
    private static final AppState STATE = new AppState();

    @Override
    public void start(Stage stage) throws IOException {
        stage.setTitle("Ethiopia Restaurant");
        scene = new Scene(loadFXML("login"), 1366, 768);
        scene.getStylesheets().add(App.class.getResource("styles.css").toExternalForm());
        stage.setScene(scene);
        stage.show();

        // Best-effort DB bootstrap (schema + seed + menu load). If DB is down/misconfigured,
        // the app should still run using the built-in in-memory menu.
        try {
            List<com.javaproject.model.MenuItem> dbMenu = DbBootstrap.initAndLoadMenu(getState().getMenuItems());
            if (dbMenu != null && !dbMenu.isEmpty()) {
                getState().setMenuItems(dbMenu);
            }
        } catch (Exception e) {
            // CHANGE: Print the error so we can see what is wrong
            System.err.println("Database connection failed!");
            e.printStackTrace(); 
        }
    }

    public static AppState getState() {
        return STATE;
    }

    static void setRoot(String fxml) throws IOException {
        if (fxml != null && fxml.startsWith("admin_") && !STATE.isAdmin()) {
            if (!STATE.isAuthenticated()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Sign in required");
                alert.setHeaderText("Please sign in");
                alert.setContentText("Sign in as an admin to open the admin panel.");
                alert.showAndWait();
                fxml = "login";
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Access Denied");
                alert.setHeaderText("Admin access required");
                alert.setContentText("Your account does not have admin access.");
                alert.showAndWait();
                fxml = "profile";
            }
        }

        scene.setRoot(loadFXML(fxml));
    }

    public static void setRootSafe(String fxml) {
        try {
            setRoot(fxml);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

}