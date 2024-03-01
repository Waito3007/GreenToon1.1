package com.my.greentoon.Model;

public class Comment {
    private String contentId; // ID của nội dung (truyện hoặc chương)
    private String userId; // ID của người dùng đã bình luận
    private String commentText; // Nội dung bình luận

    public Comment() {
        // Constructor mặc định
    }

    public Comment(String contentId, String userId, String commentText) {
        this.contentId = contentId;
        this.userId = userId;
        this.commentText = commentText;
    }

    // Getters và setters
}
