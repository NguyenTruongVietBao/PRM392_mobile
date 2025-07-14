package com.example.myapplication.data.models;

import com.example.myapplication.enums.UserRole;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class User {
    @SerializedName("_id")
    private String id;
    private String email;
    private String password;
    private String name;
    private String phone;
    private String address;
    private String avatar;
    private UserRole role;
    private boolean isActive;
    private Date createdAt;
    private Date updatedAt;

    // Constructors
    public User() {}

    public User( String email, String password, UserRole role) {
        this.email = email;
        this.password = password;
        this.role = role;
        this.isActive = true;
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }


    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }

    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }


    public boolean isManager() {
        return role == UserRole.MANAGER;
    }

}