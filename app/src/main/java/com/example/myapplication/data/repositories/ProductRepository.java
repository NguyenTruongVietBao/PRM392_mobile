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

// Repository xử lý logic liên quan đến sản phẩm (product)
public class ProductRepository {
    // Đối tượng gọi API
    private ApiService apiService;
    
    // LiveData lưu kết quả lấy danh sách sản phẩm (có phân trang)
    private MutableLiveData<ApiResponse<ProductResponse>> productsResult = new MutableLiveData<>();
    // LiveData lưu kết quả lấy một sản phẩm
    private MutableLiveData<ApiResponse<Product>> productResult = new MutableLiveData<>();
    // LiveData lưu kết quả tạo sản phẩm
    private MutableLiveData<ApiResponse<Product>> createProductResult = new MutableLiveData<>();
    // LiveData lưu kết quả cập nhật sản phẩm
    private MutableLiveData<ApiResponse<Product>> updateProductResult = new MutableLiveData<>();
    // LiveData lưu kết quả xóa sản phẩm
    private MutableLiveData<ApiResponse<Product>> deleteProductResult = new MutableLiveData<>();
    // LiveData lưu kết quả tìm kiếm sản phẩm
    private MutableLiveData<ApiResponse<ProductResponse>> searchResult = new MutableLiveData<>();
    // LiveData trạng thái loading (đang xử lý API)
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    
    // Khởi tạo repository, lấy ApiService
    public ProductRepository() {
        apiService = ApiClient.getApiService();
    }
    
    // Trả về LiveData kết quả lấy danh sách sản phẩm
    public LiveData<ApiResponse<ProductResponse>> getProductsResult() {
        return productsResult;
    }
    // Trả về LiveData kết quả lấy một sản phẩm
    public LiveData<ApiResponse<Product>> getProductResult() {
        return productResult;
    }
    // Trả về LiveData kết quả tạo sản phẩm
    public LiveData<ApiResponse<Product>> getCreateProductResult() {
        return createProductResult;
    }
    // Trả về LiveData kết quả cập nhật sản phẩm
    public LiveData<ApiResponse<Product>> getUpdateProductResult() {
        return updateProductResult;
    }
    // Trả về LiveData kết quả xóa sản phẩm
    public LiveData<ApiResponse<Product>> getDeleteProductResult() {
        return deleteProductResult;
    }
    // Trả về LiveData kết quả tìm kiếm sản phẩm
    public LiveData<ApiResponse<ProductResponse>> getSearchResult() {
        return searchResult;
    }
    // Trả về LiveData trạng thái loading
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
    
    // Lấy tất cả sản phẩm (mặc định page=1, limit=50, không lọc status)
    public void getAllProducts() {
        getAllProducts(1, 50, null);
    }
    
    // Lấy tất cả sản phẩm với phân trang và lọc theo status
    public void getAllProducts(int page, int limit, String status) {
        Call<ApiResponse<ProductResponse>> call = apiService.getAllProducts(page, limit, status);
        ApiUtils.executeCall(call, isLoading, productsResult, "Không thể lấy danh sách sản phẩm");
    }

    // Lấy thông tin một sản phẩm theo id
    public void getProductById(String productId) {
        Call<ApiResponse<Product>> call = apiService.getProductById(productId);
        ApiUtils.executeCall(call, isLoading, productResult, "Không thể lấy thông tin sản phẩm");
    }

    // Lấy sản phẩm theo danh mục (mặc định page=1, limit=10)
    public void getProductsByCategory(String categoryId) {
        getProductsByCategory(categoryId, 1, 10);
    }
    
    // Lấy sản phẩm theo danh mục với phân trang
    public void getProductsByCategory(String categoryId, int page, int limit) {
        Call<ApiResponse<ProductResponse>> call = apiService.getProductsByCategory(categoryId, page, limit);
        ApiUtils.executeCall(call, isLoading, productsResult, "Không thể lấy sản phẩm theo danh mục");
    }

    // Tìm kiếm sản phẩm theo từ khóa (mặc định page=1, limit=10)
    public void searchProducts(String keyword) {
        searchProducts(keyword, 1, 10);
    }
    
    // Tìm kiếm sản phẩm theo từ khóa với phân trang
    public void searchProducts(String keyword, int page, int limit) {
        Call<ApiResponse<ProductResponse>> call = apiService.searchProducts(keyword, page, limit);
        ApiUtils.executeCall(call, isLoading, searchResult, "Không thể tìm kiếm sản phẩm");
    }

    // Tạo mới một sản phẩm
    public void createProduct(Product product) {
        Call<ApiResponse<Product>> call = apiService.createProduct(product);
        ApiUtils.executeCall(call, isLoading, createProductResult, "Không thể tạo sản phẩm");
    }

    // Cập nhật thông tin sản phẩm
    public void updateProduct(String productId, Product product) {
        Call<ApiResponse<Product>> call = apiService.updateProduct(productId, product);
        ApiUtils.executeCall(call, isLoading, updateProductResult, "Không thể cập nhật sản phẩm");
    }

    // Xóa một sản phẩm theo id
    public void deleteProduct(String productId) {
        Call<ApiResponse<Product>> call = apiService.deleteProduct(productId);
        ApiUtils.executeCall(call, isLoading, deleteProductResult, "Không thể xóa sản phẩm");
    }
} 