package com.example.myapplication.network.serializers;

import com.example.myapplication.data.models.Category;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

/**
 * Custom Gson serializer/deserializer for Category
 * When sending to backend: serialize as ID string only
 * When receiving from backend: deserialize as full Category object
 */
public class CategorySerializer implements JsonSerializer<Category>, JsonDeserializer<Category> {

    @Override
    public JsonElement serialize(Category category, Type typeOfSrc, JsonSerializationContext context) {
        // When sending to backend, only send the ID
        if (category != null && category.getId() != null) {
            return new JsonPrimitive(category.getId());
        }
        return null;
    }

    @Override
    public Category deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) 
            throws JsonParseException {
        
        if (json.isJsonPrimitive()) {
            // If it's a string (ID only), create Category with just ID
            String categoryId = json.getAsString();
            Category category = new Category();
            category.setId(categoryId);
            return category;
        } else if (json.isJsonObject()) {
            // If it's a full object, deserialize normally
            JsonObject jsonObject = json.getAsJsonObject();
            Category category = new Category();
            
            if (jsonObject.has("_id")) {
                category.setId(jsonObject.get("_id").getAsString());
            } else if (jsonObject.has("id")) {
                category.setId(jsonObject.get("id").getAsString());
            }
            
            if (jsonObject.has("name")) {
                category.setName(jsonObject.get("name").getAsString());
            }
            
            if (jsonObject.has("description")) {
                category.setDescription(jsonObject.get("description").getAsString());
            }
            
            if (jsonObject.has("imageUrl") && !jsonObject.get("imageUrl").isJsonNull()) {
                category.setImageUrl(jsonObject.get("imageUrl").getAsString());
            }
            
            return category;
        }
        
        return null;
    }
} 