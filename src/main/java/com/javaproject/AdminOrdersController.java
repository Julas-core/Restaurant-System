package com.javaproject;

import com.javaproject.db.OrderDao;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class AdminOrdersController extends AdminBaseController {

    @FXML private TableView<AdminOrder> ordersTable;
    @FXML private TableColumn<AdminOrder, String> colId;
    @FXML private TableColumn<AdminOrder, String> colCustomer;
    @FXML private TableColumn<AdminOrder, String> colItems;
    @FXML private TableColumn<AdminOrder, String> colTotal;
    @FXML private TableColumn<AdminOrder, String> colStatus;
    @FXML private TableColumn<AdminOrder, String> colAction;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getId()));
        colCustomer.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCustomer()));
        colItems.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getItems()));
        colTotal.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTotal()));
        colStatus.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus()));
        
        // Custom cell for actions
        colAction.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("Action");

            {
                btn.getStyleClass().add("secondary-button");
                btn.setOnAction(event -> {
                    AdminOrder order = getTableView().getItems().get(getIndex());
                    System.out.println("Action on " + order.getId());
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btn);
                }
            }
        });

        ObservableList<AdminOrder> data = FXCollections.observableArrayList();
        try {
            for (OrderDao.OrderSummary o : new OrderDao().listRecent(50)) {
                data.add(new AdminOrder(
                        "#" + o.id(),
                        o.displayCustomer(),
                        o.items() == null ? "" : o.items(),
                        o.total() == null ? "$0.00" : String.format("$%.2f", o.total().doubleValue()),
                        "PLACED"
                ));
            }
        } catch (Exception ignored) {
            // If DB isn't reachable, keep the table empty.
        }

        ordersTable.setItems(data);
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
