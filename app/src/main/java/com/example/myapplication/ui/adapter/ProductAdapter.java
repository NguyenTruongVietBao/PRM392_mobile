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
import com.example.myapplication.data.models.Product;
import com.example.myapplication.utils.FormatUtils;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    
    private List<Product> products = new ArrayList<>();
    private OnProductClickListener onProductClickListener;

    public ProductAdapter() {
        this.products = new ArrayList<>();
    }

    public interface OnProductClickListener {
        void onProductClick(Product product);
    }

    public void setOnProductClickListener(OnProductClickListener listener) {
        this.onProductClickListener = listener;
    }

    public void setProducts(List<Product> products) {
        this.products = products != null ? products : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = products.get(position);
        holder.bind(product);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivProductImage;
        private TextView tvProductName;
        private TextView tvProductPrice;
        private TextView tvProductQuantity;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            initViews();
            setupClickListeners();
        }

        private void initViews() {
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            tvProductQuantity = itemView.findViewById(R.id.tvProductQuantity);
        }

        private void setupClickListeners() {
            itemView.setOnClickListener(v -> {
                if (onProductClickListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        onProductClickListener.onProductClick(products.get(position));
                    }
                }
            });
        }

        public void bind(Product product) {
            if (product != null) {
                // Set product name
                String name = product.getName();
                tvProductName.setText(name != null ? name : "Tên sản phẩm");
                
                // Set price
                tvProductPrice.setText(FormatUtils.formatCurrency(product.getPrice()));
                
                // Set quantity
                int stockQuantity = product.getStockQuantity();
                if (stockQuantity > 0) {
                    tvProductQuantity.setText("Còn lại: " + stockQuantity + " sản phẩm");
                    tvProductQuantity.setTextColor(itemView.getContext().getColor(R.color.text_secondary));
                } else {
                    tvProductQuantity.setText("Hết hàng");
                    tvProductQuantity.setTextColor(itemView.getContext().getColor(R.color.error_red));
                }

                // Load image
                String imageUrl = product.getImageUrl();
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
            }
        }
    }
} 