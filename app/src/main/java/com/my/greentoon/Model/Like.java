package com.my.greentoon.Model;

public class Like {
    private String contentId; // ID của nội dung (truyện hoặc chương)
    private String userId; // ID của người dùng đã thích
    private boolean isLiked; // Trạng thái đã thích hay chưa

    public Like() {
        // Constructor mặc định
    }

    public Like(String contentId, String userId, boolean isLiked) {
        this.contentId = contentId;
        this.userId = userId;
        this.isLiked = isLiked;
    }

    // Getters và setters
}
