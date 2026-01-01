package com.javaproject;

import com.javaproject.model.CartLine;
import com.javaproject.model.MenuItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.Optional;

public class AppState {

    private final List<MenuItem> menuItems;
    private final ObservableList<CartLine> cartLines;

    public AppState() {
        this.menuItems = List.of(
                new MenuItem("Burger", "Classic burger with lettuce and tomato.", 8.99),
                new MenuItem("Fries", "Crispy fries with a pinch of salt.", 3.49),
                new MenuItem("Salad", "Fresh greens with house dressing.", 6.25),
                new MenuItem("Soda", "Choice of cola, lemon-lime, or orange.", 1.99)
        );
        this.cartLines = FXCollections.observableArrayList();
    }

    public List<MenuItem> getMenuItems() {
        return menuItems;
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

    public void clearCart() {
        cartLines.clear();
    }

    public double getCartTotal() {
        return cartLines.stream().mapToDouble(CartLine::getLineTotal).sum();
    }
}
