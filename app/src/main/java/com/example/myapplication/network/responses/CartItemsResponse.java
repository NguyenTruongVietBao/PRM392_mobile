package com.example.myapplication.network.responses;

import com.example.myapplication.data.models.Cart;
import com.example.myapplication.data.models.CartItem;

import java.util.List;

public class CartItemsResponse {
    private Cart cart;
    private List<CartItem> items;
    private CleanupInfo cleanupInfo;

    public static class CleanupInfo {
        private int removedInvalidItems;
        private String message;

        public CleanupInfo() {}

        public int getRemovedInvalidItems() { return removedInvalidItems; }
        public void setRemovedInvalidItems(int removedInvalidItems) { this.removedInvalidItems = removedInvalidItems; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }

        public boolean hasRemovedItems() {
            return removedInvalidItems > 0;
        }
    }

    public CartItemsResponse() {}

    public CartItemsResponse(Cart cart, List<CartItem> items) {
        this.cart = cart;
        this.items = items;
    }

    public Cart getCart() { 
        return cart; 
    }
    
    public void setCart(Cart cart) { 
        this.cart = cart; 
    }

    public List<CartItem> getItems() { 
        return items; 
    }
    
    public void setItems(List<CartItem> items) { 
        this.items = items; 
    }

    public CleanupInfo getCleanupInfo() { 
        return cleanupInfo; 
    }
    
    public void setCleanupInfo(CleanupInfo cleanupInfo) { 
        this.cleanupInfo = cleanupInfo; 
    }
} 