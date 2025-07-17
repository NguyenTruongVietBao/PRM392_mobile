package com.example.myapplication.ui.fragment.manager;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.myapplication.R;
import com.example.myapplication.data.models.ApiResponse;
import com.example.myapplication.data.models.Order;
import com.example.myapplication.databinding.FragmentManagerOrderListBinding;
import com.example.myapplication.enums.OrderStatus;
import com.example.myapplication.ui.adapter.ManagerOrderAdapter;
import com.example.myapplication.ui.viewmodel.manager.ManagerOrderViewModel;
import com.google.android.material.chip.Chip;

import java.util.List;

public class ManagerOrderListFragment extends Fragment implements ManagerOrderAdapter.OnManagerOrderItemClickListener {

    private FragmentManagerOrderListBinding binding;
    private ManagerOrderViewModel viewModel;
    private ManagerOrderAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        binding = FragmentManagerOrderListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        setupViewModel();
        setupRecyclerView();
        setupSearchAndFilters();
        setupObservers();
        setupClickListeners();
        
        // Load initial data
        viewModel.loadAllOrders();
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(ManagerOrderViewModel.class);
    }

    private void setupRecyclerView() {
        adapter = new ManagerOrderAdapter(this);
        binding.recyclerViewOrders.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewOrders.setAdapter(adapter);
    }

    private void setupSearchAndFilters() {
        // Search functionality
        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                viewModel.setSearchQuery(s.toString());
            }
        });

        // Filter chips
        binding.chipGroupFilters.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (!checkedIds.isEmpty()) {
                int checkedId = checkedIds.get(0);
                OrderStatus status = getStatusFromChipId(checkedId);
                viewModel.setFilter(status);
            }
        });
    }

    private void setupObservers() {
        // Orders result
        viewModel.getOrdersResult().observe(getViewLifecycleOwner(), response -> {
            if (response.isSuccess()) {
                List<Order> orders = response.getData();
                viewModel.setAllOrders(orders);
                // showSuccessState() will be called by filteredOrders observer
            } else {
                showErrorState(response.getMessage());
            }
        });

        // Filtered orders
        viewModel.getFilteredOrders().observe(getViewLifecycleOwner(), orders -> {
            adapter.setOrders(orders);
            if (orders.isEmpty()) {
                showEmptyState();
            } else {
                showSuccessState();
            }
        });

        // Loading state
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading) {
                showLoadingState();
            }
        });

        // Update order result
        viewModel.getUpdateOrderResult().observe(getViewLifecycleOwner(), response -> {
            if (response.isSuccess()) {
                Toast.makeText(getContext(), "Cập nhật đơn hàng thành công", Toast.LENGTH_SHORT).show();
                // Refresh the entire list to get updated data from server
                viewModel.refreshOrders();
            } else {
                Toast.makeText(getContext(), "Lỗi: " + response.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupClickListeners() {
        binding.btnRetry.setOnClickListener(v -> viewModel.refreshOrders());
        binding.fabRefresh.setOnClickListener(v -> viewModel.refreshOrders());
    }

    private OrderStatus getStatusFromChipId(int chipId) {
        if (chipId == R.id.chipPending) {
            return OrderStatus.PENDING;
        } else if (chipId == R.id.chipProcessing) {
            return OrderStatus.PROCESSING;
        } else if (chipId == R.id.chipShipped) {
            return OrderStatus.SHIPPED;
        } else if (chipId == R.id.chipCancelled) {
            return OrderStatus.CANCELLED;
        } else {
            return null; // All orders
        }
    }

    private void showLoadingState() {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.recyclerViewOrders.setVisibility(View.GONE);
        binding.layoutEmptyState.setVisibility(View.GONE);
        binding.layoutErrorState.setVisibility(View.GONE);
    }

    private void showSuccessState() {
        binding.progressBar.setVisibility(View.GONE);
        binding.recyclerViewOrders.setVisibility(View.VISIBLE);
        binding.layoutEmptyState.setVisibility(View.GONE);
        binding.layoutErrorState.setVisibility(View.GONE);
    }

    private void showEmptyState() {
        binding.progressBar.setVisibility(View.GONE);
        binding.recyclerViewOrders.setVisibility(View.GONE);
        binding.layoutEmptyState.setVisibility(View.VISIBLE);
        binding.layoutErrorState.setVisibility(View.GONE);
    }

    private void showErrorState(String message) {
        binding.progressBar.setVisibility(View.GONE);
        binding.recyclerViewOrders.setVisibility(View.GONE);
        binding.layoutEmptyState.setVisibility(View.GONE);
        binding.layoutErrorState.setVisibility(View.VISIBLE);
        binding.tvErrorMessage.setText(message);
    }
    @Override
    public void onOrderClick(Order order) {
        viewModel.selectOrder(order);
        showOrderDetails(order);
    }

    @Override
    public void onUpdateStatusClick(Order order) {
        // Check if currently loading to prevent multiple requests
        Boolean isLoading = viewModel.getIsLoading().getValue();
        if (isLoading != null && isLoading) {
            Toast.makeText(getContext(), "Đang xử lý, vui lòng đợi...", Toast.LENGTH_SHORT).show();
            return;
        }
        showUpdateStatusDialog(order);
    }

    @Override
    public void onViewDetailsClick(Order order) {
        showOrderDetails(order);
    }

    @Override
    public void onViewCustomerClick(Order order) {
        String customerInfo;
        if (order.getBuyer() != null && !order.getBuyer().isEmpty()) {
            customerInfo = order.getBuyer();
        } else if (order.getUserId() != null) {
            customerInfo = order.getUserId();
        } else {
            customerInfo = "N/A";
        }
        
        Toast.makeText(getContext(), "Khách hàng: " + customerInfo, Toast.LENGTH_LONG).show();
    }

    private void showOrderDetails(Order order) {
        StringBuilder details = new StringBuilder();
        details.append("Mã đơn hàng: ").append(order.getOrderNumber()).append("\n\n");
        
        String customerInfo;
        if (order.getBuyer() != null && !order.getBuyer().isEmpty()) {
            customerInfo = order.getBuyer();
        } else if (order.getUserId() != null) {
            customerInfo = order.getUserId();
        } else {
            customerInfo = "N/A";
        }
        details.append("Khách hàng: ").append(customerInfo).append("\n");
        
        details.append("Tổng tiền: ").append(String.format("%.0f VNĐ", order.getTotalAmount())).append("\n");
        details.append("Số sản phẩm: ").append(order.getTotalItems()).append("\n");
        details.append("Địa chỉ giao hàng: ").append(order.getShippingAddress() != null ? order.getShippingAddress() : "N/A").append("\n");
        details.append("Trạng thái: ").append(getStatusDisplayName(order.getStatus())).append("\n");
        
        if (order.getOrderNote() != null && !order.getOrderNote().isEmpty()) {
            details.append("Ghi chú: ").append(order.getOrderNote()).append("\n");
        }

        new AlertDialog.Builder(getContext())
                .setTitle("Chi tiết đơn hàng")
                .setMessage(details.toString())
                .setPositiveButton("Đóng", null)
                .show();
    }

    private void showUpdateStatusDialog(Order order) {
        OrderStatus currentStatus = order.getStatus();
        String[] statusOptions;
        OrderStatus[] statusValues;

        switch (currentStatus) {
            case PENDING:
                statusOptions = new String[]{"Xác nhận đơn hàng", "Từ chối đơn hàng"};
                statusValues = new OrderStatus[]{OrderStatus.PROCESSING, OrderStatus.CANCELLED};
                break;
            case PROCESSING:
                statusOptions = new String[]{"Giao hàng thành công", "Hủy đơn hàng"};
                statusValues = new OrderStatus[]{OrderStatus.SHIPPED, OrderStatus.CANCELLED};
                break;
            case SHIPPED:
                Toast.makeText(getContext(), "Đơn hàng đã giao thành công, không thể cập nhật", Toast.LENGTH_SHORT).show();
                return;
            case CANCELLED:
                Toast.makeText(getContext(), "Đơn hàng đã bị hủy, không thể cập nhật", Toast.LENGTH_SHORT).show();
                return;
            default:
                Toast.makeText(getContext(), "Không thể cập nhật trạng thái đơn hàng này", Toast.LENGTH_SHORT).show();
                return;
        }

        new AlertDialog.Builder(getContext())
                .setTitle("Cập nhật trạng thái đơn hàng")
                .setItems(statusOptions, (dialog, which) -> {
                    OrderStatus newStatus = statusValues[which];
                    viewModel.updateOrderStatus(order.getId(), newStatus);
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private String getStatusDisplayName(OrderStatus status) {
        if (status == null) return "N/A";
        
        switch (status) {
            case PENDING: return "Đang chờ";
            case PROCESSING: return "Đang xử lý";
            case SHIPPED: return "Đã giao";
            case CANCELLED: return "Đã hủy";
            default: return status.toString();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
} 