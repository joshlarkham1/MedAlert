package com.medalert.Activity;

public class Guide {

    private String title;
    private String description;
    private String videoUrl;
    private String articleUrl;

    public Guide(String title, String description, String videoUrl, String articleUrl) {
        this.title = title;
        this.description = description;
        this.videoUrl = videoUrl;
        this.articleUrl = articleUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public String getArticleUrl() {
        return articleUrl;
    }
}
