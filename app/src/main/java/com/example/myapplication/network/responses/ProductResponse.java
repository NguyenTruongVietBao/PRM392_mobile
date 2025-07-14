package com.example.myapplication.network.responses;

import com.example.myapplication.data.models.Product;

import java.util.List;

public class ProductResponse {
    private List<Product> products;
    private Pagination pagination;

    public ProductResponse() {
    }

    public ProductResponse(List<Product> products, Pagination pagination) {
        this.products = products;
        this.pagination = pagination;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }

    // Nested Pagination class
    public static class Pagination {
        private int current;
        private int total;
        private int count;
        private int totalItems;

        public Pagination() {
        }

        public Pagination(int current, int total, int count, int totalItems) {
            this.current = current;
            this.total = total;
            this.count = count;
            this.totalItems = totalItems;
        }

        public int getCurrent() {
            return current;
        }

        public void setCurrent(int current) {
            this.current = current;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public int getTotalItems() {
            return totalItems;
        }

        public void setTotalItems(int totalItems) {
            this.totalItems = totalItems;
        }

        public boolean hasNextPage() {
            return current < total;
        }

        public boolean hasPreviousPage() {
            return current > 1;
        }
    }
} 