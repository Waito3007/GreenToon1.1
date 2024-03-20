package com.my.greentoon.Model;

public class Status {
    private String statusId;
    private String userId;
    private String statusTitle;
    private String statusContent; // URL của ảnh
    public Status() {
        // Constructor mặc định cần thiết cho Firebase
    }

    public Status(String statusId, String userId, String statusTitle, String statusContent) {
        this.statusId = statusId;
        this.userId = userId;
        this.statusTitle = statusTitle;
        this.statusContent = statusContent;
    }

    public String getStatusId() {
        return statusId;
    }

    public void setStatusId(String statusId) {
        this.statusId = statusId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStatusTitle() {
        return statusTitle;
    }

    public void setStatusTitle(String statusTitle) {
        this.statusTitle = statusTitle;
    }

    public String getStatusContent() {
        return statusContent;
    }

    public void setStatusContent(String statusContent) {
        this.statusContent = statusContent;
    }
}
