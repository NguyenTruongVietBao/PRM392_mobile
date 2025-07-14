package com.example.myapplication.network;

import androidx.lifecycle.MutableLiveData;

import retrofit2.Call;

import com.example.myapplication.data.models.ApiResponse;

public class ApiUtils {
    
    /**
     * Tạo một API call đơn giản không cần custom logic
     */
    public static <T> void executeCall(Call<ApiResponse<T>> call,
                                      MutableLiveData<Boolean> isLoading,
                                      MutableLiveData<ApiResponse<T>> result,
                                      String errorMessage) {
        if (isLoading != null) {
            isLoading.setValue(true);
        }
        
        call.enqueue(new ApiCallback<T>(isLoading, result, errorMessage) {
            // Không cần custom logic, chỉ sử dụng ApiCallback default
        });
    }
    
    /**
     * Tạo một API call với success callback
     */
    public static <T> void executeCall(Call<ApiResponse<T>> call,
                                      MutableLiveData<Boolean> isLoading,
                                      MutableLiveData<ApiResponse<T>> result,
                                      String errorMessage,
                                      OnSuccessCallback<T> onSuccessCallback) {
        if (isLoading != null) {
            isLoading.setValue(true);
        }
        
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
    
    // Interface callbacks
    public interface OnSuccessCallback<T> {
        void onSuccess(ApiResponse<T> response);
    }
    
    public interface OnErrorCallback<T> {
        void onError(ApiResponse<T> response);
    }
} 