package com.javaproject;

import com.javaproject.db.OrderDao;
import com.javaproject.model.CartLine;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

import java.io.IOException;

public class CheckoutController {

    @FXML
    private Label cartCountLabel;

    @FXML
    private ToggleButton deliveryToggle;

    @FXML
    private ToggleButton pickupToggle;

    @FXML
    private VBox orderItemsBox;

    @FXML
    private TextField promoField;

    @FXML
    private Label subtotalLabel;

    @FXML
    private Label deliveryFeeLabel;

    @FXML
    private Label taxLabel;

    @FXML
    private Label discountLabel;

    @FXML
    private Label totalLabel;

    @FXML
    private Button placeOrderButton;

    private final ToggleGroup deliveryGroup = new ToggleGroup();
    private final ToggleGroup payGroup = new ToggleGroup();

    @FXML
    private RadioButton payPaypal;

    @FXML
    private RadioButton payCash;

    @FXML
    private VBox cashMessage;

    @FXML
    private VBox telebirrContainer;

    @FXML
    private TextField telebirrMobileField;

    @FXML
    private VBox addressContainer;

    @FXML
    private VBox pickupContainer;

    @FXML
    private StackPane mapContainer;

    @FXML
    public void initialize() {
        deliveryToggle.setToggleGroup(deliveryGroup);
        pickupToggle.setToggleGroup(deliveryGroup);

        payPaypal.setToggleGroup(payGroup);
        payCash.setToggleGroup(payGroup);
        payPaypal.setSelected(true);

        if (App.getState().isDelivery()) {
            deliveryToggle.setSelected(true);
        } else {
            pickupToggle.setSelected(true);
        }

        promoField.setText(App.getState().getPromoCode() == null ? "" : App.getState().getPromoCode());

        App.getState().getCartLines().addListener((javafx.collections.ListChangeListener<CartLine>) change -> {
            refreshOrderList();
            refreshTotals();
        });

        deliveryGroup.selectedToggleProperty().addListener((obs, o, n) -> {
            boolean isDelivery = deliveryToggle.isSelected();
            App.getState().setDelivery(isDelivery);
            
            // Toggle visibility of address/map vs pickup info
            if (addressContainer != null) {
                addressContainer.setVisible(isDelivery);
                addressContainer.setManaged(isDelivery);
            }
            if (mapContainer != null) {
                mapContainer.setVisible(isDelivery);
                mapContainer.setManaged(isDelivery);
            }
            if (pickupContainer != null) {
                pickupContainer.setVisible(!isDelivery);
                pickupContainer.setManaged(!isDelivery);
            }

            refreshTotals();
        });

        payGroup.selectedToggleProperty().addListener((obs, o, n) -> {
            boolean isCash = payCash.isSelected();
            boolean isTelebirr = payPaypal.isSelected();

            if (cashMessage != null) {
                cashMessage.setVisible(isCash);
                cashMessage.setManaged(isCash);
            }
            if (telebirrContainer != null) {
                telebirrContainer.setVisible(isTelebirr);
                telebirrContainer.setManaged(isTelebirr);
            }
        });
        
        // Initial state update
        boolean isCash = payCash.isSelected();
        boolean isTelebirr = payPaypal.isSelected();
        if (cashMessage != null) {
            cashMessage.setVisible(isCash);
            cashMessage.setManaged(isCash);
        }
        if (telebirrContainer != null) {
            telebirrContainer.setVisible(isTelebirr);
            telebirrContainer.setManaged(isTelebirr);
        }

        refreshOrderList();
        refreshTotals();
    }

    @FXML
    private void requestTelebirrUSSD() {
        String mobile = telebirrMobileField.getText();
        if (mobile == null || mobile.isBlank()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please enter your Telebirr mobile number.");
            alert.show();
            return;
        }
        if (!mobile.matches("\\d{9}")) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please enter a valid 9-digit mobile number (e.g. 911223344).");
            alert.show();
            return;
        }

        // Simulate USSD Push
        Alert loading = new Alert(Alert.AlertType.INFORMATION);
        loading.setTitle("Telebirr Payment");
        loading.setHeaderText("USSD Push Sent");
        loading.setContentText("Please check your phone ("+mobile+") to enter your PIN and confirm the transaction...");
        loading.showAndWait();
        
