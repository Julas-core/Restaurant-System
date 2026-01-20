package com.javaproject;

import com.javaproject.db.MenuItemDao;
import com.javaproject.model.MenuItem;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

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
               
                btnEdit.setOnAction(e -> {
                    AdminMeal am = getTableView().getItems().get(getIndex());
                    openEditMealDialog(am.getOriginalItem());
                });
                
                btnDelete.setOnAction(e -> {
                    AdminMeal am = getTableView().getItems().get(getIndex());
                    deleteMeal(am.getOriginalItem());
                });
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

        loadMeals();
    }

    private void loadMeals() {
        if (App.getState().getMenuItems() == null) return;
        
        ObservableList<AdminMeal> data = FXCollections.observableArrayList();
        for (MenuItem item : App.getState().getMenuItems()) {
            data.add(new AdminMeal(item));
        }
        mealsTable.setItems(data);
    }
    
    private void deleteMeal(MenuItem item) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Delete " + item.getName() + "?", ButtonType.YES, ButtonType.NO);
        alert.setTitle("Confirm Delete");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.YES) {
            try {
                MenuItemDao dao = new MenuItemDao();
                dao.deleteByName(item.getName());
                // Refresh AppState
                App.getState().setMenuItems(dao.listActive());
                loadMeals();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void openAddMealDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("admin_meal_add.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Add New Meal");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            
            // Refresh table after dialog closes
            loadMeals();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void openEditMealDialog(MenuItem item) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("admin_meal_add.fxml"));
            Parent root = loader.load();
            AdminAddMealController controller = loader.getController();
            controller.setEditingMeal(item);
            
            Stage stage = new Stage();
            stage.setTitle("Edit Meal");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            
            // Refresh table after dialog closes
            loadMeals();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class AdminMeal {
        private final MenuItem originalItem;
        
        public AdminMeal(MenuItem item) {
            this.originalItem = item;
        }

        public String getName() { return originalItem.getName(); }
        public String getCategory() { return originalItem.getCategory(); }
        public String getPrice() { return String.format("ETB %.2f", originalItem.getPrice()); }
        public String getStatus() { return "Active"; }
        public MenuItem getOriginalItem() { return originalItem; }
    }
}
