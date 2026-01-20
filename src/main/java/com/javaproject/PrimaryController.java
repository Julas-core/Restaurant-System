package com.javaproject;

import com.javaproject.model.CartLine;
import com.javaproject.model.MenuItem;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;

public class PrimaryController {

    @FXML
    private TextField searchField;

    @FXML
    private ScrollPane menuScroll;

    @FXML
    private Label cartCountLabel;

    @FXML
    private FlowPane specialsPane;

    @FXML
    private FlowPane mainsPane;

    @FXML
    private Button tabPopular;

    @FXML
    private Button tabStarters;

    @FXML
    private Button tabMains;

    @FXML
    private Button tabDesserts;

    @FXML
    private Button tabDrinks;

    @FXML
    private ToggleButton chipAll;

    @FXML
    private ToggleButton chipVegetarian;

    @FXML
    private ToggleButton chipVegan;

    @FXML
    private ToggleButton chipGlutenFree;

    @FXML
    private ToggleButton chipSpicy;

    private String activeCategory = "Popular";

    @FXML
    public void initialize() {
        chipAll.setSelected(true);

        searchField.textProperty().addListener((obs, oldText, newText) -> refreshCards());

        App.getState().getCartLines().addListener((javafx.collections.ListChangeListener<CartLine>) change -> updateCartCount());
        updateCartCount();
        refreshCards();
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
    private void goToReservation() throws IOException {
        // For now, we can redirect to a contact page or show an alert.
        // Since the user asked for "Book a Table" to work, let's assume a reservation page.
        // If it doesn't exist yet, we'll create it.
        App.setRoot("contact"); 
    }

    @FXML
    private void scrollToSpecials() {
        menuScroll.setVvalue(0.35);
    }

    @FXML
    private void selectPopular() {
        activeCategory = "Popular";
        updateTabStyles();
        refreshCards();
    }

    @FXML
    private void selectStarters() {
        activeCategory = "Starters";
        updateTabStyles();
        refreshCards();
    }

    @FXML
    private void selectMains() {
        activeCategory = "Mains";
        updateTabStyles();
        refreshCards();
    }

    @FXML
    private void selectDesserts() {
        activeCategory = "Desserts";
        updateTabStyles();
        refreshCards();
    }

    @FXML
    private void selectDrinks() {
        activeCategory = "Drinks";
        updateTabStyles();
        refreshCards();
    }

    @FXML
    private void applyFilters() {
        // Make "All Filters" behave like a simple reset chip.
        boolean anySpecific = chipVegetarian.isSelected() || chipVegan.isSelected() || chipGlutenFree.isSelected() || chipSpicy.isSelected();

        if (chipAll.isSelected()) {
            chipVegetarian.setSelected(false);
            chipVegan.setSelected(false);
            chipGlutenFree.setSelected(false);
            chipSpicy.setSelected(false);
        } else if (anySpecific) {
            chipAll.setSelected(false);
        } else {
            chipAll.setSelected(true);
        }
        refreshCards();
    }

    @FXML
    private void showAll() {
        // In this beginner version, "View All" just resets to Popular.
        selectPopular();
        menuScroll.setVvalue(0.35);
    }

    private void refreshCards() {
        specialsPane.getChildren().setAll(buildCards(true));
        mainsPane.getChildren().setAll(buildCards(false));
    }

    private List<Node> buildCards(boolean chefSpecials) {
        List<Node> nodes = new ArrayList<>();
        for (MenuItem item : App.getState().getMenuItems()) {
            if (item.isChefSpecial() != chefSpecials) {
                continue;
            }

            if (!matchesActiveCategory(item)) {
                continue;
            }

            if (!matchesSearch(item)) {
                continue;
            }

            if (!matchesChips(item)) {
                continue;
            }

            nodes.add(createCard(item));
        }
        return nodes;
    }

    private boolean matchesActiveCategory(MenuItem item) {
        if ("Popular".equals(activeCategory)) {
            return true;
        }
        return item.getCategory().equals(activeCategory);
    }

    private boolean matchesSearch(MenuItem item) {
        String q = searchField.getText();
        if (q == null || q.isBlank()) {
            return true;
        }
        String needle = q.toLowerCase(Locale.ROOT).trim();
        return item.getName().toLowerCase(Locale.ROOT).contains(needle)
                || item.getDescription().toLowerCase(Locale.ROOT).contains(needle);
    }

    private boolean matchesChips(MenuItem item) {
        if (chipAll.isSelected()) {
            return true;
        }

        if (chipVegetarian.isSelected()) {
            boolean isVeg = "Vegetarian".equalsIgnoreCase(item.getLabel()) 
                         || (item.getDescription() != null && item.getDescription().toLowerCase(Locale.ROOT).contains("vegetarian"))
                         || (item.getName() != null && item.getName().toLowerCase(Locale.ROOT).contains("vegetarian"));
            if (!isVeg) return false;
        }

        if (chipVegan.isSelected()) {
            boolean isVegan = "Vegan".equalsIgnoreCase(item.getLabel())
                           || (item.getDescription() != null && item.getDescription().toLowerCase(Locale.ROOT).contains("vegan"))
                           || (item.getName() != null && item.getName().toLowerCase(Locale.ROOT).contains("vegan"));
            if (!isVegan) return false;
        }

        if (chipGlutenFree.isSelected()) {
            boolean isGF = "Gluten-Free".equalsIgnoreCase(item.getLabel())
                        || (item.getDescription() != null && item.getDescription().toLowerCase(Locale.ROOT).contains("gluten-free"))
                        || (item.getName() != null && item.getName().toLowerCase(Locale.ROOT).contains("gluten-free"));
            if (!isGF) return false;
        }

        if (chipSpicy.isSelected()) {
            boolean isSpicy = "Spicy".equalsIgnoreCase(item.getLabel())
                           || (item.getDescription() != null && item.getDescription().toLowerCase(Locale.ROOT).contains("spicy"))
                           || (item.getName() != null && item.getName().toLowerCase(Locale.ROOT).contains("spicy"));
            if (!isSpicy) return false;
        }

        return true;
    }

    private Node createCard(MenuItem item) {
        VBox card = new VBox(8);
        card.getStyleClass().add("menu-card");
        card.setPadding(new Insets(10));
        card.setPrefWidth(210);
        card.setOnMouseClicked(e -> openMealDetails(item));

        StackPane image = new StackPane();
        Rectangle rect = new Rectangle(190, 110);
        rect.getStyleClass().add("menu-card-image");
        rect.setArcWidth(14);
        rect.setArcHeight(14);
        image.getChildren().add(rect);

        if (item.getImagePath() != null && !item.getImagePath().isBlank()) {
            try {
                 Region imageRegion = new Region();
                 imageRegion.setPrefSize(190, 110);
                 imageRegion.setMinSize(190, 110);
                 imageRegion.setMaxSize(190, 110);
                 
                 // Rounded corners for the image region
                 Rectangle clip = new Rectangle(190, 110);
                 clip.setArcWidth(14);
                 clip.setArcHeight(14);
                 imageRegion.setClip(clip);
                 
                 // CSS for cover
                 imageRegion.setStyle(
                     "-fx-background-image: url('" + item.getImagePath().replace("'", "\\'") + "'); " +
                     "-fx-background-size: cover; " +
                     "-fx-background-position: center center;"
                 );
                 
                image.getChildren().add(imageRegion);
            } catch (Exception ignored) {
                // If image load fails, just show the placeholder
            }
        }

        if (item.getLabel() != null && !item.getLabel().isEmpty()) {
            Label labelPill = new Label(item.getLabel());
            labelPill.getStyleClass().addAll("pill", pillClassFor(item.getLabel()));
            StackPane.setAlignment(labelPill, javafx.geometry.Pos.TOP_RIGHT);
            StackPane.setMargin(labelPill, new Insets(8, 8, 0, 0));
            image.getChildren().add(labelPill);
        }

        Label name = new Label(item.getName());
        name.getStyleClass().add("menu-card-title");

        Label desc = new Label(item.getDescription());
        desc.getStyleClass().add("muted");
        desc.setWrapText(true);

        Label price = new Label(String.format("ETB %.2f", item.getPrice()));
        price.getStyleClass().add("menu-card-price");

        Button add = new Button("Add");
        add.getStyleClass().add("add-button");
        add.setOnAction(e -> {
            App.getState().addToCart(item);
            updateCartCount();
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox bottom = new HBox(10);
        bottom.getChildren().addAll(price, spacer, add);
        bottom.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        card.getChildren().addAll(image, name, desc, bottom);
        return card;
    }

    private void openMealDetails(MenuItem item) {
        try {
            App.getState().setSelectedMenuItem(item);
            App.setRoot("meal");
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private String pillClassFor(String label) {
        if (label == null) {
            return "pill-neutral";
        }

        return switch (label) {
            case "Spicy" -> "pill-spicy";
            case "Vegan" -> "pill-vegan";
            case "Vegetarian" -> "pill-veg";
            case "Gluten-Free" -> "pill-gf";
            default -> "pill-neutral";
        };
    }

    private void updateCartCount() {
        int count = 0;
        for (CartLine line : App.getState().getCartLines()) {
            count += line.getQuantity();
        }
        cartCountLabel.setText(String.valueOf(count));
    }

    private void updateTabStyles() {
        setTabActive(tabPopular, "Popular".equals(activeCategory));
        setTabActive(tabStarters, "Starters".equals(activeCategory));
        setTabActive(tabMains, "Mains".equals(activeCategory));
        setTabActive(tabDesserts, "Desserts".equals(activeCategory));
        setTabActive(tabDrinks, "Drinks".equals(activeCategory));
    }

    private void setTabActive(Button tab, boolean active) {
        if (tab == null) {
            return;
        }

        if (!tab.getStyleClass().contains("tabtext")) {
            tab.getStyleClass().add("tabtext");
        }

        if (active) {
            if (!tab.getStyleClass().contains("tab-active")) {
                tab.getStyleClass().add("tab-active");
            }
        } else {
            tab.getStyleClass().remove("tab-active");
        }
    }
}
