package com.example.myapplication.utils;

import android.text.TextUtils;
import android.util.Patterns;

public class ValidationUtils {
    public static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isValidPassword(String password) {
        return !TextUtils.isEmpty(password) && password.length() >= 6;
    }

    public static boolean isValidPhone(String phone) {
        return !TextUtils.isEmpty(phone) && Patterns.PHONE.matcher(phone).matches();
    }

    public static boolean isValidName(String name) {
        return !TextUtils.isEmpty(name) && name.trim().length() >= 2;
    }

    public static boolean isValidAddress(String address) {
        return !TextUtils.isEmpty(address) && address.trim().length() >= 5;
    }

    public static String validateLoginInput(String email, String password) {
        if (TextUtils.isEmpty(email)) {
            return "Email không được để trống";
        }
        if (!isValidEmail(email)) {
            return "Định dạng email không hợp lệ";
        }
        if (TextUtils.isEmpty(password)) {
            return "Mật khẩu không được để trống";
        }
        if (!isValidPassword(password)) {
            return "Mật khẩu phải có ít nhất 6 ký tự";
        }
        return null;
    }

    public static String validateRegisterInput(String email, String password, String confirmPassword) {
        String loginValidation = validateLoginInput(email, password);
        if (loginValidation != null) {
            return loginValidation;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            return "Xác nhận mật khẩu không được để trống";
        }
        if (!password.equals(confirmPassword)) {
            return "Mật khẩu xác nhận không khớp";
        }
        return null;
    }

    public static String validateRegisterInput(String email, String password, String confirmPassword, 
                                             String name, String phone, String address) {
        // Validate login fields first
        String loginValidation = validateRegisterInput(email, password, confirmPassword);
        if (loginValidation != null) {
            return loginValidation;
        }

        // Validate name
        if (TextUtils.isEmpty(name)) {
            return "Họ tên không được để trống";
        }
        if (!isValidName(name)) {
            return "Họ tên phải có ít nhất 2 ký tự";
        }

        // Validate phone
        if (TextUtils.isEmpty(phone)) {
            return "Số điện thoại không được để trống";
        }
        if (!isValidPhone(phone)) {
            return "Số điện thoại không hợp lệ";
        }

        // Validate address
        if (TextUtils.isEmpty(address)) {
            return "Địa chỉ không được để trống";
        }
        if (!isValidAddress(address)) {
            return "Địa chỉ phải có ít nhất 5 ký tự";
        }

        return null;
    }

    public static String validateUserUpdate(String name, String phone, String address) {
        // Validate name
        if (!TextUtils.isEmpty(name) && !isValidName(name)) {
            return "Họ tên phải có ít nhất 2 ký tự";
        }

        // Validate phone
        if (!TextUtils.isEmpty(phone) && !isValidPhone(phone)) {
            return "Số điện thoại không hợp lệ";
        }

        // Validate address
        if (!TextUtils.isEmpty(address) && !isValidAddress(address)) {
            return "Địa chỉ phải có ít nhất 5 ký tự";
        }

        return null;
    }
}
