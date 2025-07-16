package com.example.myapplication.data.repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.example.myapplication.data.models.ApiResponse;
import com.example.myapplication.data.models.Order;
import com.example.myapplication.network.ApiClient;
import com.example.myapplication.network.ApiService;
import com.example.myapplication.network.ApiUtils;
import com.example.myapplication.network.requests.CreateOrderRequest;
import com.example.myapplication.network.requests.UpdateOrderStatusRequest;
import com.example.myapplication.network.responses.OrdersResponse;
import com.example.myapplication.enums.OrderStatus;

import java.util.List;

// Repository xử lý logic liên quan đến đơn hàng (order)
public class OrderRepository {
    // Đối tượng gọi API
    private ApiService apiService;
    
    // LiveData lưu kết quả lấy danh sách đơn hàng
    private MutableLiveData<ApiResponse<List<Order>>> ordersResult = new MutableLiveData<>();
    // LiveData lưu kết quả lấy một đơn hàng
    private MutableLiveData<ApiResponse<Order>> orderResult = new MutableLiveData<>();
    // LiveData lưu kết quả tạo đơn hàng
    private MutableLiveData<ApiResponse<Order>> createOrderResult = new MutableLiveData<>();
    // LiveData lưu kết quả cập nhật đơn hàng
    private MutableLiveData<ApiResponse<Order>> updateOrderResult = new MutableLiveData<>();
    // LiveData lưu kết quả xóa đơn hàng
    private MutableLiveData<ApiResponse<String>> deleteOrderResult = new MutableLiveData<>();
    // LiveData trạng thái loading (đang xử lý API)
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    
    // Khởi tạo repository, lấy ApiService
    public OrderRepository() {
        apiService = ApiClient.getApiService();
    }
    
    // Trả về LiveData kết quả lấy danh sách đơn hàng
    public LiveData<ApiResponse<List<Order>>> getOrdersResult() {
        return ordersResult;
    }
    // Trả về LiveData kết quả lấy một đơn hàng
    public LiveData<ApiResponse<Order>> getOrderResult() {
        return orderResult;
    }
    // Trả về LiveData kết quả tạo đơn hàng
    public LiveData<ApiResponse<Order>> getCreateOrderResult() {
        return createOrderResult;
    }
    // Trả về LiveData kết quả cập nhật đơn hàng
    public LiveData<ApiResponse<Order>> getUpdateOrderResult() {
        return updateOrderResult;
    }
    // Trả về LiveData kết quả xóa đơn hàng
    public LiveData<ApiResponse<String>> getDeleteOrderResult() {
        return deleteOrderResult;
    }
    // Trả về LiveData trạng thái loading
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
    
