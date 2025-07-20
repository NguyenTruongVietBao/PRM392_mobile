package com.example.myapplication.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.data.models.ApiResponse;
import com.example.myapplication.data.models.Order;
import com.example.myapplication.data.models.CartItem;
import com.example.myapplication.data.repositories.OrderRepository;
import com.example.myapplication.network.requests.CreateOrderRequest;
import com.example.myapplication.ui.adapter.CheckoutItemAdapter;
import com.example.myapplication.ui.viewmodel.customer.CartViewModel;
import com.example.myapplication.utils.FormatUtils;
import com.example.myapplication.utils.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class CheckoutActivity extends AppCompatActivity {

    private static final String TAG = "CheckoutActivity";
    
    // UI Components
    private TextView tvTotalItems;
    private TextView tvTotalPrice;
    private RecyclerView recyclerViewCartItems;
    private TextInputEditText etShippingAddress;
    private TextInputEditText etOrderNote;
    private RadioGroup rgPaymentMethod;
    private RadioButton rbCOD;
    private RadioButton rbBankTransfer;
    private MaterialButton btnCreateOrder;
    private ProgressBar progressBarLoading;
    
    // Data
    private SessionManager sessionManager;
    private OrderRepository orderRepository;
    private CartViewModel cartViewModel;
    private CheckoutItemAdapter checkoutItemAdapter;

    private int totalItems = 0;
    private double totalPrice = 0.0;
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_checkout);
        
        initViews();
        initComponents();
        setupRecyclerView();
        getCartDataFromIntent();
        setupClickListeners();
        observeViewModel();
        loadCartData();
    }
    
    private void initViews() {
        tvTotalItems = findViewById(R.id.tvTotalItems);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        recyclerViewCartItems = findViewById(R.id.recyclerViewCartItems);
        etShippingAddress = findViewById(R.id.etShippingAddress);
        etOrderNote = findViewById(R.id.etOrderNote);
        rgPaymentMethod = findViewById(R.id.rgPaymentMethod);
        rbCOD = findViewById(R.id.rbCOD);
        rbBankTransfer = findViewById(R.id.rbBankTransfer);
        btnCreateOrder = findViewById(R.id.btnCreateOrder);
        progressBarLoading = findViewById(R.id.progressBarLoading);
    }
    
    private void initComponents() {
        sessionManager = SessionManager.getInstance(this);
        orderRepository = new OrderRepository();
        cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);
        checkoutItemAdapter = new CheckoutItemAdapter();

        // Setup toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Thanh toán");
        }
    }
    
    private void setupRecyclerView() {
        recyclerViewCartItems.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewCartItems.setAdapter(checkoutItemAdapter);
        recyclerViewCartItems.setNestedScrollingEnabled(false);
    }

    private void getCartDataFromIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            totalItems = intent.getIntExtra("totalItems", 0);
            totalPrice = intent.getDoubleExtra("totalPrice", 0.0);
            updateOrderSummary();
        }
    }
    
    private void loadCartData() {
        String userId = sessionManager.getUserId();
        if (userId != null && !userId.isEmpty()) {
            cartViewModel.getCartItems(userId);
        }
    }
    
    private void setupClickListeners() {
        btnCreateOrder.setOnClickListener(v -> createOrder());
    }
    
    private void observeViewModel() {
        // Observe cart items to get real-time data
        cartViewModel.getCartItemsFullResult().observe(this, result -> {
            if (result != null && result.isSuccess() && result.getData() != null) {
                // Calculate totals from real cart data
                totalItems = 0;
                totalPrice = 0.0;
                
                List<CartItem> cartItems = null;
                if (result.getData().getItems() != null) {
                    cartItems = result.getData().getItems();
                    for (CartItem item : cartItems) {
                        totalItems += item.getQuantity();
                        totalPrice += item.getTotalPrice();
                    }
                }
                
                // Update UI with cart items
                updateCartItemsDisplay(cartItems);
                updateOrderSummary();
            }
        });
        
        // Observe order creation result
        orderRepository.getCreateOrderResult().observe(this, result -> {
            if (result != null) {
                if (result.isSuccess() && result.getData() != null) {
                    Order order = result.getData();
                    // Navigate to payment success screen
                    navigateToPaymentSuccess(order);
                } else {
                    // Navigate to payment failure screen
                    navigateToPaymentFailure();
                }
            }
        });
        
        // Observe loading state
        orderRepository.getIsLoading().observe(this, isLoading -> {
            if (isLoading != null) {
                progressBarLoading.setVisibility(isLoading ? View.VISIBLE : View.GONE);
                btnCreateOrder.setEnabled(!isLoading);
            }
        });
    }
    
    private void updateCartItemsDisplay(List<CartItem> cartItems) {
        if (cartItems != null && !cartItems.isEmpty()) {
            checkoutItemAdapter.setCartItems(cartItems);
            recyclerViewCartItems.setVisibility(View.VISIBLE);
        } else {
            recyclerViewCartItems.setVisibility(View.GONE);
        }
    }

    private void updateOrderSummary() {
        tvTotalItems.setText(totalItems + " sản phẩm");
        tvTotalPrice.setText(FormatUtils.formatCurrency(totalPrice));
    }
    
    private void createOrder() {
        if (!validateInput()) {
            return;
        }
        
        String userId = sessionManager.getUserId();
        if (userId == null || userId.isEmpty()) {
            showError("Không thể xác định người dùng");
            return;
        }
        
        // Get input values
        String shippingAddress = etShippingAddress.getText().toString().trim();
        String orderNote = etOrderNote.getText().toString().trim();
        String paymentMethod = getSelectedPaymentMethod();
        
        // Create order request
        CreateOrderRequest createOrderRequest = new CreateOrderRequest(
            userId,
            shippingAddress,
            orderNote,
            paymentMethod
        );
        
        Log.d(TAG, "Creating order with: " + 
              "userId=" + userId + 
              ", address=" + shippingAddress + 
              ", payment=" + paymentMethod);
        
        orderRepository.createOrder(createOrderRequest);
    }
    
    private boolean validateInput() {
        String shippingAddress = etShippingAddress.getText().toString().trim();
        
        if (TextUtils.isEmpty(shippingAddress)) {
            etShippingAddress.setError("Vui lòng nhập địa chỉ giao hàng");
            etShippingAddress.requestFocus();
            return false;
        }
        
        if (rgPaymentMethod.getCheckedRadioButtonId() == -1) {
            showError("Vui lòng chọn phương thức thanh toán");
            return false;
        }
        
        if (totalItems <= 0) {
            showError("Giỏ hàng trống, không thể đặt hàng");
            return false;
        }
        
        return true;
    }
    
    private String getSelectedPaymentMethod() {
        int selectedId = rgPaymentMethod.getCheckedRadioButtonId();
        if (selectedId == R.id.rbCOD) {
            return "COD";
        } else if (selectedId == R.id.rbBankTransfer) {
            return "BANK_TRANSFER";
        }
        return "COD";
    }
    
    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
    private void showSuccess(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void navigateToPaymentSuccess(Order order) {
        Intent intent = new Intent(this, PaymentStatusActivity.class);

        // Pass order information
        if (order != null && order.getId() != null) {
            intent.putExtra("orderId", order.getId());
        }

        // Pass current checkout information
        intent.putExtra("totalAmount", totalPrice);
        intent.putExtra("paymentMethod", getSelectedPaymentMethod());
        intent.putExtra("isPaymentSuccess", true);

        // Pass shipping and order details
        String shippingAddress = etShippingAddress.getText() != null ?
            etShippingAddress.getText().toString().trim() : "";
        String orderNote = etOrderNote.getText() != null ?
            etOrderNote.getText().toString().trim() : "";

        intent.putExtra("shippingAddress", shippingAddress);
        intent.putExtra("orderNote", orderNote);

        startActivity(intent);
        finish();
    }

    private void navigateToPaymentFailure() {
        Intent intent = new Intent(this, PaymentStatusActivity.class);

        // Pass failure status
        intent.putExtra("isPaymentSuccess", false);
        intent.putExtra("totalAmount", totalPrice);
        intent.putExtra("paymentMethod", getSelectedPaymentMethod());

        startActivity(intent);
        finish();
    }
}
