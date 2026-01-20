package com.javaproject;

import com.javaproject.model.MenuItem;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

import java.io.IOException;
import java.util.List;

public class HomeController {

    @FXML private HBox favoritesContainer;

    @FXML
    public void initialize() {
        if (favoritesContainer != null) {
            favoritesContainer.getChildren().clear();
            List<MenuItem> items = App.getState().getMenuItems();
            // Display first 4 items as favorites/featured
            int count = 0;
            if (items != null) {
                for (MenuItem item : items) {
                    if (count >= 4) break;
                    favoritesContainer.getChildren().add(createCard(item));
                    count++;
                }
            }
        }
    }

    private VBox createCard(MenuItem item) {
        VBox card = new VBox(8);
        card.getStyleClass().add("card");
        
        // Image handling
        if (item.getImagePath() != null && !item.getImagePath().isEmpty()) {
            try {
                 Region imageRegion = new Region();
                 imageRegion.setPrefSize(170, 110);
                 imageRegion.setMinSize(170, 110);
                 imageRegion.setMaxSize(170, 110);
                 
                 // Rounded corners
                 Rectangle clip = new Rectangle(170, 110);
                 clip.setArcWidth(14);
                 clip.setArcHeight(14);
                 imageRegion.setClip(clip);
                 
                 // CSS for cover
                 imageRegion.setStyle(
                     "-fx-background-image: url('" + item.getImagePath().replace("'", "\\'") + "'); " +
                     "-fx-background-size: cover; " +
                     "-fx-background-position: center center;"
                 );
                 
                card.getChildren().add(imageRegion);
            } catch (Exception e) {
                 // Fallback if image fails to load
                 card.getChildren().add(createPlaceholder());
            }
        } else {
            card.getChildren().add(createPlaceholder());
        }

        Label nameLabel = new Label(item.getName());
        nameLabel.getStyleClass().add("card-title");

        Label priceLabel = new Label(String.format("Price: ETB %.2f", item.getPrice()));
        priceLabel.getStyleClass().add("card-price");

        Button addButton = new Button("Add to Order");
        addButton.getStyleClass().add("card-button");
        addButton.setUserData(item.getName());
        addButton.setOnAction(this::addFavorite);

        card.getChildren().addAll(nameLabel, priceLabel, addButton);
        return card;
    }

    private Rectangle createPlaceholder() {
        Rectangle rect = new Rectangle(170, 110);
        rect.setArcWidth(14);
        rect.setArcHeight(14);
        rect.getStyleClass().add("card-image");
        return rect;
    }

    @FXML
    private void goToMenu() throws IOException {
        App.setRoot("primary");
    }

    @FXML
    private void addFavorite(ActionEvent event) {
        Object userData = event.getSource() instanceof Button b ? b.getUserData() : null;
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
