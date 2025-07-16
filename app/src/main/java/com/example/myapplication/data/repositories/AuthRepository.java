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

// Repository xử lý logic xác thực người dùng (login, register, logout, lưu session)
public class AuthRepository {
    // Đối tượng gọi API
    private ApiService apiService;
    // Quản lý session người dùng (lưu thông tin đăng nhập)
    private SessionManager sessionManager;
    
    // LiveData lưu kết quả đăng nhập
    private MutableLiveData<ApiResponse<User>> loginResult = new MutableLiveData<>();
    // LiveData lưu kết quả đăng ký
    private MutableLiveData<ApiResponse<User>> registerResult = new MutableLiveData<>();
    // LiveData lưu kết quả đăng xuất
    private MutableLiveData<ApiResponse<String>> logoutResult = new MutableLiveData<>();
    // LiveData trạng thái loading (đang xử lý API)
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    
    // Khởi tạo repository với context để lấy ApiService và SessionManager
    public AuthRepository(Context context) {
        apiService = ApiClient.getApiService();
        sessionManager = SessionManager.getInstance(context);
    }
    
    // Trả về LiveData kết quả đăng nhập
    public LiveData<ApiResponse<User>> getLoginResult() {
        return loginResult;
    }
    
    // Trả về LiveData kết quả đăng ký
    public LiveData<ApiResponse<User>> getRegisterResult() {
        return registerResult;
    }
    
    // Trả về LiveData kết quả đăng xuất
    public LiveData<ApiResponse<String>> getLogoutResult() {
        return logoutResult;
    }
    
    // Trả về LiveData trạng thái loading
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
    
    // Hàm đăng nhập: gọi API, lưu thông tin user vào session nếu thành công
    public void login(String email, String password) {
        LoginRequest loginRequest = new LoginRequest(email, password);
        Call<ApiResponse<User>> call = apiService.login(loginRequest);
        
        // Gọi API và xử lý kết quả trả về
        ApiUtils.executeCall(call, isLoading, loginResult, "Đăng nhập thất bại",
            response -> {
                if (response.getData() != null) {
                    User user = response.getData();
                    // Lưu thông tin user vào session
                    sessionManager.saveUserInfo(user.getId(), user.getRole().toString());
                }
            }
        );
    }
    
    // Hàm đăng ký: gọi API đăng ký tài khoản
    public void register(String email, String password, String name, String phone, String address) {
        RegisterRequest registerRequest = new RegisterRequest(email, password, name, phone, address);
        Call<ApiResponse<User>> call = apiService.register(registerRequest);
        
        // Gọi API và xử lý kết quả trả về
        ApiUtils.executeCall(call, isLoading, registerResult, "Đăng ký thất bại");
    }
    
    // Hàm đăng xuất: xóa session và trả về kết quả thành công
    public void logout() {
        sessionManager.clearSession();
        ApiResponse<String> successResponse = ApiResponse.isSuccess(200, "Đăng xuất thành công");
        logoutResult.setValue(successResponse);
    }
    
    // Kiểm tra người dùng đã đăng nhập hay chưa
    public boolean isLoggedIn() {
        return sessionManager.isLoggedIn();
    }
    
    // Lấy ID người dùng hiện tại
    public String getCurrentUserId() {
        return sessionManager.getUserId();
    }
    
    // Lấy vai trò người dùng hiện tại
    public String getCurrentUserRole() {
        return sessionManager.getUserRole();
    }
}
