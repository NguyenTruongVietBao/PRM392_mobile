package com.example.myapplication.ui.viewmodel.customer;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.myapplication.data.models.ApiResponse;
import com.example.myapplication.data.models.Product;
import com.example.myapplication.network.responses.ProductResponse;
import com.example.myapplication.data.repositories.ProductRepository;

public class ProductDetailViewModel extends ViewModel {
    private ProductRepository productRepository;

    public ProductDetailViewModel() {
        productRepository = new ProductRepository();
    }

    // Get single product result
    public LiveData<ApiResponse<Product>> getProductResult() {
        return productRepository.getProductResult();
    }

    // Get products list result (for related products)
    public LiveData<ApiResponse<ProductResponse>> getProductsResult() {
        return productRepository.getProductsResult();
    }

    // Get loading state
    public LiveData<Boolean> getIsLoading() {
        return productRepository.getIsLoading();
    }

    // Get product by ID
    public void getProductById(String productId) {
        productRepository.getProductById(productId);
    }

    // Get products by category (for related products)
    public void getProductsByCategory(String categoryId, int page, int limit) {
        productRepository.getProductsByCategory(categoryId, page, limit);
    }

    // Get products by category with default pagination
    public void getProductsByCategory(String categoryId) {
        productRepository.getProductsByCategory(categoryId, 1, 10);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        // Clean up if needed
    }
}