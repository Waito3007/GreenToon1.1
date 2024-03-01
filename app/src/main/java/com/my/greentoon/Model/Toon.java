package com.my.greentoon.Model;

import java.util.List;

public class Toon {
    private String userId;
    private String toonCover;
    private String toonName;
    private String toonDes;
    private List<Chapter> chapters; // Danh sách các chapter thuộc toon này

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

    // Các getters và setters cho thuộc tính chapters

    public List<Chapter> getChapters() {
        return chapters;
    }

    public void setChapters(List<Chapter> chapters) {
        this.chapters = chapters;
    }

    public String getToonId() {
        return userId; // hoặc bất kỳ trường dữ liệu nào thích hợp để đại diện cho toonId
    }
    // Các getters và setters cho các thuộc tính khác...

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getToonCover() {
        return toonCover;
    }

    public void setToonCover(String toonCover) {
        this.toonCover = toonCover;
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
}
