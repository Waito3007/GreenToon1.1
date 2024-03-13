package com.my.greentoon.Model;

public class ImageItem {
    private int imageResId;

    private String tenTruyen;

    public ImageItem(int imageResId, String tenTruyen) {
        this.imageResId = imageResId;
        this.tenTruyen = tenTruyen;
    }

    public int getImageResId() {
        return imageResId;
    }

    public String getTenTruyen() {
        return tenTruyen;
    }}

