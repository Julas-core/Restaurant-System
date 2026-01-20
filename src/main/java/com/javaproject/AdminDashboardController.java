package com.javaproject;

import com.javaproject.db.OrderDao;
import com.javaproject.db.FeedbackDao;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

import java.util.Map;

public class AdminDashboardController extends AdminBaseController {

    @FXML
    private LineChart<String, Number> revenueChart;

    @FXML
    private VBox popularItemsBox;

    @FXML
    private VBox recentFeedbackBox;
    
    @FXML private Label totalRevenueLabel;
    @FXML private Label totalOrdersLabel;
    @FXML private Label pendingOrdersLabel;
    @FXML private Label avgRatingLabel;

    private OrderDao orderDao;
    private FeedbackDao feedbackDao;

    @FXML
    public void initialize() {
        try {
            orderDao = new OrderDao();
            feedbackDao = new FeedbackDao(); 
            setupStats();
            setupChart();
            setupPopularItems();
            setupRecentFeedback();
        } catch (Throwable e) {
            System.err.println("Failed to initialize Admin Dashboard:");
            e.printStackTrace();
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Dashboard Error");
            alert.setHeaderText("Failed to load dashboard data");
            alert.setContentText(e.getMessage());
            alert.show();
        }
    }

    private void setupChart() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Revenue");
        
        var weeklyData = orderDao.getWeeklyRevenue();
        if (weeklyData.isEmpty()) {
            series.getData().add(new XYChart.Data<>("Today", 0));
        } else {
            for (Map.Entry<String, Double> entry : weeklyData) {
                series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
            }
        }
        
        revenueChart.getData().clear();
        revenueChart.getData().add(series);
    }
    
    private void setupStats() {
        double revenue = orderDao.getTotalRevenue();
        int orders = orderDao.getTotalOrdersCount();
        int pending = orderDao.getOrdersCountByStatus("PENDING");
        double rating = feedbackDao.getAverageRating();
        
        if (totalRevenueLabel != null) totalRevenueLabel.setText(String.format("ETB %.2f", revenue));
        if (totalOrdersLabel != null) totalOrdersLabel.setText(String.valueOf(orders));
        if (pendingOrdersLabel != null) pendingOrdersLabel.setText(String.valueOf(pending));
        if (avgRatingLabel != null) avgRatingLabel.setText(String.format("%.1f", rating));
    }

    private void setupPopularItems() {
        popularItemsBox.getChildren().clear();
        var items = orderDao.getTopSellingItems();
        
        if (items.isEmpty()) {
             Label empty = new Label("No sales data yet.");
             empty.getStyleClass().add("muted");
             popularItemsBox.getChildren().add(empty);
             return;
        }

        for (Map.Entry<String, Integer> item : items) {
            // Price is unknowable here efficiently without another join, 
            // so we'll just show "Hot" or remove the price label for dashboard summary
            popularItemsBox.getChildren().add(
                createItemRow(item.getKey(), item.getValue() + " sold", "HOT")
            );
        }
    }

    private void setupRecentFeedback() {
        recentFeedbackBox.getChildren().clear();
        var feedbacks = feedbackDao.getAllFeedback();
        // Limit to 4 for dashboard
        int limit = Math.min(feedbacks.size(), 4);
        
        if (limit == 0) {
             Label empty = new Label("No feedback yet.");
             empty.getStyleClass().add("muted");
             recentFeedbackBox.getChildren().add(empty);
             return;
        }

        for (int i = 0; i < limit; i++) {
            var f = feedbacks.get(i);
            recentFeedbackBox.getChildren().add(
                createFeedbackRow(f.getUserName(), f.getComment(), f.getRating())
            );
        }
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
