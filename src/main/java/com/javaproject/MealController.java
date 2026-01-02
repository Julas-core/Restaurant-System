package com.javaproject;

import com.javaproject.model.MenuItem;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MealController {

    @FXML
    private Label titleLabel;

    @FXML
    private Label priceLabel;

    @FXML
    private Label ratingLabel;

    @FXML
    private Label reviewsLabel;

    @FXML
    private Label descriptionLabel;

    @FXML
    private RadioButton sizeRegular;

    @FXML
    private RadioButton sizeLarge;

    @FXML
    private CheckBox toppingCheese;

    @FXML
    private CheckBox toppingBacon;

    @FXML
    private CheckBox toppingAvocado;

    @FXML
    private TextArea instructionsArea;

    @FXML
    private Label qtyLabel;

    @FXML
    private Label bottomUnitPriceLabel;

    @FXML
    private Label bottomTotalPriceLabel;

    private final ToggleGroup sizeGroup = new ToggleGroup();

    private MenuItem baseItem;
    private int quantity = 1;

    @FXML
    public void initialize() {
        baseItem = App.getState().getSelectedMenuItem();
        if (baseItem == null) {
            // Fallback: if user navigates here directly.
            try {
                App.setRoot("primary");
            } catch (IOException ignored) {
            }
            return;
        }

        sizeRegular.setToggleGroup(sizeGroup);
        sizeLarge.setToggleGroup(sizeGroup);
        sizeRegular.setSelected(true);

        titleLabel.setText(baseItem.getName());
        descriptionLabel.setText(baseItem.getDescription());

        // For the screenshot-style UI, show a fixed review count (beginner-friendly).
        ratingLabel.setText(String.format("%.1f", baseItem.getRating()));
        reviewsLabel.setText("(124 reviews)");

        qtyLabel.setText(String.valueOf(quantity));

        sizeGroup.selectedToggleProperty().addListener((obs, o, n) -> updatePrices());
        toppingCheese.selectedProperty().addListener((obs, o, n) -> updatePrices());
        toppingBacon.selectedProperty().addListener((obs, o, n) -> updatePrices());
        toppingAvocado.selectedProperty().addListener((obs, o, n) -> updatePrices());

        updatePrices();
    }

    @FXML
    private void goBackToMenu() throws IOException {
        App.setRoot("primary");
    }

    @FXML
    private void goToCheckout() throws IOException {
        App.setRoot("checkout");
    }

    @FXML
    private void goToProfile() throws IOException {
        App.setRoot(App.getState().isAuthenticated() ? "profile" : "login");
    }

    @FXML
    private void decrementQty() {
        if (quantity <= 1) {
            return;
        }
        quantity--;
        qtyLabel.setText(String.valueOf(quantity));
        updatePrices();
    }

    @FXML
    private void incrementQty() {
        quantity++;
        qtyLabel.setText(String.valueOf(quantity));
        updatePrices();
    }

    @FXML
    private void addToOrder() throws IOException {
        if (baseItem == null) {
            return;
        }

        MenuItem customized = buildCustomizedItem();
        for (int i = 0; i < quantity; i++) {
            App.getState().addToCart(customized);
        }

        App.setRoot("primary");
    }

    private void updatePrices() {
        if (baseItem == null) {
            return;
        }

        double unit = computeUnitPrice();
        priceLabel.setText(String.format("$%.2f", unit));
        bottomUnitPriceLabel.setText(String.format("$%.2f", unit));
        bottomTotalPriceLabel.setText(String.format("$%.2f", unit * quantity));
    }

    private double computeUnitPrice() {
        double unit = baseItem.getPrice();

        if (sizeLarge.isSelected()) {
            unit += 2.00;
        }
        if (toppingCheese.isSelected()) {
            unit += 1.00;
        }
        if (toppingBacon.isSelected()) {
            unit += 1.50;
        }
        if (toppingAvocado.isSelected()) {
            unit += 2.00;
        }

        return unit;
    }

    private MenuItem buildCustomizedItem() {
        double unit = computeUnitPrice();

        String size = sizeLarge.isSelected() ? "Large" : "Regular";
        List<String> toppings = new ArrayList<>();
        if (toppingCheese.isSelected()) {
            toppings.add("Extra Cheese");
        }
        if (toppingBacon.isSelected()) {
            toppings.add("Crispy Bacon");
        }
        if (toppingAvocado.isSelected()) {
            toppings.add("Avocado");
        }

        StringBuilder name = new StringBuilder(baseItem.getName());
        name.append(" (").append(size);
        if (!toppings.isEmpty()) {
            name.append(", ").append(String.join(", ", toppings));
        }
        name.append(")");

        String instructions = instructionsArea.getText();
        String desc = baseItem.getDescription();
        if (instructions != null && !instructions.isBlank()) {
            desc = desc + "\n\nNotes: " + instructions.trim();
        }

        return new MenuItem(
                name.toString(),
                desc,
                unit,
                baseItem.getCategory(),
                baseItem.getLabel(),
                baseItem.getRating(),
                baseItem.isChefSpecial()
        );
    }
}
