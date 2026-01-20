package com.javaproject;

import com.javaproject.db.ConfigDao;
import com.javaproject.db.UserDao;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.sql.SQLException;

public class AdminSettingsController extends AdminBaseController {

    @FXML private TextField taxRateField;
    @FXML private TextField deliveryFeeField;
    
    @FXML private PasswordField adminPassCurrent;
    @FXML private PasswordField adminPassNew;
    @FXML private PasswordField adminPassConfirm;

    private final ConfigDao configDao = new ConfigDao();

    @FXML
    public void initialize() {
        taxRateField.setText(configDao.getValue("TAX_RATE", "15.0"));
        deliveryFeeField.setText(configDao.getValue("DELIVERY_FEE", "50.0"));
    }

    @FXML
    private void saveSystemSettings() {
        try {
            double tax = Double.parseDouble(taxRateField.getText());
            double del = Double.parseDouble(deliveryFeeField.getText());
            
            configDao.setValue("TAX_RATE", String.valueOf(tax));
            configDao.setValue("DELIVERY_FEE", String.valueOf(del));
            
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "System settings saved.");
            alert.show();
        } catch (NumberFormatException e) {
            showAlert("Error", "Please enter valid numbers.");
        }
    }

    @FXML
    private void changeAdminPassword() {
        String cur = adminPassCurrent.getText();
        String pwd = adminPassNew.getText();
        String cnf = adminPassConfirm.getText();

        if (cur.isBlank() || pwd.isBlank()) {
            showAlert("Error", "Please fill required fields");
            return;
        }
        if (!pwd.equals(cnf)) {
             showAlert("Error", "New passwords do not match");
             return;
        }

        try {
            UserDao dao = new UserDao();
            var user = App.getState().getCurrentUser().get(); // Admin is logged in
            
            if (dao.authenticate(user.getUsername(), cur.toCharArray()).isEmpty()) {
                showAlert("Error", "Current password incorrect");
                return;
            }

            dao.updatePassword(user.getId(), pwd.toCharArray());
            showAlert("Success", "Password updated");
            adminPassCurrent.clear();
            adminPassNew.clear();
            adminPassConfirm.clear();

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Database error");
        }
    }
    
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.show();
    }
}
