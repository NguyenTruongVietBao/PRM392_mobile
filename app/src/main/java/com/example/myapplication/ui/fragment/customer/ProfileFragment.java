package com.example.myapplication.ui.fragment.customer;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentProfileBinding;
import com.example.myapplication.ui.activity.OrderHistoryActivity;
import com.example.myapplication.ui.viewmodel.auth.AuthViewModel;
import com.example.myapplication.ui.viewmodel.auth.UserViewModel;
import com.example.myapplication.utils.SessionManager;
import com.example.myapplication.data.models.User;
import com.example.myapplication.data.models.ApiResponse;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private AuthViewModel authViewModel;
    private UserViewModel userViewModel;
    private SessionManager sessionManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeComponents();
        setupObservers();
        loadUserProfileFromAPI();
        setupClickListeners();
    }

    private void initializeComponents() {
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        sessionManager = SessionManager.getInstance(requireContext());
    }

    private void setupObservers() {
        userViewModel.getUserResult().observe(getViewLifecycleOwner(), this::handleUserResult);
        userViewModel.getIsLoading().observe(getViewLifecycleOwner(), this::handleLoadingState);
    }

    private void loadUserProfileFromAPI() {
        String userId = sessionManager.getUserId();
        if (userId != null) {
            userViewModel.getUserById(userId);
        } else {
            displayNotLoggedInState();
        }
    }

    private void handleUserResult(ApiResponse<User> response) {
        if (response != null) {
            if (response.isSuccess() && response.getData() != null) {
                displayUserProfile(response.getData());
            } else {
                showErrorMessage(response.getMessage());
                displayErrorState();
            }
        }
    }

    private void handleLoadingState(Boolean isLoading) {
        if (isLoading != null) {
            if (isLoading) {
                showLoadingState();
            } else {
                hideLoadingState();
            }
        }
    }

    private void displayUserProfile(User user) {
        if (user != null) {
            binding.tvUserName.setText(user.getName() != null ? user.getName() : getString(R.string.profile_user_name));
            binding.tvUserId.setText(getString(R.string.profile_user_id, user.getId()));
            binding.tvUserEmail.setText(user.getEmail());
            binding.chipUserRole.setText(user.getRole().toString());
            setRoleChipColor(user.getRole().toString());
            binding.tvUserPhone.setText(user.getPhone());
            // Display member since
            if (user.getCreatedAt() != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
                binding.tvMemberSince.setText(dateFormat.format(user.getCreatedAt()));
            } else {
                binding.tvMemberSince.setText(getString(R.string.profile_default_member_since));
            }
        }
    }

    private void showLoadingState() {
        binding.tvUserName.setText("Loading...");
        binding.tvUserId.setText("Loading...");
        binding.tvUserEmail.setText("Loading...");
        binding.tvUserPhone.setText("Loading...");
        binding.tvMemberSince.setText("Loading...");
        binding.chipUserRole.setText("Loading...");
    }

    private void hideLoadingState() { }

    private void displayErrorState() {
        binding.tvUserName.setText(getString(R.string.profile_error_loading));
        binding.tvUserId.setText(getString(R.string.profile_error_loading));
        binding.tvUserEmail.setText(getString(R.string.profile_error_loading));
        binding.tvUserPhone.setText(getString(R.string.profile_error_loading));
        binding.tvMemberSince.setText(getString(R.string.profile_error_loading));
        binding.chipUserRole.setText("Error");
    }

    private void displayNotLoggedInState() {
        binding.tvUserName.setText(getString(R.string.profile_not_logged_in));
        binding.tvUserId.setText(getString(R.string.profile_id_na));
        binding.chipUserRole.setText(getString(R.string.profile_guest_role));
        binding.tvUserEmail.setText(getString(R.string.profile_login_required));
        binding.tvUserPhone.setText(getString(R.string.profile_na));
        binding.tvMemberSince.setText(getString(R.string.profile_na));
    }

    private void setRoleChipColor(String userRole) {
        if (userRole != null) {
            switch (userRole.toLowerCase()) {
                case "manager":
                    binding.chipUserRole.setChipBackgroundColorResource(R.color.warning_yellow);
                    binding.chipUserRole.setTextColor(getResources().getColor(R.color.text_primary_light, null));
                    break;
                case "customer":
                default:
                    binding.chipUserRole.setChipBackgroundColorResource(R.color.primary_blue);
                    break;
            }
        }
    }

    private void setupClickListeners() {
        binding.btnOrderHistory.setOnClickListener(v -> navigateToOrderHistory());
        binding.btnLogout.setOnClickListener(v -> showLogoutConfirmationDialog());
    }

    private void showLogoutConfirmationDialog() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.dialog_logout_title)
                .setMessage(R.string.dialog_logout_message)
                .setPositiveButton(R.string.dialog_logout_confirm, (dialog, which) -> performLogout())
                .setNegativeButton(R.string.dialog_cancel, (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void navigateToOrderHistory() {
        Intent intent = new Intent(getActivity(), OrderHistoryActivity.class);
        startActivity(intent);
    }

    private void performLogout() {
        sessionManager.clearSession();
        authViewModel.logout();
        requireActivity().finish();
    }

    private void showErrorMessage(String message) {
        if (getView() != null) {
            Snackbar.make(getView(), message != null ? message : "Error loading profile", Snackbar.LENGTH_LONG)
                    .setAction("Retry", v -> loadUserProfileFromAPI())
                    .show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
