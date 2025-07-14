package com.example.myapplication.ui.fragment.manager;

import android.app.AlertDialog;
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
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.myapplication.R;
import com.example.myapplication.data.models.Product;
import com.example.myapplication.databinding.FragmentManagerProductListBinding;
import com.example.myapplication.enums.ProductStatus;
import com.example.myapplication.ui.adapter.ManagerProductAdapter;
import com.example.myapplication.ui.viewmodel.manager.ManagerProductViewModel;
import com.google.android.material.chip.Chip;

import java.util.List;

public class ManagerProductListFragment extends Fragment implements ManagerProductAdapter.OnProductActionListener {
    
    private FragmentManagerProductListBinding binding;
    private ManagerProductViewModel viewModel;
    private ManagerProductAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        binding = FragmentManagerProductListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        setupViewModel();
        setupRecyclerView();
        setupSearchView();
        setupFilterChips();
        setupSwipeRefresh();
        setupFab();
        setupObservers();
        
        // Load initial data
        loadData();
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(ManagerProductViewModel.class);
    }

    private void setupRecyclerView() {
        adapter = new ManagerProductAdapter(requireContext());
        adapter.setOnProductActionListener(this);
        
        binding.rvProducts.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvProducts.setAdapter(adapter);
    }

    private void setupSearchView() {
        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String query = s.toString().trim();
                viewModel.searchProducts(query);
            }
        });
    }

    private void setupFilterChips() {
        // Set up filter chips
        binding.chipAll.setOnCheckedChangeListener((button, isChecked) -> {
            if (isChecked) {
                clearOtherChips(binding.chipAll);
                viewModel.setFilter(null); // Show all products
            }
        });

        binding.chipActive.setOnCheckedChangeListener((button, isChecked) -> {
            if (isChecked) {
                clearOtherChips(binding.chipActive);
                viewModel.setFilter(ProductStatus.ACTIVE);
            }
        });

        binding.chipInactive.setOnCheckedChangeListener((button, isChecked) -> {
            if (isChecked) {
                clearOtherChips(binding.chipInactive);
                viewModel.setFilter(ProductStatus.INACTIVE);
            }
        });

        binding.chipOutOfStock.setOnCheckedChangeListener((button, isChecked) -> {
            if (isChecked) {
                clearOtherChips(binding.chipOutOfStock);
                // Filter products by stock quantity = 0 (out of stock)
                // We'll show all products and let adapter display "Hết hàng" for stock=0
                viewModel.setFilter(null); // Show all, stock filter handled in display logic
            }
        });
    }

    private void clearOtherChips(Chip selectedChip) {
        if (selectedChip != binding.chipAll) binding.chipAll.setChecked(false);
        if (selectedChip != binding.chipActive) binding.chipActive.setChecked(false);
        if (selectedChip != binding.chipInactive) binding.chipInactive.setChecked(false);
        if (selectedChip != binding.chipOutOfStock) binding.chipOutOfStock.setChecked(false);
    }

    private void setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener(() -> {
            viewModel.refreshProducts();
        });
    }

    private void setupFab() {
        binding.fabAddProduct.setOnClickListener(v -> {
            // Navigate to product form for creating new product
            Navigation.findNavController(v).navigate(
                R.id.action_product_list_to_form
            );
        });
    }

    private void setupObservers() {
        // Observe products result
        viewModel.getProductsResult().observe(getViewLifecycleOwner(), apiResponse -> {
            if (apiResponse != null) {
                if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                    List<Product> products = apiResponse.getData().getProducts();
                    updateProductList(products);
                } else {
                    showError("Không thể tải danh sách sản phẩm: " + apiResponse.getMessage());
                }
            }
        });

        // Observe search result
        viewModel.getSearchResult().observe(getViewLifecycleOwner(), apiResponse -> {
            if (apiResponse != null) {
                if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                    List<Product> products = apiResponse.getData().getProducts();
                    updateProductList(products);
                } else {
                    showError("Không thể tìm kiếm sản phẩm: " + apiResponse.getMessage());
                }
            }
        });

        // Observe loading state
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null) {
                binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
                binding.swipeRefresh.setRefreshing(isLoading);
            }
        });

        // Observe delete result
        viewModel.getDeleteProductResult().observe(getViewLifecycleOwner(), apiResponse -> {
            if (apiResponse != null) {
                if (apiResponse.isSuccess()) {
                    showSuccess("Xóa sản phẩm thành công");
                    viewModel.refreshProducts(); // Refresh the list
                } else {
                    showError("Không thể xóa sản phẩm: " + apiResponse.getMessage());
                }
            }
        });
    }

    private void updateProductList(List<Product> products) {
        if (products != null && !products.isEmpty()) {
            adapter.submitList(products);
            binding.layoutEmpty.setVisibility(View.GONE);
            binding.rvProducts.setVisibility(View.VISIBLE);
        } else {
            binding.layoutEmpty.setVisibility(View.VISIBLE);
            binding.rvProducts.setVisibility(View.GONE);
        }
    }

    private void loadData() {
        viewModel.loadProducts();
        viewModel.loadCategories(); // For form fragment
    }

    private void showError(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
    }

    private void showSuccess(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    // ManagerProductAdapter.OnProductActionListener implementation
    @Override
    public void onEditProduct(Product product) {
        // Navigate to product form with product ID for editing
        Bundle args = new Bundle();
        args.putString("product_id", product.getId());
        
        Navigation.findNavController(binding.getRoot()).navigate(
            R.id.action_product_list_to_form,
            args
        );
    }

    @Override
    public void onDeleteProduct(Product product) {
        new AlertDialog.Builder(requireContext())
            .setTitle("Xác nhận xóa")
            .setMessage("Bạn có chắc chắn muốn xóa sản phẩm '" + product.getName() + "'?")
            .setPositiveButton("Xóa", (dialog, which) -> {
                viewModel.deleteProduct(product.getId());
            })
            .setNegativeButton("Hủy", null)
            .show();
    }

    @Override
    public void onProductClick(Product product) {
        // For now, just trigger edit. Later could navigate to detail view
        onEditProduct(product);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
} 