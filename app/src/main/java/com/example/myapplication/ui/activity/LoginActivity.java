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
import com.example.myapplication.enums.UserRole;
import com.example.myapplication.ui.viewmodel.auth.AuthViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {

    // UI components
    private TextInputLayout tilEmail, tilPassword;
    private TextInputEditText etEmail, etPassword;
    private MaterialButton btnLogin;
    private View progressBar;
    private android.widget.TextView tvError, tvRegister;

    // ViewModel
    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        initViewModel();
        setupObservers();
        setupClickListeners();
        
        // Check if already logged in
        if (authViewModel.isLoggedIn()) {
            navigateBasedOnRole(authViewModel.getCurrentUserRole());
        }
    }

    private void initViews() {
        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        progressBar = findViewById(R.id.progressBar);
        tvError = findViewById(R.id.tvError);
        tvRegister = findViewById(R.id.tvRegister);
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

        // Observe login result
        authViewModel.getLoginResult().observe(this, apiResponse -> {
            if (apiResponse != null) {
                if (apiResponse.isSuccess()) {
                    hideError();
                    User user = apiResponse.getData();
                    if (user != null) {
                        showSuccess("Đăng nhập thành công!");
                        navigateBasedOnRole(user.getRole().toString());
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
        // Login button click
        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString();
            String password = etPassword.getText().toString();
            
            // Clear previous errors
            clearInputErrors();
            hideError();
            
            // Perform login
            authViewModel.login(email, password);
        });

        // Register link click
        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void navigateBasedOnRole(String role) {
        Intent intent;
        
        if (role == null) {
            role = UserRole.CUSTOMER.toString();
        }
        
        switch (role) {
            case "MANAGER":
                intent = new Intent(this, ManagerDashboardActivity.class);
                break;
            case "CUSTOMER":
            default:
                intent = new Intent(this, MainActivity.class);
                break;
        }
        
        startActivity(intent);
        finishAffinity(); // Clear all previous activities
    }

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        btnLogin.setEnabled(false);
        btnLogin.setText("Đang đăng nhập...");
    }

    private void hideLoading() {
        progressBar.setVisibility(View.GONE);
        btnLogin.setEnabled(true);
        btnLogin.setText("Đăng Nhập");
    }

    private void showError(String message) {
        tvError.setText(message);
        tvError.setVisibility(View.VISIBLE);
    }

    private void hideError() {
        tvError.setVisibility(View.GONE);
    }

    private void showSuccess(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void clearInputErrors() {
        tilEmail.setError(null);
        tilPassword.setError(null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Clear any previous validation errors when returning to this activity
        authViewModel.clearValidationError();
        hideError();
    }
}