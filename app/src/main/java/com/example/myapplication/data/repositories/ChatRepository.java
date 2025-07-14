package com.example.myapplication.data.repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import retrofit2.Call;

import com.example.myapplication.data.models.ApiResponse;
import com.example.myapplication.network.ApiClient;
import com.example.myapplication.network.ApiService;
import com.example.myapplication.network.ApiUtils;
import com.example.myapplication.network.requests.ChatRequest;
import com.example.myapplication.network.responses.ChatResponse;
import com.example.myapplication.network.responses.ChatHistoryResponse;

public class ChatRepository {
    private ApiService apiService;
    
    private MutableLiveData<ApiResponse<ChatResponse>> sendMessageResult = new MutableLiveData<>();
    private MutableLiveData<ApiResponse<ChatHistoryResponse>> chatHistoryResult = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    public ChatRepository() {
        apiService = ApiClient.getApiService();
    }

    public LiveData<ApiResponse<ChatResponse>> getSendMessageResult() {
        return sendMessageResult; 
    }
    
    public LiveData<ApiResponse<ChatHistoryResponse>> getChatHistoryResult() { 
        return chatHistoryResult; 
    }
    
    public LiveData<Boolean> getIsLoading() { 
        return isLoading; 
    }

    public void sendMessage(String userId, String message) {
        android.util.Log.d("ChatRepository", "Sending message - userId: " + userId + ", message: " + message);
        
        if (userId == null || userId.trim().isEmpty()) {
            android.util.Log.e("ChatRepository", "Invalid userId: " + userId);
            ApiResponse<ChatResponse> errorResponse = new ApiResponse<>();
            errorResponse.setSuccess(false);
            errorResponse.setMessage("User ID không hợp lệ");
            sendMessageResult.setValue(errorResponse);
            return;
        }
        
        if (message == null || message.trim().isEmpty()) {
            android.util.Log.e("ChatRepository", "Invalid message: " + message);
            ApiResponse<ChatResponse> errorResponse = new ApiResponse<>();
            errorResponse.setSuccess(false);
            errorResponse.setMessage("Message không được để trống");
            sendMessageResult.setValue(errorResponse);
            return;
        }

        ChatRequest request = new ChatRequest(message.trim(), userId);
        Call<ApiResponse<ChatResponse>> call = apiService.sendMessage(request);
        
        ApiUtils.executeCall(call, isLoading, sendMessageResult, "Không thể gửi tin nhắn",
            response -> android.util.Log.d("ChatRepository", "Message sent successfully: " +
                       (response.getData() != null ? response.getData().toString() : "null")),
            error -> android.util.Log.e("ChatRepository", "Failed to send message: " +
                    error.getMessage() + ", statusCode: " + error.getStatusCode())
        );
    }

    public void getChatHistory(String userId) {
        android.util.Log.d("ChatRepository", "Loading chat history for userId: " + userId);
        
        if (userId == null || userId.trim().isEmpty()) {
            android.util.Log.e("ChatRepository", "Invalid userId: " + userId);
            ApiResponse<ChatHistoryResponse> errorResponse = new ApiResponse<>();
            errorResponse.setSuccess(false);
            errorResponse.setMessage("User ID không hợp lệ");
            chatHistoryResult.setValue(errorResponse);
            return;
        }

        Call<ApiResponse<ChatHistoryResponse>> call = apiService.getChatHistory(userId);
        
        ApiUtils.executeCall(call, isLoading, chatHistoryResult, "Không thể tải lịch sử chat",
            response -> android.util.Log.d("ChatRepository", "Chat history loaded successfully: " +
                       (response.getData() != null ? response.getData().toString() : "null")),
            error -> android.util.Log.e("ChatRepository", "Failed to load chat history: " +
                    error.getMessage() + ", statusCode: " + error.getStatusCode())
        );
    }
} 