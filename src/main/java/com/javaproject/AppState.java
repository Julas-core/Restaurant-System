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
                new MenuItem("Buddha Bowl", "Quinoa, roasted chickpeas, avocado, kale, and tahini dressing.", 14.50, "Starters", "Popular", 4.9, true),
                new MenuItem("Inferno Burger", "Double beef patty, jalapeños, pepper jack cheese, and spicy aioli.", 16.00, "Mains", "Spicy", 4.7, true),
                new MenuItem("Green Goddess", "Mixed greens, cucumber, edamame, avocado, and green goddess dressing.", 13.50, "Starters", "Vegan", 4.8, true),
                new MenuItem("Tropical Mojito", "Rum, fresh mint, lime juice, soda water, and passion fruit syrup.", 10.00, "Drinks", "Vegan", 5.0, true),

                // Main Courses (second section)
                new MenuItem("Ribeye Steak", "12oz grass-fed ribeye, garlic butter, served with roasted potatoes.", 32.00, "Mains", "Popular", 4.9, false),
                new MenuItem("Grilled Salmon", "Fresh Atlantic salmon, lemon herb glaze, wild rice pilaf.", 26.50, "Mains", "Gluten-Free", 4.8, false),
                new MenuItem("Mushroom Risotto", "Arborio rice, wild mushrooms, parmesan cheese, truffle oil.", 21.00, "Mains", "Vegetarian", 4.7, false)
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
        return delivery ? 3.99 : 0.00;
    }

    public double getTax() {
        // Keep it beginner-friendly: flat 8% sales tax on subtotal.
        return getSubtotal() * 0.08;
    }

    public double getDiscountAmount() {
        if (promoCode == null) {
            return 0.0;
        }

        String code = promoCode.trim().toUpperCase();
        if (code.isBlank()) {
            return 0.0;
        }

        // Screenshot-style promo: 10% off.
        if (code.equals("STUDENT20")) {
            return getSubtotal() * 0.10;
        }

        return 0.0;
    }

    public double getTotal() {
        double total = getSubtotal() + getDeliveryFee() + getTax() - getDiscountAmount();
        return Math.max(0.0, total);
    }
}
