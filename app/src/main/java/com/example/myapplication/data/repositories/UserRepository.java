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

// Repository xử lý logic liên quan đến người dùng (user)
public class UserRepository {
    // Đối tượng gọi API
    private ApiService apiService;
    
    // LiveData lưu kết quả lấy một người dùng
    private MutableLiveData<ApiResponse<User>> userResult = new MutableLiveData<>();
    // LiveData lưu kết quả lấy danh sách người dùng
    private MutableLiveData<ApiResponse<List<User>>> usersResult = new MutableLiveData<>();
    // LiveData lưu kết quả cập nhật người dùng
    private MutableLiveData<ApiResponse<User>> updateUserResult = new MutableLiveData<>();
    // LiveData trạng thái loading (đang xử lý API)
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    
    // Khởi tạo repository, lấy ApiService
    public UserRepository() {
        apiService = ApiClient.getApiService();
    }
    
    // Trả về LiveData kết quả lấy một người dùng
    public LiveData<ApiResponse<User>> getUserResult() {
        return userResult;
    }
    // Trả về LiveData kết quả lấy danh sách người dùng
    public LiveData<ApiResponse<List<User>>> getUsersResult() {
        return usersResult;
    }
    // Trả về LiveData kết quả cập nhật người dùng
    public LiveData<ApiResponse<User>> getUpdateUserResult() {
        return updateUserResult;
    }
    // Trả về LiveData trạng thái loading
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
    
    // Lấy thông tin một người dùng theo id
    public void getUserById(String userId) {
        Call<ApiResponse<User>> call = apiService.getUserById(userId);
        ApiUtils.executeCall(call, isLoading, userResult, "Không thể lấy thông tin người dùng");
    }
    
    // Lấy danh sách tất cả người dùng
    public void getAllUsers() {
        Call<ApiResponse<List<User>>> call = apiService.getAllUsers();
        ApiUtils.executeCall(call, isLoading, usersResult, "Không thể lấy danh sách người dùng");
    }
    
    // Cập nhật thông tin người dùng
    public void updateUser(String userId, User user) {
        Call<ApiResponse<User>> call = apiService.updateUser(userId, user);
        ApiUtils.executeCall(call, isLoading, updateUserResult, "Không thể cập nhật thông tin người dùng");
    }
}
