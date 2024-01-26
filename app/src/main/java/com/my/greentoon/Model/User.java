package com.my.greentoon.Model;

public class User {
    private String userId;
    private String email;
    private String password;
    public User() {
        // Cần phải có constructor không tham số để Firebase có thể chuyển đổi dữ liệu từ database
    }

    public User(String userId, String email) {
        this.userId = userId;
        this.email = email;
        this.password= password;
    }

    public String getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }
    public String getPassword() {
        return password;
    }
}
