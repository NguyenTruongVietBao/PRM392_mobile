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

// Repository xử lý logic liên quan đến chat giữa người dùng và hệ thống
public class ChatRepository {
    // Đối tượng gọi API
    private ApiService apiService;
    
    // LiveData lưu kết quả gửi tin nhắn
    private MutableLiveData<ApiResponse<ChatResponse>> sendMessageResult = new MutableLiveData<>();
    // LiveData lưu kết quả lấy lịch sử chat
    private MutableLiveData<ApiResponse<ChatHistoryResponse>> chatHistoryResult = new MutableLiveData<>();
    // LiveData trạng thái loading (đang xử lý API)
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    // Khởi tạo repository, lấy ApiService
    public ChatRepository() {
        apiService = ApiClient.getApiService();
    }

    // Trả về LiveData kết quả gửi tin nhắn
    public LiveData<ApiResponse<ChatResponse>> getSendMessageResult() {
        return sendMessageResult; 
    }
    // Trả về LiveData kết quả lấy lịch sử chat
    public LiveData<ApiResponse<ChatHistoryResponse>> getChatHistoryResult() { 
        return chatHistoryResult; 
    }
    // Trả về LiveData trạng thái loading
    public LiveData<Boolean> getIsLoading() { 
        return isLoading; 
    }

    // Gửi tin nhắn từ user lên server
    public void sendMessage(String userId, String message) {
        android.util.Log.d("ChatRepository", "Sending message - userId: " + userId + ", message: " + message);
        
        // Kiểm tra userId hợp lệ
        if (userId == null || userId.trim().isEmpty()) {
            android.util.Log.e("ChatRepository", "Invalid userId: " + userId);
            ApiResponse<ChatResponse> errorResponse = new ApiResponse<>();
            errorResponse.setSuccess(false);
            errorResponse.setMessage("User ID không hợp lệ");
            sendMessageResult.setValue(errorResponse);
            return;
        }
        // Kiểm tra message hợp lệ
        if (message == null || message.trim().isEmpty()) {
            android.util.Log.e("ChatRepository", "Invalid message: " + message);
            ApiResponse<ChatResponse> errorResponse = new ApiResponse<>();
            errorResponse.setSuccess(false);
            errorResponse.setMessage("Message không được để trống");
            sendMessageResult.setValue(errorResponse);
            return;
        }

        // Tạo request gửi tin nhắn
        ChatRequest request = new ChatRequest(message.trim(), userId);
        Call<ApiResponse<ChatResponse>> call = apiService.sendMessage(request);
        
        // Gọi API và xử lý kết quả trả về
        ApiUtils.executeCall(call, isLoading, sendMessageResult, "Không thể gửi tin nhắn",
            response -> android.util.Log.d("ChatRepository", "Message sent successfully: " +
                       (response.getData() != null ? response.getData().toString() : "null")),
            error -> android.util.Log.e("ChatRepository", "Failed to send message: " +
                    error.getMessage() + ", statusCode: " + error.getStatusCode())
        );
    }

    // Lấy lịch sử chat của user
    public void getChatHistory(String userId) {
        android.util.Log.d("ChatRepository", "Loading chat history for userId: " + userId);
        
        // Kiểm tra userId hợp lệ
        if (userId == null || userId.trim().isEmpty()) {
            android.util.Log.e("ChatRepository", "Invalid userId: " + userId);
            ApiResponse<ChatHistoryResponse> errorResponse = new ApiResponse<>();
            errorResponse.setSuccess(false);
            errorResponse.setMessage("User ID không hợp lệ");
            chatHistoryResult.setValue(errorResponse);
            return;
        }

        // Gọi API lấy lịch sử chat
        Call<ApiResponse<ChatHistoryResponse>> call = apiService.getChatHistory(userId);
        
        // Xử lý kết quả trả về
        ApiUtils.executeCall(call, isLoading, chatHistoryResult, "Không thể tải lịch sử chat",
            response -> android.util.Log.d("ChatRepository", "Chat history loaded successfully: " +
                       (response.getData() != null ? response.getData().toString() : "null")),
            error -> android.util.Log.e("ChatRepository", "Failed to load chat history: " +
                    error.getMessage() + ", statusCode: " + error.getStatusCode())
        );
    }
} 