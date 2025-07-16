package com.example.myapplication.data.repositories;

import android.content.Context;

// Quản lý các repository, cung cấp singleton cho toàn bộ app
public class RepositoryManager {
    // Singleton instance
    private static RepositoryManager instance;

    // Các repository quản lý dữ liệu cho từng chức năng
    private AuthRepository authRepository;
    private UserRepository userRepository;
    private ProductRepository productRepository;
    private CartRepository cartRepository;
    private OrderRepository orderRepository;
    private CategoryRepository categoryRepository;
    private PaymentRepository paymentRepository;

    // Constructor private để chỉ cho phép khởi tạo qua getInstance
    private RepositoryManager(Context context) {
        initializeRepositories(context);
    }

    // Lấy instance duy nhất của RepositoryManager (singleton)
    public static synchronized RepositoryManager getInstance(Context context) {
        if (instance == null) {
            instance = new RepositoryManager(context.getApplicationContext());
        }
        return instance;
    }

    // Khởi tạo các repository, truyền context nếu cần thiết
    private void initializeRepositories(Context context) {
        authRepository = new AuthRepository(context);
        userRepository = new UserRepository();
        productRepository = new ProductRepository();
        cartRepository = new CartRepository();
        orderRepository = new OrderRepository();
        categoryRepository = new CategoryRepository();
        paymentRepository = new PaymentRepository();
    }
    
    // Lấy AuthRepository
    public AuthRepository getAuthRepository() {
        return authRepository;
    }
    // Lấy UserRepository
    public UserRepository getUserRepository() {
        return userRepository;
    }
    // Lấy ProductRepository
    public ProductRepository getProductRepository() {
        return productRepository;
    }
    // Lấy CartRepository
    public CartRepository getCartRepository() {
        return cartRepository;
    }
    // Lấy OrderRepository
    public OrderRepository getOrderRepository() {
        return orderRepository;
    }
    // Lấy CategoryRepository
    public CategoryRepository getCategoryRepository() {
        return categoryRepository;
    }
    // Lấy PaymentRepository
    public PaymentRepository getPaymentRepository() {
        return paymentRepository;
    }

} 