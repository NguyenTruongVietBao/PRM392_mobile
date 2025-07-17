package com.example.myapplication.ui.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.data.models.ApiResponse;
import com.example.myapplication.data.models.Order;
import com.example.myapplication.data.repositories.OrderRepository;
import com.example.myapplication.enums.OrderStatus;
import com.example.myapplication.ui.adapter.OrderHistoryAdapter;
import com.example.myapplication.utils.SessionManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class OrderHistoryActivity extends AppCompatActivity implements OrderHistoryAdapter.OnOrderItemClickListener {

    private static final String TAG = "OrderHistoryActivity";

    // UI Components
    private MaterialToolbar toolbar;
    private ChipGroup chipGroupStatus;
    private Chip chipAll, chipPending, chipProcessing, chipDelivered;
    private RecyclerView recyclerViewOrders;
    private LinearLayout layoutEmptyState;
    private ProgressBar progressBarLoading;
    private FloatingActionButton fabRefresh;

    // Data
    private OrderRepository orderRepository;
    private SessionManager sessionManager;
    private OrderHistoryAdapter orderAdapter;

    // State
    private List<Order> allOrders = new ArrayList<>();
    private String currentStatusFilter = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        initViews();
        initComponents();
        setupToolbar();
        setupRecyclerView();
        setupClickListeners();
        observeViewModel();
        loadOrders();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        chipGroupStatus = findViewById(R.id.chipGroupStatus);
        chipAll = findViewById(R.id.chipAll);
        chipPending = findViewById(R.id.chipPending);
        chipProcessing = findViewById(R.id.chipProcessing);
        chipDelivered = findViewById(R.id.chipDelivered);
        recyclerViewOrders = findViewById(R.id.recyclerViewOrders);
        layoutEmptyState = findViewById(R.id.layoutEmptyState);
        progressBarLoading = findViewById(R.id.progressBarLoading);
        fabRefresh = findViewById(R.id.fabRefresh);
    }

    private void initComponents() {
        orderRepository = new OrderRepository();
        sessionManager = SessionManager.getInstance(this);
        orderAdapter = new OrderHistoryAdapter(this);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupRecyclerView() {
        recyclerViewOrders.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewOrders.setAdapter(orderAdapter);
    }

    private void setupClickListeners() {
        // Chip filter listeners
        chipGroupStatus.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;

            int checkedId = checkedIds.get(0);
            String statusFilter = null;

            if (checkedId == R.id.chipPending) {
                statusFilter = "PENDING";
            } else if (checkedId == R.id.chipProcessing) {
                statusFilter = "PROCESSING";
            } else if (checkedId == R.id.chipDelivered) {
                statusFilter = "DELIVERED";
            }
            // chipAll or others = null (show all)

            currentStatusFilter = statusFilter;
            filterOrders();
        });

        // Refresh button
        fabRefresh.setOnClickListener(v -> loadOrders());
    }

    private void observeViewModel() {
        // Observe orders result
        orderRepository.getOrdersResult().observe(this, this::handleOrdersResult);

        // Observe loading state
        orderRepository.getIsLoading().observe(this, isLoading -> {
            if (isLoading != null) {
                progressBarLoading.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            }
        });
    }

    private void loadOrders() {
        String userId = sessionManager.getUserId();
        if (userId != null && !userId.isEmpty()) {
            Log.d(TAG, "Loading orders for user: " + userId);
            orderRepository.getOrdersByUser(userId);
        } else {
            Log.e(TAG, "User ID is null or empty");
            showError("Không thể xác định người dùng");
        }
    }

    private void handleOrdersResult(ApiResponse<List<Order>> result) {
        if (result != null) {
            if (result.isSuccess() && result.getData() != null) {
                Log.d(TAG, "Orders loaded successfully: " + result.getData().size() + " orders");
                allOrders = result.getData();
                filterOrders();
            } else {
                Log.e(TAG, "Failed to load orders: " + result.getMessage());
                showError("Không thể tải lịch sử đơn hàng: " + result.getMessage());
                showEmptyState();
            }
        } else {
            Log.e(TAG, "Orders result is null");
            showError("Không nhận được dữ liệu từ server");
            showEmptyState();
        }
    }

    private void filterOrders() {
        List<Order> filteredOrders = new ArrayList<>();

        for (Order order : allOrders) {
            if (currentStatusFilter == null ||
                    (order.getStatus() != null && order.getStatus().toString().equals(currentStatusFilter))) {
                filteredOrders.add(order);
            }
        }

        Log.d(TAG, "Filtered orders: " + filteredOrders.size() + " (filter: " + currentStatusFilter + ")");

        if (filteredOrders.isEmpty()) {
            showEmptyState();
        } else {
            showOrdersList(filteredOrders);
        }
    }

    private void showOrdersList(List<Order> orders) {
        layoutEmptyState.setVisibility(View.GONE);
        recyclerViewOrders.setVisibility(View.VISIBLE);
        orderAdapter.setOrders(orders);
    }

    private void showEmptyState() {
        layoutEmptyState.setVisibility(View.VISIBLE);
        recyclerViewOrders.setVisibility(View.GONE);
        orderAdapter.clearOrders();
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    // OrderHistoryAdapter.OnOrderItemClickListener implementation
    @Override
    public void onOrderClick(Order order) {
        Log.d(TAG, "Order clicked: " + order.getOrderNumber());
        // TODO: Navigate to order detail activity
        Toast.makeText(this, "Chi tiết đơn hàng: " + order.getOrderNumber(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onViewDetailsClick(Order order) {
        Log.d(TAG, "View details clicked for order: " + order.getOrderNumber());
        // TODO: Navigate to order detail activity
        Toast.makeText(this, "Xem chi tiết: " + order.getOrderNumber(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTrackOrderClick(Order order) {
        Log.d(TAG, "Track order clicked for order: " + order.getOrderNumber());
        // TODO: Navigate to order tracking activity
        Toast.makeText(this, "Theo dõi đơn hàng: " + order.getOrderNumber(), Toast.LENGTH_SHORT).show();
    }
} 