package com.example.myapplication.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.myapplication.R;
import com.example.myapplication.data.models.Product;
import com.example.myapplication.databinding.ActivityProductDetailBinding;
import com.example.myapplication.ui.viewmodel.customer.CartViewModel;
import com.example.myapplication.ui.viewmodel.customer.ProductDetailViewModel;
import com.example.myapplication.utils.SessionManager;
import com.example.myapplication.utils.FormatUtils;
import com.google.android.material.snackbar.Snackbar;

public class ProductDetailActivity extends AppCompatActivity {

    public static final String EXTRA_PRODUCT_ID = "extra_product_id";

    private ActivityProductDetailBinding binding;
    private ProductDetailViewModel productDetailViewModel;
    private CartViewModel cartViewModel;
    private SessionManager sessionManager;
    private Product currentProduct;
    private int quantity = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupToolbar();
        initViewModels();
        sessionManager = SessionManager.getInstance(this);

        String productId = getIntent().getStringExtra(EXTRA_PRODUCT_ID);

        if (productId == null || productId.trim().isEmpty()) {
            showErrorAndFinish("Sản phẩm không hợp lệ");
            return;
        }

        setupObservers();
        setupClickListeners();
        setupCollapsingToolbar();
        loadProductDetails(productId);
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false); // Hide title initially
        }
        
        // Setup action buttons
        binding.cardBack.setOnClickListener(v -> onBackPressed());
        binding.cardCart.setOnClickListener(v -> navigateToCart());
    }

    private void setupCollapsingToolbar() {
        // Setup collapsing toolbar behavior
        binding.collapsingToolbar.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));
        binding.collapsingToolbar.setCollapsedTitleTextColor(getResources().getColor(R.color.white));
        
        // Add scroll listener for dynamic UI changes
        binding.appBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            float ratio = (float) Math.abs(verticalOffset) / (float) appBarLayout.getTotalScrollRange();
            
            // Fade bottom card based on scroll
            float alpha = Math.max(0.3f, 1.0f - ratio * 2);
            binding.bottomCard.setAlpha(alpha);
            
            // Scale image slightly based on scroll
            float scale = Math.max(0.95f, 1.0f - ratio * 0.05f);
            binding.ivProductImage.setScaleX(scale);
            binding.ivProductImage.setScaleY(scale);
        });
    }

    private void initViewModels() {
        productDetailViewModel = new ViewModelProvider(this).get(ProductDetailViewModel.class);
        cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);
    }

    private void setupObservers() {
        productDetailViewModel.getIsLoading().observe(this, isLoading -> {
            if (isLoading) {
                showLoadingState();
            } else {
                hideLoadingState();
            }
        });

        productDetailViewModel.getProductResult().observe(this, response -> {
            hideLoadingState();
            if (response != null && response.isSuccess() && response.getData() != null) {
                currentProduct = response.getData();
                displayProductDetails(currentProduct);
                enableInteractions();
                animateContentEntrance();
            } else {
                String errorMsg = response != null ? response.getMessage() : "Không thể tải chi tiết sản phẩm";
                showErrorAndFinish(errorMsg);
            }
        });

        cartViewModel.getCartItemResult().observe(this, response -> {
            if (response != null) {
                if (response.isSuccess()) {
                    showSuccessMessage("Đã thêm sản phẩm vào giỏ hàng thành công!");
                    resetQuantity();
                    animateAddToCartSuccess();
                } else {
                    String errorMsg = response.getMessage() != null ? response.getMessage() : "Không thể thêm vào giỏ hàng";
                    showErrorMessage("Lỗi: " + errorMsg);
                }
            }
        });

        cartViewModel.getIsLoading().observe(this, isLoading -> {
            updateAddToCartButton(isLoading);
        });
    }

    private void setupClickListeners() {
        binding.btnAddQuantity.setOnClickListener(v -> updateQuantity(1));
        binding.btnRemoveQuantity.setOnClickListener(v -> updateQuantity(-1));
        binding.btnAddToCart.setOnClickListener(v -> addToCart());
        
        // Add favorite button click listener (if needed)
        binding.btnFavorite.setOnClickListener(v -> {
            // Implement favorite functionality
            animateFavoriteClick();
            Toast.makeText(this, "Tính năng yêu thích sẽ sớm được cập nhật", Toast.LENGTH_SHORT).show();
        });
    }

    private void animateContentEntrance() {
        // Animate content cards entrance
        View[] cards = {
            binding.scrollContent.getChildAt(0), // First card
        };
        
        for (int i = 0; i < cards.length; i++) {
            if (cards[i] != null) {
                cards[i].setAlpha(0f);
                cards[i].setTranslationY(50f);
                cards[i].animate()
                        .alpha(1f)
                        .translationY(0f)
                        .setDuration(400)
                        .setStartDelay(i * 100)
                        .start();
            }
        }
        
        // Animate bottom card
        binding.bottomCard.setTranslationY(100f);
        binding.bottomCard.animate()
                .translationY(0f)
                .setDuration(500)
                .setStartDelay(200)
                .start();
    }

    private void animateAddToCartSuccess() {
        // Success animation for add to cart
        binding.btnAddToCart.animate()
                .scaleX(1.1f)
                .scaleY(1.1f)
                .setDuration(150)
                .withEndAction(() -> {
                    binding.btnAddToCart.animate()
                            .scaleX(1.0f)
                            .scaleY(1.0f)
                            .setDuration(150)
                            .start();
                })
                .start();
    }

    private void animateFavoriteClick() {
        binding.btnFavorite.animate()
                .scaleX(1.3f)
                .scaleY(1.3f)
                .setDuration(100)
                .withEndAction(() -> {
                    binding.btnFavorite.animate()
                            .scaleX(1.0f)
                            .scaleY(1.0f)
                            .setDuration(100)
                            .start();
                })
                .start();
    }



    private void updateQuantity(int change) {
        if (currentProduct == null || !currentProduct.isInStock()) return;

        int newQuantity = quantity + change;
        if (newQuantity >= 1 && newQuantity <= currentProduct.getStockQuantity()) {
            quantity = newQuantity;

            // Enhanced quantity change animation
            binding.tvQuantity.animate()
                    .scaleX(1.3f)
                    .scaleY(1.3f)
                    .setDuration(100)
                    .withEndAction(() -> {
                        binding.tvQuantity.setText(String.valueOf(quantity));
                        binding.tvQuantity.animate()
                                .scaleX(1.0f)
                                .scaleY(1.0f)
                                .setDuration(100)
                                .start();
                    })
                    .start();

            // Update button states with animation
            updateQuantityButtonStates();
        } else {
            // Enhanced shake animation when reaching limits
            binding.tvQuantity.animate()
                    .translationXBy(15f)
                    .setDuration(80)
                    .withEndAction(() -> {
                        binding.tvQuantity.animate()
                                .translationXBy(-30f)
                                .setDuration(80)
                                .withEndAction(() -> {
                                    binding.tvQuantity.animate()
                                            .translationX(0f)
                                            .setDuration(80)
                                            .start();
                                })
                                .start();
                    })
                    .start();
        }
    }

    private void updateQuantityButtonStates() {
        boolean canDecrease = quantity > 1;
        boolean canIncrease = currentProduct != null && quantity < currentProduct.getStockQuantity();

        // Animate button state changes
        binding.btnRemoveQuantity.animate()
                .alpha(canDecrease ? 1.0f : 0.5f)
                .setDuration(200)
                .start();
        binding.btnRemoveQuantity.setEnabled(canDecrease);

        binding.btnAddQuantity.animate()
                .alpha(canIncrease ? 1.0f : 0.5f)
                .setDuration(200)
                .start();
        binding.btnAddQuantity.setEnabled(canIncrease);
    }

    private void addToCart() {
        if (currentProduct == null) {
            showErrorMessage("Sản phẩm không hợp lệ");
            return;
        }

        if (!currentProduct.isInStock()) {
            showErrorMessage("Sản phẩm hiện đã hết hàng");
            return;
        }

        String userId = sessionManager.getUserId();
        if (userId == null || userId.trim().isEmpty()) {
            showErrorMessage("Vui lòng đăng nhập để thêm sản phẩm vào giỏ hàng");
            return;
        }

        // Validation quantity
        if (quantity <= 0 || quantity > currentProduct.getStockQuantity()) {
            showErrorMessage("Số lượng không hợp lệ");
            return;
        }

        cartViewModel.addItemToCart(userId, currentProduct.getId(), quantity);
    }

    private void enableInteractions() {
        binding.btnAddToCart.setEnabled(true);
        enableQuantityControls(currentProduct != null && currentProduct.isInStock());
    }

    private void disableInteractions() {
        binding.btnAddToCart.setEnabled(false);
        enableQuantityControls(false);
    }

    private void enableQuantityControls(boolean enabled) {
        if (enabled) {
            updateQuantityButtonStates();
        } else {
            binding.btnAddQuantity.setEnabled(false);
            binding.btnRemoveQuantity.setEnabled(false);
            binding.btnAddQuantity.setAlpha(0.5f);
            binding.btnRemoveQuantity.setAlpha(0.5f);
        }
    }

    private void updateAddToCartButton(boolean isLoading) {
        if (currentProduct != null && !currentProduct.isInStock()) {
            disableAddToCartButton();
            return;
        }

        binding.btnAddToCart.setEnabled(!isLoading);

        if (isLoading) {
            binding.btnAddToCart.setText("Đang thêm...");
            binding.btnAddToCart.animate()
                    .scaleX(0.98f)
                    .scaleY(0.98f)
                    .setDuration(200)
                    .start();
        } else {
            binding.btnAddToCart.setText("Thêm vào giỏ hàng");
            binding.btnAddToCart.animate()
                    .scaleX(1.0f)
                    .scaleY(1.0f)
                    .setDuration(200)
                    .start();
        }
    }

    private void disableAddToCartButton() {
        binding.btnAddToCart.setEnabled(false);
        binding.btnAddToCart.setText("Hết hàng");
        binding.btnAddToCart.setAlpha(0.6f);
    }

    private void resetQuantity() {
        quantity = 1;
        binding.tvQuantity.setText(String.valueOf(quantity));
        updateQuantityButtonStates();
    }

    private void showSnackbar(String message, int backgroundColor, boolean hasAction) {
        Snackbar snackbar = Snackbar.make(binding.scrollContent, message, Snackbar.LENGTH_LONG)
                .setBackgroundTint(getResources().getColor(backgroundColor))
                .setTextColor(getResources().getColor(R.color.white))
                .setActionTextColor(getResources().getColor(R.color.white));
        
        // Add action for error messages
        if (hasAction) {
            snackbar.setAction("Đóng", v -> {});
        }

        // Set margin to avoid overlap with bottom card
        View snackbarView = snackbar.getView();
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) snackbarView.getLayoutParams();
        params.bottomMargin = getResources().getDimensionPixelSize(R.dimen.bottom_card_height) +
                              getResources().getDimensionPixelSize(R.dimen.spacing_normal);
        snackbarView.setLayoutParams(params);
        
        // Animate snackbar appearance with smooth entrance
        snackbarView.setTranslationY(120f);
        snackbarView.setAlpha(0f);
        snackbar.show();
        snackbarView.animate()
                .translationY(0f)
                .alpha(1f)
                .setDuration(400)
                .setInterpolator(new android.view.animation.DecelerateInterpolator())
                .start();
    }

    private void showSuccessMessage(String message) {
        showSnackbar(message, R.color.success_green, false);
    }

    private void showErrorMessage(String message) {
        // Error shake animation for button
        binding.btnAddToCart.animate()
                .translationXBy(15f)
                .setDuration(80)
                .withEndAction(() -> {
                    binding.btnAddToCart.animate()
                            .translationXBy(-30f)
                            .setDuration(80)
                            .withEndAction(() -> {
                                binding.btnAddToCart.animate()
                                        .translationX(0f)
                                        .setDuration(80)
                                        .start();
                            })
                            .start();
                })
                .start();

        showSnackbar(message, R.color.error_red, true);
    }

    private void displayProductDetails(Product product) {
        if (product == null) return;

        // Set collapsing toolbar title
        binding.collapsingToolbar.setTitle(product.getName());

        loadProductImage(product.getImageUrl());
        binding.tvProductName.setText(product.getName());
        binding.tvProductPrice.setText(FormatUtils.formatCurrency(product.getPrice()));
        binding.tvProductDescription.setText(product.getDescription());

        // Show category if available
        if (product.getCategory() != null && product.getCategory().getName() != null) {
            binding.chipCategory.setText(product.getCategory().getName());
            binding.chipCategory.setVisibility(View.VISIBLE);
        } else {
            binding.chipCategory.setVisibility(View.GONE);
        }

        // Show specifications if available
        showSpecifications(product);

        // Update stock info
        updateStockInfo(product);
    }

    private void showSpecifications(Product product) {
        boolean hasColorOrSize = false;

        if (product.getColor() != null && !product.getColor().trim().isEmpty()) {
            binding.tvColor.setText(product.getColor());
            binding.tvColor.setVisibility(View.VISIBLE);
            hasColorOrSize = true;
        } else {
            binding.tvColor.setVisibility(View.GONE);
        }

        if (product.getSize() != null && !product.getSize().trim().isEmpty()) {
            binding.tvSize.setText(product.getSize());
            binding.tvSize.setVisibility(View.VISIBLE);
            hasColorOrSize = true;
        } else {
            binding.tvSize.setVisibility(View.GONE);
        }

        if (hasColorOrSize) {
            binding.cardSpecifications.setVisibility(View.VISIBLE);
            // Animate specifications card appearance
            binding.cardSpecifications.setAlpha(0f);
            binding.cardSpecifications.animate()
                    .alpha(1f)
                    .setDuration(400)
                    .setStartDelay(300)
                    .start();
        } else {
            binding.cardSpecifications.setVisibility(View.GONE);
        }
    }

    private void loadProductImage(String imageUrl) {
        if (imageUrl != null && !imageUrl.trim().isEmpty()) {
            binding.imageLoadingOverlay.setVisibility(View.VISIBLE);

            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_product_placeholder)
                    .error(R.drawable.ic_product_placeholder)
                    .transition(DrawableTransitionOptions.withCrossFade(300))
                    .centerCrop()
                    .listener(new com.bumptech.glide.request.RequestListener<android.graphics.drawable.Drawable>() {
                        @Override
                        public boolean onLoadFailed(com.bumptech.glide.load.engine.GlideException e, Object model,
                                                    com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable> target,
                                                    boolean isFirstResource) {
                            binding.imageLoadingOverlay.animate()
                                    .alpha(0f)
                                    .setDuration(200)
                                    .withEndAction(() -> binding.imageLoadingOverlay.setVisibility(View.GONE))
                                    .start();
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(android.graphics.drawable.Drawable resource, Object model,
                                                       com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable> target,
                                                       com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {
                            binding.imageLoadingOverlay.animate()
                                    .alpha(0f)
                                    .setDuration(200)
                                    .withEndAction(() -> binding.imageLoadingOverlay.setVisibility(View.GONE))
                                    .start();
                            return false;
                        }
                    })
                    .into(binding.ivProductImage);
        } else {
            binding.ivProductImage.setImageResource(R.drawable.ic_product_placeholder);
            binding.imageLoadingOverlay.setVisibility(View.GONE);
        }
    }

    private void updateStockInfo(Product product) {
        if (product.isInStock()) {
            binding.chipStock.setText("Còn " + product.getStockQuantity() + " sản phẩm");
            binding.chipStock.setChipBackgroundColorResource(R.color.stock_chip_bg);
            binding.chipStock.setTextColor(getResources().getColor(R.color.stock_text));
        } else {
            binding.chipStock.setText("Hết hàng");
            binding.chipStock.setChipBackgroundColorResource(R.color.error_red);
            binding.chipStock.setTextColor(getResources().getColor(R.color.white));
        }
    }

    private void loadProductDetails(String productId) {
        showLoadingState();
        disableInteractions();
        productDetailViewModel.getProductById(productId);
    }

    private void showLoadingState() {
        binding.contentLoadingOverlay.setVisibility(View.VISIBLE);
        binding.scrollContent.setVisibility(View.GONE);
        binding.bottomCard.setVisibility(View.GONE);

        // Clear current data
        binding.tvProductName.setText("");
        binding.tvProductPrice.setText("");
        binding.tvProductDescription.setText("");
        binding.chipCategory.setVisibility(View.GONE);
        binding.cardSpecifications.setVisibility(View.GONE);
    }

    private void hideLoadingState() {
        binding.contentLoadingOverlay.animate()
                .alpha(0f)
                .setDuration(300)
                .withEndAction(() -> {
                    binding.contentLoadingOverlay.setVisibility(View.GONE);
                    binding.scrollContent.setVisibility(View.VISIBLE);
                    binding.bottomCard.setVisibility(View.VISIBLE);
                })
                .start();
    }

    private void showErrorAndFinish(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        finish();
    }

    private void navigateToCart() {
        // Navigate to MainActivity with cart tab selected
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("selected_tab", "cart");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // Add custom back animation
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}