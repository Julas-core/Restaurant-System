package com.javaproject;

import com.javaproject.auth.AuthService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    @FXML
    private void login() throws IOException {
        String username = usernameField == null ? "" : usernameField.getText();
        String password = passwordField == null ? "" : passwordField.getText();

        AuthService.getInstance().login(username, password).ifPresentOrElse(user -> {
            App.getState().setCurrentUser(user);
            App.setRootSafe("profile");
        }, () -> showAlert("Login failed", "Invalid username or password."));
    }

    @FXML
    private void goRegister() throws IOException {
        App.setRoot("register");
    }

    @FXML
    private void goBack() throws IOException {
        App.setRoot("primary");
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
