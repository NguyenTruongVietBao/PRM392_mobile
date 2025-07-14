package com.example.myapplication.data.models;

import com.example.myapplication.enums.ProductStatus;
import com.google.gson.annotations.SerializedName;
import com.google.gson.annotations.JsonAdapter;
import com.example.myapplication.network.serializers.CategorySerializer;

import java.util.Date;

public class Product {
    @SerializedName(value = "id", alternate = "_id")
    private String id;
    private String name;
    private String description;
    private double price;
    private String imageUrl;
    @JsonAdapter(CategorySerializer.class)
    private Category category;
    private int stockQuantity;
    private String color;
    private String size;
    private ProductStatus status;
    private int rating;
    private int reviewCount;
    private Date createdAt;
    private Date updatedAt;

    // Constructors
    public Product() {
        this.status = ProductStatus.INACTIVE;
        this.rating = 0;
        this.reviewCount = 0;
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    public Product(String name, String description, double price, Category category) {
        this();
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public int getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(int stockQuantity) { this.stockQuantity = stockQuantity; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }


    public ProductStatus getStatus() { return status; }
    public void setStatus(ProductStatus status) { this.status = status; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public int getReviewCount() { return reviewCount; }
    public void setReviewCount(int reviewCount) { this.reviewCount = reviewCount; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }

    // Utility methods
    public boolean isInStock() {
        return stockQuantity > 0;
    }

    public String getFormattedPrice() {
        return String.format("$%.2f", price);
    }

    public String getFormattedRating() {
        return String.format("%.1f (%d)", rating, reviewCount);
    }

    // Debug method
    @Override
    public String toString() {
        return "Product{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", stockQuantity=" + stockQuantity +
                ", status=" + status +
                '}';
    }
}


