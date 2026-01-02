package com.javaproject;

import javafx.fxml.FXML;

import java.io.IOException;

public class FeedbackController {

    @FXML
    private void goBackToOrders() throws IOException {
        App.setRoot("profile");
    }

    @FXML
    private void submitReview() throws IOException {
        App.setRoot("profile");
    }
}
