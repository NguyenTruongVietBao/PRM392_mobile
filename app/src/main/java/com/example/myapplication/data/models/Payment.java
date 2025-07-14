package com.example.myapplication.data.models;

import com.example.myapplication.enums.PaymentStatus;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Payment {
    @SerializedName(value = "id", alternate = "_id")
    private String id;
    private String orderId;
    private String paymentMethod;
    private double amount;
    private String currency;
    private PaymentStatus status;
    private String transactionId;
    private String gatewayResponse;
    private Date paymentDate;
    private Date createdAt;
    private Date updatedAt;

    // Constructors
    public Payment() {
        this.currency = "USD";
        this.status = PaymentStatus.PENDING;
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    public Payment(String orderId, String paymentMethod, double amount) {
        this();
        this.orderId = orderId;
        this.paymentMethod = paymentMethod;
        this.amount = amount;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public PaymentStatus getStatus() { return status; }
    public void setStatus(PaymentStatus status) { this.status = status; }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public String getGatewayResponse() { return gatewayResponse; }
    public void setGatewayResponse(String gatewayResponse) { this.gatewayResponse = gatewayResponse; }

    public Date getPaymentDate() { return paymentDate; }
    public void setPaymentDate(Date paymentDate) { this.paymentDate = paymentDate; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }

    // Utility methods
    public String getFormattedAmount() {
        return String.format("$%.2f", amount);
    }

    public boolean isSuccessful() {
        return status == PaymentStatus.COMPLETED;
    }
}