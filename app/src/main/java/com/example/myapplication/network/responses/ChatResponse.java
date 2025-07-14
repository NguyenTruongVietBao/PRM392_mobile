package com.example.myapplication.network.responses;

public class ChatResponse {
    private String message;
    private String response;
    private String chatId;

    public ChatResponse() {
    }

    public ChatResponse(String message, String response, String chatId) {
        this.message = message;
        this.response = response;
        this.chatId = chatId;
    }

    // Getters and Setters
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getResponse() { return response; }
    public void setResponse(String response) { this.response = response; }

    public String getChatId() { return chatId; }
    public void setChatId(String chatId) { this.chatId = chatId; }

    @Override
    public String toString() {
        return "ChatResponse{" +
                "message='" + message + '\'' +
                ", response='" + response + '\'' +
                ", chatId='" + chatId + '\'' +
                '}';
    }
} 