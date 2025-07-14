package com.example.myapplication.ui.viewmodel.auth;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myapplication.data.models.ApiResponse;
import com.example.myapplication.data.models.User;
import com.example.myapplication.data.repositories.AuthRepository;
import com.example.myapplication.utils.ValidationUtils;

public class AuthViewModel extends AndroidViewModel {
    private AuthRepository authRepository;
    
    // LiveData cho UI observe
    private MutableLiveData<String> validationError = new MutableLiveData<>();
    
    public AuthViewModel(@NonNull Application application) {
        super(application);
        authRepository = new AuthRepository(application);
    }
    
    // Getters cho LiveData từ repository
    public LiveData<ApiResponse<User>> getLoginResult() {
        return authRepository.getLoginResult();
    }
    
    public LiveData<ApiResponse<User>> getRegisterResult() {
        return authRepository.getRegisterResult();
    }
    
    public LiveData<ApiResponse<String>> getLogoutResult() {
        return authRepository.getLogoutResult();
    }
    
    public LiveData<Boolean> getIsLoading() {
        return authRepository.getIsLoading();
    }
    
    public LiveData<String> getValidationError() {
        return validationError;
    }
    
    /**
     * Login method với validation
     */
    public void login(String email, String password) {
        // Validate input
        String error = ValidationUtils.validateLoginInput(email, password);
        if (error != null) {
            validationError.setValue(error);
            return;
        }
        
        // Clear previous validation error
        validationError.setValue(null);
        
        // Perform login
        authRepository.login(email.trim(), password);
    }
    
    /**
     * Register method với validation
     */
    public void register(String name, String email, String phone, String address, 
                        String password, String confirmPassword) {
        // Validate input
        String error = ValidationUtils.validateRegisterInput(
            email, password, confirmPassword, name, phone, address
        );
        if (error != null) {
            validationError.setValue(error);
            return;
        }
        
        // Clear previous validation error
        validationError.setValue(null);
        
        // Perform register
        authRepository.register(
            email.trim(), 
            password, 
            name.trim(), 
            phone.trim(), 
            address.trim()
        );
    }

    public void logout() {
        authRepository.logout();
    }

    public boolean isLoggedIn() {
        return authRepository.isLoggedIn();
    }

    public String getCurrentUserId() {
        return authRepository.getCurrentUserId();
    }
    public String getCurrentUserRole() {
        return authRepository.getCurrentUserRole();
    }

    public void clearValidationError() {
        validationError.setValue(null);
    }
} 