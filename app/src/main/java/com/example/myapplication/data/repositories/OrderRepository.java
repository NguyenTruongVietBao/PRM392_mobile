package com.example.myapplication.data.repositories;






import android.util.Log;

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





public class OrderRepository {


    private ApiService apiService;


    





    private MutableLiveData<ApiResponse<List<Order>>> ordersResult = new MutableLiveData<>();


    private MutableLiveData<ApiResponse<Order>> orderResult = new MutableLiveData<>();


    private MutableLiveData<ApiResponse<Order>> createOrderResult = new MutableLiveData<>();


    private MutableLiveData<ApiResponse<Order>> updateOrderResult = new MutableLiveData<>();


    private MutableLiveData<ApiResponse<String>> deleteOrderResult = new MutableLiveData<>();


    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();


    


    public OrderRepository() {


        apiService = ApiClient.getApiService();


    }


    


    public LiveData<ApiResponse<List<Order>>> getOrdersResult() {


        return ordersResult;


    }


    


    public LiveData<ApiResponse<Order>> getOrderResult() {


        return orderResult;


    }


    


    public LiveData<ApiResponse<Order>> getCreateOrderResult() {


        return createOrderResult;


    }


    


    public LiveData<ApiResponse<Order>> getUpdateOrderResult() {


        return updateOrderResult;


    }


    


    public LiveData<ApiResponse<String>> getDeleteOrderResult() {


        return deleteOrderResult;


    }


    


    public LiveData<Boolean> getIsLoading() {


        return isLoading;


    }


    


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


                        // Extract orders from OrdersResponse


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


                        // Extract orders from OrdersResponse


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


    


    public void getOrderById(String orderId) {


        Call<ApiResponse<Order>> call = apiService.getOrderById(orderId);


        ApiUtils.executeCall(call, isLoading, orderResult, "Không thể lấy thông tin đơn hàng");


    }





    public void createOrder(CreateOrderRequest createOrderRequest) {


        Call<ApiResponse<Order>> call = apiService.createOrder(createOrderRequest);


        ApiUtils.executeCall(call, isLoading, createOrderResult, "Không thể tạo đơn hàng");


    }


    


    public void updateOrder(String orderId, Order order) {


        Call<ApiResponse<Order>> call = apiService.updateOrder(orderId, order);


        ApiUtils.executeCall(call, isLoading, updateOrderResult, "Không thể cập nhật đơn hàng");


    }


    


    public void updateOrderStatus(String orderId, OrderStatus status) {


        UpdateOrderStatusRequest statusRequest = new UpdateOrderStatusRequest(status.toString());


        Call<ApiResponse<Order>> call = apiService.updateOrderStatus(orderId, statusRequest);


        ApiUtils.executeCall(call, isLoading, updateOrderResult, "Không thể cập nhật trạng thái đơn hàng");


    }


    


    public void deleteOrder(String orderId) {


        Call<ApiResponse<String>> call = apiService.deleteOrder(orderId);


        ApiUtils.executeCall(call, isLoading, deleteOrderResult, "Không thể hủy đơn hàng");


    }


} 