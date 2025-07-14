package com.example.myapplication.utils;

import com.example.myapplication.data.models.Product;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ProductSortUtils {

    public static void sortProducts(List<Product> products, SortOption sortOption) {
        if (products == null || products.isEmpty()) {
            return;
        }

        switch (sortOption) {
            case NAME_ASC:
                Collections.sort(products, new Comparator<Product>() {
                    @Override
                    public int compare(Product p1, Product p2) {
                        String name1 = p1.getName() != null ? p1.getName() : "";
                        String name2 = p2.getName() != null ? p2.getName() : "";
                        return name1.compareToIgnoreCase(name2);
                    }
                });
                break;

            case NAME_DESC:
                Collections.sort(products, new Comparator<Product>() {
                    @Override
                    public int compare(Product p1, Product p2) {
                        String name1 = p1.getName() != null ? p1.getName() : "";
                        String name2 = p2.getName() != null ? p2.getName() : "";
                        return name2.compareToIgnoreCase(name1);
                    }
                });
                break;

            case PRICE_ASC:
                Collections.sort(products, new Comparator<Product>() {
                    @Override
                    public int compare(Product p1, Product p2) {
                        return Double.compare(p1.getPrice(), p2.getPrice());
                    }
                });
                break;

            case PRICE_DESC:
                Collections.sort(products, new Comparator<Product>() {
                    @Override
                    public int compare(Product p1, Product p2) {
                        return Double.compare(p2.getPrice(), p1.getPrice());
                    }
                });
                break;

            case STOCK_ASC:
                Collections.sort(products, new Comparator<Product>() {
                    @Override
                    public int compare(Product p1, Product p2) {
                        return Integer.compare(p1.getStockQuantity(), p2.getStockQuantity());
                    }
                });
                break;

            case STOCK_DESC:
                Collections.sort(products, new Comparator<Product>() {
                    @Override
                    public int compare(Product p1, Product p2) {
                        return Integer.compare(p2.getStockQuantity(), p1.getStockQuantity());
                    }
                });
                break;

            case DEFAULT:
            default:
                // Không sắp xếp, giữ nguyên thứ tự ban đầu
                break;
        }
    }
}