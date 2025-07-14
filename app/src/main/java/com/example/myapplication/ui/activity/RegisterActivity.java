package com.example.myapplication.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.R;
import com.example.myapplication.data.models.User;
import com.example.myapplication.ui.viewmodel.auth.AuthViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class RegisterActivity extends AppCompatActivity {

    // UI components
    private TextInputLayout tilName, tilEmail, tilPhone, tilAddress, tilPassword, tilConfirmPassword;
    private TextInputEditText etName, etEmail, etPhone, etAddress, etPassword, etConfirmPassword;
    private MaterialButton btnRegister;
    private View progressBar;
    private android.widget.TextView tvError, tvLogin;

    // ViewModel
    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        initViewModel();
        setupObservers();
        setupClickListeners();
    }

    private void initViews() {
        tilName = findViewById(R.id.tilName);
        tilEmail = findViewById(R.id.tilEmail);
        tilPhone = findViewById(R.id.tilPhone);
        tilAddress = findViewById(R.id.tilAddress);
        tilPassword = findViewById(R.id.tilPassword);
        tilConfirmPassword = findViewById(R.id.tilConfirmPassword);
        
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etAddress = findViewById(R.id.etAddress);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        
        btnRegister = findViewById(R.id.btnRegister);
        progressBar = findViewById(R.id.progressBar);
        tvError = findViewById(R.id.tvError);
        tvLogin = findViewById(R.id.tvLogin);
    }

    private void initViewModel() {
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
    }

    private void setupObservers() {
        // Observe loading state
        authViewModel.getIsLoading().observe(this, isLoading -> {
            if (isLoading) {
                showLoading();
            } else {
                hideLoading();
            }
        });

        // Observe register result
        authViewModel.getRegisterResult().observe(this, apiResponse -> {
            if (apiResponse != null) {
                if (apiResponse.isSuccess()) {
                    hideError();
                    User user = apiResponse.getData();
                    if (user != null) {
                        showSuccess("Đăng ký thành công! Vui lòng đăng nhập.");
                        navigateToLogin();
                    }
                } else {
                    showError(apiResponse.getMessage());
                }
            }
        });

        // Observe validation errors
        authViewModel.getValidationError().observe(this, error -> {
            if (error != null) {
                showError(error);
            }
        });
    }

    private void setupClickListeners() {
        // Register button click
        btnRegister.setOnClickListener(v -> {
            String name = etName.getText().toString();
            String email = etEmail.getText().toString();
            String phone = etPhone.getText().toString();
            String address = etAddress.getText().toString();
            String password = etPassword.getText().toString();
            String confirmPassword = etConfirmPassword.getText().toString();
            
            // Clear previous errors
            clearInputErrors();
            hideError();
            
            // Perform register
            authViewModel.register(name, email, phone, address, password, confirmPassword);
        });

        // Login link click
        tvLogin.setOnClickListener(v -> {
            navigateToLogin();
        });
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish(); // Close register activity
    }

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        btnRegister.setEnabled(false);
        btnRegister.setText("Đang đăng ký...");
    }

    private void hideLoading() {
        progressBar.setVisibility(View.GONE);
        btnRegister.setEnabled(true);
        btnRegister.setText("Đăng Ký");
    }

    private void showError(String message) {
        tvError.setText(message);
        tvError.setVisibility(View.VISIBLE);
    }

    private void hideError() {
        tvError.setVisibility(View.GONE);
    }

    private void showSuccess(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void clearInputErrors() {
        tilName.setError(null);
        tilEmail.setError(null);
        tilPhone.setError(null);
        tilAddress.setError(null);
        tilPassword.setError(null);
        tilConfirmPassword.setError(null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Clear any previous validation errors when returning to this activity
        authViewModel.clearValidationError();
        hideError();
    }
}