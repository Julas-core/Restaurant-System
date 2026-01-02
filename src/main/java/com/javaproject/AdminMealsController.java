package com.javaproject;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;

public class AdminMealsController extends AdminBaseController {

    @FXML private TableView<AdminMeal> mealsTable;
    @FXML private TableColumn<AdminMeal, String> colName;
    @FXML private TableColumn<AdminMeal, String> colCategory;
    @FXML private TableColumn<AdminMeal, String> colPrice;
    @FXML private TableColumn<AdminMeal, String> colStatus;
    @FXML private TableColumn<AdminMeal, String> colAction;

    @FXML
    public void initialize() {
        colName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        colCategory.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCategory()));
        colPrice.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPrice()));
        colStatus.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus()));
        
        colAction.setCellFactory(param -> new TableCell<>() {
            private final Button btnEdit = new Button("✎");
            private final Button btnDelete = new Button("🗑");
            private final HBox pane = new HBox(5, btnEdit, btnDelete);

            {
                btnEdit.getStyleClass().add("secondary-button");
                btnDelete.getStyleClass().add("secondary-button");
                btnDelete.setStyle("-fx-text-fill: red;");
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(pane);
                }
            }
        });

        ObservableList<AdminMeal> data = FXCollections.observableArrayList(
            new AdminMeal("Spicy Chicken Sandwich", "Main Course", "$12.50", "Active"),
            new AdminMeal("Caesar Salad", "Appetizer", "$8.00", "Active"),
            new AdminMeal("Truffle Pasta", "Main Course", "$18.00", "Sold Out"),
            new AdminMeal("Iced Lemon Tea", "Beverage", "$4.50", "Active"),
            new AdminMeal("Chocolate Lava Cake", "Dessert", "$9.00", "Active")
        );

        mealsTable.setItems(data);
    }

    public static class AdminMeal {
        private final String name;
        private final String category;
        private final String price;
        private final String status;

        public AdminMeal(String name, String category, String price, String status) {
            this.name = name;
            this.category = category;
            this.price = price;
            this.status = status;
        }

        public String getName() { return name; }
        public String getCategory() { return category; }
        public String getPrice() { return price; }
        public String getStatus() { return status; }
    }
}
