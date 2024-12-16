package com.alexnerd.blog.service.enums;

public enum ContentType {
    ARTICLE("articles"),
    ARTICLE_TEASER("posts"),
    LAST_ARTICLES("articles"),
    POST("posts");

    private final String baseDir;

    ContentType(String baseDir) {
        this.baseDir = baseDir;
    }

    public String getBaseDir() {
        return baseDir;
    }
}
