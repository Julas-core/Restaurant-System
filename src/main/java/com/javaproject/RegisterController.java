package com.javaproject;

import com.javaproject.auth.AuthService;
import com.javaproject.auth.Role;
import com.javaproject.auth.User;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

public class RegisterController {

    @FXML private TextField fullNameField;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private CheckBox registerAsAdmin;

    @FXML
    private void register() throws IOException {
        String fullName = fullNameField == null ? "" : fullNameField.getText();
        String username = usernameField == null ? "" : usernameField.getText();
        String password = passwordField == null ? "" : passwordField.getText();
        String confirm = confirmPasswordField == null ? "" : confirmPasswordField.getText();

        if (!password.equals(confirm)) {
            showAlert("Registration failed", "Passwords do not match.");
            return;
        }

        Role role = (registerAsAdmin != null && registerAsAdmin.isSelected()) ? Role.ADMIN : Role.USER;

        try {
            User created = AuthService.getInstance().register(username, password, role);
            App.getState().setCurrentUser(created);

            // Seed profile data from registration form
            ProfileData profile = App.getState().getCurrentProfile().orElse(new ProfileData());
            String[] parts = splitName(fullName);
            profile.setFirstName(parts[0]);
            profile.setLastName(parts[1]);
            profile.setEmail(created.getUsername());
            App.getState().saveCurrentProfile(profile);

            App.setRoot("profile");
        } catch (IllegalArgumentException ex) {
            showAlert("Registration failed", ex.getMessage());
        }
    }

    @FXML
    private void goLogin() throws IOException {
        App.setRoot("login");
    }

    @FXML
    private void goBack() throws IOException {
        App.setRoot("primary");
    }

    private String[] splitName(String fullName) {
        if (fullName == null) {
            return new String[]{"", ""};
        }
        String trimmed = fullName.trim();
        if (trimmed.isBlank()) {
            return new String[]{"", ""};
        }
        String[] parts = trimmed.split("\\s+", 2);
        String first = parts.length > 0 ? parts[0] : "";
        String last = parts.length > 1 ? parts[1] : "";
        return new String[]{first, last};
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
