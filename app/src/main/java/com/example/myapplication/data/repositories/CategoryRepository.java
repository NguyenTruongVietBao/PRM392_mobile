package com.example.myapplication.data.repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import retrofit2.Call;

import com.example.myapplication.data.models.ApiResponse;
import com.example.myapplication.data.models.Category;
import com.example.myapplication.network.ApiClient;
import com.example.myapplication.network.ApiService;
import com.example.myapplication.network.ApiUtils;

import java.util.List;

// Repository xử lý logic liên quan đến danh mục sản phẩm (category)
public class CategoryRepository {
    // Đối tượng gọi API
    private ApiService apiService;

    // LiveData lưu kết quả lấy danh sách danh mục
    private MutableLiveData<ApiResponse<List<Category>>> categoriesResult = new MutableLiveData<>();
    // LiveData lưu kết quả lấy một danh mục
    private MutableLiveData<ApiResponse<Category>> categoryResult = new MutableLiveData<>();
    // LiveData lưu kết quả tạo danh mục
    private MutableLiveData<ApiResponse<Category>> createCategoryResult = new MutableLiveData<>();
    // LiveData lưu kết quả cập nhật danh mục
    private MutableLiveData<ApiResponse<Category>> updateCategoryResult = new MutableLiveData<>();
    // LiveData lưu kết quả xóa danh mục
    private MutableLiveData<ApiResponse<String>> deleteCategoryResult = new MutableLiveData<>();
    // LiveData trạng thái loading (đang xử lý API)
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    
    // Khởi tạo repository, lấy ApiService
    public CategoryRepository() {
        apiService = ApiClient.getApiService();
    }
    
    // Trả về LiveData kết quả lấy danh sách danh mục
    public LiveData<ApiResponse<List<Category>>> getCategoriesResult() {
        return categoriesResult;
    }
    // Trả về LiveData kết quả lấy một danh mục
    public LiveData<ApiResponse<Category>> getCategoryResult() {
        return categoryResult;
    }
    // Trả về LiveData kết quả tạo danh mục
    public LiveData<ApiResponse<Category>> getCreateCategoryResult() {
        return createCategoryResult;
    }
    // Trả về LiveData kết quả cập nhật danh mục
    public LiveData<ApiResponse<Category>> getUpdateCategoryResult() {
        return updateCategoryResult;
    }
    // Trả về LiveData kết quả xóa danh mục
    public LiveData<ApiResponse<String>> getDeleteCategoryResult() {
        return deleteCategoryResult;
    }
    // Trả về LiveData trạng thái loading
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
    
    // Lấy tất cả danh mục sản phẩm
    public void getAllCategories() {
        Call<ApiResponse<List<Category>>> call = apiService.getAllCategories();
        ApiUtils.executeCall(call, isLoading, categoriesResult, "Không thể lấy danh sách danh mục");
    }
    
    // Lấy thông tin một danh mục theo id
    public void getCategoryById(String categoryId) {
        Call<ApiResponse<Category>> call = apiService.getCategoryById(categoryId);
        ApiUtils.executeCall(call, isLoading, categoryResult, "Không thể lấy thông tin danh mục");
    }
    
    // Tạo mới một danh mục
    public void createCategory(Category category) {
        Call<ApiResponse<Category>> call = apiService.createCategory(category);
        ApiUtils.executeCall(call, isLoading, createCategoryResult, "Không thể tạo danh mục");
    }
    
    // Cập nhật thông tin danh mục
    public void updateCategory(String categoryId, Category category) {
        Call<ApiResponse<Category>> call = apiService.updateCategory(categoryId, category);
        ApiUtils.executeCall(call, isLoading, updateCategoryResult, "Không thể cập nhật danh mục");
    }
    
    // Xóa một danh mục theo id
    public void deleteCategory(String categoryId) {
        Call<ApiResponse<String>> call = apiService.deleteCategory(categoryId);
        ApiUtils.executeCall(call, isLoading, deleteCategoryResult, "Không thể xóa danh mục");
    }
} 