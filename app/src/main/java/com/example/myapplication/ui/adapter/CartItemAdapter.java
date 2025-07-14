package com.example.myapplication.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.data.models.CartItem;
import com.example.myapplication.utils.FormatUtils;

import java.util.ArrayList;
import java.util.List;

public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.CartItemViewHolder> {
    
    private List<CartItem> cartItems = new ArrayList<>();
    private OnCartItemClickListener onCartItemClickListener;

    public interface OnCartItemClickListener {
        void onIncreaseQuantity(CartItem cartItem);
        void onDecreaseQuantity(CartItem cartItem);
        void onRemoveItem(CartItem cartItem);
        void onItemClick(CartItem cartItem);
    }

    public CartItemAdapter() {
        this.cartItems = new ArrayList<>();
    }

    public CartItemAdapter(OnCartItemClickListener listener) {
        this();
        this.onCartItemClickListener = listener;
    }

    public void setOnCartItemClickListener(OnCartItemClickListener listener) {
        this.onCartItemClickListener = listener;
    }

    public void setCartItems(List<CartItem> cartItems) {
        this.cartItems = cartItems != null ? cartItems : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CartItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart, parent, false);
        return new CartItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartItemViewHolder holder, int position) {
        CartItem cartItem = cartItems.get(position);
        holder.bind(cartItem);
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public class CartItemViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivProductImage;
        private TextView tvProductName;
        private TextView tvUnitPrice;
        private TextView tvQuantity;
        private TextView tvTotalPrice;
        private ImageView ivDecrease;
        private ImageView ivIncrease;
        private ImageView ivRemove;

        public CartItemViewHolder(@NonNull View itemView) {
            super(itemView);
            initViews();
            setupClickListeners();
        }

        private void initViews() {
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvUnitPrice = itemView.findViewById(R.id.tvUnitPrice);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvTotalPrice = itemView.findViewById(R.id.tvTotalPrice);
            ivDecrease = itemView.findViewById(R.id.ivDecrease);
            ivIncrease = itemView.findViewById(R.id.ivIncrease);
            ivRemove = itemView.findViewById(R.id.ivRemove);
        }

        private void setupClickListeners() {
            itemView.setOnClickListener(v -> {
                if (onCartItemClickListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        onCartItemClickListener.onItemClick(cartItems.get(position));
                    }
                }
            });

            ivIncrease.setOnClickListener(v -> {
                if (onCartItemClickListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        onCartItemClickListener.onIncreaseQuantity(cartItems.get(position));
                    }
                }
            });

            ivDecrease.setOnClickListener(v -> {
                if (onCartItemClickListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        onCartItemClickListener.onDecreaseQuantity(cartItems.get(position));
                    }
                }
            });

            ivRemove.setOnClickListener(v -> {
                if (onCartItemClickListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        onCartItemClickListener.onRemoveItem(cartItems.get(position));
                    }
                }
            });
        }

        public void bind(CartItem cartItem) {
            if (cartItem != null && cartItem.getProduct() != null) {
                // Set product name
                String name = cartItem.getProduct().getName();
                tvProductName.setText(name != null ? name : "Tên sản phẩm");

                // Set prices and quantity
                tvUnitPrice.setText(FormatUtils.formatCurrency(cartItem.getUnitPrice()));
                tvQuantity.setText(String.valueOf(cartItem.getQuantity()));
                tvTotalPrice.setText(FormatUtils.formatCurrency(cartItem.getTotalPrice()));

                // Load image
                String imageUrl = cartItem.getProduct().getImageUrl();
                if (imageUrl != null && !imageUrl.trim().isEmpty()) {
                    Glide.with(itemView.getContext())
                        .load(imageUrl)
                        .placeholder(R.drawable.ic_product_placeholder)
                        .error(R.drawable.ic_product_placeholder)
                        .centerCrop()
                        .into(ivProductImage);
                } else {
                    ivProductImage.setImageResource(R.drawable.ic_product_placeholder);
                }

                // Enable/disable buttons based on quantity
                ivDecrease.setEnabled(cartItem.getQuantity() > 1);
                ivDecrease.setAlpha(cartItem.getQuantity() > 1 ? 1.0f : 0.5f);
                
                ivIncrease.setEnabled(true);
                ivRemove.setEnabled(true);
                ivIncrease.setAlpha(1.0f);
                ivRemove.setAlpha(1.0f);
                
            } else {
                // Fallback for invalid items
                tvProductName.setText("Sản phẩm không khả dụng");
                tvUnitPrice.setText("--");
                tvQuantity.setText(cartItem != null ? String.valueOf(cartItem.getQuantity()) : "0");
                tvTotalPrice.setText("--");
                ivProductImage.setImageResource(R.drawable.ic_product_placeholder);
                
                // Disable action buttons for invalid items
                ivDecrease.setEnabled(false);
                ivIncrease.setEnabled(false);
                ivRemove.setEnabled(true);
                ivDecrease.setAlpha(0.5f);
                ivIncrease.setAlpha(0.5f);
                ivRemove.setAlpha(1.0f);
            }
        }
    }
} 