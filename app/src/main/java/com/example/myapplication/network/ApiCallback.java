package com.example.myapplication.network;

import androidx.lifecycle.MutableLiveData;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.example.myapplication.data.models.ApiResponse;
import com.example.myapplication.utils.Constants;

// Callback dùng cho retrofit để xử lý kết quả trả về từ API, hỗ trợ LiveData và xử lý trạng thái loading
public abstract class ApiCallback<T> implements Callback<ApiResponse<T>> {
    // LiveData trạng thái loading (đang gọi API)
    private MutableLiveData<Boolean> isLoading;
    // LiveData lưu kết quả trả về từ API
    private MutableLiveData<ApiResponse<T>> result;
    // Thông báo lỗi mặc định nếu có lỗi
    private String errorMessage;

    // Constructor truyền vào LiveData và thông báo lỗi
    public ApiCallback(MutableLiveData<Boolean> isLoading, 
                      MutableLiveData<ApiResponse<T>> result, 
                      String errorMessage) {
        this.isLoading = isLoading;
        this.result = result;
        this.errorMessage = errorMessage;
    }

    // Xử lý khi nhận được response từ API
    @Override
    public void onResponse(Call<ApiResponse<T>> call, Response<ApiResponse<T>> response) {
        if (isLoading != null) {
            isLoading.setValue(false);
        }
        if (response.isSuccessful() && response.body() != null) {
            ApiResponse<T> apiResponse = response.body();
            if (apiResponse.isSuccess()) {
                onSuccess(apiResponse); // Gọi callback thành công
            } else {
                onError(apiResponse); // Gọi callback lỗi
            }
            if (result != null) {
                result.setValue(apiResponse);
            }
        } else {
            // Tạo response lỗi nếu không thành công
            ApiResponse<T> errorResponse = ApiResponse.isError(
                response.code(), 
                errorMessage != null ? errorMessage : "Có lỗi xảy ra"
            );
            onError(errorResponse);
            if (result != null) {
                result.setValue(errorResponse);
            }
        }
    }

    // Xử lý khi gọi API thất bại (lỗi mạng, exception...)
    @Override
    public void onFailure(Call<ApiResponse<T>> call, Throwable t) {
        if (isLoading != null) {
            isLoading.setValue(false);
        }
        ApiResponse<T> errorResponse = ApiResponse.isError(
            Constants.HTTP_INTERNAL_ERROR, 
            Constants.ERROR_NETWORK
        );
        onError(errorResponse);
        if (result != null) {
            result.setValue(errorResponse);
        }
    }

    // Callback khi thành công (có thể override ở class con)
    protected void onSuccess(ApiResponse<T> response) {
    }

    // Callback khi lỗi (có thể override ở class con)
    protected void onError(ApiResponse<T> response) {
    }
} 