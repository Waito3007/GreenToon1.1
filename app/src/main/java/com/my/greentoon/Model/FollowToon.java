package com.my.greentoon.Model;

public class FollowToon {
    private String userId;
    private String toonId;

    public FollowToon() {
       // khoi tạo sẳn
    }

    public FollowToon(String userId, String toonId) {
        this.userId = userId;
        this.toonId = toonId;
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
}
