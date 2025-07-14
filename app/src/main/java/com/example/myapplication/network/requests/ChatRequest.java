package com.example.myapplication.network.requests;

public class ChatRequest {
    private String message;
    private String userId;

    public ChatRequest() {
    }

    public ChatRequest(String message, String userId) {
        this.message = message;
        this.userId = userId;
    }

    // Getters and Setters
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    @Override
    public String toString() {
        return "ChatRequest{" +
                "message='" + message + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }
} 