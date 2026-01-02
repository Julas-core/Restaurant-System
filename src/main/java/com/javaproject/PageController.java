package com.javaproject;

import javafx.fxml.FXML;
import java.io.IOException;

public class PageController {

    @FXML
    private void goToHome() throws IOException {
        App.setRoot("primary");
    }

    @FXML
    private void goToMenu() throws IOException {
        App.setRoot("primary");
    }

    @FXML
    private void goToAbout() throws IOException {
        App.setRoot("about");
    }

    @FXML
    private void goToContact() throws IOException {
        App.setRoot("contact");
    }

    @FXML
    private void goToLocation() throws IOException {
        App.setRoot("location");
    }

    @FXML
    private void goToCheckout() throws IOException {
        App.setRoot("checkout");
    }

    @FXML
    private void goToProfile() throws IOException {
        App.setRoot(App.getState().isAuthenticated() ? "profile" : "login");
    }
}
