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

public class CategoryRepository {
    private ApiService apiService;

    private MutableLiveData<ApiResponse<List<Category>>> categoriesResult = new MutableLiveData<>();
    private MutableLiveData<ApiResponse<Category>> categoryResult = new MutableLiveData<>();
    private MutableLiveData<ApiResponse<Category>> createCategoryResult = new MutableLiveData<>();
    private MutableLiveData<ApiResponse<Category>> updateCategoryResult = new MutableLiveData<>();
    private MutableLiveData<ApiResponse<String>> deleteCategoryResult = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    
    public CategoryRepository() {
        apiService = ApiClient.getApiService();
    }
    
    public LiveData<ApiResponse<List<Category>>> getCategoriesResult() {
        return categoriesResult;
    }
    
    public LiveData<ApiResponse<Category>> getCategoryResult() {
        return categoryResult;
    }
    
    public LiveData<ApiResponse<Category>> getCreateCategoryResult() {
        return createCategoryResult;
    }
    
    public LiveData<ApiResponse<Category>> getUpdateCategoryResult() {
        return updateCategoryResult;
    }
    
    public LiveData<ApiResponse<String>> getDeleteCategoryResult() {
        return deleteCategoryResult;
    }
    
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
    
    public void getAllCategories() {
        Call<ApiResponse<List<Category>>> call = apiService.getAllCategories();
        ApiUtils.executeCall(call, isLoading, categoriesResult, "Không thể lấy danh sách danh mục");
    }
    
    public void getCategoryById(String categoryId) {
        Call<ApiResponse<Category>> call = apiService.getCategoryById(categoryId);
        ApiUtils.executeCall(call, isLoading, categoryResult, "Không thể lấy thông tin danh mục");
    }
    
    public void createCategory(Category category) {
        Call<ApiResponse<Category>> call = apiService.createCategory(category);
        ApiUtils.executeCall(call, isLoading, createCategoryResult, "Không thể tạo danh mục");
    }
    
    public void updateCategory(String categoryId, Category category) {
        Call<ApiResponse<Category>> call = apiService.updateCategory(categoryId, category);
        ApiUtils.executeCall(call, isLoading, updateCategoryResult, "Không thể cập nhật danh mục");
    }
    
    public void deleteCategory(String categoryId) {
        Call<ApiResponse<String>> call = apiService.deleteCategory(categoryId);
        ApiUtils.executeCall(call, isLoading, deleteCategoryResult, "Không thể xóa danh mục");
    }
} 