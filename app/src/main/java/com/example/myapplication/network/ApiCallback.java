package com.example.myapplication.network;

import androidx.lifecycle.MutableLiveData;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.example.myapplication.data.models.ApiResponse;
import com.example.myapplication.utils.Constants;

public abstract class ApiCallback<T> implements Callback<ApiResponse<T>> {
    private MutableLiveData<Boolean> isLoading;
    private MutableLiveData<ApiResponse<T>> result;
    private String errorMessage;

    public ApiCallback(MutableLiveData<Boolean> isLoading, 
                      MutableLiveData<ApiResponse<T>> result, 
                      String errorMessage) {
        this.isLoading = isLoading;
        this.result = result;
        this.errorMessage = errorMessage;
    }

    @Override
    public void onResponse(Call<ApiResponse<T>> call, Response<ApiResponse<T>> response) {
        if (isLoading != null) {
            isLoading.setValue(false);
        }
        if (response.isSuccessful() && response.body() != null) {
            ApiResponse<T> apiResponse = response.body();
            if (apiResponse.isSuccess()) {
                onSuccess(apiResponse);
            } else {
                onError(apiResponse);
            }
            if (result != null) {
                result.setValue(apiResponse);
            }
        } else {
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

    protected void onSuccess(ApiResponse<T> response) {
    }

    protected void onError(ApiResponse<T> response) {
    }
} 