        // In a real app, we'd poll a backend status here. 
        // For simulation, we assume success after they close the alert.
    }

    @FXML
    private void goBack() throws IOException {
        App.setRoot("primary");
    }

    @FXML
    private void goMenu() throws IOException {
        App.setRoot("primary");
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
    private void goToProfile() throws IOException {
        App.setRoot(App.getState().isAuthenticated() ? "profile" : "login");
    }

    @FXML
    private void applyPromo() {
        String code = promoField.getText();
        if (code == null || code.isBlank()) {
             return;
        }
        
        // Validate against DB
        var promoDao = new com.javaproject.db.PromoDao();
        var discountOpt = promoDao.getDiscountPercent(code);
        
        if (discountOpt.isPresent()) {
            App.getState().setPromoCode(code);
            App.getState().setDiscountPercent(discountOpt.get());
            
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Promo applied: " + discountOpt.get() + "% Off");
            alert.show();
        } else {
            App.getState().setPromoCode(null);
            App.getState().setDiscountPercent(0.0);
            Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid Promo Code");
            alert.show();
        }
        
        refreshTotals();
    }

    @FXML
    private void placeOrder() throws IOException {
        if (App.getState().getCartLines().isEmpty()) {
            return;
        }

        // Persist to DB first; clear cart only after successful insert.
        double subtotal = App.getState().getSubtotal();
        double tax = App.getState().getTax();
        double deliveryFee = App.getState().getDeliveryFee();
        double discount = App.getState().getDiscountAmount();
        double total = App.getState().getTotal();

        long orderId;
        try {
            orderId = invokeCreateOrder(
                    new OrderDao(),
                    App.getState().getCurrentUser(),
                    java.util.List.copyOf(App.getState().getCartLines()),
                    App.getState().isDelivery(),
                    App.getState().getPromoCode(),
                    subtotal,
                    tax,
                    deliveryFee,
                    discount,
                    total
            );
        } catch (Exception ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Order Not Saved");
            alert.setHeaderText("Could not save your order to the database");
            alert.setContentText("Please check your Postgres connection settings (DB_URL / DB_USER / DB_PASSWORD) and try again.");
            alert.showAndWait();
            return;
        }

        App.getState().clearCart();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Order Placed");
        alert.setHeaderText("Thanks for your order!");
        alert.setContentText(String.format("Order #%d placed. Your total was ETB %.2f", orderId, total));
        alert.showAndWait();

        App.setRoot("profile");
    }

    private long invokeCreateOrder(
            OrderDao dao,
            java.util.Optional<com.javaproject.auth.User> currentUser,
            java.util.List<CartLine> cartLines,
            boolean isDelivery,
            String promoCode,
            double subtotal,
            double tax,
            double deliveryFee,
            double discount,
            double total
    ) throws Exception {
        return dao.createOrder(
                currentUser,
                cartLines,
                isDelivery,
                promoCode,
                subtotal,
                tax,
                deliveryFee,
                discount,
                total
        );
    }

    private void refreshOrderList() {
        orderItemsBox.getChildren().clear();

        for (CartLine line : App.getState().getCartLines()) {
            orderItemsBox.getChildren().add(createOrderRow(line));
        }
    }

    private Node createOrderRow(CartLine line) {
        HBox row = new HBox(12);
        row.getStyleClass().add("order-row");
        row.setPadding(new Insets(10));

        StackPane thumb = new StackPane();
        Rectangle rect = new Rectangle(44, 44);
        rect.setArcWidth(12);
        rect.setArcHeight(12);
        rect.getStyleClass().add("order-thumb");
        thumb.getChildren().add(rect);

        VBox texts = new VBox(4);
        Label name = new Label(line.getItem().getName());
        name.getStyleClass().add("order-item-title");
        Label subtitle = new Label("Customizations applied");
        subtitle.getStyleClass().add("muted");
        Label price = new Label(String.format("ETB %.2f", line.getItem().getPrice()));
        price.getStyleClass().add("order-item-price");
        texts.getChildren().addAll(name, subtitle, price);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button minus = new Button("−");
        minus.getStyleClass().add("qty-mini-btn");
        minus.setOnAction(e -> App.getState().removeOneFromCart(line));

        Label qty = new Label(String.valueOf(line.getQuantity()));
        qty.getStyleClass().add("qty-mini");

        Button plus = new Button("+");
        plus.getStyleClass().add("qty-mini-btn");
        plus.setOnAction(e -> App.getState().addToCart(line.getItem()));

        HBox stepper = new HBox(6, minus, qty, plus);
        stepper.getStyleClass().add("qty-mini-stepper");

        row.getChildren().addAll(thumb, texts, spacer, stepper);
        return row;
    }

    private void refreshTotals() {
        int count = App.getState().getCartLines().stream().mapToInt(CartLine::getQuantity).sum();
        cartCountLabel.setText(String.valueOf(count));

        subtotalLabel.setText(String.format("ETB %.2f", App.getState().getSubtotal()));
        deliveryFeeLabel.setText(String.format("ETB %.2f", App.getState().getDeliveryFee()));
        taxLabel.setText(String.format("ETB %.2f", App.getState().getTax()));

        double discount = App.getState().getDiscountAmount();
        discountLabel.setText(discount <= 0.0 ? "ETB 0.00" : String.format("-ETB %.2f", discount));

        totalLabel.setText(String.format("ETB %.2f", App.getState().getTotal()));

        placeOrderButton.setDisable(App.getState().getCartLines().isEmpty());
    }
}
