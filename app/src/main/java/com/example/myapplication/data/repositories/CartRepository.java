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

// Repository xử lý logic liên quan đến giỏ hàng (cart) của người dùng
public class CartRepository {
    // Đối tượng gọi API
    private ApiService apiService;
    
    // LiveData lưu kết quả lấy giỏ hàng
    private MutableLiveData<ApiResponse<Cart>> cartResult = new MutableLiveData<>();
    // LiveData lưu danh sách sản phẩm trong giỏ hàng (dạng List<CartItem>)
    private MutableLiveData<ApiResponse<List<CartItem>>> cartItemsResult = new MutableLiveData<>();
    // LiveData lưu kết quả lấy giỏ hàng đầy đủ (CartItemsResponse)
    private MutableLiveData<ApiResponse<CartItemsResponse>> cartItemsFullResult = new MutableLiveData<>();
    // LiveData lưu kết quả thao tác với từng CartItem
    private MutableLiveData<ApiResponse<CartItem>> cartItemResult = new MutableLiveData<>();
    // LiveData trạng thái loading (đang xử lý API)
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    
    // Khởi tạo repository, lấy ApiService
    public CartRepository() {
        apiService = ApiClient.getApiService();
    }
    
    // Trả về LiveData kết quả lấy giỏ hàng
    public LiveData<ApiResponse<Cart>> getCartResult() { return cartResult; }
    // Trả về LiveData danh sách sản phẩm trong giỏ hàng (List<CartItem>)
    public LiveData<ApiResponse<List<CartItem>>> getCartItemsResult() { return cartItemsResult; }
    // Trả về LiveData kết quả lấy giỏ hàng đầy đủ (CartItemsResponse)
    public LiveData<ApiResponse<CartItemsResponse>> getCartItemsFullResult() { return cartItemsFullResult; }
    // Trả về LiveData thao tác với từng CartItem
    public LiveData<ApiResponse<CartItem>> getCartItemResult() { return cartItemResult; }
    // Trả về LiveData trạng thái loading
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    
    // Lấy thông tin giỏ hàng của user theo userId
    public void getCartByUserId(String userId) {
        Call<ApiResponse<Cart>> call = apiService.getCartByUserId(userId);
        ApiUtils.executeCall(call, isLoading, cartResult, "Không thể lấy giỏ hàng");
    }

    // Lấy danh sách sản phẩm trong giỏ hàng của user
    public void getCartItems(String userId) {
        Call<ApiResponse<CartItemsResponse>> call = apiService.getCartItems(userId);
        
        // Gọi API và xử lý kết quả trả về
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
    
    // Thêm sản phẩm vào giỏ hàng
    public void addItemToCart(String userId, String productId, int quantity) {
        AddToCartRequest request = new AddToCartRequest(productId, quantity);
        Call<ApiResponse<CartItem>> call = apiService.addItemToCart(userId, request);
        ApiUtils.executeCall(call, isLoading, cartItemResult, "Không thể thêm vào giỏ hàng",
            response -> android.util.Log.d("CartRepository", "Added item to cart successfully: " + productId),
            error -> android.util.Log.e("CartRepository", "Failed to add item to cart: " + error.getMessage())
        );
    }
    
    // Cập nhật số lượng sản phẩm trong giỏ hàng
    public void updateCartItem(String userId, String itemId, int quantity) {
        AddToCartRequest request = new AddToCartRequest(null, quantity); // productId is not needed for update
        Call<ApiResponse<CartItem>> call = apiService.updateCartItem(userId, itemId, request);
        ApiUtils.executeCall(call, isLoading, cartItemResult, "Không thể cập nhật giỏ hàng",
            response -> android.util.Log.d("CartRepository", "Updated cart item successfully: " + itemId + ", quantity: " + quantity),
            error -> android.util.Log.e("CartRepository", "Failed to update cart item: " + error.getMessage())
        );
    }
    
    // Xóa một sản phẩm khỏi giỏ hàng
    public void removeCartItem(String userId, String itemId) {
        Call<ApiResponse<CartItem>> call = apiService.removeCartItem(userId, itemId);
        ApiUtils.executeCall(call, isLoading, cartItemResult, "Không thể xóa sản phẩm khỏi giỏ hàng",
            response -> android.util.Log.d("CartRepository", "Removed cart item successfully: " + itemId),
            error -> android.util.Log.e("CartRepository", "Failed to remove cart item: " + error.getMessage())
        );
    }
    
    // Xóa toàn bộ giỏ hàng của user
    public void clearCart(String userId) {
        Call<ApiResponse<Cart>> call = apiService.clearCart(userId);
        ApiUtils.executeCall(call, isLoading, cartResult, "Không thể xóa giỏ hàng",
            response -> android.util.Log.d("CartRepository", "Cleared cart successfully for user: " + userId),
            error -> android.util.Log.e("CartRepository", "Failed to clear cart: " + error.getMessage())
        );
    }
} 