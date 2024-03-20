package com.my.greentoon.Model;

public class Like {
    private String toonId; // ID của nội dung (truyện hoặc chương)
    private String userId; // ID của người dùng đã thích
    private boolean isLiked; // Trạng thái đã thích hay chưa

    public Like() {
        // Constructor mặc định
    }

    public Like(String toonId, String userId, boolean isLiked) {
        this.toonId = toonId;
        this.userId = userId;
        this.isLiked = isLiked;
    }

    // Getters và setters
}
