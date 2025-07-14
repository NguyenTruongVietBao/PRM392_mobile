package com.example.myapplication.data.repositories;

import android.content.Context;

public class RepositoryManager {
    private static RepositoryManager instance;

    private AuthRepository authRepository;
    private UserRepository userRepository;
    private ProductRepository productRepository;
    private CartRepository cartRepository;
    private OrderRepository orderRepository;
    private CategoryRepository categoryRepository;
    private PaymentRepository paymentRepository;

    private RepositoryManager(Context context) {
        initializeRepositories(context);
    }

    public static synchronized RepositoryManager getInstance(Context context) {
        if (instance == null) {
            instance = new RepositoryManager(context.getApplicationContext());
        }
        return instance;
    }

    private void initializeRepositories(Context context) {
        authRepository = new AuthRepository(context);
        userRepository = new UserRepository();
        productRepository = new ProductRepository();
        cartRepository = new CartRepository();
        orderRepository = new OrderRepository();
        categoryRepository = new CategoryRepository();
        paymentRepository = new PaymentRepository();
    }
    
    public AuthRepository getAuthRepository() {
        return authRepository;
    }
    
    public UserRepository getUserRepository() {
        return userRepository;
    }
    
    public ProductRepository getProductRepository() {
        return productRepository;
    }
    
    public CartRepository getCartRepository() {
        return cartRepository;
    }
    
    public OrderRepository getOrderRepository() {
        return orderRepository;
    }
    
    public CategoryRepository getCategoryRepository() {
        return categoryRepository;
    }
    
    public PaymentRepository getPaymentRepository() {
        return paymentRepository;
    }

} 