package com.example.myapplication.data.models;

import com.google.gson.annotations.SerializedName;

public class Chat {
    @SerializedName(value = "id", alternate = "_id")
    private String id;
    
    @SerializedName("user")
    private String userId;
    
    private String message;
    private String response;
    
    @SerializedName("createdAt")
    private String createdAt;
    
    @SerializedName("updatedAt") 
    private String updatedAt;

    public Chat() {
    }

    public Chat(String userId, String message) {
        this.userId = userId;
        this.message = message;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getResponse() { return response; }
    public void setResponse(String response) { this.response = response; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "Chat{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", message='" + message + '\'' +
                ", response='" + response + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                '}';
    }
} 