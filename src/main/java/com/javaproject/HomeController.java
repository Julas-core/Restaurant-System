package com.javaproject;

import com.javaproject.model.MenuItem;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.io.IOException;

public class HomeController {

    @FXML
    private void goToMenu() throws IOException {
        App.setRoot("primary");
    }

    @FXML
    private void addFavorite(ActionEvent event) {
        Object userData = event.getSource() instanceof javafx.scene.control.Button b ? b.getUserData() : null;
        if (!(userData instanceof String name)) {
            return;
        }

        for (MenuItem item : App.getState().getMenuItems()) {
            if (item.getName().equals(name)) {
                App.getState().addToCart(item);
                break;
            }
        }
    }
}
