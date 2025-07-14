package com.example.myapplication.network.serializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.example.myapplication.data.models.CartItem;
import com.example.myapplication.data.models.Product;

import java.lang.reflect.Type;

public class CartItemDeserializer implements JsonDeserializer<CartItem> {
    
    @Override
    public CartItem deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) 
            throws JsonParseException {
        
        JsonObject jsonObject = json.getAsJsonObject();
        CartItem cartItem = new CartItem();
        
        // Handle id field (could be _id or id)
        if (jsonObject.has("_id")) {
            cartItem.setId(jsonObject.get("_id").getAsString());
        } else if (jsonObject.has("id")) {
            cartItem.setId(jsonObject.get("id").getAsString());
        }
        
        // Handle cartId
        if (jsonObject.has("cartId")) {
            cartItem.setCartId(jsonObject.get("cartId").getAsString());
        }
        
        // Handle product field - could be object or string
        if (jsonObject.has("product")) {
            JsonElement productElement = jsonObject.get("product");
            
            if (productElement.isJsonObject()) {
                // Product is an object - deserialize normally
                Product product = context.deserialize(productElement, Product.class);
                cartItem.setProduct(product);
            } else if (productElement.isJsonPrimitive()) {
                // Product is just a string ID
                String productId = productElement.getAsString();
                cartItem.setProductId(productId);
                // Create a minimal product object if needed
                Product product = new Product();
                product.setId(productId);
                product.setName("Sản phẩm"); // Default name
                cartItem.setProduct(product);
            }
        }
        
        // Handle other fields
        if (jsonObject.has("quantity")) {
            cartItem.setQuantity(jsonObject.get("quantity").getAsInt());
        }
        
        if (jsonObject.has("unitPrice")) {
            cartItem.setUnitPrice(jsonObject.get("unitPrice").getAsDouble());
        }
        
        if (jsonObject.has("totalPrice")) {
            cartItem.setTotalPrice(jsonObject.get("totalPrice").getAsDouble());
        }
        
        return cartItem;
    }
} 