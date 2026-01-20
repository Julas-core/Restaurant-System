package com.javaproject.model;

import java.sql.Timestamp;
import java.util.List;

public class Order {
    private long id;
    private long userId;
    private Timestamp placedAt;
    private String status;
    private boolean delivery;
    private String promoCode;
    private double subtotal;
    private double tax;
    private double deliveryFee;
    private double discount;
    private double total;
    
    // Optional list of items if we want to fetch details eagerly
    private List<CartLine> items;

    public Order(long id, long userId, Timestamp placedAt, String status, boolean delivery, String promoCode,
                 double subtotal, double tax, double deliveryFee, double discount, double total) {
        this.id = id;
        this.userId = userId;
        this.placedAt = placedAt;
        this.status = status;
        this.delivery = delivery;
        this.promoCode = promoCode;
        this.subtotal = subtotal;
        this.tax = tax;
        this.deliveryFee = deliveryFee;
        this.discount = discount;
        this.total = total;
    }

    public long getId() { return id; }
    public long getUserId() { return userId; }
    public Timestamp getPlacedAt() { return placedAt; }
    public String getStatus() { return status; }
    public boolean isDelivery() { return delivery; }
    public String getPromoCode() { return promoCode; }
    public double getSubtotal() { return subtotal; }
    public double getTax() { return tax; }
    public double getDeliveryFee() { return deliveryFee; }
    public double getDiscount() { return discount; }
    public double getTotal() { return total; }

    public void setItems(List<CartLine> items) { this.items = items; }
    public List<CartLine> getItems() { return items; }
}
