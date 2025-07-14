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

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.OrderViewHolder> {

    private List<Order> orders;
    private OnOrderItemClickListener listener;

    public interface OnOrderItemClickListener {
        void onOrderClick(Order order);
        void onViewDetailsClick(Order order);
        void onTrackOrderClick(Order order);
    }

    public OrderHistoryAdapter(OnOrderItemClickListener listener) {
        this.orders = new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order_history, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
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

    public void clearOrders() {
        this.orders.clear();
        notifyDataSetChanged();
    }

    class OrderViewHolder extends RecyclerView.ViewHolder {
        private TextView tvOrderNumber;
        private TextView tvOrderDate;
        private TextView tvTotalAmount;
        private TextView tvShippingAddress;
        private Chip chipOrderStatus;
        private MaterialButton btnViewDetails;
        private MaterialButton btnTrackOrder;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            initViews();
            setupClickListeners();
        }

        private void initViews() {
            tvOrderNumber = itemView.findViewById(R.id.tvOrderNumber);
            tvOrderDate = itemView.findViewById(R.id.tvOrderDate);
            tvTotalAmount = itemView.findViewById(R.id.tvTotalAmount);
            tvShippingAddress = itemView.findViewById(R.id.tvShippingAddress);
            chipOrderStatus = itemView.findViewById(R.id.chipOrderStatus);
            btnViewDetails = itemView.findViewById(R.id.btnViewDetails);
            btnTrackOrder = itemView.findViewById(R.id.btnTrackOrder);
        }

        private void setupClickListeners() {
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onOrderClick(orders.get(getAdapterPosition()));
                }
            });

            btnViewDetails.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onViewDetailsClick(orders.get(getAdapterPosition()));
                }
            });

            btnTrackOrder.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onTrackOrderClick(orders.get(getAdapterPosition()));
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
                return;
            }

            String statusText;
            int backgroundColor;

            switch (status) {
                case PENDING:
                    statusText = "Đang chờ";
                    backgroundColor = R.color.warning_yellow;
                    break;
                case CONFIRMED:
                    statusText = "Đã xác nhận";
                    backgroundColor = R.color.primary_blue;
                    break;
                case PROCESSING:
                    statusText = "Đang xử lý";
                    backgroundColor = R.color.secondary_orange;
                    break;
                case SHIPPED:
                    statusText = "Đã giao";
                    backgroundColor = R.color.success_green;
                    break;
                case DELIVERED:
                    statusText = "Đã giao";
                    backgroundColor = R.color.success_green;
                    break;
                case CANCELLED:
                    statusText = "Đã hủy";
                    backgroundColor = R.color.error_red;
                    break;
                default:
                    statusText = status.toString();
                    backgroundColor = R.color.text_secondary_light;
                    break;
            }

            chipOrderStatus.setText(statusText);
            chipOrderStatus.setChipBackgroundColorResource(backgroundColor);
            chipOrderStatus.setTextColor(itemView.getContext().getResources().getColor(R.color.white, null));
        }
    }
} 