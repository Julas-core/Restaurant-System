package com.javaproject;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

public class AdminFeedbackController extends AdminBaseController {

    @FXML
    private VBox reviewsList;

    @FXML
    public void initialize() {
        reviewsList.getChildren().addAll(
            createReviewCard("Alice Johnson", "Oct 24, 2023", 5, "The pasta was absolutely amazing! However, the service was a bit slow during the rush hour. Would love to come back for the truffle risotto next time.", "PENDING", null),
            createReviewCard("Mark Smith", "Oct 23, 2023", 3, "Decent food but the music was way too loud. Hard to have a conversation.", "REPLIED", "Hi Mark, thanks for the feedback. We'll adjust the volume levels for dinner service. Hope to see you again!"),
            createReviewCard("Sarah Lee", "Oct 22, 2023", 5, "Best cheesecake in town! No question.", "PENDING", null)
        );
    }

    private VBox createReviewCard(String name, String date, int stars, String text, String status, String reply) {
        VBox card = new VBox(10);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(15));

        HBox header = new HBox(15);
        
        Circle avatar = new Circle(24);
        avatar.getStyleClass().add("image-placeholder");
        
        VBox meta = new VBox(2);
        Label n = new Label(name);
        n.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        Label d = new Label(date);
        d.getStyleClass().add("muted");
        
        Label statusLabel = new Label(status);
        statusLabel.getStyleClass().add("badge");
        if ("PENDING".equals(status)) {
            statusLabel.getStyleClass().add("badge-warning");
        } else {
            statusLabel.getStyleClass().add("badge-success");
        }
        
        meta.getChildren().addAll(n, d, statusLabel);
        
        header.getChildren().addAll(avatar, meta);
        
        // Stars
        StringBuilder starStr = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            starStr.append(i < stars ? "★" : "☆");
        }
        Label starLabel = new Label(starStr.toString());
        starLabel.setStyle("-fx-text-fill: #ffc107; -fx-font-size: 16px;");
        
        Label content = new Label(text);
        content.setWrapText(true);
        
        card.getChildren().addAll(header, starLabel, content);
        
        if (reply != null) {
            VBox replyBox = new VBox(5);
            replyBox.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 10; -fx-background-radius: 8;");
            Label rLabel = new Label("Your Response:");
            rLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #0d6efd;");
            Label rText = new Label(reply);
            rText.setWrapText(true);
            replyBox.getChildren().addAll(rLabel, rText);
            card.getChildren().add(replyBox);
            
            HBox actions = new HBox(10);
            Button btnEdit = new Button("✎ Edit Reply");
            btnEdit.getStyleClass().add("link-button");
            Region r = new Region();
            HBox.setHgrow(r, Priority.ALWAYS);
            Button btnArchive = new Button("Archive");
            btnArchive.getStyleClass().add("link-button");
            btnArchive.setStyle("-fx-text-fill: red;");
            actions.getChildren().addAll(btnEdit, r, btnArchive);
            card.getChildren().add(actions);
        } else {
            HBox actions = new HBox(10);
            Button btnReply = new Button("Reply");
            btnReply.getStyleClass().add("primary-button");
            btnReply.setStyle("-fx-background-color: #0d6efd;");
            
            Button btnHandled = new Button("✔ Mark Handled");
            btnHandled.getStyleClass().add("secondary-button");
            
            actions.getChildren().addAll(btnReply, btnHandled);
            card.getChildren().add(actions);
        }

        return card;
    }
}
