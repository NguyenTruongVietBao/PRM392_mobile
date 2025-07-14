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

//Định nghĩa các endpoint
public interface ApiService {
    
    // Authentication APIs
    @POST("api/auth/login")
    Call<ApiResponse<User>> login(@Body LoginRequest loginRequest);
    
    @POST("api/auth/register")
    Call<ApiResponse<User>> register(@Body RegisterRequest registerRequest);
    
    // User APIs
    @GET("api/users/{id}")
    Call<ApiResponse<User>> getUserById(@Path("id") String userId);
    
    @PUT("api/users/{id}")
    Call<ApiResponse<User>> updateUser(@Path("id") String userId, @Body User user);
    
    @GET("api/users")
    Call<ApiResponse<List<User>>> getAllUsers();
    
    // Product APIs
    @GET("api/products")
    Call<ApiResponse<ProductResponse>> getAllProducts(@Query("page") Integer page, 
                                                     @Query("limit") Integer limit, 
                                                     @Query("status") String status);
    
    @GET("api/products/{id}")
    Call<ApiResponse<Product>> getProductById(@Path("id") String productId);
    
    @GET("api/products/category/{categoryId}")
    Call<ApiResponse<ProductResponse>> getProductsByCategory(@Path("categoryId") String categoryId,
                                                            @Query("page") Integer page, 
                                                            @Query("limit") Integer limit);
    
    @GET("api/products/search")
    Call<ApiResponse<ProductResponse>> searchProducts(@Query("q") String keyword,
                                                     @Query("page") Integer page, 
                                                     @Query("limit") Integer limit);
    
    @POST("api/products")
    Call<ApiResponse<Product>> createProduct(@Body Product product);
    
    @PUT("api/products/{id}")
    Call<ApiResponse<Product>> updateProduct(@Path("id") String productId, @Body Product product);
    
    @DELETE("api/products/{id}")
    Call<ApiResponse<Product>> deleteProduct(@Path("id") String productId);
    
    // Category APIs
    @GET("api/categories")
    Call<ApiResponse<List<Category>>> getAllCategories();
    
    @GET("api/categories/{id}")
    Call<ApiResponse<Category>> getCategoryById(@Path("id") String categoryId);
    
    @POST("api/categories")
    Call<ApiResponse<Category>> createCategory(@Body Category category);
    
    @PUT("api/categories/{id}")
    Call<ApiResponse<Category>> updateCategory(@Path("id") String categoryId, @Body Category category);
    
    @DELETE("api/categories/{id}")
    Call<ApiResponse<String>> deleteCategory(@Path("id") String categoryId);
    
    // Order APIs
    @GET("api/orders")
    Call<ApiResponse<OrdersResponse>> getAllOrders();
    
    @GET("api/orders/user/{userId}")
    Call<ApiResponse<OrdersResponse>> getOrdersByUser(@Path("userId") String userId);
    
    @GET("api/orders/{id}")
    Call<ApiResponse<Order>> getOrderById(@Path("id") String orderId);
    
    @POST("api/orders")
    Call<ApiResponse<Order>> createOrder(@Body CreateOrderRequest createOrderRequest);
    
    @PUT("api/orders/{id}")
    Call<ApiResponse<Order>> updateOrder(@Path("id") String orderId, @Body Order order);
    
    @PUT("api/orders/{id}/status")
    Call<ApiResponse<Order>> updateOrderStatus(@Path("id") String orderId, @Body UpdateOrderStatusRequest statusRequest);
    
    @DELETE("api/orders/{id}")
    Call<ApiResponse<String>> deleteOrder(@Path("id") String orderId);
    
    // Cart APIs
    @GET("api/cart/{userId}")
    Call<ApiResponse<Cart>> getCartByUserId(@Path("userId") String userId);

    @GET("api/cart/{userId}/items")
    Call<ApiResponse<CartItemsResponse>> getCartItems(@Path("userId") String userId);
    
    @POST("api/cart/{userId}/items")
    Call<ApiResponse<CartItem>> addItemToCart(@Path("userId") String userId, @Body AddToCartRequest addToCartRequest);

    @PUT("api/cart/{userId}/items/{itemId}")
    Call<ApiResponse<CartItem>> updateCartItem(@Path("userId") String userId, @Path("itemId") String itemId, @Body AddToCartRequest updateCartRequest);
    
    @DELETE("api/cart/{userId}/items/{itemId}")
    Call<ApiResponse<CartItem>> removeCartItem(@Path("userId") String userId, @Path("itemId") String itemId);

    @DELETE("api/cart/{userId}")
    Call<ApiResponse<Cart>> clearCart(@Path("userId") String userId);

    // Chat APIs
    @POST("api/chat/send")
    Call<ApiResponse<ChatResponse>> sendMessage(@Body ChatRequest chatRequest);

    @GET("api/chat/history/{userId}")
    Call<ApiResponse<ChatHistoryResponse>> getChatHistory(@Path("userId") String userId);
}
