package com.example.myapplication.ui.fragment.customer;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.example.myapplication.data.models.ApiResponse;
import com.example.myapplication.data.models.Cart;
import com.example.myapplication.data.models.CartItem;
import com.example.myapplication.ui.activity.CheckoutActivity;
import com.example.myapplication.ui.adapter.CartItemAdapter;
import com.example.myapplication.ui.viewmodel.customer.CartViewModel;
import com.example.myapplication.network.responses.CartItemsResponse;
import com.example.myapplication.utils.FormatUtils;
import com.example.myapplication.utils.SessionManager;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class CartFragment extends Fragment implements CartItemAdapter.OnCartItemClickListener {
    
    private CartViewModel cartViewModel;
    private CartItemAdapter cartItemAdapter;
    private SessionManager sessionManager;
    
    // UI Components
    private RecyclerView recyclerViewCartItems;
    private LinearLayout layoutEmptyCart;
    private LinearLayout layoutCartContent;
    private ProgressBar progressBarLoading;
    private TextView tvClearCart;
    private TextView tvTotalItems;
    private TextView tvTotalPrice;
    private MaterialButton btnCheckout;
    
    // Cart data
    private int currentTotalItems = 0;
    private double currentTotalPrice = 0.0;

    public static CartFragment newInstance() {
        return new CartFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        initComponents();
        setupRecyclerView();
        setupClickListeners();
        observeViewModel();
        loadCartData();
    }

    private void initViews(View view) {
        recyclerViewCartItems = view.findViewById(R.id.recyclerViewCartItems);
        layoutEmptyCart = view.findViewById(R.id.layoutEmptyCart);
        layoutCartContent = view.findViewById(R.id.layoutCartContent);
        progressBarLoading = view.findViewById(R.id.progressBarLoading);
        tvClearCart = view.findViewById(R.id.tvClearCart);
        tvTotalItems = view.findViewById(R.id.tvTotalItems);
        tvTotalPrice = view.findViewById(R.id.tvTotalPrice);
        btnCheckout = view.findViewById(R.id.btnCheckout);
    }

    private void initComponents() {
        cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);
        sessionManager = SessionManager.getInstance(requireContext());
        cartItemAdapter = new CartItemAdapter(this);
    }

    private void setupRecyclerView() {
        recyclerViewCartItems.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewCartItems.setAdapter(cartItemAdapter);
    }

    private void setupClickListeners() {
        tvClearCart.setOnClickListener(v -> clearCart());
        btnCheckout.setOnClickListener(v -> proceedToCheckout());
    }

    private void observeViewModel() {
        cartViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null) {
                progressBarLoading.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            }
        });

        cartViewModel.getCartItemsFullResult().observe(getViewLifecycleOwner(), result -> {
            if (result != null) {
                if (result.isSuccess() && result.getData() != null) {
                    CartItemsResponse cartData = result.getData();
                    List<CartItem> cartItems = cartData.getItems();
                    
                    // Check for removed products and notify user
                    if (cartData.getCleanupInfo() != null && cartData.getCleanupInfo().hasRemovedItems()) {
                        String message = cartData.getCleanupInfo().getMessage();
                        if (message != null && !message.isEmpty()) {
                            showRemovedProductsNotification(message, cartData.getCleanupInfo().getRemovedInvalidItems());
                        }
                    }
                    
                    updateCartDisplay(cartItems);
                } else {
                    showError("Không thể tải giỏ hàng: " + result.getMessage());
                    showEmptyCart();
                }
            } else {
                showError("Không nhận được dữ liệu từ server");
                showEmptyCart();
            }
        });

        cartViewModel.getCartItemResult().observe(getViewLifecycleOwner(), result -> {
            if (result != null) {
                if (result.isSuccess()) {
                    loadCartData();
                } else {
                    String userMessage = result.getMessage();
                    if (userMessage == null || userMessage.isEmpty()) {
                        userMessage = "Thao tác thất bại. Vui lòng thử lại.";
                    }
                    showError("Lỗi: " + userMessage);
                }
            } else {
                showError("Không nhận được phản hồi từ server");
            }
        });

        cartViewModel.getCartResult().observe(getViewLifecycleOwner(), result -> {
            if (result != null) {
                if (result.isSuccess()) {
                    loadCartData();
                } else {
                    showError("Không thể xóa giỏ hàng: " + result.getMessage());
                }
            }
        });
    }

    private void loadCartData() {
        String userId = sessionManager.getUserId();
        if (userId != null && !userId.isEmpty()) {
            cartViewModel.getCartItems(userId);
        } else {
            showError("Không thể xác định người dùng");
        }
    }

    private void updateCartDisplay(List<CartItem> cartItems) {
        if (cartItems == null || cartItems.isEmpty()) {
            showEmptyCart();
        } else {
            showCartContent(cartItems);
        }
    }

    private void showEmptyCart() {
        layoutEmptyCart.setVisibility(View.VISIBLE);
        layoutCartContent.setVisibility(View.GONE);
        tvClearCart.setVisibility(View.GONE);
    }

    private void showCartContent(List<CartItem> cartItems) {
        layoutEmptyCart.setVisibility(View.GONE);
        layoutCartContent.setVisibility(View.VISIBLE);
        tvClearCart.setVisibility(View.VISIBLE);
        
        cartItemAdapter.setCartItems(cartItems);
        
        // Calculate totals
        int totalItems = 0;
        double totalPrice = 0.0;
        
        for (CartItem item : cartItems) {
            totalItems += item.getQuantity();
            totalPrice += item.getTotalPrice();
        }
        
        currentTotalItems = totalItems;
        currentTotalPrice = totalPrice;
        
        tvTotalItems.setText(totalItems + " sản phẩm");
        tvTotalPrice.setText(FormatUtils.formatCurrency(totalPrice));
    }

    private void clearCart() {
        String userId = sessionManager.getUserId();
        if (userId != null && !userId.isEmpty()) {
            cartViewModel.clearCart(userId);
        }
    }

    private void proceedToCheckout() {
        if (currentTotalItems <= 0) {
            showError("Giỏ hàng trống, không thể thanh toán");
            return;
        }
        
        Intent intent = new Intent(getContext(), CheckoutActivity.class);
        intent.putExtra("totalItems", currentTotalItems);
        intent.putExtra("totalPrice", currentTotalPrice);
        startActivity(intent);
    }

    private void showError(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
        }
    }

    private void showRemovedProductsNotification(String message, int removedCount) {
        if (getContext() != null) {
            String vietnameseMessage = "Đã xóa " + removedCount + " sản phẩm khỏi giỏ hàng vì không còn khả dụng.";
            Toast.makeText(getContext(), vietnameseMessage, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onIncreaseQuantity(CartItem cartItem) {
        String userId = sessionManager.getUserId();
        if (userId != null && cartItem != null) {
            int newQuantity = cartItem.getQuantity() + 1;
            cartViewModel.updateCartItem(userId, cartItem.getId(), newQuantity);
        } else {
            showError("Không thể tăng số lượng sản phẩm");
        }
    }

    @Override
    public void onDecreaseQuantity(CartItem cartItem) {
        String userId = sessionManager.getUserId();
        if (userId != null && cartItem != null) {
            int newQuantity = cartItem.getQuantity() - 1;
            if (newQuantity > 0) {
                cartViewModel.updateCartItem(userId, cartItem.getId(), newQuantity);
            } else {
                onRemoveItem(cartItem);
            }
        } else {
            showError("Không thể giảm số lượng sản phẩm");
        }
    }

    @Override
    public void onRemoveItem(CartItem cartItem) {
        String userId = sessionManager.getUserId();
        if (userId != null && cartItem != null && cartItem.getId() != null) {
            cartViewModel.removeCartItem(userId, cartItem.getId());
        } else {
            showError("Không thể xóa sản phẩm - thông tin không hợp lệ");
        }
    }

    @Override
    public void onItemClick(CartItem cartItem) {
        // TODO: Navigate to product detail if needed
    }

    @Override
    public void onResume() {
        super.onResume();
        loadCartData();
    }
}