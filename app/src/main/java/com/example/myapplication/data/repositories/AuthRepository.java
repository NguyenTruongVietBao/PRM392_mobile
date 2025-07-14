package com.example.myapplication.data.repositories;

import android.content.Context;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import retrofit2.Call;

import com.example.myapplication.data.models.ApiResponse;
import com.example.myapplication.data.models.User;
import com.example.myapplication.network.ApiClient;
import com.example.myapplication.network.ApiService;
import com.example.myapplication.network.ApiUtils;
import com.example.myapplication.network.requests.LoginRequest;
import com.example.myapplication.network.requests.RegisterRequest;
import com.example.myapplication.utils.SessionManager;

public class AuthRepository {
    private ApiService apiService;
    private SessionManager sessionManager;
    
    private MutableLiveData<ApiResponse<User>> loginResult = new MutableLiveData<>();
    private MutableLiveData<ApiResponse<User>> registerResult = new MutableLiveData<>();
    private MutableLiveData<ApiResponse<String>> logoutResult = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    
    public AuthRepository(Context context) {
        apiService = ApiClient.getApiService();
        sessionManager = SessionManager.getInstance(context);
    }
    
    public LiveData<ApiResponse<User>> getLoginResult() {
        return loginResult;
    }
    
    public LiveData<ApiResponse<User>> getRegisterResult() {
        return registerResult;
    }
    
    public LiveData<ApiResponse<String>> getLogoutResult() {
        return logoutResult;
    }
    
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
    
    public void login(String email, String password) {
        LoginRequest loginRequest = new LoginRequest(email, password);
        Call<ApiResponse<User>> call = apiService.login(loginRequest);
        
        ApiUtils.executeCall(call, isLoading, loginResult, "Đăng nhập thất bại",
            response -> {
                if (response.getData() != null) {
                    User user = response.getData();
                    sessionManager.saveUserInfo(user.getId(), user.getRole().toString());
                }
            }
        );
    }
    
    public void register(String email, String password, String name, String phone, String address) {
        RegisterRequest registerRequest = new RegisterRequest(email, password, name, phone, address);
        Call<ApiResponse<User>> call = apiService.register(registerRequest);
        
        ApiUtils.executeCall(call, isLoading, registerResult, "Đăng ký thất bại");
    }
    
    public void logout() {
        sessionManager.clearSession();
        ApiResponse<String> successResponse = ApiResponse.isSuccess(200, "Đăng xuất thành công");
        logoutResult.setValue(successResponse);
    }
    
    public boolean isLoggedIn() {
        return sessionManager.isLoggedIn();
    }
    
    public String getCurrentUserId() {
        return sessionManager.getUserId();
    }
    
    public String getCurrentUserRole() {
        return sessionManager.getUserRole();
    }
}
