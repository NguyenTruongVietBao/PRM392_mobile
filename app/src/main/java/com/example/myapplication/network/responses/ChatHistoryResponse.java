package com.example.myapplication.network.responses;

import com.example.myapplication.data.models.ChatMessage;
import java.util.List;

public class ChatHistoryResponse {
    private List<ChatMessage> chatHistory;
    private int totalMessages;
    private int totalConversations;

    public ChatHistoryResponse() {
    }

    public ChatHistoryResponse(List<ChatMessage> chatHistory, int totalMessages, int totalConversations) {
        this.chatHistory = chatHistory;
        this.totalMessages = totalMessages;
        this.totalConversations = totalConversations;
    }

    // Getters and Setters
    public List<ChatMessage> getChatHistory() { return chatHistory; }
    public void setChatHistory(List<ChatMessage> chatHistory) { this.chatHistory = chatHistory; }

    public int getTotalMessages() { return totalMessages; }
    public void setTotalMessages(int totalMessages) { this.totalMessages = totalMessages; }

    public int getTotalConversations() { return totalConversations; }
    public void setTotalConversations(int totalConversations) { this.totalConversations = totalConversations; }

    @Override
    public String toString() {
        return "ChatHistoryResponse{" +
                "chatHistory=" + (chatHistory != null ? chatHistory.size() + " messages" : "null") +
                ", totalMessages=" + totalMessages +
                ", totalConversations=" + totalConversations +
                '}';
    }
} 