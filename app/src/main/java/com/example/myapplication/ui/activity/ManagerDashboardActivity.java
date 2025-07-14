package com.example.myapplication.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.example.myapplication.R;
import com.example.myapplication.databinding.ActivityManagerDashboardBinding;
import com.example.myapplication.ui.viewmodel.auth.AuthViewModel;

public class ManagerDashboardActivity extends AppCompatActivity {

    private ActivityManagerDashboardBinding binding;
    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Setup ViewModel
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        
        // Check if user is logged in and has manager role
        if (!authViewModel.isLoggedIn()) {
            navigateToLogin();
            return;
        }

        binding = ActivityManagerDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupWindowInsets();
        setupBottomNavigation();
        setupLogoutObserver();
    }

    private void setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.container, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return WindowInsetsCompat.CONSUMED;
        });
    }

    private void setupBottomNavigation() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_manager);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }

    private void setupLogoutObserver() {
        authViewModel.getLogoutResult().observe(this, apiResponse -> {
            if (apiResponse != null && apiResponse.isSuccess()) {
                navigateToLogin();
            }
        });
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finishAffinity();
    }
}
