package com.javaproject;

import com.javaproject.model.CartLine;
import com.javaproject.model.MenuItem;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.io.IOException;

public class PrimaryController {

    @FXML
    private ListView<MenuItem> menuListView;

    @FXML
    private Label itemNameLabel;

    @FXML
    private Label itemDescriptionLabel;

    @FXML
    private Label itemPriceLabel;

    @FXML
    private Button addToCartButton;

    @FXML
    private ListView<CartLine> cartListView;

    @FXML
    private Button removeOneButton;

    @FXML
    private Label totalLabel;

    @FXML
    public void initialize() {
        menuListView.getItems().setAll(App.getState().getMenuItems());
        cartListView.setItems(App.getState().getCartLines());

        addToCartButton.setDisable(true);
        removeOneButton.setDisable(true);

        menuListView.getSelectionModel().selectedItemProperty().addListener((obs, oldItem, newItem) -> {
            if (newItem == null) {
                itemNameLabel.setText("Select an item");
                itemDescriptionLabel.setText("");
                itemPriceLabel.setText("");
                addToCartButton.setDisable(true);
                return;
            }

            itemNameLabel.setText(newItem.getName());
            itemDescriptionLabel.setText(newItem.getDescription());
            itemPriceLabel.setText(String.format("Price: $%.2f", newItem.getPrice()));
            addToCartButton.setDisable(false);
        });

        cartListView.getSelectionModel().selectedItemProperty().addListener((obs, oldLine, newLine) ->
                removeOneButton.setDisable(newLine == null)
        );

        updateTotal();
        App.getState().getCartLines().addListener((javafx.collections.ListChangeListener<CartLine>) change -> updateTotal());
    }

    @FXML
    private void addSelectedItemToCart() {
        MenuItem selected = menuListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return;
        }
        App.getState().addToCart(selected);
        updateTotal();
    }

    @FXML
    private void removeSelectedCartLine() {
        CartLine selected = cartListView.getSelectionModel().getSelectedItem();
        App.getState().removeOneFromCart(selected);
        updateTotal();
    }

    private void updateTotal() {
        totalLabel.setText(String.format("Total: $%.2f", App.getState().getCartTotal()));
    }

    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("secondary");
    }
}
