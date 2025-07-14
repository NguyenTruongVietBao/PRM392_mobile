package com.example.myapplication.ui.fragment.customer;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.myapplication.databinding.FragmentHomeBinding;
import com.example.myapplication.ui.activity.ProductDetailActivity;
import com.example.myapplication.ui.adapter.ProductAdapter;
import com.example.myapplication.ui.viewmodel.customer.HomeViewModel;
import com.example.myapplication.data.models.Product;
import com.example.myapplication.data.models.ApiResponse;
import com.example.myapplication.network.responses.ProductResponse;
import com.example.myapplication.utils.SortOption;

public class HomeFragment extends Fragment implements ProductAdapter.OnProductClickListener {

    private FragmentHomeBinding binding;
    private HomeViewModel homeViewModel;
    private ProductAdapter productAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViewModel();
        initRecyclerView();
        setupObservers();
        setupClickListeners();
        initData();
    }

    private void initViewModel() {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
    }

    private void initRecyclerView() {
        productAdapter = new ProductAdapter();
        productAdapter.setOnProductClickListener(this);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        binding.recyclerViewProducts.setLayoutManager(gridLayoutManager);
        binding.recyclerViewProducts.setAdapter(productAdapter);
        binding.recyclerViewProducts.setHasFixedSize(true);

        // Add spacing between grid items
        int spacing = (int) (8 * getResources().getDisplayMetrics().density);
        binding.recyclerViewProducts.addItemDecoration(
                new com.example.myapplication.utils.GridSpacingItemDecoration(2, spacing, false));
    }

    private void setupObservers() {
        if (homeViewModel == null) return;

        homeViewModel.getIsLoading().observe(getViewLifecycleOwner(), this::handleLoadingState);
        homeViewModel.getProductsResult().observe(getViewLifecycleOwner(), this::handleProductsResponse);
        homeViewModel.getSearchResult().observe(getViewLifecycleOwner(), this::handleProductsResponse);
        homeViewModel.getProducts().observe(getViewLifecycleOwner(), this::handleProductsList);
        homeViewModel.getErrorMessage().observe(getViewLifecycleOwner(), this::handleErrorMessage);
        homeViewModel.getIsEmpty().observe(getViewLifecycleOwner(), this::handleEmptyState);
        homeViewModel.getCurrentSortOption().observe(getViewLifecycleOwner(), this::handleSortOptionChange);
    }

    private void setupClickListeners() {
        binding.btnRetry.setOnClickListener(v -> homeViewModel.refreshProducts());

        // Sort button click listener
        binding.btnSort.setOnClickListener(v -> showSortDialog());

        // Search functionality
        binding.etSearch.addTextChangedListener(new TextWatcher() {
            private final android.os.Handler searchHandler = new android.os.Handler();
            private Runnable searchRunnable;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (searchRunnable != null) {
                    searchHandler.removeCallbacks(searchRunnable);
                }

                searchRunnable = () -> {
                    String query = s != null ? s.toString().trim() : "";
                    if (query.isEmpty()) {
                        homeViewModel.clearFilters();
                    } else {
                        homeViewModel.searchProducts(query);
                    }
                };

                searchHandler.postDelayed(searchRunnable, 500);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void handleLoadingState(Boolean isLoading) {
        if (isLoading != null) {
            binding.loadingOverlay.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            if (isLoading) {
                hideAllStates();
            }
        }
    }

    private void handleProductsResponse(ApiResponse<ProductResponse> response) {
        if (response != null) {
            if (response.isSuccess() && response.getData() != null) {
                ProductResponse productResponse = response.getData();
                if (productResponse.getProducts() != null) {
                    homeViewModel.setProducts(productResponse.getProducts());
                } else {
                    homeViewModel.setProducts(null);
                    homeViewModel.setErrorMessage("Không có dữ liệu sản phẩm");
                }
            } else {
                homeViewModel.setErrorMessage(response.getMessage());
            }
        }
    }

    private void handleProductsList(java.util.List<Product> products) {
        if (products != null && !products.isEmpty()) {
            productAdapter.setProducts(products);
            showProductsList();
        }
    }

    private void handleErrorMessage(String message) {
        if (message != null && !message.isEmpty()) {
            showErrorState(message);
        }
    }

    private void handleEmptyState(Boolean isEmpty) {
        if (isEmpty != null && isEmpty) {
            showEmptyState();
        }
    }

    private void handleSortOptionChange(SortOption sortOption) {
        if (sortOption != null) {
            // Update the sort button text to show current sort option
            binding.btnSort.setText(sortOption.getDisplayName());
        }
    }

    private void showSortDialog() {
        if (getContext() == null) return;

        // Get all sort options
        SortOption[] sortOptions = SortOption.values();
        String[] optionNames = new String[sortOptions.length];

        for (int i = 0; i < sortOptions.length; i++) {
            optionNames[i] = sortOptions[i].getDisplayName();
        }

        // Get current selected option index
        SortOption currentSort = homeViewModel.getCurrentSortOption().getValue();
        int selectedIndex = 0;
        if (currentSort != null) {
            for (int i = 0; i < sortOptions.length; i++) {
                if (sortOptions[i] == currentSort) {
                    selectedIndex = i;
                    break;
                }
            }
        }

        // Create and show dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Sắp xếp theo")
                .setSingleChoiceItems(optionNames, selectedIndex, (dialog, which) -> {
                    SortOption selectedOption = sortOptions[which];
                    homeViewModel.sortProducts(selectedOption);
                    dialog.dismiss();
                })
                .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    private void showProductsList() {
        hideAllStates();
        binding.recyclerViewProducts.setVisibility(View.VISIBLE);
        binding.nestedScrollView.setVisibility(View.VISIBLE);
    }

    private void showErrorState(String message) {
        hideAllStates();
        binding.errorStateContainer.setVisibility(View.VISIBLE);
        binding.tvError.setText(message);
    }

    private void showEmptyState() {
        hideAllStates();
        binding.emptyStateContainer.setVisibility(View.VISIBLE);
    }

    private void hideAllStates() {
        binding.recyclerViewProducts.setVisibility(View.GONE);
        binding.errorStateContainer.setVisibility(View.GONE);
        binding.emptyStateContainer.setVisibility(View.GONE);
        binding.loadingOverlay.setVisibility(View.GONE);
    }

    @Override
    public void onProductClick(Product product) {
        if (product != null && product.getId() != null && !product.getId().trim().isEmpty()) {
            Intent intent = new Intent(getContext(), ProductDetailActivity.class);
            intent.putExtra(ProductDetailActivity.EXTRA_PRODUCT_ID, product.getId());
            startActivity(intent);
        } else {
            Toast.makeText(getContext(), "Sản phẩm không hợp lệ", Toast.LENGTH_SHORT).show();
        }
    }

    private void initData() {
        if (homeViewModel != null) {
            homeViewModel.initData();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}