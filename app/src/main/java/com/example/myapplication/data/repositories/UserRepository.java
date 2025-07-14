package com.example.myapplication.data.repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import retrofit2.Call;

import com.example.myapplication.data.models.ApiResponse;
import com.example.myapplication.data.models.User;
import com.example.myapplication.network.ApiClient;
import com.example.myapplication.network.ApiService;
import com.example.myapplication.network.ApiUtils;

import java.util.List;

public class UserRepository {
    private ApiService apiService;
    
    private MutableLiveData<ApiResponse<User>> userResult = new MutableLiveData<>();
    private MutableLiveData<ApiResponse<List<User>>> usersResult = new MutableLiveData<>();
    private MutableLiveData<ApiResponse<User>> updateUserResult = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    
    public UserRepository() {
        apiService = ApiClient.getApiService();
    }
    
    public LiveData<ApiResponse<User>> getUserResult() {
        return userResult;
    }
    
    public LiveData<ApiResponse<List<User>>> getUsersResult() {
        return usersResult;
    }
    
    public LiveData<ApiResponse<User>> getUpdateUserResult() {
        return updateUserResult;
    }
    
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
    
    public void getUserById(String userId) {
        Call<ApiResponse<User>> call = apiService.getUserById(userId);
        ApiUtils.executeCall(call, isLoading, userResult, "Không thể lấy thông tin người dùng");
    }
    
    public void getAllUsers() {
        Call<ApiResponse<List<User>>> call = apiService.getAllUsers();
        ApiUtils.executeCall(call, isLoading, usersResult, "Không thể lấy danh sách người dùng");
    }
    
    public void updateUser(String userId, User user) {
        Call<ApiResponse<User>> call = apiService.updateUser(userId, user);
        ApiUtils.executeCall(call, isLoading, updateUserResult, "Không thể cập nhật thông tin người dùng");
    }
}
