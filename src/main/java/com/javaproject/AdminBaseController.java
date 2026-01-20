package com.javaproject;

import javafx.fxml.FXML;
import java.io.IOException;

public class AdminBaseController {

    @FXML
    protected void goDashboard() throws IOException {
        App.setRoot("admin_dashboard");
    }

    @FXML
    protected void goOrders() throws IOException {
        App.setRoot("admin_orders");
    }

    @FXML
    protected void goMenu() throws IOException {
        App.setRoot("admin_meals");
    }

    @FXML
    protected void goUsers() throws IOException {
        App.setRoot("admin_users");
    }

    @FXML
    protected void goFeedback() throws IOException {
        App.setRoot("admin_feedback");
    }

    @FXML
    protected void goSettings() throws IOException {
        App.setRoot("admin_settings");
    }

    @FXML
    protected void doLogout() throws IOException {
        App.getState().logout();
        App.setRoot("primary");
    }

    // Public Navigation for shared Menu Bar
    @FXML
    protected void goToHome() throws IOException {
        App.setRoot("primary");
    }

    @FXML
    protected void goToMenu() throws IOException {
        App.setRoot("primary");
    }

    @FXML
    protected void goToAbout() throws IOException {
        App.setRoot("about");
    }

    @FXML
    protected void goToContact() throws IOException {
        App.setRoot("contact");
    }

    @FXML
    protected void goToLocation() throws IOException {
        App.setRoot("location");
    }

    @FXML
    protected void goToCheckout() throws IOException {
        App.setRoot("checkout");
    }

    @FXML
    protected void goToProfile() throws IOException {
        App.setRoot(App.getState().isAuthenticated() ? "profile" : "login");
    }
}
