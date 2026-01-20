package com.javaproject;

import com.javaproject.model.CartLine;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.io.IOException;

public class SecondaryController {

    @FXML
    private ListView<CartLine> orderSummaryListView;

    @FXML
    private Label checkoutTotalLabel;

    @FXML
    private Button placeOrderButton;

    @FXML
    public void initialize() {
        orderSummaryListView.setItems(App.getState().getCartLines());
        updateTotal();

        placeOrderButton.setDisable(App.getState().getCartLines().isEmpty());

        App.getState().getCartLines().addListener((javafx.collections.ListChangeListener<CartLine>) change -> {
            updateTotal();
            placeOrderButton.setDisable(App.getState().getCartLines().isEmpty());
        });
    }

    @FXML
    private void placeOrder() throws IOException {
        if (App.getState().getCartLines().isEmpty()) {
            return;
        }

        double total = App.getState().getCartTotal();
        App.getState().clearCart();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Order Placed");
        alert.setHeaderText("Thanks for your order!");
        alert.setContentText(String.format("Your total was ETB %.2f", total));
        alert.showAndWait();

        App.setRoot("primary");
    }

    private void updateTotal() {
        checkoutTotalLabel.setText(String.format("Total: ETB %.2f", App.getState().getCartTotal()));
    }

    @FXML
    private void switchToPrimary() throws IOException {
        App.setRoot("primary");
    }
}