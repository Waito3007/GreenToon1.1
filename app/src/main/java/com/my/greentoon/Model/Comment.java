package com.my.greentoon.Model;

public class Comment {
    private String commentId;
    private String userId;
    private String userName; // Thêm trường userName
    private String commentText;

    public Comment() {
        // Constructor mặc định
    }

    public Comment(String userId,String userName, String commentText) {
        this.userId = userId;
        this.commentText = commentText;
    }

    public Comment(String commentId, String userId, String userName, String commentText) {
        this.commentId = commentId;
        this.userId = userId;
        this.userName = userName;
        this.commentText = commentText;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }
}
