package com.alexnerd.blog.service;

import com.alexnerd.blog.entity.Content;
import com.alexnerd.blog.service.adapters.ContentDeserializer;
import com.alexnerd.blog.service.adapters.TitleNormalizer;
import com.alexnerd.blog.service.enums.ContentType;
import com.alexnerd.blog.service.enums.Lang;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BlogService {

    @Autowired
    private StorageService storageService;

    @Autowired
    private RenderService renderService;

    @Autowired
    private TitleNormalizer titleNormalizer;

    private ContentDeserializer deserializer;

    @PostConstruct
    public void init() {
        deserializer = new ContentDeserializer();
    }

    public String getPost(Lang lang, ContentType type, String date, String title) {
        String fileName = titleNormalizer.normalize(title);
        try {
            String stringified = storageService.getContent(lang, type, date, fileName);
            Content content = deserializer.deserialize(stringified, type);
            return renderService.renderPost(content);
        } catch (FileNotFoundException | JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public String getLast(Lang lang, ContentType type, int limit) {
        List<String> lastContent = storageService.getLastContent(lang, type, limit);
        return lastContent.stream().map(s -> {
                    try {
                        return deserializer.deserialize(s, type);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                }).map(content -> renderService.renderLast(content))
                .collect(Collectors.joining());
    }
}
