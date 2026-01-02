package com.javaproject.model;

import java.util.Objects;

public class MenuItem {

    private final String name;
    private final String description;
    private final double price;
    private final String category;
    private final String label;
    private final double rating;
    private final boolean chefSpecial;

    public MenuItem(String name, String description, double price, String category, String label, double rating, boolean chefSpecial) {
        this.name = Objects.requireNonNull(name, "name");
        this.description = Objects.requireNonNull(description, "description");
        this.price = price;
        this.category = Objects.requireNonNull(category, "category");
        this.label = Objects.requireNonNull(label, "label");
        this.rating = rating;
        this.chefSpecial = chefSpecial;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public String getCategory() {
        return category;
    }

    public String getLabel() {
        return label;
    }

    public double getRating() {
        return rating;
    }

    public boolean isChefSpecial() {
        return chefSpecial;
    }

    @Override
    public String toString() {
        return name;
    }
}
