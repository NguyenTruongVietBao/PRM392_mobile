package com.example.myapplication.ui.viewmodel.auth;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.myapplication.data.models.ApiResponse;
import com.example.myapplication.data.models.User;
import com.example.myapplication.data.repositories.UserRepository;

import java.util.List;

public class UserViewModel extends ViewModel {
    private UserRepository userRepository;

    public UserViewModel() {
        userRepository = new UserRepository();
    }

    // Get user by ID
    public void getUserById(String userId) {
        userRepository.getUserById(userId);
    }

    public void getAllUsers() {
        userRepository.getAllUsers();
    }

    // Update user
    public void updateUser(String userId, User user) {
        userRepository.updateUser(userId, user);
    }

    // Expose LiveData from repository
    public LiveData<ApiResponse<User>> getUserResult() {
        return userRepository.getUserResult();
    }

    public LiveData<ApiResponse<List<User>>> getUsersResult() {
        return userRepository.getUsersResult();
    }

    public LiveData<ApiResponse<User>> getUpdateUserResult() {
        return userRepository.getUpdateUserResult();
    }

    public LiveData<Boolean> getIsLoading() {
        return userRepository.getIsLoading();
    }
}
