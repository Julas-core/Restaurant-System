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

public class AdminUsersController extends AdminBaseController {

    @FXML private TableView<AdminUser> usersTable;
    @FXML private TableColumn<AdminUser, String> colIdentity;
    @FXML private TableColumn<AdminUser, String> colRole;
    @FXML private TableColumn<AdminUser, String> colDiscount;
    @FXML private TableColumn<AdminUser, String> colAction;

    @FXML
    public void initialize() {
        colIdentity.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getIdentity()));
        colRole.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRole()));
        colDiscount.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDiscount()));
        
        colAction.setCellFactory(param -> new TableCell<>() {
            private final Button btnEdit = new Button("Edit");

            {
                btnEdit.getStyleClass().add("secondary-button");
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btnEdit);
                }
            }
        });

        ObservableList<AdminUser> data = FXCollections.observableArrayList(
            new AdminUser("Alice Johnson (alice@example.com)", "Customer", "Student"),
            new AdminUser("Bob Smith (bob.s@restaurant.com)", "Staff", "None"),
            new AdminUser("Charlie Brown (charlie@example.com)", "Customer", "Pending"),
            new AdminUser("Diana Prince (diana@admin.com)", "Admin", "VIP"),
            new AdminUser("Evan Wright (evan@example.com)", "Customer", "None")
        );

        usersTable.setItems(data);
    }

    public static class AdminUser {
        private final String identity;
        private final String role;
        private final String discount;

        public AdminUser(String identity, String role, String discount) {
            this.identity = identity;
            this.role = role;
            this.discount = discount;
        }

        public String getIdentity() { return identity; }
        public String getRole() { return role; }
        public String getDiscount() { return discount; }
    }
}
