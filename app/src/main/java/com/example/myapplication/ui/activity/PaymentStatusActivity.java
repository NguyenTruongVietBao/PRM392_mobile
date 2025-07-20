package com.example.myapplication.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.R;
import com.example.myapplication.utils.FormatUtils;
import com.google.android.material.button.MaterialButton;

public class PaymentStatusActivity extends AppCompatActivity {

    private ImageView ivStatusIcon;
    private TextView tvStatusTitle;
    private TextView tvStatusMessage;
    private TextView tvOrderId;
    private TextView tvTotalAmount;
    private TextView tvPaymentMethod;
    private TextView tvShippingAddress;
    private TextView tvOrderNote;
    private MaterialButton btnBackToHome;
    private MaterialButton btnViewOrders;

    // Order data
    private String orderId;
    private double totalAmount;
    private String paymentMethod;
    private String shippingAddress;
    private String orderNote;
    private boolean isPaymentSuccess = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_payment_status);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        getDataFromIntent();
        setupUI();
        setupClickListeners();
        setupOnBackPressed();
    }

    private void initViews() {
        ivStatusIcon = findViewById(R.id.ivStatusIcon);
        tvStatusTitle = findViewById(R.id.tvStatusTitle);
        tvStatusMessage = findViewById(R.id.tvStatusMessage);
        tvOrderId = findViewById(R.id.tvOrderId);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        tvPaymentMethod = findViewById(R.id.tvPaymentMethod);
        tvShippingAddress = findViewById(R.id.tvShippingAddress);
        tvOrderNote = findViewById(R.id.tvOrderNote);
        btnBackToHome = findViewById(R.id.btnBackToHome);
        btnViewOrders = findViewById(R.id.btnViewOrders);
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            orderId = intent.getStringExtra("orderId");
            totalAmount = intent.getDoubleExtra("totalAmount", 0.0);
            paymentMethod = intent.getStringExtra("paymentMethod");
            shippingAddress = intent.getStringExtra("shippingAddress");
            orderNote = intent.getStringExtra("orderNote");
            isPaymentSuccess = intent.getBooleanExtra("isPaymentSuccess", true);
        }
    }

    private void setupUI() {
        if (isPaymentSuccess) {
            setupSuccessUI();
        } else {
            setupFailureUI();
        }

        // Set order details
        if (orderId != null && !orderId.isEmpty()) {
            tvOrderId.setText("Mã đơn hàng: " + orderId);
        } else {
            tvOrderId.setVisibility(View.GONE);
        }

        tvTotalAmount.setText("Tổng tiền: " + FormatUtils.formatCurrency(totalAmount));

        String paymentMethodText = getPaymentMethodText(paymentMethod);
        tvPaymentMethod.setText("Phương thức thanh toán: " + paymentMethodText);

        if (shippingAddress != null && !shippingAddress.isEmpty()) {
            tvShippingAddress.setText("Địa chỉ giao hàng: " + shippingAddress);
        } else {
            tvShippingAddress.setVisibility(View.GONE);
        }

        if (orderNote != null && !orderNote.isEmpty()) {
            tvOrderNote.setText("Ghi chú: " + orderNote);
        } else {
            tvOrderNote.setVisibility(View.GONE);
        }
    }

    private void setupSuccessUI() {
        ivStatusIcon.setImageResource(android.R.drawable.ic_menu_save);
        tvStatusTitle.setText("Đặt hàng thành công!");
        tvStatusMessage.setText("Cảm ơn bạn đã đặt hàng. Chúng tôi sẽ xử lý đơn hàng và liên hệ với bạn sớm nhất có thể.");

        // Show success colors
        tvStatusTitle.setTextColor(getResources().getColor(R.color.success_green, null));
    }

    private void setupFailureUI() {
        ivStatusIcon.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
        tvStatusTitle.setText("Đặt hàng thất bại!");
        tvStatusMessage.setText("Có lỗi xảy ra trong quá trình đặt hàng. Vui lòng thử lại sau.");

        // Show error colors
        tvStatusTitle.setTextColor(getResources().getColor(R.color.error_red, null));

        // Hide order details for failed orders
        tvOrderId.setVisibility(View.GONE);
        tvShippingAddress.setVisibility(View.GONE);
        tvOrderNote.setVisibility(View.GONE);
    }

    private String getPaymentMethodText(String method) {
        if (method == null) return "Không xác định";

        switch (method.toLowerCase()) {
            case "cod":
                return "Thanh toán khi nhận hàng (COD)";
            case "bank_transfer":
                return "Chuyển khoản ngân hàng";
            default:
                return method;
        }
    }

    private void setupClickListeners() {
        btnBackToHome.setOnClickListener(v -> navigateToHome());
        btnViewOrders.setOnClickListener(v -> navigateToOrders());
    }

    private void setupOnBackPressed() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                navigateToHome();
            }
        });
    }

    private void navigateToHome() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void navigateToOrders() {
        // Navigate to orders list activity
        Intent intent = new Intent(this, OrderHistoryActivity.class);
        startActivity(intent);
        finish();
    }
}