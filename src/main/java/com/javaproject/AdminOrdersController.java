package com.javaproject;

import com.javaproject.db.OrderDao;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import javafx.scene.control.Label;

public class AdminOrdersController extends AdminBaseController {

    @FXML private TableView<AdminOrder> ordersTable;
    @FXML private TableColumn<AdminOrder, String> colId;
    @FXML private TableColumn<AdminOrder, String> colCustomer;
    @FXML private TableColumn<AdminOrder, String> colItems;
    @FXML private TableColumn<AdminOrder, String> colTotal;
    @FXML private TableColumn<AdminOrder, String> colStatus;
    @FXML private TableColumn<AdminOrder, String> colAction;

    @FXML private TextField searchField;
    @FXML private ComboBox<String> statusFilterBox;
    
    @FXML private Label pendingOrdersLabel;
    @FXML private Label inKitchenLabel;
    @FXML private Label readyLabel;
    @FXML private Label revenueTodayLabel;

    @FXML
    public void initialize() {
        statusFilterBox.setItems(FXCollections.observableArrayList(
            "ALL", "PLACED", "PREPARING", "DELIVERED", "COMPLETED", "CANCELLED"
        ));
        statusFilterBox.setValue("ALL");

        colId.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getId()));
        colCustomer.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCustomer()));
        colItems.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getItems()));
        colTotal.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTotal()));
        colStatus.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus()));
        
        // Custom cell for actions
        colAction.setCellFactory(param -> new TableCell<>() {
            private final Button btnAction = new Button();
            private final Button btnDetails = new Button("ℹ");
            private final javafx.scene.layout.HBox pane = new javafx.scene.layout.HBox(5, btnAction, btnDetails);
            
            {
                btnDetails.getStyleClass().add("secondary-button");
                btnDetails.setOnAction(event -> {
                    AdminOrder order = getTableView().getItems().get(getIndex());
                    showOrderDetails(order);
                });

                btnAction.getStyleClass().add("primary-button");
                btnAction.setOnAction(event -> {
                    AdminOrder order = getTableView().getItems().get(getIndex());
                    handleAction(order);
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    AdminOrder order = getTableView().getItems().get(getIndex());
                    String status = order.getStatus();
                    String action = getNextAction(status);
                    
                    if (action == null) {
                        btnAction.setVisible(false);
                        btnAction.setManaged(false);
                    } else {
                        btnAction.setText(action);
                        btnAction.setVisible(true);
                        btnAction.setManaged(true);
                    }
                    setGraphic(pane);
                }
            }
        });

        loadOrders();
    }
    
    // New Method for showing details
    private void showOrderDetails(AdminOrder orderSummary) {
        long orderId = Long.parseLong(orderSummary.getId().replace("#", ""));
        
        try {
            var dao = new OrderDao();
            // We need a way to get robust details including customer phone/address if possible
            // But currently the data model only loosely links profile via User ID.
            // Let's create a custom dialog.
            
            var items = dao.getOrderItems(orderId);
            
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Order Details - " + orderSummary.getId());
            alert.setHeaderText("Customer: " + orderSummary.getCustomer() + "\nStatus: " + orderSummary.getStatus());
            
            StringBuilder content = new StringBuilder();
            content.append("ITEMS:\n");
            for (var line : items) {
                content.append(String.format("• %dx %s (ETB %.2f)\n", line.getQuantity(), line.getItem().getName(), line.getItem().getPrice()));
            }
            content.append("\nTotal: ").append(orderSummary.getTotal());
            
            // If checking delivery, we'd ideally fetch profile address here
            // For now, let's keep it simple as requested
            
            javafx.scene.control.TextArea area = new javafx.scene.control.TextArea(content.toString());
            area.setEditable(false);
            area.setWrapText(true);
            
            area.setMaxWidth(Double.MAX_VALUE);
            area.setMaxHeight(Double.MAX_VALUE);
            javafx.scene.layout.GridPane.setVgrow(area, javafx.scene.layout.Priority.ALWAYS);
            javafx.scene.layout.GridPane.setHgrow(area, javafx.scene.layout.Priority.ALWAYS);

            javafx.scene.layout.GridPane expContent = new javafx.scene.layout.GridPane();
            expContent.setMaxWidth(Double.MAX_VALUE);
            expContent.add(area, 0, 1);

            alert.getDialogPane().setExpandableContent(expContent);
            alert.getDialogPane().setExpanded(true);
            alert.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleSearch() {
        loadOrders();
    }

    @FXML
    private void handleFilter() {
        loadOrders();
    }

    @FXML
    private void resetFilters() {
        searchField.clear();
        statusFilterBox.setValue("ALL");
        loadOrders();
        updateStats();
    }
    
    private void updateStats() {
        if (pendingOrdersLabel == null) return;
        try {
            OrderDao dao = new OrderDao();
            pendingOrdersLabel.setText(String.valueOf(dao.getOrdersCountByStatus("PLACED")));
            inKitchenLabel.setText(String.valueOf(dao.getOrdersCountByStatus("PREPARING")));
            readyLabel.setText(String.valueOf(dao.getOrdersCountByStatus("DELIVERED")));
            revenueTodayLabel.setText(String.format("ETB %.2f", dao.getRevenueToday()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadOrders() {
        ObservableList<AdminOrder> data = FXCollections.observableArrayList();
        String query = searchField == null ? "" : searchField.getText();
        String status = statusFilterBox == null ? "ALL" : statusFilterBox.getValue();
        
        try {
            for (OrderDao.OrderSummary o : new OrderDao().searchOrders(query, status)) {
                data.add(new AdminOrder(
                        "#" + o.id(),
                        o.displayCustomer(),
                        o.items() == null ? "" : o.items(),
                        o.total() == null ? "ETB 0.00" : String.format("ETB %.2f", o.total().doubleValue()),
                        o.status()
                ));
            }
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
        ordersTable.setItems(data);
    }
    
    private String getNextAction(String status) {
        if ("PLACED".equalsIgnoreCase(status)) return "Start Prep";
        if ("PREPARING".equalsIgnoreCase(status)) return "Deliver";
        if ("DELIVERED".equalsIgnoreCase(status)) return "Complete";
        return null;
    }
    
    private void handleAction(AdminOrder order) {
        String status = order.getStatus();
        String nextStatus = null;
        
        if ("PLACED".equalsIgnoreCase(status)) nextStatus = "PREPARING";
        else if ("PREPARING".equalsIgnoreCase(status)) nextStatus = "DELIVERED";
        else if ("DELIVERED".equalsIgnoreCase(status)) nextStatus = "COMPLETED";
        
        if (nextStatus != null) {
            try {
                // Parse ID from "#123"
                long id = Long.parseLong(order.getId().replace("#", ""));
                new OrderDao().updateStatus(id, nextStatus);
                loadOrders(); // Refresh
                updateStats(); 
            } catch (Exception e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to update order");
                alert.show();
            }
        }
    }

    public static class AdminOrder {
        private final String id;
        private final String customer;
        private final String items;
        private final String total;
        private final String status;

        public AdminOrder(String id, String customer, String items, String total, String status) {
            this.id = id;
            this.customer = customer;
            this.items = items;
            this.total = total;
            this.status = status;
        }

        public String getId() { return id; }
        public String getCustomer() { return customer; }
        public String getItems() { return items; }
        public String getTotal() { return total; }
        public String getStatus() { return status; }
    }
}
