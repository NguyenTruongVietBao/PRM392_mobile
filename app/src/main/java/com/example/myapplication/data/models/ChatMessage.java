package com.example.myapplication.data.models;

public class ChatMessage {
    public static final String ROLE_USER = "user";
    public static final String ROLE_ASSISTANT = "assistant";

    private String id;
    private String role; // "user" or "assistant"
    private String content;
    private String timestamp;
    private boolean isLoading; // For showing loading indicator

    public ChatMessage() {
    }

    public ChatMessage(String role, String content) {
        this.role = role;
        this.content = content;
        this.timestamp = String.valueOf(System.currentTimeMillis());
    }

    public ChatMessage(String id, String role, String content, String timestamp) {
        this.id = id;
        this.role = role;
        this.content = content;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public boolean isLoading() { return isLoading; }
    public void setLoading(boolean loading) { isLoading = loading; }

    // Utility methods
    public boolean isFromUser() {
        return ROLE_USER.equals(role);
    }

    public boolean isFromAssistant() {
        return ROLE_ASSISTANT.equals(role);
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "id='" + id + '\'' +
                ", role='" + role + '\'' +
                ", content='" + content + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", isLoading=" + isLoading +
                '}';
    }
} 