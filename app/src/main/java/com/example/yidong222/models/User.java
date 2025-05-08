package com.example.yidong222.models;

import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("id")
    private int id;

    @SerializedName("username")
    private String username;

    @SerializedName("is_admin")
    private int isAdminValue;

    @SerializedName("token")
    private String token;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("updated_at")
    private String updatedAt;

    // 构造函数
    public User() {
    }

    public User(int id, String username, boolean isAdmin, String token) {
        this.id = id;
        this.username = username;
        this.isAdminValue = isAdmin ? 1 : 0;
        this.token = token;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isAdmin() {
        return isAdminValue == 1;
    }

    public void setAdmin(boolean admin) {
        this.isAdminValue = admin ? 1 : 0;
    }

    public int getIsAdminValue() {
        return isAdminValue;
    }

    public void setIsAdminValue(int isAdminValue) {
        this.isAdminValue = isAdminValue;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}