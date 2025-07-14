package com.example.myapplication.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.utils.FormatUtils;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PaymentSuccessActivity extends AppCompatActivity {

    // UI Components
    private TextView tvOrderId;
    private TextView tvOrderDate;
    private TextView tvTotalItems;
    private TextView tvTotalAmount;
    private TextView tvPaymentMethod;
    private TextView tvOrderStatus;
    private TextView tvShippingAddress;
    private TextView tvOrderNote;
    private MaterialButton btnTrackOrder;
    private MaterialButton btnContinueShopping;

    // Order data
    private String orderId;
    private int totalItems;
    private double totalAmount;
    private String paymentMethod;
    private String shippingAddress;
    private String orderNote;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_success);

        initViews();
        getOrderDataFromIntent();
        setupClickListeners();
        displayOrderInfo();
    }

    private void initViews() {
        tvOrderId = findViewById(R.id.tvOrderId);
        tvOrderDate = findViewById(R.id.tvOrderDate);
        tvTotalItems = findViewById(R.id.tvTotalItems);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        tvPaymentMethod = findViewById(R.id.tvPaymentMethod);
        tvOrderStatus = findViewById(R.id.tvOrderStatus);
        tvShippingAddress = findViewById(R.id.tvShippingAddress);
        tvOrderNote = findViewById(R.id.tvOrderNote);
        btnTrackOrder = findViewById(R.id.btnTrackOrder);
        btnContinueShopping = findViewById(R.id.btnContinueShopping);

        // Hide action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }

    private void getOrderDataFromIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            orderId = intent.getStringExtra("orderId");
            totalItems = intent.getIntExtra("totalItems", 0);
            totalAmount = intent.getDoubleExtra("totalAmount", 0.0);
            paymentMethod = intent.getStringExtra("paymentMethod");
            shippingAddress = intent.getStringExtra("shippingAddress");
            orderNote = intent.getStringExtra("orderNote");
        }
    }

    private void setupClickListeners() {
        btnTrackOrder.setOnClickListener(v -> trackOrder());
        btnContinueShopping.setOnClickListener(v -> continueShopping());
    }

    private void displayOrderInfo() {
        // Display order ID
        if (orderId != null && !orderId.isEmpty()) {
            tvOrderId.setText("#" + orderId);
        } else {
            tvOrderId.setText("#ORD" + System.currentTimeMillis());
        }

        // Display current date and time
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy - HH:mm", Locale.getDefault());
        tvOrderDate.setText(sdf.format(new Date()));

        // Display total items
        tvTotalItems.setText(totalItems + " sản phẩm");

        // Display total amount
        tvTotalAmount.setText(FormatUtils.formatCurrency(totalAmount));

        // Display payment method
        String paymentMethodText = getPaymentMethodText(paymentMethod);
        tvPaymentMethod.setText(paymentMethodText);

        // Display order status
        tvOrderStatus.setText("Đang xử lý");

        // Display shipping address
        if (shippingAddress != null && !shippingAddress.isEmpty()) {
            tvShippingAddress.setText(shippingAddress);
        } else {
            tvShippingAddress.setText("Chưa có thông tin");
        }

        // Display order note
        if (orderNote != null && !orderNote.isEmpty()) {
            tvOrderNote.setText(orderNote);
        } else {
            tvOrderNote.setText("Không có ghi chú");
        }
    }

    private String getPaymentMethodText(String method) {
        if (method == null) return "Thanh toán khi nhận hàng";

        switch (method) {
            case "COD":
                return "Thanh toán khi nhận hàng";
            case "BANK_TRANSFER":
                return "Chuyển khoản ngân hàng";
            default:
                return "Thanh toán khi nhận hàng";
        }
    }

    private void trackOrder() {
        // TODO: Navigate to order tracking screen
        // For now, just show a message or navigate to orders list
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("navigate_to", "orders");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void continueShopping() {
        // Navigate back to main activity (home page)
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        // Override back button to go to main activity instead of checkout
        continueShopping();
    }
}
