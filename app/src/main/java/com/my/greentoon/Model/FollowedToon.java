package com.my.greentoon.Model;

public class FollowedToon {
    private String userId; // ID của người dùng
    private String toonId; // ID của truyện
    private boolean isFavorite; // Trạng thái truyện yêu thích của người dùng

    public FollowedToon() {
        // Constructor mặc định
    }

    public FollowedToon(String userId, String toonId, boolean isFavorite) {
        this.userId = userId;
        this.toonId = toonId;
        this.isFavorite = isFavorite;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getToonId() {
        return toonId;
    }

    public void setToonId(String toonId) {
        this.toonId = toonId;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }
}
