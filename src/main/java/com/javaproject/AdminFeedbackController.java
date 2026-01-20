package com.javaproject;

import com.javaproject.db.FeedbackDao;
import com.javaproject.model.Feedback;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

import java.text.SimpleDateFormat;
import java.util.List;

public class AdminFeedbackController extends AdminBaseController {

    @FXML
    private VBox reviewsList;
    
    @FXML private Label avgRatingLabel;
    @FXML private Label unreadReviewsLabel;
    @FXML private Label totalFeedbackLabel;

    private final FeedbackDao feedbackDao = new FeedbackDao();

    @FXML
    public void initialize() {
        refreshFeedbackList();
    }
    
    private void refreshFeedbackList() {
        updateStats();
        reviewsList.getChildren().clear();
        List<Feedback> feedbacks = feedbackDao.getAllFeedback();
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");

        if (feedbacks.isEmpty()) {
            Label empty = new Label("No new feedback to display.");
            empty.getStyleClass().add("muted");
            reviewsList.getChildren().add(empty);
            return;
        }

        for (Feedback f : feedbacks) {
            String dateStr = f.getCreatedAt() != null ? sdf.format(f.getCreatedAt()) : "N/A";
            String status = (f.getAdminReply() != null && !f.getAdminReply().isBlank()) ? "REPLIED" : "NEW";
            
            reviewsList.getChildren().add(
                createReviewCard(f.getId(), f.getUserName(), dateStr, f.getRating(), f.getComment(), status, f.getAdminReply(), f.getMenuItemName(), f.getTags())
            );
        }
    }

    private void updateStats() {
        if (avgRatingLabel == null) return;
        try {
             avgRatingLabel.setText(String.format("%.1f / 5.0", feedbackDao.getAverageRating()));
             unreadReviewsLabel.setText(String.valueOf(feedbackDao.getUnreadFeedbackCount()));
             totalFeedbackLabel.setText(String.valueOf(feedbackDao.getTotalFeedbackCount()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private VBox createReviewCard(long id, String name, String date, int stars, String text, String status, String reply, String foodName, String tags) {
        VBox card = new VBox(10);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(15));
        
        Label foodLabel = new Label(foodName == null ? "GENERAL" : foodName.toUpperCase());
        foodLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: white; -fx-background-color: " + (foodName == null ? "#6c757d" : "#fd7e14") + "; -fx-padding: 2 6; -fx-background-radius: 4;");

        HBox header = new HBox(15);
        
        Circle avatar = new Circle(24);
        avatar.getStyleClass().add("image-placeholder");
        
        VBox meta = new VBox(2);
        
        HBox nameBox = new HBox(10);
        Label n = new Label(name);
        n.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        // nameBox.getChildren().addAll(n, foodLabel);
        
        // Add badges for name and food type
        HBox.setHgrow(nameBox, Priority.ALWAYS);
        nameBox.getChildren().add(n);
        
        if (foodName != null) {
            nameBox.getChildren().add(foodLabel);
        }

        Label d = new Label(date);
        d.getStyleClass().add("muted");
        
        Label statusLabel = new Label(status);
        statusLabel.getStyleClass().add("badge");
        if ("NEW".equals(status)) {
            statusLabel.getStyleClass().add("badge-warning");
        } else {
            statusLabel.getStyleClass().add("badge-success");
        }
        
        meta.getChildren().addAll(nameBox, d, statusLabel);
        
        header.getChildren().addAll(avatar, meta);
        
        // Stars
        StringBuilder starStr = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            starStr.append(i < stars ? "★" : "☆");
        }
        Label starLabel = new Label(starStr.toString());
        starLabel.setStyle("-fx-text-fill: #ffc107; -fx-font-size: 16px;");
        
        // Tags display
        HBox tagsBox = new HBox(5);
        if (tags != null && !tags.isBlank()) {
            String[] tagsList = tags.split(",");
            for (String t : tagsList) {
                if (!t.isBlank()) {
                    Label tLabel = new Label(t.trim());
                    tLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #495057; -fx-background-color: #dee2e6; -fx-padding: 2 6; -fx-background-radius: 12;");
                    tagsBox.getChildren().add(tLabel);
                }
            }
        }
        
        Label content = new Label(text);
        content.setWrapText(true);
        
        card.getChildren().addAll(header, starLabel);
        if (!tagsBox.getChildren().isEmpty()) {
            card.getChildren().add(tagsBox);
        }
        card.getChildren().add(content);
        
        if (reply != null && !reply.isBlank()) {
            VBox replyBox = new VBox(5);
            replyBox.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 10; -fx-background-radius: 8;");
            Label rLabel = new Label("Your Response:");
            rLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #0d6efd;");
            Label rText = new Label(reply);
            rText.setWrapText(true);
            replyBox.getChildren().addAll(rLabel, rText);
            card.getChildren().add(replyBox);
            
            HBox actions = new HBox(10);
            Region r = new Region();
            HBox.setHgrow(r, Priority.ALWAYS);
            Button btnArchive = new Button("Archive");
            btnArchive.getStyleClass().add("link-button");
            btnArchive.setStyle("-fx-text-fill: red;");
            
            btnArchive.setOnAction(e -> {
                feedbackDao.archiveFeedback(id);
                refreshFeedbackList();
            });

            actions.getChildren().addAll(r, btnArchive);
            card.getChildren().add(actions);
        } else {
            HBox actions = new HBox(10);
            
            javafx.scene.control.TextField replyField = new javafx.scene.control.TextField();
            replyField.setPromptText("Type your reply...");
            HBox.setHgrow(replyField, Priority.ALWAYS);

            Button btnReply = new Button("Send Reply");
            btnReply.getStyleClass().add("primary-button");
            btnReply.setStyle("-fx-background-color: #0d6efd;");
            
            btnReply.setOnAction(e -> {
                 String rText = replyField.getText();
                 if (rText != null && !rText.isBlank()) {
                     feedbackDao.replyToFeedback(id, rText);
                     refreshFeedbackList();
                 }
            });
            
            Button btnArchive = new Button("Archive (Spam)");
            btnArchive.getStyleClass().add("secondary-button");
            btnArchive.setStyle("-fx-text-fill: red;");
            btnArchive.setOnAction(e -> {
                feedbackDao.archiveFeedback(id);
                refreshFeedbackList();
            });
            
            actions.getChildren().addAll(replyField, btnReply, btnArchive);
            card.getChildren().add(actions);
        }

        return card;
    }
}
