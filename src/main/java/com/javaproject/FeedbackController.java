package com.javaproject;

import com.javaproject.model.MenuItem;
import com.javaproject.db.FeedbackDao;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.ImagePattern;
import javafx.scene.image.Image;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.Arrays;

public class FeedbackController {

    @FXML private Label star1;
    @FXML private Label star2;
    @FXML private Label star3;
    @FXML private Label star4;
    @FXML private Label star5;
    @FXML private TextArea commentArea;
    
    @FXML private Label contextTitle;
    @FXML private Label contextSubtitle;

    @FXML private HBox tagsContainer;
    @FXML private Label tagsLabel;
    @FXML private Rectangle feedbackImageRect;

    private int rating = 0;
    private final FeedbackDao feedbackDao = new FeedbackDao();
    private MenuItem targetItem;
    private final Set<String> selectedTags = new HashSet<>();

    private static final List<String> TAGS_POSITIVE = Arrays.asList(
        "Taste", "Portion Size", "Freshness", "Temperature", "Packaging", "Value"
    );

    private static final List<String> TAGS_NEGATIVE = Arrays.asList(
        "Late Delivery", "Cold Food", "Wrong Item", "Stale", "Damaged", "Salty", "Bland"
    );

    @FXML
    private void initialize() {
        try {
            // Check if we are reviewing a specific item
            targetItem = App.getState().getSelectedMenuItem();
            
            if (targetItem != null) {
                if (contextTitle != null) contextTitle.setText("Reviewing: " + targetItem.getName());
                if (contextSubtitle != null) contextSubtitle.setText(targetItem.getCategory() + " • " + String.format("ETB %.2f", targetItem.getPrice()));
                
                // Set image
                if (feedbackImageRect != null) {
                    try {
                        String imagePath = targetItem.getImagePath();
                        if (imagePath != null && !imagePath.isEmpty()) {
                             java.net.URL imgUrl = getClass().getResource(imagePath);
                             if (imgUrl != null) {
                                 Image img = new Image(imgUrl.toExternalForm());
                                 feedbackImageRect.setFill(new ImagePattern(img));
                             } else {
                                 feedbackImageRect.setFill(javafx.scene.paint.Color.LIGHTGRAY);
                             }
                        } else {
                            feedbackImageRect.setFill(javafx.scene.paint.Color.LIGHTGRAY);
                        }
                    } catch (Exception e) {
                        System.err.println("Image load failed: " + e.getMessage());
                        feedbackImageRect.setFill(javafx.scene.paint.Color.LIGHTGRAY);
                    }
                }
    
            } else {
                if (contextTitle != null) contextTitle.setText("General Feedback");
                if (contextSubtitle != null) contextSubtitle.setText("Let us know how we are doing");
                if (feedbackImageRect != null) feedbackImageRect.setFill(javafx.scene.paint.Color.LIGHTGRAY);
            }
    
            // Initialize empty state
            if (tagsContainer != null) tagsContainer.getChildren().clear();
            if (tagsLabel != null) tagsLabel.setVisible(false);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("FeedbackController initialization failed: " + e.getMessage());
        }
    }
    
    @FXML
    private void clearTarget() {
        targetItem = null;
        App.getState().setSelectedMenuItem(null);
        initialize();
    }

    @FXML
    private void goBackToOrders() throws IOException {
        App.setRoot("profile");
    }

    @FXML
    private void handleRating(MouseEvent event) {
        Object source = event.getSource();
        if (source == star1) rating = 1;
        else if (source == star2) rating = 2;
        else if (source == star3) rating = 3;
        else if (source == star4) rating = 4;
        else if (source == star5) rating = 5;

        updateStars();
        updateTags();
    }

    private void updateStars() {
        Label[] stars = {star1, star2, star3, star4, star5};
        for (int i = 0; i < stars.length; i++) {
            if (i < rating) {
                stars[i].setText("★");
                stars[i].setStyle("-fx-text-fill: #ffc107; -fx-font-size: 22px; cursor: hand;");
            } else {
                stars[i].setText("☆");
                stars[i].setStyle("-fx-text-fill: rgba(0,0,0,0.25); -fx-font-size: 22px; cursor: hand;");
            }
        }
    }

    private void updateTags() {
        if (tagsContainer == null) return;
        
        tagsContainer.getChildren().clear();
        selectedTags.clear();

        if (rating == 0) {
            if (tagsLabel != null) tagsLabel.setVisible(false);
            return;
        }

        if (tagsLabel != null) tagsLabel.setVisible(true);
        List<String> tagsToShow;

        if (rating >= 4) {
            if (tagsLabel != null) tagsLabel.setText("WHAT MADE IT GREAT?");
            tagsToShow = TAGS_POSITIVE;
        } else {
            if (tagsLabel != null) tagsLabel.setText("WHAT WENT WRONG?");
            tagsToShow = TAGS_NEGATIVE;
        }

        for (String tagText : tagsToShow) {
            Label tag = createTag(tagText);
            tagsContainer.getChildren().add(tag);
        }
    }

    private Label createTag(String text) {
        Label tag = new Label(text);
        tag.getStyleClass().add("chip");
        tag.setOnMouseClicked(e -> {
            if (selectedTags.contains(text)) {
                selectedTags.remove(text);
                tag.getStyleClass().remove("chip-selected");
            } else {
                selectedTags.add(text);
                tag.getStyleClass().add("chip-selected");
            }
        });
        return tag;
    }

    @FXML
    private void submitReview() throws IOException {
        if (rating == 0) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please select a star rating.");
            alert.show();
            return;
        }

        var userOpt = App.getState().getCurrentUser();
        if (userOpt.isEmpty()) {
             App.setRoot("login");
             return;
        }
        
        String comment = commentArea.getText().trim();
        Long itemId = targetItem == null ? null : targetItem.getId();
        
        String tagsString = selectedTags.isEmpty() ? null : String.join(",", selectedTags);

        feedbackDao.addFeedback(userOpt.get().getId(), rating, comment, itemId, tagsString);
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Thank you for your feedback!");
        alert.showAndWait();
        
        App.setRoot("profile");
    }
}
