package com.my.greentoon.Model;

import java.util.List;

public class Chapter {
    private String chapterId;
    private String chapterName;
    private String chapterTitle;
    private List<String> listImgChapter; // Danh sách URL của các hình ảnh
    private String toonId; // ID
    private int numChapter; // Dùng cho việc next hay back chap trong ChapterActivity

    public Chapter() {
        // Constructor mặc định
    }

    public Chapter(String chapterId, String chapterName, String chapterTitle, List<String> listImgChapter, int numChapter) {
        this.chapterId = chapterId;
        this.chapterName = chapterName;
        this.chapterTitle = chapterTitle;
        this.listImgChapter = listImgChapter;
        this.numChapter = numChapter;
    }

    public String getChapterId() {
        return chapterId;
    }

    public void setChapterId(String chapterId) {
        this.chapterId = chapterId;
    }

    public String getChapterName() {
        return chapterName;
    }

    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
    }

    public String getChapterTitle() {
        return chapterTitle;
    }

    public void setChapterTitle(String chapterTitle) {
        this.chapterTitle = chapterTitle;
    }

    public List<String> getListImgChapter() {
        return listImgChapter;
    }

    public void setListImgChapter(List<String> listImgChapter) {
        this.listImgChapter = listImgChapter;
    }

    public String getToonId() {
        return toonId;
    }

    public void setToonId(String toonId) {
        this.toonId = toonId;
    }

    public int getNumChapter() {
        return numChapter;
    }

    public void setNumChapter(int numChapter) {
        this.numChapter = numChapter;
    }
}
