package com.alexnerd.blog.service.templates;

import com.alexnerd.blog.entity.Content;

public interface TemplateProvider {
    public static String get(Content content) {
        return switch (content.type()) {
            case POST, ARTICLE -> TemplateCollection.POST;
            case ARTICLE_TEASER -> TemplateCollection.ARTICLE_TEASER;
            case LAST_ARTICLES -> TemplateCollection.LAST_ARTICLES;
            default -> throw new RuntimeException("Unsupported content type");
        };
    }
}
