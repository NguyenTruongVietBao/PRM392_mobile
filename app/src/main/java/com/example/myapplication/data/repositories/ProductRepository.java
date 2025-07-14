package com.example.myapplication.data.repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import retrofit2.Call;

import com.example.myapplication.data.models.ApiResponse;
import com.example.myapplication.data.models.Product;
import com.example.myapplication.network.responses.ProductResponse;
import com.example.myapplication.network.ApiClient;
import com.example.myapplication.network.ApiService;
import com.example.myapplication.network.ApiUtils;

public class ProductRepository {
    private ApiService apiService;
    
    private MutableLiveData<ApiResponse<ProductResponse>> productsResult = new MutableLiveData<>();
    private MutableLiveData<ApiResponse<Product>> productResult = new MutableLiveData<>();
    private MutableLiveData<ApiResponse<Product>> createProductResult = new MutableLiveData<>();
    private MutableLiveData<ApiResponse<Product>> updateProductResult = new MutableLiveData<>();
    private MutableLiveData<ApiResponse<Product>> deleteProductResult = new MutableLiveData<>();
    private MutableLiveData<ApiResponse<ProductResponse>> searchResult = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    
    public ProductRepository() {
        apiService = ApiClient.getApiService();
    }
    
    public LiveData<ApiResponse<ProductResponse>> getProductsResult() {
        return productsResult;
    }
    
    public LiveData<ApiResponse<Product>> getProductResult() {
        return productResult;
    }
    
    public LiveData<ApiResponse<Product>> getCreateProductResult() {
        return createProductResult;
    }
    
    public LiveData<ApiResponse<Product>> getUpdateProductResult() {
        return updateProductResult;
    }
    
    public LiveData<ApiResponse<Product>> getDeleteProductResult() {
        return deleteProductResult;
    }
    
    public LiveData<ApiResponse<ProductResponse>> getSearchResult() {
        return searchResult;
    }
    
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
    
    public void getAllProducts() {
        getAllProducts(1, 50, null);
    }
    
    public void getAllProducts(int page, int limit, String status) {
        Call<ApiResponse<ProductResponse>> call = apiService.getAllProducts(page, limit, status);
        ApiUtils.executeCall(call, isLoading, productsResult, "Không thể lấy danh sách sản phẩm");
    }

    public void getProductById(String productId) {
        Call<ApiResponse<Product>> call = apiService.getProductById(productId);
        ApiUtils.executeCall(call, isLoading, productResult, "Không thể lấy thông tin sản phẩm");
    }

    public void getProductsByCategory(String categoryId) {
        getProductsByCategory(categoryId, 1, 10);
    }
    
    public void getProductsByCategory(String categoryId, int page, int limit) {
        Call<ApiResponse<ProductResponse>> call = apiService.getProductsByCategory(categoryId, page, limit);
        ApiUtils.executeCall(call, isLoading, productsResult, "Không thể lấy sản phẩm theo danh mục");
    }

    public void searchProducts(String keyword) {
        searchProducts(keyword, 1, 10);
    }
    
    public void searchProducts(String keyword, int page, int limit) {
        Call<ApiResponse<ProductResponse>> call = apiService.searchProducts(keyword, page, limit);
        ApiUtils.executeCall(call, isLoading, searchResult, "Không thể tìm kiếm sản phẩm");
    }

    public void createProduct(Product product) {
        Call<ApiResponse<Product>> call = apiService.createProduct(product);
        ApiUtils.executeCall(call, isLoading, createProductResult, "Không thể tạo sản phẩm");
    }

    public void updateProduct(String productId, Product product) {
        Call<ApiResponse<Product>> call = apiService.updateProduct(productId, product);
        ApiUtils.executeCall(call, isLoading, updateProductResult, "Không thể cập nhật sản phẩm");
    }

    public void deleteProduct(String productId) {
        Call<ApiResponse<Product>> call = apiService.deleteProduct(productId);
        ApiUtils.executeCall(call, isLoading, deleteProductResult, "Không thể xóa sản phẩm");
    }
} 