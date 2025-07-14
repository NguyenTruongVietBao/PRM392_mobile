package com.example.myapplication.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static SessionManager instance;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private SessionManager(Context context) {
        sharedPreferences = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public static synchronized SessionManager getInstance(Context context) {
        if (instance == null) {
            instance = new SessionManager(context.getApplicationContext());
        }
        return instance;
    }

    public void saveUserId(String userId) {
        editor.putString(Constants.USER_ID_KEY, userId);
        editor.apply();
    }

    public String getUserId() {
        return sharedPreferences.getString(Constants.USER_ID_KEY, null);
    }

    public void saveUserRole(String role) {
        editor.putString(Constants.USER_ROLE_KEY, role);
        editor.apply();
    }

    public String getUserRole() {
        return sharedPreferences.getString(Constants.USER_ROLE_KEY, null);
    }

    public void saveUserInfo(String userId, String role) {
        editor.putString(Constants.USER_ID_KEY, userId);
        editor.putString(Constants.USER_ROLE_KEY, role);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return getUserId() != null;
    }

    public void clearSession() {
        editor.clear();
        editor.apply();
    }
}