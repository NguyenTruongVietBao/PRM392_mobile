package com.example.myapplication.utils;

public enum SortOption {
    DEFAULT("Mặc định", "default"),
    NAME_ASC("Tên A-Z", "name_asc"),
    NAME_DESC("Tên Z-A", "name_desc"),
    PRICE_ASC("Giá thấp đến cao", "price_asc"),
    PRICE_DESC("Giá cao đến thấp", "price_desc"),
    STOCK_ASC("Tồn kho ít nhất", "stock_asc"),
    STOCK_DESC("Tồn kho nhiều nhất", "stock_desc");

    private String displayName;
    private String value;

    SortOption(String displayName, String value) {
        this.displayName = displayName;
        this.value = value;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getValue() {
        return value;
    }

    public static SortOption fromValue(String value) {
        for (SortOption option : values()) {
            if (option.getValue().equals(value)) {
                return option;
            }
        }
        return DEFAULT;
    }
}