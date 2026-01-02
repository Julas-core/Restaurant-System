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
    protected void doLogout() throws IOException {
        App.getState().logout();
        App.setRoot("primary");
    }
}
