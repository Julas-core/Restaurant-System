package com.javaproject;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class ProfileController {

    @FXML private VBox recentOrdersBox;

    // Profile header
    @FXML private Label profileNameLabel;
    @FXML private Label profileEmailLabel;
    @FXML private Label greetingLabel;
    @FXML private Button adminPanelButton;

    @FXML private Button editProfileButton;

    // Auth actions
    @FXML private HBox signedOutActions;
    @FXML private Button logoutButton;

    // Profile form
    @FXML private VBox signedOutCard;
    @FXML private VBox personalInfoCard;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;


    // Sidebar Buttons
    @FXML private Button btnOverview;
    @FXML private Button btnOrders;
    @FXML private Button btnAddresses;
    @FXML private Button btnPayment;
    @FXML private Button btnFavorites;

    // Views
    @FXML private VBox viewOverview;
    @FXML private VBox viewOrders;
    @FXML private VBox viewAddresses;
    @FXML private VBox viewPayment;
    @FXML private VBox viewFavorites;

    // Editable Fields
    @FXML private TextField addrHome;
    @FXML private TextField addrWork;

    private boolean isEditMode = false;

    @FXML
    public void initialize() {
        // Static sample data to match the UI design.
        recentOrdersBox.getChildren().setAll(
                orderRow("Spicy Ramen Bowl + 2 Sides", "Preparing", "$24.50", "2 Items"),
                orderRow("Classic Cheeseburger Meal", "Delivered", "$18.20", "1 Item"),
                orderRow("Sushi Platter Deluxe", "Delivered", "$45.00", "3 Items"),
                orderRow("Veggie Pizza (Large)", "Cancelled", "$0.00", "Refunded")
        );

        refreshProfileUI();
    }

    @FXML
    private void goHome() throws IOException {
        App.setRoot("primary");
    }

    @FXML
    private void goMenu() throws IOException {
        App.setRoot("primary");
    }

    @FXML
    private void goCheckout() throws IOException {
        App.setRoot("checkout");
    }

    @FXML
    private void goAbout() throws IOException {
        App.setRoot("about");
    }

    @FXML
    private void goContact() throws IOException {
        App.setRoot("contact");
    }

    @FXML
    private void goLocation() throws IOException {
        App.setRoot("location");
    }

    @FXML
    private void goAdmin() throws IOException {
        if (!App.getState().isAdmin()) {
            showAlert("Access Denied", "Admin access required.");
            return;
        }

        App.setRoot("admin_dashboard");
    }

    @FXML
    private void goLogin() throws IOException {
        App.setRoot("login");
    }

    @FXML
    private void goRegister() throws IOException {
        App.setRoot("register");
    }

    @FXML
    private void logout() {
        App.getState().logout();
        isEditMode = false;
        refreshProfileUI();
    }

    private void refreshProfileUI() {
        boolean signedIn = App.getState().isAuthenticated();

        if (signedOutActions != null) {
            signedOutActions.setVisible(!signedIn);
            signedOutActions.setManaged(!signedIn);
        }
        if (logoutButton != null) {
            logoutButton.setVisible(signedIn);
            logoutButton.setManaged(signedIn);
        }
        if (editProfileButton != null) {
            editProfileButton.setDisable(!signedIn);
            editProfileButton.setText(isEditMode ? "Save Changes" : "Edit Profile");
        }

        if (!signedIn) {
            if (profileNameLabel != null) {
                profileNameLabel.setText("Guest");
            }
            if (profileEmailLabel != null) {
                profileEmailLabel.setText("Not signed in");
            }
            if (greetingLabel != null) {
                greetingLabel.setText("Hello!");
            }

            if (signedOutCard != null) {
                signedOutCard.setVisible(true);
                signedOutCard.setManaged(true);
            }
            if (personalInfoCard != null) {
                personalInfoCard.setVisible(false);
                personalInfoCard.setManaged(false);
            }

            setEditable(false);
            clearProfileFields();
        } else {
            ProfileData profile = App.getState().getCurrentProfile().orElse(new ProfileData());
            String displayName = (profile.getFirstName() == null ? "" : profile.getFirstName().trim());
            if (displayName.isBlank()) {
                displayName = App.getState().getCurrentUser().map(u -> u.getUsername()).orElse("User");
            }

            if (profileNameLabel != null) {
                profileNameLabel.setText(displayName);
            }
            if (profileEmailLabel != null) {
                profileEmailLabel.setText(profile.getEmail() == null ? "" : profile.getEmail());
            }
            if (greetingLabel != null) {
                greetingLabel.setText("Hello, " + displayName + "!");
            }

            if (signedOutCard != null) {
                signedOutCard.setVisible(false);
                signedOutCard.setManaged(false);
            }
            if (personalInfoCard != null) {
                personalInfoCard.setVisible(true);
                personalInfoCard.setManaged(true);
            }

            loadProfileFields(profile);
            setEditable(isEditMode);
        }

        boolean admin = App.getState().isAdmin();
        if (adminPanelButton != null) {
            adminPanelButton.setVisible(admin);
            adminPanelButton.setManaged(admin);
        }
    }

    private void loadProfileFields(ProfileData profile) {
        if (firstNameField != null) {
            firstNameField.setText(profile.getFirstName() == null ? "" : profile.getFirstName());
        }
        if (lastNameField != null) {
            lastNameField.setText(profile.getLastName() == null ? "" : profile.getLastName());
        }
        if (emailField != null) {
            emailField.setText(profile.getEmail() == null ? "" : profile.getEmail());
        }
        if (phoneField != null) {
            phoneField.setText(profile.getPhone() == null ? "" : profile.getPhone());
        }

        if (addrHome != null) {
            addrHome.setText(profile.getAddrHome() == null ? "" : profile.getAddrHome());
        }
        if (addrWork != null) {
            addrWork.setText(profile.getAddrWork() == null ? "" : profile.getAddrWork());
        }
    }

    private void clearProfileFields() {
        if (firstNameField != null) firstNameField.clear();
        if (lastNameField != null) lastNameField.clear();
        if (emailField != null) emailField.clear();
        if (phoneField != null) phoneField.clear();
        if (addrHome != null) addrHome.clear();
        if (addrWork != null) addrWork.clear();
    }

    private void setEditable(boolean editable) {
        if (firstNameField != null) firstNameField.setEditable(editable);
        if (lastNameField != null) lastNameField.setEditable(editable);
        if (emailField != null) emailField.setEditable(editable);
        if (phoneField != null) phoneField.setEditable(editable);
        if (addrHome != null) addrHome.setEditable(editable);
        if (addrWork != null) addrWork.setEditable(editable);
    }

    // Tab Switching Logic
    @FXML
    private void showOverview() {
        switchView(viewOverview, btnOverview);
    }

    @FXML
    private void showOrders() {
        switchView(viewOrders, btnOrders);
    }

    @FXML
    private void showAddresses() {
        switchView(viewAddresses, btnAddresses);
    }

    @FXML
    private void showPayment() {
        switchView(viewPayment, btnPayment);
    }

    @FXML
    private void showFavorites() {
        switchView(viewFavorites, btnFavorites);
    }

    private void switchView(VBox targetView, Button activeBtn) {
        // Hide all views
        viewOverview.setVisible(false);
        viewOverview.setManaged(false);
        viewOrders.setVisible(false);
        viewOrders.setManaged(false);
        viewAddresses.setVisible(false);
        viewAddresses.setManaged(false);
        viewPayment.setVisible(false);
        viewPayment.setManaged(false);
        viewFavorites.setVisible(false);
        viewFavorites.setManaged(false);

        // Show target view
        targetView.setVisible(true);
        targetView.setManaged(true);

        // Update button styles
        resetButtonStyles();
        activeBtn.getStyleClass().add("side-active");
    }

    private void resetButtonStyles() {
        btnOverview.getStyleClass().remove("side-active");
        btnOrders.getStyleClass().remove("side-active");
        btnAddresses.getStyleClass().remove("side-active");
        btnPayment.getStyleClass().remove("side-active");
        btnFavorites.getStyleClass().remove("side-active");
    }

    @FXML
    private void toggleEditProfile() {
        if (!App.getState().isAuthenticated()) {
            showAlert("Sign in required", "Please sign in to edit your profile.");
            return;
        }

        isEditMode = !isEditMode;

        if (!isEditMode) {
            // Save
            ProfileData profile = App.getState().getCurrentProfile().orElseThrow();
            profile.setFirstName(firstNameField == null ? "" : firstNameField.getText());
            profile.setLastName(lastNameField == null ? "" : lastNameField.getText());
            profile.setEmail(emailField == null ? "" : emailField.getText());
            profile.setPhone(phoneField == null ? "" : phoneField.getText());
            profile.setAddrHome(addrHome == null ? "" : addrHome.getText());
            profile.setAddrWork(addrWork == null ? "" : addrWork.getText());
            App.getState().saveCurrentProfile(profile);
            showAlert("Saved", "Your profile changes have been saved.");
        }

        refreshProfileUI();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private Node orderRow(String title, String status, String amount, String meta) {
        HBox row = new HBox(12);
        row.getStyleClass().add("order-list-row");

        VBox left = new VBox(4);
        Label t = new Label(title);
        t.getStyleClass().add("order-title");
        Label m = new Label(meta);
        m.getStyleClass().add("muted");
        left.getChildren().addAll(t, m);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        VBox right = new VBox(4);
        Label s = new Label(status);
        s.getStyleClass().add("status");
        Label a = new Label(amount);
        a.getStyleClass().add("muted");
        right.getChildren().addAll(s, a);

        row.getChildren().addAll(left, spacer, right);
        return row;
    }
}
