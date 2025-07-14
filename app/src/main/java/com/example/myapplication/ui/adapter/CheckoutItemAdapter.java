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

public class CheckoutItemAdapter extends RecyclerView.Adapter<CheckoutItemAdapter.CheckoutItemViewHolder> {

    private List<CartItem> cartItems = new ArrayList<>();

    public CheckoutItemAdapter() {
        this.cartItems = new ArrayList<>();
    }

    public void setCartItems(List<CartItem> cartItems) {
        this.cartItems = cartItems != null ? cartItems : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CheckoutItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_checkout_product, parent, false);
        return new CheckoutItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CheckoutItemViewHolder holder, int position) {
        CartItem cartItem = cartItems.get(position);
        holder.bind(cartItem);
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public class CheckoutItemViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivProductImage;
        private TextView tvProductName;
        private TextView tvUnitPrice;
        private TextView tvQuantity;
        private TextView tvTotalPrice;

        public CheckoutItemViewHolder(@NonNull View itemView) {
            super(itemView);
            initViews();
        }

        private void initViews() {
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvUnitPrice = itemView.findViewById(R.id.tvUnitPrice);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvTotalPrice = itemView.findViewById(R.id.tvTotalPrice);
        }

        public void bind(CartItem cartItem) {
            if (cartItem != null && cartItem.getProduct() != null) {
                // Set product name
                String name = cartItem.getProduct().getName();
                tvProductName.setText(name != null ? name : "Tên sản phẩm");

                // Set prices and quantity
                tvUnitPrice.setText(FormatUtils.formatCurrency(cartItem.getUnitPrice()));
                tvQuantity.setText("x" + cartItem.getQuantity());
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

            } else {
                // Fallback for invalid items
                tvProductName.setText("Sản phẩm không khả dụng");
                tvUnitPrice.setText("--");
                tvQuantity.setText(cartItem != null ? "x" + cartItem.getQuantity() : "x0");
                tvTotalPrice.setText("--");
                ivProductImage.setImageResource(R.drawable.ic_product_placeholder);
            }
        }
    }
}
