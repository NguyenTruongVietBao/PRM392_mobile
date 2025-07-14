package com.example.myapplication.data.repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import retrofit2.Call;

import com.example.myapplication.data.models.ApiResponse;
import com.example.myapplication.data.models.Payment;
import com.example.myapplication.network.ApiClient;
import com.example.myapplication.network.ApiService;
import com.example.myapplication.network.ApiUtils;

public class PaymentRepository {
    private ApiService apiService;
    
    private MutableLiveData<ApiResponse<Payment>> paymentResult = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    
    public PaymentRepository() {
        apiService = ApiClient.getApiService();
    }
    
    public LiveData<ApiResponse<Payment>> getPaymentResult() {
        return paymentResult;
    }
    
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
    
    public void processPayment(Payment payment) {
        isLoading.setValue(true);
        new Thread(() -> {
            try {
                Thread.sleep(2000);
                payment.setTransactionId("TXN_" + System.currentTimeMillis());
                payment.setGatewayResponse("Payment successful");
                ApiResponse<Payment> successResponse = ApiResponse.isSuccess(200, "Thanh toán thành công", payment);
                isLoading.postValue(false);
                paymentResult.postValue(successResponse);
            } catch (InterruptedException e) {
                ApiResponse<Payment> errorResponse = ApiResponse.isError(500, "Lỗi xử lý thanh toán");
                isLoading.postValue(false);
                paymentResult.postValue(errorResponse);
            }
        }).start();
    }

    public void verifyPayment(String transactionId) {
        isLoading.setValue(true);
        new Thread(() -> {
            try {
                Thread.sleep(1500);
                Payment payment = new Payment();
                payment.setTransactionId(transactionId);
                payment.setGatewayResponse("Payment verified");
                ApiResponse<Payment> successResponse = ApiResponse.isSuccess(200, "Xác thực thanh toán thành công", payment);
                isLoading.postValue(false);
                paymentResult.postValue(successResponse);
                
            } catch (InterruptedException e) {
                ApiResponse<Payment> errorResponse = ApiResponse.isError(500, "Lỗi xác thực thanh toán");
                isLoading.postValue(false);
                paymentResult.postValue(errorResponse);
            }
        }).start();
    }
    
    public void refundPayment(String transactionId, double amount) {
        isLoading.setValue(true);
        new Thread(() -> {
            try {
                Thread.sleep(2500);
                Payment payment = new Payment();
                payment.setTransactionId("REF_" + System.currentTimeMillis());
                payment.setAmount(amount);
                payment.setGatewayResponse("Refund successful");
                ApiResponse<Payment> successResponse = ApiResponse.isSuccess(200, "Hoàn tiền thành công", payment);
                isLoading.postValue(false);
                paymentResult.postValue(successResponse);
            } catch (InterruptedException e) {
                ApiResponse<Payment> errorResponse = ApiResponse.isError(500, "Lỗi hoàn tiền");
                isLoading.postValue(false);
                paymentResult.postValue(errorResponse);
            }
        }).start();
    }
} 