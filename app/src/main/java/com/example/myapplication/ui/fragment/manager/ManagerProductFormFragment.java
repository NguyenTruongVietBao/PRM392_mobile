package com.example.myapplication.ui.fragment.manager;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.data.models.Category;
import com.example.myapplication.data.models.Product;
import com.example.myapplication.databinding.FragmentManagerProductFormBinding;
import com.example.myapplication.enums.ProductStatus;
import com.example.myapplication.ui.viewmodel.manager.ManagerProductViewModel;
import com.example.myapplication.utils.FirebaseHelper;

import java.util.ArrayList;
import java.util.List;

public class ManagerProductFormFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;

    private FragmentManagerProductFormBinding binding;
    private ManagerProductViewModel viewModel;
    private List<Category> categories = new ArrayList<>();
    private Category selectedCategory;
    private String productId; // For edit mode
    private boolean isEditMode = false;

    // Image handling
    private Uri selectedImageUri;
    private String uploadedImageUrl;
    private FirebaseHelper firebaseHelper;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firebase helper
        firebaseHelper = new FirebaseHelper();

        // Get product ID from arguments for edit mode
        if (getArguments() != null) {
            productId = getArguments().getString("product_id");
            isEditMode = productId != null;
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentManagerProductFormBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupViewModel();
        setupToolbar();
        setupForm();
        setupObservers();

        // Load data
        loadCategories();

        if (isEditMode) {
            loadProductForEdit();
        }
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(ManagerProductViewModel.class);
    }

    private void setupToolbar() {
        binding.toolbar.setTitle(isEditMode ? "Sửa sản phẩm" : "Thêm sản phẩm");
        binding.toolbar.setNavigationOnClickListener(v -> {
            Navigation.findNavController(v).navigateUp();
        });
    }

    private void setupForm() {
        // Setup category dropdown
        setupCategoryDropdown();

        // Setup buttons
        binding.btnCancel.setOnClickListener(v -> {
            Navigation.findNavController(v).navigateUp();
        });

        binding.btnSave.setOnClickListener(v -> {
            saveProduct();
        });

        // Setup image selection
        binding.btnChooseImage.setOnClickListener(v -> {
            openImagePicker();
        });

        // Setup status switch
        binding.switchStatus.setOnCheckedChangeListener((buttonView, isChecked) -> {
            binding.switchStatus.setText(isChecked ? "Đăng bán" : "Ngừng bán");
        });
    }

    private void setupCategoryDropdown() {
        // Setup AutoCompleteTextView for categories
        AutoCompleteTextView categoryDropdown = (AutoCompleteTextView) binding.tilCategory.getEditText();
        if (categoryDropdown != null) {
            categoryDropdown.setOnItemClickListener((parent, view, position, id) -> {
                if (position < categories.size()) {
                    selectedCategory = categories.get(position);
                }
            });
        }
    }

    private void setupObservers() {
        // Observe categories
        viewModel.getCategoriesResult().observe(getViewLifecycleOwner(), apiResponse -> {
            if (apiResponse != null && apiResponse.isSuccess() && apiResponse.getData() != null) {
                categories = apiResponse.getData();
                updateCategoryDropdown();
            } else {
                showError("Không thể tải danh mục sản phẩm");
            }
        });

        // Observe product result (for edit mode)
        viewModel.getProductResult().observe(getViewLifecycleOwner(), apiResponse -> {
            if (apiResponse != null && apiResponse.isSuccess() && apiResponse.getData() != null) {
                populateForm(apiResponse.getData());
            } else if (isEditMode) {
                showError("Không thể tải thông tin sản phẩm");
            }
        });

        // Observe create result
        viewModel.getCreateProductResult().observe(getViewLifecycleOwner(), apiResponse -> {
            if (apiResponse != null) {
                if (apiResponse.isSuccess()) {
                    showSuccess("Thêm sản phẩm thành công");
                    Navigation.findNavController(binding.getRoot()).navigateUp();
                } else {
                    showError("Không thể thêm sản phẩm: " + apiResponse.getMessage());
                }
            }
        });

        // Observe update result
        viewModel.getUpdateProductResult().observe(getViewLifecycleOwner(), apiResponse -> {
            if (apiResponse != null) {
                if (apiResponse.isSuccess()) {
                    showSuccess("Cập nhật sản phẩm thành công");
                    Navigation.findNavController(binding.getRoot()).navigateUp();
                } else {
                    showError("Không thể cập nhật sản phẩm: " + apiResponse.getMessage());
                }
            }
        });

        // Observe loading state
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null) {
                binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
                binding.btnSave.setEnabled(!isLoading);
                binding.btnSave.setText(isLoading ? "Đang lưu..." : "Lưu");
            }
        });

        // Observe validation errors
        viewModel.getValidationError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                binding.tvError.setText(error);
                binding.tvError.setVisibility(View.VISIBLE);
            } else {
                binding.tvError.setVisibility(View.GONE);
            }
        });
    }

    private void updateCategoryDropdown() {
        if (categories.isEmpty()) return;

        List<String> categoryNames = new ArrayList<>();
        for (Category category : categories) {
            categoryNames.add(category.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                categoryNames
        );

        AutoCompleteTextView categoryDropdown = (AutoCompleteTextView) binding.tilCategory.getEditText();
        if (categoryDropdown != null) {
            categoryDropdown.setAdapter(adapter);
        }
    }

    private void populateForm(Product product) {
        binding.etProductName.setText(product.getName());
        binding.etProductDescription.setText(product.getDescription());
        binding.etPrice.setText(String.valueOf(product.getPrice()));
        binding.etStockQuantity.setText(String.valueOf(product.getStockQuantity()));
        binding.etColor.setText(product.getColor());
        binding.etSize.setText(product.getSize());
        binding.switchStatus.setChecked(product.getStatus() == ProductStatus.ACTIVE);

        // Set category
        if (product.getCategory() != null) {
            selectedCategory = product.getCategory();
            AutoCompleteTextView categoryDropdown = (AutoCompleteTextView) binding.tilCategory.getEditText();
            if (categoryDropdown != null) {
                categoryDropdown.setText(product.getCategory().getName(), false);
            }
        }

        // Load product image if available
        if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
            uploadedImageUrl = product.getImageUrl();
            Glide.with(this)
                    .load(product.getImageUrl())
                    .centerCrop()
                    .placeholder(R.drawable.ic_dashboard_black_24dp)
                    .error(R.drawable.ic_dashboard_black_24dp)
                    .into(binding.ivProductImage);
            binding.btnChooseImage.setText("Thay đổi ảnh");
        }
    }

    private void saveProduct() {
        // Clear previous errors
        viewModel.clearValidationError();
        clearFieldErrors();

        // Get form data
        String name = getTextFromEditText(binding.etProductName);
        String description = getTextFromEditText(binding.etProductDescription);
        String priceStr = getTextFromEditText(binding.etPrice);
        String stockStr = getTextFromEditText(binding.etStockQuantity);
        String color = getTextFromEditText(binding.etColor);
        String size = getTextFromEditText(binding.etSize);
        boolean isActive = binding.switchStatus.isChecked();

        // Validate required fields
        boolean hasError = false;

        if (name.isEmpty()) {
            binding.tilProductName.setError("Tên sản phẩm không được để trống");
            hasError = true;
        }

        if (priceStr.isEmpty()) {
            binding.tilPrice.setError("Giá sản phẩm không được để trống");
            hasError = true;
        }

        if (stockStr.isEmpty()) {
            binding.tilStockQuantity.setError("Số lượng không được để trống");
            hasError = true;
        }

        if (selectedCategory == null) {
            binding.tilCategory.setError("Vui lòng chọn danh mục");
            hasError = true;
        }

        if (hasError) {
            return;
        }

        // Parse numeric values
        double price;
        int stockQuantity;

        try {
            price = Double.parseDouble(priceStr);
        } catch (NumberFormatException e) {
            binding.tilPrice.setError("Giá không hợp lệ");
            return;
        }

        try {
            stockQuantity = Integer.parseInt(stockStr);
        } catch (NumberFormatException e) {
            binding.tilStockQuantity.setError("Số lượng không hợp lệ");
            return;
        }

        // Check if need to upload new image
        if (selectedImageUri != null) {
            // Show uploading state
            showUploadingState();
            uploadImageAndSaveProduct(name, description, price, stockQuantity, selectedCategory, color, size, isActive);
        } else {
            // Use existing image URL or null
            saveProductWithImageUrl(name, description, price, stockQuantity, selectedCategory, color, size, isActive, uploadedImageUrl);
        }
    }

    private void showUploadingState() {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.btnSave.setEnabled(false);
        binding.btnSave.setText("Đang tải ảnh...");
    }

    private void hideUploadingState() {
        binding.progressBar.setVisibility(View.GONE);
        binding.btnSave.setEnabled(true);
        binding.btnSave.setText("Lưu");
    }

    private void uploadImageAndSaveProduct(String name, String description, double price,
                                           int stockQuantity, Category category, String color,
                                           String size, boolean isActive) {
        firebaseHelper.uploadImage(selectedImageUri, new FirebaseHelper.OnUploadCompleteListener() {
            @Override
            public void onSuccess(String downloadUrl) {
                uploadedImageUrl = downloadUrl;
                saveProductWithImageUrl(name, description, price, stockQuantity, category, color, size, isActive, downloadUrl);
            }

            @Override
            public void onFailure(String error) {
                hideUploadingState();
                showError("Không thể tải ảnh lên: " + error);
            }
        });
    }

    private void saveProductWithImageUrl(String name, String description, double price,
                                         int stockQuantity, Category category, String color,
                                         String size, boolean isActive, String imageUrl) {
        // Create product object
        Product product = viewModel.createProductFromForm(
                name, description, price, stockQuantity,
                category, color, size, isActive, imageUrl
        );

        // Save product
        if (isEditMode) {
            viewModel.updateProduct(productId, product);
        } else {
            viewModel.createProduct(product);
        }
    }

    private String getTextFromEditText(com.google.android.material.textfield.TextInputEditText editText) {
        return editText.getText() != null ? editText.getText().toString().trim() : "";
    }

    private void clearFieldErrors() {
        binding.tilProductName.setError(null);
        binding.tilProductDescription.setError(null);
        binding.tilPrice.setError(null);
        binding.tilStockQuantity.setError(null);
        binding.tilCategory.setError(null);
        binding.tilColor.setError(null);
        binding.tilSize.setError(null);
    }

    private void loadCategories() {
        viewModel.loadCategories();
    }

    private void loadProductForEdit() {
        viewModel.loadProductForEdit(productId);
    }

    private void showError(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
    }

    private void showSuccess(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void openImagePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Chọn ảnh sản phẩm"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK
                && data != null && data.getData() != null) {
            selectedImageUri = data.getData();

            // Display selected image
            displaySelectedImage();
        }
    }

    private void displaySelectedImage() {
        if (selectedImageUri != null) {
            Glide.with(this)
                    .load(selectedImageUri)
                    .centerCrop()
                    .placeholder(R.drawable.ic_dashboard_black_24dp)
                    .error(R.drawable.ic_dashboard_black_24dp)
                    .into(binding.ivProductImage);

            binding.btnChooseImage.setText("Thay đổi ảnh");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
} 