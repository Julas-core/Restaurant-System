package com.javaproject;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;

public class ProfileController {

    @FXML private VBox recentOrdersBox;
    @FXML private HBox recommendedBox;

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
    @FXML private Button btnFavorites;
    @FXML private Button btnSettings;

    // Views
    @FXML private VBox viewOverview;
    @FXML private VBox viewOrders;
    @FXML private VBox viewAddresses;
    @FXML private VBox viewFavorites;
    @FXML private VBox viewSettings;
    @FXML private javafx.scene.layout.FlowPane favoritesContainer;
    
    @FXML private PasswordField currentPasswordField;
    @FXML private PasswordField newPasswordField;

    // Editable Fields
    @FXML private TextField addrHome;
    @FXML private TextField addrWork;

    private boolean isEditMode = false;

    @FXML
    public void initialize() {
        refreshOrders();
        refreshRecommendations();
        refreshProfileUI();
    }
    
    // Refresh recommended items based on simple random selection for now
    private void refreshRecommendations() {
        if (recommendedBox == null) return;
        recommendedBox.getChildren().clear();
        
        try {
            com.javaproject.db.MenuItemDao dao = new com.javaproject.db.MenuItemDao();
            List<com.javaproject.model.MenuItem> items = dao.listActive();
            if (items.isEmpty()) {
                Label l = new Label("No recommendations available.");
                l.getStyleClass().add("muted");
                recommendedBox.getChildren().add(l);
                return;
            }
            
            // Shuffle to simulate "recommendation engine"
            Collections.shuffle(items);
            
            // Pick top 3
            int count = 0;
            for (com.javaproject.model.MenuItem item : items) {
                if (count >= 3) break;
                recommendedBox.getChildren().add(createRecommendationCard(item));
                count++;
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
    }

    private Node createRecommendationCard(com.javaproject.model.MenuItem item) {
        VBox card = new VBox(8);
        card.setPrefWidth(220);
        card.getStyleClass().add("card");

        // Image container with clipping
        StackPane imageContainer = new StackPane();
        imageContainer.setPrefSize(190, 90);
        
        Rectangle clip = new Rectangle(190, 90);
        clip.setArcWidth(14);
        clip.setArcHeight(14);
        imageContainer.setClip(clip);

        ImageView imageView = new ImageView();
        imageView.setFitWidth(190);
        imageView.setFitHeight(90);
        imageView.setPreserveRatio(false); // Fill the box

        try {
            boolean loaded = false;
            String path = item.getImagePath();
            if (path != null && !path.isBlank()) {
                // Remove potential quotes
                path = path.replace("\"", "");

                // Try absolute/relative file path first
                File f = new File(path);
                if (f.exists()) {
                    imageView.setImage(new Image(f.toURI().toString()));
                    loaded = true;
                } 
                // Try classpath resource
                else {
                    var resource = getClass().getResource(path);
                    if (resource != null) {
                        imageView.setImage(new Image(resource.toExternalForm()));
                        loaded = true;
                    }
                    else if (!path.startsWith("/")) {
                         resource = getClass().getResource("/" + path);
                         if (resource != null) {
                             imageView.setImage(new Image(resource.toExternalForm()));
                             loaded = true;
                         }
                    }
                }
            }

            if (!loaded) {
                // Fallback placeholder color
                Rectangle placeholder = new Rectangle(190, 90);
                placeholder.getStyleClass().add("card-image"); // Use CSS style for color
                imageContainer.getChildren().add(placeholder);
            } else {
                imageContainer.getChildren().add(imageView);
            }
        } catch (Exception e) {
             // Fallback on error
             Rectangle placeholder = new Rectangle(190, 90);
             placeholder.getStyleClass().add("card-image");
             imageContainer.getChildren().add(placeholder);
        }

        Label name = new Label(item.getName());
        name.getStyleClass().add("card-title");
        name.setWrapText(true);

        Label price = new Label("ETB " + item.getPrice());
        price.getStyleClass().add("menu-card-price");

        Button action = new Button("View Details");
        action.getStyleClass().add("link-button");
        action.setOnAction(e -> {
            try {
                // Set the selected item in state and navigate to the meal detail page
                App.getState().setSelectedMenuItem(item);
                App.setRoot("meal");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        // Make the whole card clickable as well
        card.setOnMouseClicked(e -> {
            try {
                App.getState().setSelectedMenuItem(item);
                App.setRoot("meal");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        card.setStyle("-fx-cursor: hand;");

        card.getChildren().addAll(imageContainer, name, price, action);
        return card;
    }

    private void refreshOrders() {
        if (recentOrdersBox == null) return;
        recentOrdersBox.getChildren().clear();
        var userOpt = App.getState().getCurrentUser();
        if (userOpt.isPresent()) {
            com.javaproject.db.OrderDao orderDao = new com.javaproject.db.OrderDao();
            // Assuming we have getOrdersByUser in OrderDao
            var orders = orderDao.getOrdersByUser(userOpt.get().getId());
            
            if (orders.isEmpty()) {
                Label empty = new Label("No recent orders.");
                empty.getStyleClass().add("muted");
                recentOrdersBox.getChildren().add(empty);
            } else {
                for (com.javaproject.model.Order o : orders) {
                    var items = orderDao.getOrderItems(o.getId());
                    String title = items.isEmpty() ? "Order #" + o.getId() : items.get(0).getItem().getName();
                    if (items.size() > 1) {
                         title += " + " + (items.size() - 1) + " more";
                    }
                    
                    String itemCountStr = items.stream().mapToInt(com.javaproject.model.CartLine::getQuantity).sum() + " Items";
                    recentOrdersBox.getChildren().add(
                        orderRow(title, o.getStatus(), "ETB " + o.getTotal(), itemCountStr)
                    );
                }
            }
        } else {
            recentOrdersBox.getChildren().add(new Label("Sign in to see orders"));
        }
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
    private void goAdmin() {
        try {
            if (!App.getState().isAdmin()) {
                showAlert("Access Denied", "Admin access required.");
                return;
            }

            App.setRoot("admin_dashboard");
        } catch (Exception e) {
            e.printStackTrace();
            Throwable cause = e.getCause();
            String msg = (cause != null) ? cause.toString() : e.getMessage();
            showAlert("Error", "Failed to open Admin Dashboard: " + msg);
        }
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
        refreshOrderHistory();
    }

    @FXML
    private void showAddresses() {
        switchView(viewAddresses, btnAddresses);
    }

    @FXML
    private void showFavorites() {
        switchView(viewFavorites, btnFavorites);
        refreshFavorites();
    }

    @FXML
    private void showSettings() {
        switchView(viewSettings, btnSettings);
    }

    private void switchView(VBox targetView, Button activeBtn) {
        // Hide all views
        viewOverview.setVisible(false);
        viewOverview.setManaged(false);
        viewOrders.setVisible(false);
        viewOrders.setManaged(false);
        viewAddresses.setVisible(false);
        viewAddresses.setManaged(false);
        viewFavorites.setVisible(false);
        viewFavorites.setManaged(false);
        if (viewSettings != null) {
            viewSettings.setVisible(false);
            viewSettings.setManaged(false);
        }

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
        btnFavorites.getStyleClass().remove("side-active");
        btnSettings.getStyleClass().remove("side-active");
    }

    private void refreshOrderHistory() {
        viewOrders.getChildren().clear();
        Label title = new Label("Order History");
        title.getStyleClass().add("section-title");
        Label sub = new Label("View all your past orders here.");
        sub.getStyleClass().add("muted");
        viewOrders.getChildren().addAll(title, sub);

        var userOpt = App.getState().getCurrentUser();
        if (userOpt.isPresent()) {
            com.javaproject.db.OrderDao orderDao = new com.javaproject.db.OrderDao();
            var orders = orderDao.getOrdersByUser(userOpt.get().getId());
            
            if (orders.isEmpty()) {
                Label empty = new Label("No past orders found.");
                empty.getStyleClass().add("muted");
                viewOrders.getChildren().add(empty);
            } else {
                for (com.javaproject.model.Order o : orders) {
                    var items = orderDao.getOrderItems(o.getId());
                    String titleStr = items.isEmpty() ? "Order #" + o.getId() : items.get(0).getItem().getName();
                    if (items.size() > 1) {
                         titleStr += " + " + (items.size() - 1) + " more";
                    }
                    
                    String itemCountStr = items.stream().mapToInt(com.javaproject.model.CartLine::getQuantity).sum() + " Items";
                    
                    VBox card = new VBox(8);
                    card.getStyleClass().add("card");
                    
                    HBox top = new HBox();
                    Label t = new Label(titleStr);
                    t.getStyleClass().add("card-title");
                    Region sp = new Region();
                    HBox.setHgrow(sp, Priority.ALWAYS);
                    Label d = new Label(o.getPlacedAt().toString());
                    d.getStyleClass().add("muted");
                    top.getChildren().addAll(t, sp, d);
                    
                    HBox mid = new HBox(12);
                    Label st = new Label(o.getStatus());
                    st.getStyleClass().add("status");
                    Label tot = new Label("ETB " + o.getTotal());
                    tot.getStyleClass().add("menu-card-price");
                    mid.getChildren().addAll(st, tot);
                    
                    Label cnt = new Label(itemCountStr);
                    cnt.getStyleClass().add("muted");
                    
                    card.getChildren().addAll(top, mid, cnt);
                    viewOrders.getChildren().add(card);
                }
            }
        } else {
             viewOrders.getChildren().add(new Label("Sign in to view orders"));
        }
    }

    private void refreshFavorites() {
        if (favoritesContainer == null) return;
        favoritesContainer.getChildren().clear();
        
        var userOpt = App.getState().getCurrentUser();
        if (userOpt.isPresent()) {
            try {
                com.javaproject.db.FavoriteDao dao = new com.javaproject.db.FavoriteDao();
                List<com.javaproject.model.MenuItem> items = dao.getFavorites(userOpt.get().getId());
                if (items.isEmpty()) {
                    Label l = new Label("No favorites saved yet.");
                    l.getStyleClass().add("muted");
                    favoritesContainer.getChildren().add(l);
                } else {
                    for (com.javaproject.model.MenuItem item : items) {
                        favoritesContainer.getChildren().add(createRecommendationCard(item));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            favoritesContainer.getChildren().add(new Label("Sign in to view favorites."));
        }
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

    @FXML
    private void updatePassword() {
        var userOpt = App.getState().getCurrentUser();
        if (userOpt.isEmpty()) {
            showAlert("Error", "You must be logged in to change your password.");
            return;
        }

        String currentPass = currentPasswordField.getText();
        String newPass = newPasswordField.getText();

        if (currentPass == null || currentPass.isEmpty() || newPass == null || newPass.isEmpty()) {
            showAlert("Error", "Please fill in all password fields.");
            return;
        }

        com.javaproject.db.UserDao dao = new com.javaproject.db.UserDao();
        // Verify current password first (simple re-login check)
        try {
            var user = dao.findUserRecordByUsername(userOpt.get().getUsername());
            if (user.isPresent()) {
                 // Check via PasswordHasher using the salt from the DB user
                 com.javaproject.db.UserDao.UserRecord rec = user.get();
                 com.javaproject.db.PasswordHasher.PasswordHash stored = 
                    new com.javaproject.db.PasswordHasher.PasswordHash(rec.salt(), rec.hash(), rec.iterations());
                 
                 boolean valid = com.javaproject.db.PasswordHasher.verify(
                     currentPass.toCharArray(), 
                     stored
                 );
                 
                 if (!valid) {
                     showAlert("Error", "Incorrect current password.");
                     return;
                 }
                 
                 // Update
                 dao.updatePassword(rec.id(), newPass.toCharArray());
                 showAlert("Success", "Password updated successfully.");
                 currentPasswordField.clear();
                 newPasswordField.clear();
             
            } else {
                 showAlert("Error", "User record not found.");
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Database access error: " + e.getMessage());
        }
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