    // Lấy tất cả đơn hàng (dành cho admin/manager)
    public void getAllOrders() {
        isLoading.setValue(true);
        Call<ApiResponse<OrdersResponse>> call = apiService.getAllOrders();
        call.enqueue(new Callback<ApiResponse<OrdersResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<OrdersResponse>> call, Response<ApiResponse<OrdersResponse>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<OrdersResponse> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        // Lấy danh sách đơn hàng từ OrdersResponse
                        List<Order> orders = apiResponse.getData().getOrders();
                        ApiResponse<List<Order>> ordersResponse = new ApiResponse<>();
                        ordersResponse.setSuccess(true);
                        ordersResponse.setMessage(apiResponse.getMessage());
                        ordersResponse.setData(orders);
                        ordersResult.setValue(ordersResponse);
                    } else {
                        ApiResponse<List<Order>> errorResponse = new ApiResponse<>();
                        errorResponse.setSuccess(false);
                        errorResponse.setMessage(apiResponse.getMessage());
                        ordersResult.setValue(errorResponse);
                    }
                } else {
                    ApiResponse<List<Order>> errorResponse = new ApiResponse<>();
                    errorResponse.setSuccess(false);
                    errorResponse.setMessage("Không thể lấy danh sách đơn hàng");
                    ordersResult.setValue(errorResponse);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<OrdersResponse>> call, Throwable t) {
                isLoading.setValue(false);
                ApiResponse<List<Order>> errorResponse = new ApiResponse<>();
                errorResponse.setSuccess(false);
                errorResponse.setMessage("Không thể lấy danh sách đơn hàng: " + t.getMessage());
                ordersResult.setValue(errorResponse);
            }
        });
    }
    
    // Lấy danh sách đơn hàng của một user
    public void getOrdersByUser(String userId) {
        isLoading.setValue(true);
        Call<ApiResponse<OrdersResponse>> call = apiService.getOrdersByUser(userId);
        call.enqueue(new Callback<ApiResponse<OrdersResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<OrdersResponse>> call, Response<ApiResponse<OrdersResponse>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<OrdersResponse> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        // Lấy danh sách đơn hàng từ OrdersResponse
                        List<Order> orders = apiResponse.getData().getOrders();
                        ApiResponse<List<Order>> ordersResponse = new ApiResponse<>();
                        ordersResponse.setSuccess(true);
                        ordersResponse.setMessage(apiResponse.getMessage());
                        ordersResponse.setData(orders);
                        ordersResult.setValue(ordersResponse);
                    } else {
                        ApiResponse<List<Order>> errorResponse = new ApiResponse<>();
                        errorResponse.setSuccess(false);
                        errorResponse.setMessage(apiResponse.getMessage());
                        ordersResult.setValue(errorResponse);
                    }
                } else {
                    ApiResponse<List<Order>> errorResponse = new ApiResponse<>();
                    errorResponse.setSuccess(false);
                    errorResponse.setMessage("Không thể lấy đơn hàng của người dùng");
                    ordersResult.setValue(errorResponse);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<OrdersResponse>> call, Throwable t) {
                isLoading.setValue(false);
                ApiResponse<List<Order>> errorResponse = new ApiResponse<>();
                errorResponse.setSuccess(false);
                errorResponse.setMessage("Không thể lấy đơn hàng của người dùng: " + t.getMessage());
                ordersResult.setValue(errorResponse);
            }
        });
    }
    
    // Lấy thông tin một đơn hàng theo id
    public void getOrderById(String orderId) {
        Call<ApiResponse<Order>> call = apiService.getOrderById(orderId);
        ApiUtils.executeCall(call, isLoading, orderResult, "Không thể lấy thông tin đơn hàng");
    }

    // Tạo mới một đơn hàng
    public void createOrder(CreateOrderRequest createOrderRequest) {
        Call<ApiResponse<Order>> call = apiService.createOrder(createOrderRequest);
        ApiUtils.executeCall(call, isLoading, createOrderResult, "Không thể tạo đơn hàng");
    }
    
    // Cập nhật thông tin đơn hàng
    public void updateOrder(String orderId, Order order) {
        Call<ApiResponse<Order>> call = apiService.updateOrder(orderId, order);
        ApiUtils.executeCall(call, isLoading, updateOrderResult, "Không thể cập nhật đơn hàng");
    }
    
    // Cập nhật trạng thái đơn hàng
    public void updateOrderStatus(String orderId, OrderStatus status) {
        UpdateOrderStatusRequest statusRequest = new UpdateOrderStatusRequest(status.toString());
        Call<ApiResponse<Order>> call = apiService.updateOrderStatus(orderId, statusRequest);
        ApiUtils.executeCall(call, isLoading, updateOrderResult, "Không thể cập nhật trạng thái đơn hàng");
    }
    
    // Xóa/hủy một đơn hàng
    public void deleteOrder(String orderId) {
        Call<ApiResponse<String>> call = apiService.deleteOrder(orderId);
        ApiUtils.executeCall(call, isLoading, deleteOrderResult, "Không thể hủy đơn hàng");
    }
} 