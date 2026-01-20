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
    private final String imagePath;
    private Long id;

    public MenuItem(String name, String description, double price, String category, String label, double rating, boolean chefSpecial, String imagePath) {
        this.name = Objects.requireNonNull(name, "name");
        this.description = Objects.requireNonNull(description, "description");
        this.price = price;
        this.category = Objects.requireNonNull(category, "category");
        this.label = Objects.requireNonNull(label, "label");
        this.rating = rating;
        this.chefSpecial = chefSpecial;
        this.imagePath = imagePath == null ? "" : imagePath;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
    
    // Constructor for backward compatibility if needed, but better to update all usages
    public MenuItem(String name, String description, double price, String category, String label, double rating, boolean chefSpecial) {
        this(name, description, price, category, label, rating, chefSpecial, "");
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

    public String getImagePath() {
        return imagePath;
    }

    @Override
    public String toString() {
        return name;
    }
}
