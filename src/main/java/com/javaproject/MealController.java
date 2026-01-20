package com.javaproject;

import com.javaproject.model.MenuItem;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MealController {

    @FXML
    private Label titleLabel;

    @FXML
    private StackPane mealImageContainer;

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

    @FXML
    private javafx.scene.control.Button btnFavorite;

    private final ToggleGroup sizeGroup = new ToggleGroup();

    private MenuItem baseItem;
    private int quantity = 1;
    private boolean isFavorite = false;

    @FXML
    public void goToReview() throws IOException {
        App.setRoot("feedback");
    }

    @FXML
    public void toggleFavorite() {
        if (!App.getState().isAuthenticated()) {
            return; // Or prompt login
        }
        var user = App.getState().getCurrentUser().get();
        // Ensure item has ID. If it's a static item not in DB, we can't fave it easily unless we insert it.
        // Assuming all displayed items are from DB.
        if (baseItem.getId() == null) {
            return;
        }

        try {
            com.javaproject.db.FavoriteDao dao = new com.javaproject.db.FavoriteDao();
            if (isFavorite) {
                dao.removeFavorite(user.getId(), baseItem.getId());
                isFavorite = false;
            } else {
                dao.addFavorite(user.getId(), baseItem.getId());
                isFavorite = true;
            }
            updateFavoriteBtn();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateFavoriteBtn() {
        if (btnFavorite == null) return;
        if (isFavorite) {
            btnFavorite.setText("♥");
            btnFavorite.setStyle("-fx-text-fill: #e02424; -fx-font-size: 20px;");
        } else {
            btnFavorite.setText("♡");
            btnFavorite.setStyle("-fx-text-fill: black; -fx-font-size: 20px;");
        }
    }

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
        
        // Check favorite status
        if (App.getState().isAuthenticated() && baseItem.getId() != null) {
            try {
                com.javaproject.db.FavoriteDao dao = new com.javaproject.db.FavoriteDao();
                isFavorite = dao.isFavorite(App.getState().getCurrentUser().get().getId(), baseItem.getId());
                updateFavoriteBtn();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        sizeRegular.setToggleGroup(sizeGroup);
        sizeLarge.setToggleGroup(sizeGroup);
        sizeRegular.setSelected(true);

        titleLabel.setText(baseItem.getName());
        descriptionLabel.setText(baseItem.getDescription());

        // Load image if available
        if (mealImageContainer != null) {
            // Apply clip for rounded corners
            Rectangle clip = new Rectangle(520, 320);
            clip.setArcWidth(16);
            clip.setArcHeight(16);
            mealImageContainer.setClip(clip);

            if (baseItem.getImagePath() != null && !baseItem.getImagePath().isBlank()) {
                try {
                    mealImageContainer.setStyle(
                        "-fx-background-image: url('" + baseItem.getImagePath().replace("'", "\\'") + "'); " +
                        "-fx-background-size: cover; " +
                        "-fx-background-position: center center;"
                    );
                } catch (Exception e) {
                    // ignore load failure
                }
            }
        }

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
        priceLabel.setText(String.format("ETB %.2f", unit));
        bottomUnitPriceLabel.setText(String.format("ETB %.2f", unit));
        bottomTotalPriceLabel.setText(String.format("ETB %.2f", unit * quantity));
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
