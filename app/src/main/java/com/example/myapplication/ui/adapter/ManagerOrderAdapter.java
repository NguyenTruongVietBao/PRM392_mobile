package com.example.myapplication.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.data.models.Order;
import com.example.myapplication.enums.OrderStatus;
import com.example.myapplication.utils.FormatUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ManagerOrderAdapter extends RecyclerView.Adapter<ManagerOrderAdapter.ManagerOrderViewHolder> {

    private List<Order> orders;
    private OnManagerOrderItemClickListener listener;

    public interface OnManagerOrderItemClickListener {
        void onOrderClick(Order order);
        void onUpdateStatusClick(Order order);
        void onViewDetailsClick(Order order);
        void onViewCustomerClick(Order order);
    }

    public ManagerOrderAdapter(OnManagerOrderItemClickListener listener) {
        this.orders = new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public ManagerOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_manager_order, parent, false);
        return new ManagerOrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ManagerOrderViewHolder holder, int position) {
        Order order = orders.get(position);
        holder.bind(order);
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders != null ? orders : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void addOrders(List<Order> newOrders) {
        if (newOrders != null && !newOrders.isEmpty()) {
            int startPosition = this.orders.size();
            this.orders.addAll(newOrders);
            notifyItemRangeInserted(startPosition, newOrders.size());
        }
    }

    public void updateOrder(Order updatedOrder) {
        for (int i = 0; i < orders.size(); i++) {
            if (orders.get(i).getId().equals(updatedOrder.getId())) {
                orders.set(i, updatedOrder);
                notifyItemChanged(i);
                break;
            }
        }
    }

    public void clearOrders() {
        this.orders.clear();
        notifyDataSetChanged();
    }

    class ManagerOrderViewHolder extends RecyclerView.ViewHolder {
        private TextView tvOrderNumber;
        private TextView tvOrderDate;
        private TextView tvTotalAmount;
        private TextView tvCustomerInfo;
        private TextView tvShippingAddress;
        private TextView tvTotalItems;
        private Chip chipOrderStatus;
        private MaterialButton btnUpdateStatus;
        private MaterialButton btnViewDetails;
        private MaterialButton btnViewCustomer;

        public ManagerOrderViewHolder(@NonNull View itemView) {
            super(itemView);
            initViews();
            setupClickListeners();
        }

        private void initViews() {
            tvOrderNumber = itemView.findViewById(R.id.tvOrderNumber);
            tvOrderDate = itemView.findViewById(R.id.tvOrderDate);
            tvTotalAmount = itemView.findViewById(R.id.tvTotalAmount);
            tvCustomerInfo = itemView.findViewById(R.id.tvCustomerInfo);
            tvShippingAddress = itemView.findViewById(R.id.tvShippingAddress);
            tvTotalItems = itemView.findViewById(R.id.tvTotalItems);
            chipOrderStatus = itemView.findViewById(R.id.chipOrderStatus);
            btnUpdateStatus = itemView.findViewById(R.id.btnUpdateStatus);
            btnViewDetails = itemView.findViewById(R.id.btnViewDetails);
            btnViewCustomer = itemView.findViewById(R.id.btnViewCustomer);
        }

        private void setupClickListeners() {
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onOrderClick(orders.get(getAdapterPosition()));
                }
            });

            btnUpdateStatus.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onUpdateStatusClick(orders.get(getAdapterPosition()));
                }
            });

            btnViewDetails.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onViewDetailsClick(orders.get(getAdapterPosition()));
                }
            });

            btnViewCustomer.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onViewCustomerClick(orders.get(getAdapterPosition()));
                }
            });
        }

        public void bind(Order order) {
            // Order Number
            tvOrderNumber.setText(order.getOrderNumber() != null ? order.getOrderNumber() : "N/A");

            // Order Date
            if (order.getOrderDate() != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                tvOrderDate.setText(dateFormat.format(order.getOrderDate()));
            } else {
                tvOrderDate.setText("N/A");
            }

            // Total Amount
            tvTotalAmount.setText(FormatUtils.formatCurrency(order.getTotalAmount()));

            // Customer Info - use buyer email if available, otherwise fallback to userId
            String customerInfo;
            if (order.getBuyer() != null && !order.getBuyer().isEmpty()) {
                customerInfo = "Khách hàng: " + order.getBuyer();
            } else if (order.getUserId() != null) {
                customerInfo = "Khách hàng: " + order.getUserId();
            } else {
                customerInfo = "Khách hàng: N/A";
            }
            tvCustomerInfo.setText(customerInfo);

            // Total Items
            int totalItems = order.getTotalItems();
            tvTotalItems.setText(totalItems + " sản phẩm");

            // Shipping Address
            String address = order.getShippingAddress();
            tvShippingAddress.setText(address != null && !address.isEmpty() ? address : "Chưa có địa chỉ");

            // Order Status
            setOrderStatus(order.getStatus());
        }

        private void setOrderStatus(OrderStatus status) {
            if (status == null) {
                chipOrderStatus.setText("N/A");
                chipOrderStatus.setChipBackgroundColorResource(R.color.text_secondary_light);
                btnUpdateStatus.setEnabled(false);
                return;
            }

            String statusText;
            int backgroundColor;
            boolean canUpdate = true;

            switch (status) {
                case PENDING:
                    statusText = "Đang chờ";
                    backgroundColor = R.color.warning_yellow;
                    break;
                case PROCESSING:
                    statusText = "Đang xử lý";
                    backgroundColor = R.color.secondary_orange;
                    break;
                case SHIPPED:
                    statusText = "Đã giao";
                    backgroundColor = R.color.success_green;
                    canUpdate = false; // Cannot update shipped orders
                    break;
                case CANCELLED:
                    statusText = "Đã hủy";
                    backgroundColor = R.color.error_red;
                    canUpdate = false; // Cannot update cancelled orders
                    break;
                default:
                    statusText = status.toString();
                    backgroundColor = R.color.text_secondary_light;
                    break;
            }

            chipOrderStatus.setText(statusText);
            chipOrderStatus.setChipBackgroundColorResource(backgroundColor);
            chipOrderStatus.setTextColor(itemView.getContext().getResources().getColor(R.color.white, null));
            btnUpdateStatus.setEnabled(canUpdate);
        }
    }
} 