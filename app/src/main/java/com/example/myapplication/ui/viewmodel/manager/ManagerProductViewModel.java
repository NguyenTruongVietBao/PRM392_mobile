package com.example.myapplication.ui.viewmodel.manager;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myapplication.data.models.ApiResponse;
import com.example.myapplication.data.models.Product;
import com.example.myapplication.data.models.Category;
import com.example.myapplication.data.repositories.ProductRepository;
import com.example.myapplication.data.repositories.CategoryRepository;
import com.example.myapplication.network.responses.ProductResponse;
import com.example.myapplication.enums.ProductStatus;
import com.example.myapplication.utils.Constants;

import java.util.List;

public class ManagerProductViewModel extends ViewModel {
    
    private ProductRepository productRepository;
    private CategoryRepository categoryRepository;
    
    // Form validation
    private MutableLiveData<String> validationError = new MutableLiveData<>();
    private MutableLiveData<Boolean> isFormValid = new MutableLiveData<>();
    
    // Search and filter
    private MutableLiveData<String> searchQuery = new MutableLiveData<>();
    private MutableLiveData<ProductStatus> filterStatus = new MutableLiveData<>();
    
    // Current editing product
    private MutableLiveData<Product> editingProduct = new MutableLiveData<>();
    private MutableLiveData<Boolean> isEditMode = new MutableLiveData<>();
    
    public ManagerProductViewModel() {
        productRepository = new ProductRepository();
        categoryRepository = new CategoryRepository();
        isEditMode.setValue(false);
        filterStatus.setValue(ProductStatus.ACTIVE);
    }
    
    // Repository access
    public LiveData<ApiResponse<ProductResponse>> getProductsResult() {
        return productRepository.getProductsResult();
    }
    
    public LiveData<ApiResponse<Product>> getProductResult() {
        return productRepository.getProductResult();
    }
    
    public LiveData<ApiResponse<Product>> getCreateProductResult() {
        return productRepository.getCreateProductResult();
    }
    
    public LiveData<ApiResponse<Product>> getUpdateProductResult() {
        return productRepository.getUpdateProductResult();
    }
    
    public LiveData<ApiResponse<Product>> getDeleteProductResult() {
        return productRepository.getDeleteProductResult();
    }
    
    public LiveData<ApiResponse<ProductResponse>> getSearchResult() {
        return productRepository.getSearchResult();
    }
    
    public LiveData<Boolean> getIsLoading() {
        return productRepository.getIsLoading();
    }
    
    // Category repository access
    public LiveData<ApiResponse<List<Category>>> getCategoriesResult() {
        return categoryRepository.getCategoriesResult();
    }
    
    // Form validation and state
    public LiveData<String> getValidationError() {
        return validationError;
    }
    
    public LiveData<Boolean> getIsFormValid() {
        return isFormValid;
    }
    
    public LiveData<String> getSearchQuery() {
        return searchQuery;
    }
    
    public LiveData<ProductStatus> getFilterStatus() {
        return filterStatus;
    }
    
    public LiveData<Product> getEditingProduct() {
        return editingProduct;
    }
    
    public LiveData<Boolean> getIsEditMode() {
        return isEditMode;
    }
    
    // Actions
    public void loadProducts() {
        productRepository.getAllProducts(1, 50, null); // Load all products by default
    }
    
    public void loadProducts(int page, int limit, String status) {
        productRepository.getAllProducts(page, limit, status);
    }
    
    public void loadCategories() {
        categoryRepository.getAllCategories();
    }
    
    public void searchProducts(String query) {
        searchQuery.setValue(query);
        if (query == null || query.trim().isEmpty()) {
            loadProducts();
        } else {
            productRepository.searchProducts(query.trim());
        }
    }
    
    public void setFilter(ProductStatus status) {
        filterStatus.setValue(status);
        String statusString = null;
        if (status == ProductStatus.ACTIVE) {
            statusString = Constants.PRODUCT_STATUS_ACTIVE;
        } else if (status == ProductStatus.INACTIVE) {
            statusString = Constants.PRODUCT_STATUS_INACTIVE;
        }
        
        String query = searchQuery.getValue();
        if (query != null && !query.trim().isEmpty()) {
            // If there's a search query, search with filter
            productRepository.searchProducts(query.trim());
        } else {
            // Load products with filter
            loadProducts(1, 50, statusString);
        }
    }
    
    public void loadProductForEdit(String productId) {
        if (productId != null) {
            isEditMode.setValue(true);
            productRepository.getProductById(productId);
        } else {
            isEditMode.setValue(false);
            editingProduct.setValue(null);
        }
    }
    
    public void setEditingProduct(Product product) {
        editingProduct.setValue(product);
        isEditMode.setValue(product != null);
    }
    
    public void createProduct(Product product) {
        if (validateProduct(product)) {
            productRepository.createProduct(product);
        }
    }
    
    public void updateProduct(String productId, Product product) {
        if (validateProduct(product)) {
            productRepository.updateProduct(productId, product);
        }
    }
    
    public void deleteProduct(String productId) {
        productRepository.deleteProduct(productId);
    }
    
    public void refreshProducts() {
        String query = searchQuery.getValue();
        if (query != null && !query.trim().isEmpty()) {
            searchProducts(query);
        } else {
            loadProducts();
        }
    }
    
    // Form validation
    public boolean validateProduct(Product product) {
        if (product == null) {
            validationError.setValue("Thông tin sản phẩm không hợp lệ");
            isFormValid.setValue(false);
            return false;
        }
        
        if (product.getName() == null || product.getName().trim().isEmpty()) {
            validationError.setValue("Tên sản phẩm không được để trống");
            isFormValid.setValue(false);
            return false;
        }
        
        if (product.getName().length() < 3) {
            validationError.setValue("Tên sản phẩm phải có ít nhất 3 ký tự");
            isFormValid.setValue(false);
            return false;
        }
        
        if (product.getPrice() <= 0) {
            validationError.setValue("Giá sản phẩm phải lớn hơn 0");
            isFormValid.setValue(false);
            return false;
        }
        
        if (product.getStockQuantity() < 0) {
            validationError.setValue("Số lượng trong kho không được âm");
            isFormValid.setValue(false);
            return false;
        }
        
        if (product.getCategory() == null) {
            validationError.setValue("Vui lòng chọn danh mục sản phẩm");
            isFormValid.setValue(false);
            return false;
        }
        
        // Clear validation error if all checks pass
        validationError.setValue(null);
        isFormValid.setValue(true);
        return true;
    }
    
    public Product createProductFromForm(String name, String description, double price, 
                                       int stockQuantity, Category category, String color, 
                                       String size, boolean isActive, String imageUrl) {
        Product product = new Product();
        product.setName(name != null ? name.trim() : "");
        product.setDescription(description != null ? description.trim() : "");
        product.setPrice(price);
        product.setStockQuantity(stockQuantity);
        product.setCategory(category);
        product.setColor(color != null ? color.trim() : "");
        product.setSize(size != null ? size.trim() : "");
        product.setStatus(isActive ? ProductStatus.ACTIVE : ProductStatus.INACTIVE);
        product.setImageUrl(imageUrl);
        
        return product;
    }
    
    public void clearValidationError() {
        validationError.setValue(null);
    }
    
    public void clearEditingProduct() {
        editingProduct.setValue(null);
        isEditMode.setValue(false);
    }
} 