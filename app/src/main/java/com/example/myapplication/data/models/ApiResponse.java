package com.example.myapplication.data.models;

public class ApiResponse<T> {
    private boolean success;
    private int statusCode;
    private String message;
    private T data;

    public ApiResponse() {
    }

    public ApiResponse(boolean success, int statusCode, String message, T data) {
        this.success = success;
        this.statusCode = statusCode;
        this.message = message;
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    // Utility methods
    public static <T> ApiResponse<T> isSuccess(int statusCode, String message, T data) {
        return new ApiResponse<>(true, statusCode, message, data);
    }

    public static <T> ApiResponse<T> isSuccess(int statusCode, T data) {
        return new ApiResponse<>(true, statusCode, "Success", data);
    }

    public static <T> ApiResponse<T> isError(int statusCode, String message) {
        return new ApiResponse<>(false, statusCode, message, null);
    }
}