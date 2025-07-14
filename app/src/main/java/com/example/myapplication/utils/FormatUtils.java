package com.example.myapplication.utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FormatUtils {

    // Format currency với ký hiệu VND
    public static String formatCurrency(double amount) {
        DecimalFormat formatter = new DecimalFormat("#,###");
        return formatter.format(amount) + " VND";
    }

    // Format currency với ký hiệu USD
    public static String formatCurrencyUSD(double amount) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.US);
        return formatter.format(amount);
    }

    // Format số lượng với dấu phân cách
    public static String formatNumber(int number) {
        DecimalFormat formatter = new DecimalFormat("#,###");
        return formatter.format(number);
    }

    // Format ngày tháng
    public static String formatDate(Date date) {
        if (date == null) return "";
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return formatter.format(date);
    }

    // Format ngày tháng với giờ
    public static String formatDateTime(Date date) {
        if (date == null) return "";
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        return formatter.format(date);
    }

    // Format rating với sao
    public static String formatRating(float rating, int reviewCount) {
        if (reviewCount == 0) {
            return "Chưa có đánh giá";
        }
        return String.format(Locale.getDefault(), "%.1f ⭐ (%d đánh giá)", rating, reviewCount);
    }

    // Format phần trăm giảm giá
    public static String formatDiscount(double originalPrice, double salePrice) {
        if (originalPrice <= salePrice) return "";
        
        double discountPercent = ((originalPrice - salePrice) / originalPrice) * 100;
        return String.format(Locale.getDefault(), "-%.0f%%", discountPercent);
    }

    // Truncate text với ellipsis
    public static String truncateText(String text, int maxLength) {
        if (text == null || text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength - 3) + "...";
    }

    // Format trạng thái đơn hàng
    public static String formatOrderStatus(String status) {
        if (status == null) return "Không xác định";
        
        switch (status.toUpperCase()) {
            case "PENDING":
                return "Chờ xử lý";
            case "CONFIRMED":
                return "Đã xác nhận";
            case "PROCESSING":
                return "Đang xử lý";
            case "SHIPPED":
                return "Đang giao";
            case "DELIVERED":
                return "Đã giao";
            case "CANCELLED":
                return "Đã hủy";
            default:
                return status;
        }
    }

    // Format trạng thái thanh toán
    public static String formatPaymentStatus(String status) {
        if (status == null) return "Không xác định";
        
        switch (status.toUpperCase()) {
            case "PENDING":
                return "Chờ thanh toán";
            case "PROCESSING":
                return "Đang xử lý";
            case "COMPLETED":
                return "Đã thanh toán";
            case "FAILED":
                return "Thất bại";
            case "CANCELLED":
                return "Đã hủy";
            case "REFUNDED":
                return "Đã hoàn tiền";
            default:
                return status;
        }
    }
}
