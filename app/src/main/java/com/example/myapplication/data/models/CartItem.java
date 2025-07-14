package com.example.myapplication.data.models;

import com.google.gson.annotations.SerializedName;

public class CartItem {
    private String id;
    private String cartId;
    private Product product;
    private String productId; // Backup field when product is string ID only
    
    private int quantity;
    private double unitPrice;
    private double totalPrice;

    public CartItem() {
        this.quantity = 1;
    }

    public CartItem(Product product, int quantity) {
        this();
        this.product = product;
        this.quantity = quantity;
        this.unitPrice = product.getPrice();
        calculateTotalPrice();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCartId() { return cartId; }
    public void setCartId(String cartId) { this.cartId = cartId; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) {
        this.product = product;
        if (product != null) {
            this.unitPrice = product.getPrice();
            calculateTotalPrice();
        }
    }
    
    public String getProductId() { 
        if (product != null) {
            return product.getId();
        }
        return productId; 
    }
    public void setProductId(String productId) { 
        this.productId = productId; 
    }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
        calculateTotalPrice();
    }

    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
        calculateTotalPrice();
    }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }


    // Utility methods
    private void calculateTotalPrice() {
        this.totalPrice = this.unitPrice * this.quantity;
    }

    public String getFormattedUnitPrice() {
        return String.format("$%.2f", unitPrice);
    }

    public String getFormattedTotalPrice() {
        return String.format("$%.2f", totalPrice);
    }

    @Override
    public String toString() {
        return "CartItem{" +
                "id='" + id + '\'' +
                ", cartId='" + cartId + '\'' +
                ", product=" + (product != null ? product.getName() : "null") +
                ", productId='" + productId + '\'' +
                ", quantity=" + quantity +
                ", unitPrice=" + unitPrice +
                ", totalPrice=" + totalPrice +
                '}';
    }
}
