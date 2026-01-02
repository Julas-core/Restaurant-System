package com.javaproject;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

public class AdminDashboardController extends AdminBaseController {

    @FXML
    private LineChart<String, Number> revenueChart;

    @FXML
    private VBox popularItemsBox;

    @FXML
    private VBox recentFeedbackBox;

    @FXML
    public void initialize() {
        setupChart();
        setupPopularItems();
        setupRecentFeedback();
    }

    private void setupChart() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Revenue");
        series.getData().add(new XYChart.Data<>("Mon", 1200));
        series.getData().add(new XYChart.Data<>("Tue", 1500));
        series.getData().add(new XYChart.Data<>("Wed", 1100));
        series.getData().add(new XYChart.Data<>("Thu", 1800));
        series.getData().add(new XYChart.Data<>("Fri", 2400));
        series.getData().add(new XYChart.Data<>("Sat", 3000));
        series.getData().add(new XYChart.Data<>("Sun", 2800));
        revenueChart.getData().add(series);
    }

    private void setupPopularItems() {
        popularItemsBox.getChildren().addAll(
            createItemRow("Spicy Chicken Sandwich", "124 orders", "$12.50"),
            createItemRow("Truffle Pasta", "98 orders", "$18.00"),
            createItemRow("Caesar Salad", "85 orders", "$8.00")
        );
    }

    private void setupRecentFeedback() {
        recentFeedbackBox.getChildren().addAll(
            createFeedbackRow("Alice Johnson", "The pasta was absolutely amazing!", 5),
            createFeedbackRow("Mark Smith", "Music was a bit loud.", 3),
            createFeedbackRow("Sarah Lee", "Best cheesecake in town!", 5)
        );
    }

    private HBox createItemRow(String name, String count, String price) {
        HBox row = new HBox(10);
        row.setStyle("-fx-padding: 5;");
        
        Circle img = new Circle(16);
        img.getStyleClass().add("image-placeholder");
        
        VBox info = new VBox(2);
        Label n = new Label(name);
        n.setStyle("-fx-font-weight: bold;");
        Label c = new Label(count);
        c.getStyleClass().add("muted");
        info.getChildren().addAll(n, c);
        
        Region r = new Region();
        HBox.setHgrow(r, Priority.ALWAYS);
        
        Label p = new Label(price);
        p.setStyle("-fx-font-weight: bold;");
        
        row.getChildren().addAll(img, info, r, p);
        return row;
    }

    private HBox createFeedbackRow(String user, String text, int stars) {
        HBox row = new HBox(10);
        row.setStyle("-fx-padding: 5;");
        
        Circle avatar = new Circle(16);
        avatar.getStyleClass().add("image-placeholder");
        
        VBox content = new VBox(2);
        HBox header = new HBox(5);
        Label u = new Label(user);
        u.setStyle("-fx-font-weight: bold;");
        Label s = new Label("★ " + stars);
        s.setStyle("-fx-text-fill: #f05a28; -fx-font-size: 10px;");
        header.getChildren().addAll(u, s);
        
        Label t = new Label(text);
        t.setWrapText(true);
        t.getStyleClass().add("muted");
        
        content.getChildren().addAll(header, t);
        
        row.getChildren().addAll(avatar, content);
        return row;
    }
}
