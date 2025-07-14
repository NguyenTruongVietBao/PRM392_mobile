package com.example.myapplication.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.data.models.Product;
import com.example.myapplication.enums.ProductStatus;
import com.example.myapplication.utils.FormatUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;

public class ManagerProductAdapter extends ListAdapter<Product, ManagerProductAdapter.ProductViewHolder> {

    public interface OnProductActionListener {
        void onEditProduct(Product product);
        void onDeleteProduct(Product product);
        void onProductClick(Product product);
    }

    private OnProductActionListener listener;
    private Context context;

    public ManagerProductAdapter(Context context) {
        super(new ProductDiffCallback());
        this.context = context;
    }

    public void setOnProductActionListener(OnProductActionListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_manager_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = getItem(position);
        holder.bind(product);
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivProductImage;
        private TextView tvProductName;
        private TextView tvProductPrice;
        private TextView tvStock;
        private TextView tvCategory;
        private Chip chipStatus;
        private MaterialButton btnEdit;
        private MaterialButton btnDelete;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);

            ivProductImage = itemView.findViewById(R.id.ivProductImage);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            tvStock = itemView.findViewById(R.id.tvStock);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            chipStatus = itemView.findViewById(R.id.chipStatus);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }

        public void bind(Product product) {
            // Set product name
            tvProductName.setText(product.getName());

            // Set product price
            String formattedPrice = FormatUtils.formatCurrency(product.getPrice());
            tvProductPrice.setText(formattedPrice);

            // Set stock quantity
            tvStock.setText(context.getString(R.string.product_stock_format, product.getStockQuantity()));

            // Set category
            if (product.getCategory() != null) {
                tvCategory.setText(product.getCategory().getName());
            } else {
                tvCategory.setText("Chưa phân loại");
            }

            // Set status chip
            setupStatusChip(product.getStatus(), product.getStockQuantity());

            // Load product image using Glide
            loadProductImage(product.getImageUrl());

            // Set click listeners
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onProductClick(product);
                }
            });

            btnEdit.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEditProduct(product);
                }
            });

            btnDelete.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteProduct(product);
                }
            });
        }

        private void loadProductImage(String imageUrl) {
            if (imageUrl != null && !imageUrl.isEmpty()) {
                Glide.with(context)
                        .load(imageUrl)
                        .centerCrop()
                        .placeholder(R.drawable.ic_product_placeholder)
                        .error(R.drawable.ic_product_placeholder)
                        .into(ivProductImage);
            } else {
                // Set default placeholder image
                Glide.with(context)
                        .load(R.drawable.ic_product_placeholder)
                        .centerCrop()
                        .into(ivProductImage);
            }
        }

        private void setupStatusChip(ProductStatus status, int stock) {
            if (stock <= 0) {
                chipStatus.setText("Hết hàng");
                chipStatus.setTextColor(context.getColor(R.color.white));
            } else if (status == ProductStatus.ACTIVE) {
                chipStatus.setText("Đăng bán");
                chipStatus.setTextColor(context.getColor(R.color.white));
            } else if (status == ProductStatus.INACTIVE) {
                chipStatus.setText("Ngừng bán");
                chipStatus.setChipBackgroundColorResource(R.color.text_secondary_light);
                chipStatus.setTextColor(context.getColor(R.color.white));
            } else {
                chipStatus.setText("Không xác định");
                chipStatus.setChipBackgroundColorResource(R.color.text_secondary_light);
                chipStatus.setTextColor(context.getColor(R.color.white));
            }
        }
    }

    static class ProductDiffCallback extends DiffUtil.ItemCallback<Product> {
        @Override
        public boolean areItemsTheSame(@NonNull Product oldItem, @NonNull Product newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Product oldItem, @NonNull Product newItem) {
            return oldItem.getName().equals(newItem.getName()) &&
                    oldItem.getPrice() == newItem.getPrice() &&
                    oldItem.getStockQuantity() == newItem.getStockQuantity() &&
                    oldItem.getStatus().equals(newItem.getStatus()) &&
                    java.util.Objects.equals(oldItem.getImageUrl(), newItem.getImageUrl()) &&
                    ((oldItem.getCategory() == null && newItem.getCategory() == null) ||
                            (oldItem.getCategory() != null && newItem.getCategory() != null &&
                                    oldItem.getCategory().getId().equals(newItem.getCategory().getId())));
        }
    }
} 