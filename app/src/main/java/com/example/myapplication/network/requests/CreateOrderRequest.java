package com.example.myapplication.network.requests;

public class CreateOrderRequest {
    private String userId;
    private String shippingAddress;
    private String orderNote;
    private String paymentMethod;

    // Constructors
    public CreateOrderRequest() {}

    public CreateOrderRequest(String userId, String shippingAddress, String orderNote, String paymentMethod) {
        this.userId = userId;
        this.shippingAddress = shippingAddress;
        this.orderNote = orderNote;
        this.paymentMethod = paymentMethod;
    }

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getOrderNote() {
        return orderNote;
    }

    public void setOrderNote(String orderNote) {
        this.orderNote = orderNote;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
} 