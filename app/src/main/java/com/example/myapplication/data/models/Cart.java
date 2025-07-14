package com.example.myapplication.data.models;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

public class Cart {
    @SerializedName(value = "id", alternate = "_id")
    private String id;
    private String userId;
    private List<CartItem> items;
    private double totalPrice;
    private int totalItems;

    public Cart() {
        this.items = new ArrayList<>();
        this.totalPrice = 0.0;
        this.totalItems = 0;
    }

    public Cart(String userId) {
        this();
        this.userId = userId;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public List<CartItem> getItems() { return items; }
    public void setItems(List<CartItem> items) {
        this.items = items;
        calculateTotals();
    }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

    public int getTotalItems() { return totalItems; }
    public void setTotalItems(int totalItems) { this.totalItems = totalItems; }


    // Utility methods
    public void addItem(CartItem item) {
        // Check if product already exists in cart
        for (CartItem existingItem : items) {
            if (existingItem.getProduct().getId().equals(item.getProduct().getId())) {
                existingItem.setQuantity(existingItem.getQuantity() + item.getQuantity());
                calculateTotals();
                return;
            }
        }
        items.add(item);
        calculateTotals();
    }

    public void removeItem(String itemId) {
        items.removeIf(item -> item.getId().equals(itemId));
        calculateTotals();
    }

    public void updateItemQuantity(String itemId, int quantity) {
        if (quantity <= 0) {
            removeItem(itemId);
            return;
        }

        for (CartItem item : items) {
            if (item.getId().equals(itemId)) {
                item.setQuantity(quantity);
                break;
            }
        }
        calculateTotals();
    }

    public void clearCart() {
        items.clear();
        calculateTotals();
    }

    private void calculateTotals() {
        totalPrice = 0.0;
        totalItems = 0;
        for (CartItem item : items) {
            totalPrice += item.getTotalPrice();
            totalItems += item.getQuantity();
        }
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public String getFormattedTotal() {
        return String.format("$%.2f", totalPrice);
    }
}