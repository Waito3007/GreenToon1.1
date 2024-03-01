package com.my.greentoon.Model;

import java.util.List;

public class Chapter {
    private String chapterId;
    private String chapterName;
    private String chapterTitle;
    private List<String> listImgChapter; // Danh sách URL của các hình ảnh

    public Chapter() {
        // Constructor mặc định
    }

    public Chapter(String chapterId, String chapterName, String chapterTitle, List<String> listImgChapter) {
        this.chapterId = chapterId;
        this.chapterName = chapterName;
        this.chapterTitle = chapterTitle;
        this.listImgChapter = listImgChapter;
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
}
