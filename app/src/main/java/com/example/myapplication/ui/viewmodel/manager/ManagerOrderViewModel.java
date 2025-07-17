package com.example.myapplication.ui.viewmodel.manager;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myapplication.data.models.ApiResponse;
import com.example.myapplication.data.models.Order;
import com.example.myapplication.data.repositories.OrderRepository;
import com.example.myapplication.enums.OrderStatus;

import java.util.ArrayList;
import java.util.List;

public class ManagerOrderViewModel extends ViewModel {
    
    private OrderRepository orderRepository;
    
    // Filter and search
    private MutableLiveData<OrderStatus> filterStatus = new MutableLiveData<>();
    private MutableLiveData<String> searchQuery = new MutableLiveData<>();
    
    // Current selected order
    private MutableLiveData<Order> selectedOrder = new MutableLiveData<>();
    
    // Filtered orders
    private MutableLiveData<List<Order>> filteredOrders = new MutableLiveData<>();
    private List<Order> allOrders = new ArrayList<>();
    
    public ManagerOrderViewModel() {
        orderRepository = new OrderRepository();
        filteredOrders.setValue(new ArrayList<>());
    }
    
    // Repository access
    public LiveData<ApiResponse<List<Order>>> getOrdersResult() {
        return orderRepository.getOrdersResult();
    }
    
    public LiveData<ApiResponse<Order>> getUpdateOrderResult() {
        return orderRepository.getUpdateOrderResult();
    }
    
    public LiveData<Boolean> getIsLoading() {
        return orderRepository.getIsLoading();
    }
    
    // ViewModel state
    public LiveData<OrderStatus> getFilterStatus() {
        return filterStatus;
    }
    
    public LiveData<String> getSearchQuery() {
        return searchQuery;
    }
    
    public LiveData<Order> getSelectedOrder() {
        return selectedOrder;
    }
    
    public LiveData<List<Order>> getFilteredOrders() {
        return filteredOrders;
    }
    
    // Actions
    public void loadAllOrders() {
        orderRepository.getAllOrders();
    }
    
    public void refreshOrders() {
        loadAllOrders();
    }
    
    public void setAllOrders(List<Order> orders) {
        this.allOrders = orders != null ? orders : new ArrayList<>();
        applyFilters();
    }
    
    public void setFilter(OrderStatus status) {
        filterStatus.setValue(status);
        applyFilters();
    }
    
    public void setSearchQuery(String query) {
        searchQuery.setValue(query);
        applyFilters();
    }
    
    public void selectOrder(Order order) {
        selectedOrder.setValue(order);
    }
    
    public void updateOrderStatus(String orderId, OrderStatus newStatus) {
        // Call API to update status on server
        // Don't update local data here, let the refresh handle it
        orderRepository.updateOrderStatus(orderId, newStatus);
    }
    
    // Utility methods
    private void applyFilters() {
        List<Order> filtered = new ArrayList<>();
        
        String query = searchQuery.getValue();
        OrderStatus status = filterStatus.getValue();
        
        for (Order order : allOrders) {
            boolean matchesQuery = true;
            boolean matchesStatus = true;
            
            // Search filter
            if (query != null && !query.trim().isEmpty()) {
                String lowerQuery = query.toLowerCase().trim();
                matchesQuery = (order.getOrderNumber() != null && order.getOrderNumber().toLowerCase().contains(lowerQuery)) ||
                              (order.getShippingAddress() != null && order.getShippingAddress().toLowerCase().contains(lowerQuery));
            }
            
            // Status filter
            if (status != null) {
                matchesStatus = order.getStatus() == status;
            }
            
            if (matchesQuery && matchesStatus) {
                filtered.add(order);
            }
        }
        
        filteredOrders.setValue(filtered);
    }
    
    private Order findOrderById(String orderId) {
        for (Order order : allOrders) {
            if (order.getId() != null && order.getId().equals(orderId)) {
                return order;
            }
        }
        return null;
    }
    
    public void clearFilters() {
        filterStatus.setValue(null);
        searchQuery.setValue("");
        applyFilters();
    }
} 