package com.my.greentoon.Model;

public class ViewCount {
    private int count;

    public ViewCount() {
        // Default constructor required for Firebase
    }

    public ViewCount(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}