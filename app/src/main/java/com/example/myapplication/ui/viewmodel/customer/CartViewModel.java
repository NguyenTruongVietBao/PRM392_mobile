package com.example.myapplication.ui.viewmodel.customer;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.myapplication.data.models.ApiResponse;
import com.example.myapplication.data.models.Cart;
import com.example.myapplication.data.models.CartItem;
import com.example.myapplication.network.responses.CartItemsResponse;
import com.example.myapplication.data.repositories.CartRepository;

import java.util.List;

public class CartViewModel extends ViewModel {
    private CartRepository cartRepository;

    public CartViewModel() {
        cartRepository = new CartRepository();
    }

    // Getters
    public LiveData<ApiResponse<Cart>> getCartResult() {
        return cartRepository.getCartResult();
    }

    public LiveData<ApiResponse<List<CartItem>>> getCartItemsResult() {
        return cartRepository.getCartItemsResult();
    }

    public LiveData<ApiResponse<CartItemsResponse>> getCartItemsFullResult() {
        return cartRepository.getCartItemsFullResult();
    }

    public LiveData<ApiResponse<CartItem>> getCartItemResult() {
        return cartRepository.getCartItemResult();
    }

    public LiveData<Boolean> getIsLoading() {
        return cartRepository.getIsLoading();
    }

    // Cart operations
    public void getCartByUserId(String userId) {
        cartRepository.getCartByUserId(userId);
    }

    public void getCartItems(String userId) {
        cartRepository.getCartItems(userId);
    }

    public void addItemToCart(String userId, String productId, int quantity) {
        cartRepository.addItemToCart(userId, productId, quantity);
    }

    public void updateCartItem(String userId, String itemId, int quantity) {
        cartRepository.updateCartItem(userId, itemId, quantity);
    }

    public void removeCartItem(String userId, String itemId) {
        cartRepository.removeCartItem(userId, itemId);
    }

    public void clearCart(String userId) {
        cartRepository.clearCart(userId);
    }
} 