package com.my.greentoon.Model;

public class Toon {

    private String userId;
    private String toonCover;
    private String toonName;
    private String toonDes;

    // Constructors, getters, setters

    public Toon() {
        // Default constructor required for calls to DataSnapshot.getValue(Toon.class)
    }

    public Toon(String userId, String toonName, String toonDes) {
        this.userId = userId;
        this.toonName = toonName;
        this.toonDes = toonDes;
    }

    public Toon(String userId, String toonCover, String toonName, String toonDes) {
        this.userId = userId;
        this.toonCover = toonCover;
        this.toonName = toonName;
        this.toonDes = toonDes;
    }

    public String getToonName() {
        return toonName;
    }

    public void setToonName(String toonName) {
        this.toonName = toonName;
    }

    public String getToonDes() {
        return toonDes;
    }

    public void setToonDes(String toonDes) {
        this.toonDes = toonDes;
    }

    public String getToonCover() {
        return toonCover;
    }

    public void setToonCover(String toonCover) {
        this.toonCover = toonCover;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
