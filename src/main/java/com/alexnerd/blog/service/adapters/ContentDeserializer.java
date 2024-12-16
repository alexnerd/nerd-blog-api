package com.alexnerd.blog.service.adapters;

import com.alexnerd.blog.entity.Content;
import com.alexnerd.blog.service.enums.ContentType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ContentDeserializer {
    private final ObjectMapper mapper = new ObjectMapper();

    public Content deserialize(String content, ContentType type) throws JsonProcessingException {
        return switch (type) {
            case POST, ARTICLE, ARTICLE_TEASER -> mapper.readValue(content, Content.class);
            case LAST_ARTICLES -> {
                Content post = mapper.readValue(content, Content.class);
                yield new Content(post.title(), ContentType.LAST_ARTICLES, null, null,
                        post.createDate(), post.link());
            }
            default -> throw new IllegalStateException("Unsupported content type: " + type);
        };
    }
}
