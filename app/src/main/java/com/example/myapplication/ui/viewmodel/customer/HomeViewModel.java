package com.example.myapplication.ui.viewmodel.customer;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myapplication.data.models.ApiResponse;
import com.example.myapplication.data.models.Product;
import com.example.myapplication.network.responses.ProductResponse;
import com.example.myapplication.data.models.Category;
import com.example.myapplication.data.repositories.ProductRepository;
import com.example.myapplication.data.repositories.CategoryRepository;
import com.example.myapplication.utils.SortOption;
import com.example.myapplication.utils.ProductSortUtils;

import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends ViewModel {

    private ProductRepository productRepository;
    private CategoryRepository categoryRepository;

    private MutableLiveData<List<Product>> products = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private MutableLiveData<Boolean> isEmpty = new MutableLiveData<>();
    private MutableLiveData<List<Category>> categories = new MutableLiveData<>();
    private MutableLiveData<SortOption> currentSortOption = new MutableLiveData<>();

    // Lưu trữ danh sách sản phẩm gốc để sort
    private List<Product> originalProducts = new ArrayList<>();

    private String currentSearchQuery = "";
    private String currentCategoryId = null;

    public HomeViewModel() {
        productRepository = new ProductRepository();
        categoryRepository = new CategoryRepository();
        currentSortOption.setValue(SortOption.DEFAULT);
    }

    // Getters
    public LiveData<ApiResponse<ProductResponse>> getProductsResult() {
        return productRepository.getProductsResult();
    }

    public LiveData<ApiResponse<ProductResponse>> getSearchResult() {
        return productRepository.getSearchResult();
    }

    public LiveData<ApiResponse<List<Category>>> getCategoriesResult() {
        return categoryRepository.getCategoriesResult();
    }

    public LiveData<Boolean> getIsLoading() {
        return productRepository.getIsLoading();
    }

    public LiveData<List<Product>> getProducts() {
        return products;
    }

    public LiveData<List<Category>> getCategories() {
        return categories;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> getIsEmpty() {
        return isEmpty;
    }

    public LiveData<SortOption> getCurrentSortOption() {
        return currentSortOption;
    }

    // Load methods
    public void loadCategories() {
        categoryRepository.getAllCategories();
    }

    public void loadProducts() {
        if (currentSearchQuery != null && !currentSearchQuery.isEmpty()) {
            productRepository.searchProducts(currentSearchQuery);
        } else if (currentCategoryId != null) {
            productRepository.getProductsByCategory(currentCategoryId);
        } else {
            productRepository.getAllProducts();
        }
    }

    public void refreshProducts() {
        loadProducts();
    }

    public void searchProducts(String keyword) {
        currentSearchQuery = keyword != null ? keyword.trim() : "";
        currentCategoryId = null;
        loadProducts();
    }

    public void filterByCategory(String categoryId) {
        currentCategoryId = categoryId;
        currentSearchQuery = "";
        loadProducts();
    }

    public void clearFilters() {
        currentSearchQuery = "";
        currentCategoryId = null;
        loadProducts();
    }

    // Sort methods
    public void sortProducts(SortOption sortOption) {
        if (sortOption == null) {
            sortOption = SortOption.DEFAULT;
        }

        currentSortOption.setValue(sortOption);

        List<Product> currentProducts = new ArrayList<>(originalProducts);
        ProductSortUtils.sortProducts(currentProducts, sortOption);

        setProducts(currentProducts);
    }

    public void applySortToCurrentProducts() {
        SortOption sortOption = currentSortOption.getValue();
        if (sortOption != null) {
            sortProducts(sortOption);
        }
    }

    // Setters
    public void setProducts(List<Product> productList) {
        // Lưu trữ danh sách gốc
        if (productList != null) {
            this.originalProducts = new ArrayList<>(productList);
        } else {
            this.originalProducts = new ArrayList<>();
        }

        // Áp dụng sort nếu có
        List<Product> sortedProducts = new ArrayList<>(this.originalProducts);
        SortOption sortOption = currentSortOption.getValue();
        if (sortOption != null && sortOption != SortOption.DEFAULT) {
            ProductSortUtils.sortProducts(sortedProducts, sortOption);
        }

        this.products.setValue(sortedProducts);
        this.isEmpty.setValue(sortedProducts.isEmpty());
    }

    public void setCategories(List<Category> categoryList) {
        this.categories.setValue(categoryList);
    }

    public void setErrorMessage(String message) {
        this.errorMessage.setValue(message);
    }

    public void initData() {
        loadCategories();
        loadProducts();
    }
}