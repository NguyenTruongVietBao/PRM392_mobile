package com.example.myapplication.utils;

public class Constants {
    public static final String BASE_URL = "http://10.0.2.2:8080/";
    public static final int TIMEOUT_DURATION = 60;

    // SharedPreferences Keys
    public static final String PREF_NAME = "PRM392_PREF";
    public static final String USER_ID_KEY = "user_id";
    public static final String USER_ROLE_KEY = "user_role";
    public static final String CART_ID_KEY = "cart_id";

    // Request Headers
    public static final String CONTENT_TYPE_JSON = "application/json";

    // HTTP Status Codes
    public static final int HTTP_OK = 200;
    public static final int HTTP_CREATED = 201;
    public static final int HTTP_BAD_REQUEST = 400;
    public static final int HTTP_UNAUTHORIZED = 401;
    public static final int HTTP_FORBIDDEN = 403;
    public static final int HTTP_NOT_FOUND = 404;
    public static final int HTTP_INTERNAL_ERROR = 500;

    // Error Messages
    public static final String ERROR_NETWORK = "Lỗi kết nối mạng";
    public static final String ERROR_TIMEOUT = "Hết thời gian chờ";
    public static final String ERROR_UNAUTHORIZED = "Không có quyền truy cập";
    public static final String ERROR_UNKNOWN = "Lỗi không xác định";

    public static final String PRODUCT_STATUS_ACTIVE = "ACTIVE";
    public static final String PRODUCT_STATUS_INACTIVE = "INACTIVE";

    // Image Config
    public static final int MAX_IMAGE_SIZE_MB = 5;
    public static final String[] ALLOWED_IMAGE_TYPES = {"jpg", "jpeg", "png", "webp"};
}
