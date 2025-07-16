package com.example.myapplication.network;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.DELETE;
import retrofit2.http.Path;
import retrofit2.http.Query;

import com.example.myapplication.data.models.ApiResponse;
import com.example.myapplication.data.models.User;
import com.example.myapplication.data.models.Product;
import com.example.myapplication.network.responses.ProductResponse;
import com.example.myapplication.network.responses.OrdersResponse;
import com.example.myapplication.data.models.Category;
import com.example.myapplication.data.models.Order;
import com.example.myapplication.data.models.Cart;
import com.example.myapplication.data.models.CartItem;
import com.example.myapplication.network.responses.CartItemsResponse;
import com.example.myapplication.network.requests.LoginRequest;
import com.example.myapplication.network.requests.RegisterRequest;
import com.example.myapplication.network.requests.AddToCartRequest;
import com.example.myapplication.network.requests.CreateOrderRequest;
import com.example.myapplication.network.requests.UpdateOrderStatusRequest;
import com.example.myapplication.network.requests.ChatRequest;
import com.example.myapplication.network.responses.ChatResponse;
import com.example.myapplication.network.responses.ChatHistoryResponse;

import java.util.List;

// Định nghĩa các endpoint API sử dụng retrofit cho toàn bộ ứng dụng
public interface ApiService {
    
    // ===== Authentication APIs =====
    // Đăng nhập
    @POST("api/auth/login")
    Call<ApiResponse<User>> login(@Body LoginRequest loginRequest);
    // Đăng ký
    @POST("api/auth/register")
    Call<ApiResponse<User>> register(@Body RegisterRequest registerRequest);
    
    // ===== User APIs =====
    // Lấy thông tin user theo id
    @GET("api/users/{id}")
    Call<ApiResponse<User>> getUserById(@Path("id") String userId);
    // Cập nhật thông tin user
    @PUT("api/users/{id}")
    Call<ApiResponse<User>> updateUser(@Path("id") String userId, @Body User user);
    // Lấy danh sách tất cả user
    @GET("api/users")
    Call<ApiResponse<List<User>>> getAllUsers();
    
    // ===== Product APIs =====
    // Lấy danh sách sản phẩm (có phân trang, lọc status)
    @GET("api/products")
    Call<ApiResponse<ProductResponse>> getAllProducts(@Query("page") Integer page, 
                                                     @Query("limit") Integer limit, 
                                                     @Query("status") String status);
    // Lấy thông tin sản phẩm theo id
    @GET("api/products/{id}")
    Call<ApiResponse<Product>> getProductById(@Path("id") String productId);
    // Lấy sản phẩm theo danh mục (có phân trang)
    @GET("api/products/category/{categoryId}")
    Call<ApiResponse<ProductResponse>> getProductsByCategory(@Path("categoryId") String categoryId,
                                                            @Query("page") Integer page, 
                                                            @Query("limit") Integer limit);
    // Tìm kiếm sản phẩm theo từ khóa (có phân trang)
    @GET("api/products/search")
    Call<ApiResponse<ProductResponse>> searchProducts(@Query("q") String keyword,
                                                     @Query("page") Integer page, 
                                                     @Query("limit") Integer limit);
    // Tạo mới sản phẩm
    @POST("api/products")
    Call<ApiResponse<Product>> createProduct(@Body Product product);
    // Cập nhật sản phẩm
    @PUT("api/products/{id}")
    Call<ApiResponse<Product>> updateProduct(@Path("id") String productId, @Body Product product);
    // Xóa sản phẩm
    @DELETE("api/products/{id}")
    Call<ApiResponse<Product>> deleteProduct(@Path("id") String productId);
    
    // ===== Category APIs =====
    // Lấy tất cả danh mục
    @GET("api/categories")
    Call<ApiResponse<List<Category>>> getAllCategories();
    // Lấy thông tin danh mục theo id
    @GET("api/categories/{id}")
    Call<ApiResponse<Category>> getCategoryById(@Path("id") String categoryId);
    // Tạo mới danh mục
    @POST("api/categories")
    Call<ApiResponse<Category>> createCategory(@Body Category category);
    // Cập nhật danh mục
    @PUT("api/categories/{id}")
    Call<ApiResponse<Category>> updateCategory(@Path("id") String categoryId, @Body Category category);
    // Xóa danh mục
    @DELETE("api/categories/{id}")
    Call<ApiResponse<String>> deleteCategory(@Path("id") String categoryId);
    
    // ===== Order APIs =====
    // Lấy tất cả đơn hàng
    @GET("api/orders")
    Call<ApiResponse<OrdersResponse>> getAllOrders();
    // Lấy đơn hàng theo user
    @GET("api/orders/user/{userId}")
    Call<ApiResponse<OrdersResponse>> getOrdersByUser(@Path("userId") String userId);
    // Lấy thông tin đơn hàng theo id
    @GET("api/orders/{id}")
    Call<ApiResponse<Order>> getOrderById(@Path("id") String orderId);
    // Tạo mới đơn hàng
    @POST("api/orders")
    Call<ApiResponse<Order>> createOrder(@Body CreateOrderRequest createOrderRequest);
    // Cập nhật đơn hàng
    @PUT("api/orders/{id}")
    Call<ApiResponse<Order>> updateOrder(@Path("id") String orderId, @Body Order order);
    // Cập nhật trạng thái đơn hàng
    @PUT("api/orders/{id}/status")
    Call<ApiResponse<Order>> updateOrderStatus(@Path("id") String orderId, @Body UpdateOrderStatusRequest statusRequest);
    // Xóa/hủy đơn hàng
    @DELETE("api/orders/{id}")
    Call<ApiResponse<String>> deleteOrder(@Path("id") String orderId);
    
    // ===== Cart APIs =====
    // Lấy giỏ hàng của user
    @GET("api/cart/{userId}")
    Call<ApiResponse<Cart>> getCartByUserId(@Path("userId") String userId);
    // Lấy danh sách sản phẩm trong giỏ hàng
    @GET("api/cart/{userId}/items")
    Call<ApiResponse<CartItemsResponse>> getCartItems(@Path("userId") String userId);
    // Thêm sản phẩm vào giỏ hàng
    @POST("api/cart/{userId}/items")
    Call<ApiResponse<CartItem>> addItemToCart(@Path("userId") String userId, @Body AddToCartRequest addToCartRequest);
    // Cập nhật số lượng sản phẩm trong giỏ hàng
    @PUT("api/cart/{userId}/items/{itemId}")
    Call<ApiResponse<CartItem>> updateCartItem(@Path("userId") String userId, @Path("itemId") String itemId, @Body AddToCartRequest updateCartRequest);
    // Xóa một sản phẩm khỏi giỏ hàng
    @DELETE("api/cart/{userId}/items/{itemId}")
    Call<ApiResponse<CartItem>> removeCartItem(@Path("userId") String userId, @Path("itemId") String itemId);
    // Xóa toàn bộ giỏ hàng
    @DELETE("api/cart/{userId}")
    Call<ApiResponse<Cart>> clearCart(@Path("userId") String userId);
    
    // ===== Chat APIs =====
    // Gửi tin nhắn chat
    @POST("api/chat/send")
    Call<ApiResponse<ChatResponse>> sendMessage(@Body ChatRequest chatRequest);
    // Lấy lịch sử chat của user
    @GET("api/chat/history/{userId}")
    Call<ApiResponse<ChatHistoryResponse>> getChatHistory(@Path("userId") String userId);
}
