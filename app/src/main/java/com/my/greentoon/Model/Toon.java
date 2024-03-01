package com.my.greentoon.Model;

import java.util.List;

public class Toon {
    private String toonId; // Thay userId thành toonId
    private String toonCover;
    private String toonName;
    private String toonDes;
    private int viewCount; // Số lượt xem của toon
    private List<Chapter> chapters; // Danh sách các chapter thuộc toon này

    // Constructors, getters, setters

    public Toon() {
        // Default constructor required for calls to DataSnapshot.getValue(Toon.class)
    }

    public Toon(String toonId, String toonName, String toonDes) {
        this.toonId = toonId;
        this.toonName = toonName;
        this.toonDes = toonDes;
    }

    public Toon(String toonId, String toonCover, String toonName, String toonDes) {
        this.toonId = toonId;
        this.toonCover = toonCover;
        this.toonName = toonName;
        this.toonDes = toonDes;
    }

    // Getter và setter cho viewCount

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    // Getter và setter cho chapters

    public List<Chapter> getChapters() {
        return chapters;
    }

    public void setChapters(List<Chapter> chapters) {
        this.chapters = chapters;
    }

    // Thay đổi userId thành toonId

    public String getToonId() {
        return toonId; // hoặc bất kỳ trường dữ liệu nào thích hợp để đại diện cho toonId
    }

    // Getter và setter cho các thuộc tính khác...

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
