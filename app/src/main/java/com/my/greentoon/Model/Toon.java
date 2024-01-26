package com.my.greentoon.Model;

import java.io.Serializable;

public class Toon implements Serializable {
    private String userId;
    private String storyName;
    private String storyGenre;
    private String storyDescription;
    private String storyBookCover;

    public Toon() {
        // Default constructor required for calls to DataSnapshot.getValue(Toon.class)
    }

    public Toon(String userId, String storyName, String storyGenre, String storyDescription, String storyBookCover) {
        this.userId = userId;
        this.storyName = storyName;
        this.storyGenre = storyGenre;
        this.storyDescription = storyDescription;
        this.storyBookCover = storyBookCover;
    }

    public String getUserId() {
        return userId;
    }

    public String getStoryName() {
        return storyName;
    }

    public void setStoryName(String storyName) {
        this.storyName = storyName;
    }

    public String getStoryGenre() {
        return storyGenre;
    }

    public void setStoryGenre(String storyGenre) {
        this.storyGenre = storyGenre;
    }

    public String getStoryDescription() {
        return storyDescription;
    }

    public void setStoryDescription(String storyDescription) {
        this.storyDescription = storyDescription;
    }

    public String getStoryBookCover() {
        return storyBookCover;
    }

    public void setStoryBookCover(String storyBookCover) {
        this.storyBookCover = storyBookCover;
    }
}
