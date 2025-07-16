package com.example.myapplication.network;

import androidx.lifecycle.MutableLiveData;

import retrofit2.Call;

import com.example.myapplication.data.models.ApiResponse;

// Tiện ích hỗ trợ gọi API sử dụng retrofit, giúp đơn giản hóa việc xử lý callback và trạng thái loading
public class ApiUtils {
    
    /**
     * Tạo một API call đơn giản không cần custom logic
     * Tự động set trạng thái loading và trả kết quả về LiveData
     */
    public static <T> void executeCall(Call<ApiResponse<T>> call,
                                      MutableLiveData<Boolean> isLoading,
                                      MutableLiveData<ApiResponse<T>> result,
                                      String errorMessage) {
        if (isLoading != null) {
            isLoading.setValue(true);
        }
        
        // Gọi API và sử dụng ApiCallback mặc định
        call.enqueue(new ApiCallback<T>(isLoading, result, errorMessage) {
            // Không cần custom logic, chỉ sử dụng ApiCallback default
        });
    }
    
    /**
     * Tạo một API call với success callback
     * Cho phép custom xử lý khi thành công
     */
    public static <T> void executeCall(Call<ApiResponse<T>> call,
                                      MutableLiveData<Boolean> isLoading,
                                      MutableLiveData<ApiResponse<T>> result,
                                      String errorMessage,
                                      OnSuccessCallback<T> onSuccessCallback) {
        if (isLoading != null) {
            isLoading.setValue(true);
        }
        
        // Gọi API và custom xử lý khi thành công
        call.enqueue(new ApiCallback<T>(isLoading, result, errorMessage) {
            @Override
            protected void onSuccess(ApiResponse<T> response) {
                if (onSuccessCallback != null) {
                    onSuccessCallback.onSuccess(response);
                }
            }
        });
    }
    
    /**
     * Tạo một API call với cả success và error callback
     * Cho phép custom xử lý khi thành công hoặc thất bại
     */
    public static <T> void executeCall(Call<ApiResponse<T>> call,
                                      MutableLiveData<Boolean> isLoading,
                                      MutableLiveData<ApiResponse<T>> result,
                                      String errorMessage,
                                      OnSuccessCallback<T> onSuccessCallback,
                                      OnErrorCallback<T> onErrorCallback) {
        if (isLoading != null) {
            isLoading.setValue(true);
        }
        
        // Gọi API và custom xử lý khi thành công hoặc thất bại
        call.enqueue(new ApiCallback<T>(isLoading, result, errorMessage) {
            @Override
            protected void onSuccess(ApiResponse<T> response) {
                if (onSuccessCallback != null) {
                    onSuccessCallback.onSuccess(response);
                }
            }
            
            @Override
            protected void onError(ApiResponse<T> response) {
                if (onErrorCallback != null) {
                    onErrorCallback.onError(response);
                }
            }
        });
    }
    
    // Interface callback cho thành công
    public interface OnSuccessCallback<T> {
        void onSuccess(ApiResponse<T> response);
    }
    
    // Interface callback cho thất bại
    public interface OnErrorCallback<T> {
        void onError(ApiResponse<T> response);
    }
} 