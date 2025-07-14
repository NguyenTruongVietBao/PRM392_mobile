package com.example.myapplication.ui.viewmodel.customer;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myapplication.data.models.ApiResponse;
import com.example.myapplication.data.models.ChatMessage;
import com.example.myapplication.data.repositories.ChatRepository;
import com.example.myapplication.network.responses.ChatResponse;
import com.example.myapplication.network.responses.ChatHistoryResponse;

import java.util.ArrayList;
import java.util.List;

public class ChatViewModel extends ViewModel {
    private ChatRepository chatRepository;
    
    // UI State
    private MutableLiveData<List<ChatMessage>> chatMessages = new MutableLiveData<>(new ArrayList<>());
    private MutableLiveData<Boolean> isMessageSending = new MutableLiveData<>(false);
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public ChatViewModel() {
        chatRepository = new ChatRepository();
        observeRepository();
    }

    // Getters for UI
    public LiveData<List<ChatMessage>> getChatMessages() { return chatMessages; }
    public LiveData<Boolean> getIsLoading() { return chatRepository.getIsLoading(); }
    public LiveData<Boolean> getIsMessageSending() { return isMessageSending; }
    public LiveData<String> getErrorMessage() { return errorMessage; }

    // Actions
    public void loadChatHistory(String userId) {
        android.util.Log.d("ChatViewModel", "Loading chat history for user: " + userId);
        chatRepository.getChatHistory(userId);
    }

    public void sendMessage(String userId, String message) {
        if (message == null || message.trim().isEmpty()) {
            errorMessage.setValue("Tin nhắn không được để trống");
            return;
        }

        android.util.Log.d("ChatViewModel", "Sending message: " + message);
        
        // Add user message to UI immediately
        List<ChatMessage> currentMessages = chatMessages.getValue();
        if (currentMessages == null) currentMessages = new ArrayList<>();
        
        ChatMessage userMessage = new ChatMessage(ChatMessage.ROLE_USER, message.trim());
        currentMessages.add(userMessage);
        chatMessages.setValue(new ArrayList<>(currentMessages));
        
        // Add loading message for assistant response
        ChatMessage loadingMessage = new ChatMessage(ChatMessage.ROLE_ASSISTANT, "Đang suy nghĩ...");
        loadingMessage.setLoading(true);
        currentMessages.add(loadingMessage);
        chatMessages.setValue(new ArrayList<>(currentMessages));
        
        isMessageSending.setValue(true);
        chatRepository.sendMessage(userId, message);
    }

    public void clearError() {
        errorMessage.setValue(null);
    }

    public void clearMessages() {
        chatMessages.setValue(new ArrayList<>());
    }

    private void observeRepository() {
        // Observe send message result
        chatRepository.getSendMessageResult().observeForever(result -> {
            isMessageSending.setValue(false);
            
            if (result != null) {
                List<ChatMessage> currentMessages = chatMessages.getValue();
                if (currentMessages == null) currentMessages = new ArrayList<>();
                
                // Remove loading message (last message should be loading)
                if (!currentMessages.isEmpty() && 
                    currentMessages.get(currentMessages.size() - 1).isLoading()) {
                    currentMessages.remove(currentMessages.size() - 1);
                }
                
                if (result.isSuccess() && result.getData() != null) {
                    // Add AI response
                    ChatResponse chatResponse = result.getData();
                    ChatMessage assistantMessage = new ChatMessage(
                        ChatMessage.ROLE_ASSISTANT, 
                        chatResponse.getResponse()
                    );
                    currentMessages.add(assistantMessage);
                    
                    android.util.Log.d("ChatViewModel", "AI response added: " + chatResponse.getResponse());
                } else {
                    // Add error message
                    ChatMessage errorMsg = new ChatMessage(
                        ChatMessage.ROLE_ASSISTANT, 
                        "Xin lỗi, tôi không thể phản hồi ngay bây giờ. Vui lòng thử lại sau."
                    );
                    currentMessages.add(errorMsg);
                    
                    errorMessage.setValue(result.getMessage());
                    android.util.Log.e("ChatViewModel", "Send message failed: " + result.getMessage());
                }
                
                chatMessages.setValue(new ArrayList<>(currentMessages));
            }
        });

        // Observe chat history result
        chatRepository.getChatHistoryResult().observeForever(result -> {
            if (result != null) {
                if (result.isSuccess() && result.getData() != null) {
                    ChatHistoryResponse historyResponse = result.getData();
                    List<ChatMessage> history = historyResponse.getChatHistory();
                    
                    if (history != null && !history.isEmpty()) {
                        chatMessages.setValue(new ArrayList<>(history));
                        android.util.Log.d("ChatViewModel", "Chat history loaded: " + history.size() + " messages");
                    } else {
                        chatMessages.setValue(new ArrayList<>());
                        android.util.Log.d("ChatViewModel", "No chat history found");
                    }
                } else {
                    errorMessage.setValue(result.getMessage());
                    android.util.Log.e("ChatViewModel", "Load chat history failed: " + result.getMessage());
                }
            }
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        // Clean up if needed
    }
} 