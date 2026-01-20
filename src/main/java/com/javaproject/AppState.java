package com.javaproject;

import com.javaproject.auth.Role;
import com.javaproject.auth.User;
import com.javaproject.db.ProfileDao;
import com.javaproject.model.CartLine;
import com.javaproject.model.MenuItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.Optional;

public class AppState {

    private List<MenuItem> menuItems;
    private final ObservableList<CartLine> cartLines;
    private MenuItem selectedMenuItem;

    private boolean delivery = true;
    private String promoCode;

    private User currentUser;

    private ProfileData currentProfile;

    private final ProfileDao profileDao = new ProfileDao();

    public AppState() {
        this.menuItems = List.of(
                // Chef's Specials (top row)
                new MenuItem("Doro Wat", "Spicy chicken stew with hard-boiled eggs, served with injera.", 850.00, "Mains", "Special", 4.9, true, "images/Doro_Wot_1768745545495.jpg"),
                new MenuItem("Kitfo", "Minced raw beef marinated in mitmita and niter kibbeh.", 900.00, "Mains", "Popular", 4.8, true, "images/Kitfo_1768808301122.jpg"),
                new MenuItem("Vegan Platter", "Assortment of lentil and vegetable stews served on injera.", 600.00, "Mains", "Vegan", 4.9, true, "images/Beyaynetu_1768808217373.jpg"),
                new MenuItem("Tej", "Traditional Ethiopian honey wine.", 300.00, "Drinks", "Special", 4.7, true, "images/Birzi_1768805797011.jpg"),

                // Main Courses (second section)
                new MenuItem("Tibs", "Sautéed beef strips with vegetables and rosemary.", 750.00, "Mains", "Popular", 4.8, false, "images/Shekla_Tibs_1768807889729.jpg"),
                new MenuItem("Shiro Tegamino", "Chickpea flour stew served in a clay pot.", 450.00, "Mains", "Vegetarian", 4.6, false, "images/Shiro_Tegamino_1768807333084.jpg"),
                new MenuItem("Ethiopian Coffee", "Freshly brewed traditional coffee.", 80.00, "Drinks", "Popular", 5.0, false, "images/Traditional_Coffee_1768805842468.jpg")
        );
        this.cartLines = FXCollections.observableArrayList();
    }

    public void setMenuItems(List<MenuItem> menuItems) {
        if (menuItems == null || menuItems.isEmpty()) {
            return;
        }
        this.menuItems = menuItems;
    }

    public Optional<User> getCurrentUser() {
        return Optional.ofNullable(currentUser);
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
        this.currentProfile = null;
        if (currentUser != null) {
            refreshProfileFromDb();
        }
    }

    public void logout() {
        this.currentUser = null;
        this.currentProfile = null;
    }

    public boolean isAuthenticated() {
        return currentUser != null;
    }

    public boolean isAdmin() {
        return currentUser != null && currentUser.getRole() == Role.ADMIN;
    }

    public Optional<ProfileData> getCurrentProfile() {
        if (currentUser == null) {
            return Optional.empty();
        }
        if (currentProfile == null) {
            refreshProfileFromDb();
        }
        return Optional.ofNullable(currentProfile);
    }

    public void saveCurrentProfile(ProfileData profile) {
        if (currentUser == null || profile == null) {
            return;
        }
        try {
            profileDao.upsertByUsername(currentUser.getUsername(), profile);
            currentProfile = profile;
        } catch (Exception ignored) {
            // If DB save fails, keep in-memory value so the UI reflects the user's edits.
            currentProfile = profile;
        }
    }

    private void refreshProfileFromDb() {
        if (currentUser == null) {
            return;
        }
        try {
            currentProfile = profileDao.findByUsername(currentUser.getUsername()).orElse(null);
        } catch (Exception ignored) {
            currentProfile = null;
        }
    }

    public List<MenuItem> getMenuItems() {
        return menuItems;
    }

    public MenuItem getSelectedMenuItem() {
        return selectedMenuItem;
    }

    public void setSelectedMenuItem(MenuItem selectedMenuItem) {
        this.selectedMenuItem = selectedMenuItem;
    }

    public boolean isDelivery() {
        return delivery;
    }

    public void setDelivery(boolean delivery) {
        this.delivery = delivery;
    }

    public String getPromoCode() {
        return promoCode;
    }

    public void setPromoCode(String promoCode) {
        this.promoCode = promoCode;
    }

    public ObservableList<CartLine> getCartLines() {
        return cartLines;
    }

    public void addToCart(MenuItem item) {
        Optional<CartLine> existing = cartLines.stream()
                .filter(line -> line.getItem().getName().equals(item.getName()))
                .findFirst();

        if (existing.isPresent()) {
            CartLine line = existing.get();
            line.setQuantity(line.getQuantity() + 1);
            // Force refresh for ListView display
            cartLines.set(cartLines.indexOf(line), line);
        } else {
            cartLines.add(new CartLine(item, 1));
        }
    }

    public void removeOneFromCart(CartLine line) {
        if (line == null) {
            return;
        }

        if (line.getQuantity() <= 1) {
            cartLines.remove(line);
        } else {
            line.setQuantity(line.getQuantity() - 1);
            cartLines.set(cartLines.indexOf(line), line);
        }
    }

    public void removeLine(CartLine line) {
        if (line == null) {
            return;
        }
        cartLines.remove(line);
    }

    public void clearCart() {
        cartLines.clear();
    }

    public double getCartTotal() {
        return getTotal();
    }

    public double getSubtotal() {
        return cartLines.stream().mapToDouble(CartLine::getLineTotal).sum();
    }

    public double getDeliveryFee() {
        if (!delivery) return 0.0;
        return new com.javaproject.db.ConfigDao().getDouble("DELIVERY_FEE", 50.0);
    }

    public double getTax() {
        double rate = new com.javaproject.db.ConfigDao().getDouble("TAX_RATE", 15.0);
        return getSubtotal() * (rate / 100.0);
    }

    public double getDiscountAmount() {
        if (promoCode == null) {
            return 0.0;
        }
        // Discount 10% on "WELCOME10", else 0. 
        // We will move this into DB validation in Checkout Controller, 
        // but for app state calculation we need to know the percent.
        // Let's store discountPercent in AppState instead of checking code string every time.
        return getSubtotal() * (discountPercent / 100.0);
    }
    
    private double discountPercent = 0.0;

    public void setDiscountPercent(double percent) {
        this.discountPercent = percent;
    }

    public double getTotal() {
        double total = getSubtotal() + getDeliveryFee() + getTax() - getDiscountAmount();
        return Math.max(0.0, total);
    }
}
