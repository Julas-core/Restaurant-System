package com.javaproject.model;

import java.util.Objects;

public class CartLine {

    private final MenuItem item;
    private int quantity;

    public CartLine(MenuItem item, int quantity) {
        this.item = Objects.requireNonNull(item, "item");
        this.quantity = quantity;
    }

    public MenuItem getItem() {
        return item;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getLineTotal() {
        return item.getPrice() * quantity;
    }

    @Override
    public String toString() {
        return String.format("%s x%d (ETB %.2f)", item.getName(), quantity, getLineTotal());
    }
}
