package com.example.myapplication.network.serializers;

import com.example.myapplication.data.models.Payment;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class PaymentDeserializer implements JsonDeserializer<Payment> {
    @Override
    public Payment deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json.isJsonNull()) {
            return null;
        }
        
        if (json.isJsonPrimitive() && json.getAsJsonPrimitive().isString()) {
            // If payment is just a string ID, return null or create a minimal Payment object
            return null;
        }
        
        if (json.isJsonObject()) {
            // If payment is a full object, deserialize normally
            return context.deserialize(json, Payment.class);
        }
        
        return null;
    }
} 