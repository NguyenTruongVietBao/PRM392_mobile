package com.example.myapplication.data.repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import retrofit2.Call;

import com.example.myapplication.data.models.ApiResponse;
import com.example.myapplication.data.models.Cart;
import com.example.myapplication.data.models.CartItem;
import com.example.myapplication.network.responses.CartItemsResponse;
import com.example.myapplication.network.ApiClient;
import com.example.myapplication.network.ApiService;
import com.example.myapplication.network.ApiUtils;
import com.example.myapplication.network.requests.AddToCartRequest;

import java.util.List;

public class CartRepository {
    private ApiService apiService;
    
    private MutableLiveData<ApiResponse<Cart>> cartResult = new MutableLiveData<>();
    private MutableLiveData<ApiResponse<List<CartItem>>> cartItemsResult = new MutableLiveData<>();
    private MutableLiveData<ApiResponse<CartItemsResponse>> cartItemsFullResult = new MutableLiveData<>();
    private MutableLiveData<ApiResponse<CartItem>> cartItemResult = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    
    public CartRepository() {
        apiService = ApiClient.getApiService();
    }
    
    public LiveData<ApiResponse<Cart>> getCartResult() { return cartResult; }
    public LiveData<ApiResponse<List<CartItem>>> getCartItemsResult() { return cartItemsResult; }
    public LiveData<ApiResponse<CartItemsResponse>> getCartItemsFullResult() { return cartItemsFullResult; }
    public LiveData<ApiResponse<CartItem>> getCartItemResult() { return cartItemResult; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    
    public void getCartByUserId(String userId) {
        Call<ApiResponse<Cart>> call = apiService.getCartByUserId(userId);
        ApiUtils.executeCall(call, isLoading, cartResult, "Không thể lấy giỏ hàng");
    }

    public void getCartItems(String userId) {
        Call<ApiResponse<CartItemsResponse>> call = apiService.getCartItems(userId);
        
        ApiUtils.executeCall(call, isLoading, cartItemsFullResult, "Không thể lấy sản phẩm trong giỏ hàng",
            response -> {
                if (response.getData() != null && response.getData().getItems() != null) {
                    List<CartItem> items = response.getData().getItems();
                    ApiResponse<List<CartItem>> itemsResponse = new ApiResponse<>();
                    itemsResponse.setSuccess(true);
                    itemsResponse.setStatusCode(response.getStatusCode());
                    itemsResponse.setMessage(response.getMessage());
                    itemsResponse.setData(items);
                    cartItemsResult.setValue(itemsResponse);
                } else {
                    ApiResponse<List<CartItem>> errorResponse = new ApiResponse<>();
                    errorResponse.setSuccess(false);
                    errorResponse.setMessage("Không có dữ liệu giỏ hàng");
                    cartItemsResult.setValue(errorResponse);
                }
            }
        );
    }
    
    public void addItemToCart(String userId, String productId, int quantity) {
        AddToCartRequest request = new AddToCartRequest(productId, quantity);
        Call<ApiResponse<CartItem>> call = apiService.addItemToCart(userId, request);
        ApiUtils.executeCall(call, isLoading, cartItemResult, "Không thể thêm vào giỏ hàng",
            response -> android.util.Log.d("CartRepository", "Added item to cart successfully: " + productId),
            error -> android.util.Log.e("CartRepository", "Failed to add item to cart: " + error.getMessage())
        );
    }
    
    public void updateCartItem(String userId, String itemId, int quantity) {
        AddToCartRequest request = new AddToCartRequest(null, quantity); // productId is not needed for update
        Call<ApiResponse<CartItem>> call = apiService.updateCartItem(userId, itemId, request);
        ApiUtils.executeCall(call, isLoading, cartItemResult, "Không thể cập nhật giỏ hàng",
            response -> android.util.Log.d("CartRepository", "Updated cart item successfully: " + itemId + ", quantity: " + quantity),
            error -> android.util.Log.e("CartRepository", "Failed to update cart item: " + error.getMessage())
        );
    }
    
    public void removeCartItem(String userId, String itemId) {
        Call<ApiResponse<CartItem>> call = apiService.removeCartItem(userId, itemId);
        ApiUtils.executeCall(call, isLoading, cartItemResult, "Không thể xóa sản phẩm khỏi giỏ hàng",
            response -> android.util.Log.d("CartRepository", "Removed cart item successfully: " + itemId),
            error -> android.util.Log.e("CartRepository", "Failed to remove cart item: " + error.getMessage())
        );
    }
    
    public void clearCart(String userId) {
        Call<ApiResponse<Cart>> call = apiService.clearCart(userId);
        ApiUtils.executeCall(call, isLoading, cartResult, "Không thể xóa giỏ hàng",
            response -> android.util.Log.d("CartRepository", "Cleared cart successfully for user: " + userId),
            error -> android.util.Log.e("CartRepository", "Failed to clear cart: " + error.getMessage())
        );
    }
} 