package com.example.myapplication.data.models;

import com.example.myapplication.enums.OrderStatus;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Order {
    @SerializedName(value = "id", alternate = "_id")
    private String id;
    private String userId;
    private String orderNumber;
    private List<OrderItem> items;
    private double totalAmount;
    private OrderStatus status;
    private String shippingAddress;
    private String orderNote;
    @SerializedName(value = "orderDate", alternate = {"createdAt", "created_at"})
    private Date orderDate;
    private Payment payment;
    
    // Additional fields from server response
    private String buyer; // Customer email
    private int itemCount; // Number of items from server

    // Constructors
    public Order() {
        this.items = new ArrayList<>();
        this.status = OrderStatus.PENDING;
        this.orderDate = new Date();
        this.orderNumber = generateOrderNumber();
    }

    public Order(String userId) {
        this();
        this.userId = userId;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }

    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) {
        this.items = items;
        calculateTotals();
    }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }

    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }

    public String getOrderNote() { return orderNote; }
    public void setOrderNote(String orderNote) { this.orderNote = orderNote; }

    public Date getOrderDate() { return orderDate; }
    public void setOrderDate(Date orderDate) { this.orderDate = orderDate; }

    public Payment getPayment() { return payment; }
    public void setPayment(Payment payment) { this.payment = payment; }

    public String getBuyer() { return buyer; }
    public void setBuyer(String buyer) { this.buyer = buyer; }

    public int getItemCount() { return itemCount; }
    public void setItemCount(int itemCount) { this.itemCount = itemCount; }

    // Utility methods
    private void calculateTotals() {
        totalAmount = 0.0;
        for (OrderItem item : items) {
            totalAmount += item.getTotalPrice();
        }
    }

    private String generateOrderNumber() {
        return "ORD" + System.currentTimeMillis();
    }

    public int getTotalItems() {
        // Use itemCount from server if available, otherwise calculate from items
        if (itemCount > 0) {
            return itemCount;
        }
        
        int total = 0;
        if (items != null) {
            for (OrderItem item : items) {
                total += item.getQuantity();
            }
        }
        return total;
    }

    public String getFormattedTotal() {
        return String.format("$%.2f", totalAmount);
    }
}