package com.my.greentoon.Model;

public class User {
    private String userId;
    private String email;
    private String password;
    private String avatarUser;
    private String nameUser;
    private boolean isAdmin; // phan quyen
    public User() {
        // Cần phải có constructor không tham số để Firebase có thể chuyển đổi dữ liệu từ database
    }
    public String toString() {
        return nameUser; // Trả về tên người dùng khi gọi toString()
    }
    public String userId() {
        return nameUser;
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
    public String getAvatarUser() {
        return avatarUser;
    }
    public void setAvatarUser(String avatarUser) {
        this.avatarUser = avatarUser;
    }
    public String getNameUser() {
        return nameUser;
    }
    public void setNameUser(String nameUser) {
        this.nameUser = nameUser;
    }

    //phanquyen
    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }
}
//toon

