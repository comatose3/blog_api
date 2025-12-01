package com.example.BlogAPI.user;

public enum UserRole {
    ROLE_USER,
    ROLE_ADMIN;

    // Можно добавить методы для удобства
    public String getAuthority() {
        return this.name();
    }
}
