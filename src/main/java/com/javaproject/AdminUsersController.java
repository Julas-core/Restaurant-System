package com.javaproject;

import com.javaproject.auth.Role;
import com.javaproject.db.ProfileDao;
import com.javaproject.db.UserDao;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class AdminUsersController extends AdminBaseController {

    @FXML private TableView<AdminUser> usersTable;
    @FXML private TableColumn<AdminUser, String> colIdentity;
    @FXML private TableColumn<AdminUser, String> colRole;
    @FXML private TableColumn<AdminUser, String> colDiscount;
    @FXML private TableColumn<AdminUser, String> colAction;
    
    @FXML private Label totalUsersLabel;

    private final UserDao userDao = new UserDao();
    private final ProfileDao profileDao = new ProfileDao();

    @FXML
    public void initialize() {
        colIdentity.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getIdentity()));
        colRole.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRole()));
        colDiscount.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDiscount()));
        
        colAction.setCellFactory(param -> new TableCell<>() {
            private final Button btnEdit = new Button("Edit Role");

            {
                btnEdit.getStyleClass().add("secondary-button");
                btnEdit.setOnAction(event -> {
                    AdminUser u = getTableView().getItems().get(getIndex());
                    handleEditRole(u);
                });
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

        loadUsers();
        if (totalUsersLabel != null) {
            totalUsersLabel.setText(String.valueOf(userDao.getTotalUsers()));
        }
    }

    private void loadUsers() {
        try {
            List<UserDao.UserRecord> records = userDao.getAllUsers();
            ObservableList<AdminUser> data = FXCollections.observableArrayList();
            
            for (UserDao.UserRecord r : records) {
                String identity = r.username();
                // append email if available
                Optional<ProfileData> p = profileDao.findByUsername(r.username());
                if (p.isPresent() && !p.get().getEmail().isBlank()) {
                    identity += " (" + p.get().getEmail() + ")";
                }

                data.add(new AdminUser(r.id(), identity, r.role().name(), "None"));
            }
            usersTable.setItems(data);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void handleEditRole(AdminUser user) {
        ChoiceDialog<Role> dialog = new ChoiceDialog<>(Role.valueOf(user.getRole()), Role.values());
        dialog.setTitle("Edit Role");
        dialog.setHeaderText("Change role for " + user.getIdentity());
        dialog.setContentText("Select new role:");

        Optional<Role> result = dialog.showAndWait();
        result.ifPresent(newRole -> {
            try {
                userDao.updateUserRole(user.getId(), newRole);
                loadUsers(); // Refresh
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Role updated successfully!");
                alert.show();
            } catch (SQLException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to update role.");
                alert.show();
            }
        });
    }

    public static class AdminUser {
        private final long id;
        private final String identity;
        private final String role;
        private final String discount;

        public AdminUser(long id, String identity, String role, String discount) {
            this.id = id;
            this.identity = identity;
            this.role = role;
            this.discount = discount;
        }

        public long getId() { return id; }
        public String getIdentity() { return identity; }
        public String getRole() { return role; }
        public String getDiscount() { return discount; }
    }
}
