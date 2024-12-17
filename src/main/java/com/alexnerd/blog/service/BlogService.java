package com.alexnerd.blog.service;

import com.alexnerd.blog.entity.Content;
import com.alexnerd.blog.service.adapters.TitleNormalizer;
import com.alexnerd.blog.service.enums.ContentType;
import com.alexnerd.blog.service.enums.Lang;
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

    public String getPost(Lang lang, ContentType type, String date, String title) {
        String fileName = titleNormalizer.normalize(title);
        try {
            String stringified = storageService.getContent(lang, type, date, fileName);
            return renderService.renderPost(new Content(type, stringified));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public String getLast(Lang lang, ContentType type, int limit) {
        List<String> lastContent = storageService.getLastContent(lang, type, limit);
        return lastContent.stream()
                .map(s -> new Content(type, s))
                .map(content -> renderService.renderLast(content))
                .collect(Collectors.joining());
    }
}
