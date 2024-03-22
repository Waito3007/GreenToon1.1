package com.my.greentoon.Model;

import java.util.List;
import java.util.Map;

public class Toon {
    private String toonId;
    private String toonCover;
    private String toonName;
    private String toonDes;
    private int viewCount;
    private List<Chapter> chapters;
    private Map<String, Boolean> genres; // Danh sách các thể loại của bộ truyện
    public String toString() {return "" + toonName;}
    public Toon() {
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

    public String getToonId() {
        return toonId;
    }

    public void setToonId(String toonId) {
        this.toonId = toonId;
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

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public List<Chapter> getChapters() {
        return chapters;
    }

    public void setChapters(List<Chapter> chapters) {
        this.chapters = chapters;
    }

    public Map<String, Boolean> getGenres() {
        return genres;
    }

    public void setGenres(Map<String, Boolean> genres) {
        this.genres = genres;
    }
}
