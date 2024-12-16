package com.alexnerd.blog.service;

import com.alexnerd.blog.entity.Content;
import com.alexnerd.blog.service.templates.TemplateProvider;
import com.alexnerd.blog.service.compilers.CompilerJS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RenderService {

    @Autowired
    private CompilerJS compilerJS;

    public String renderPost(Content content) {
        String template = TemplateProvider.get(content);
        return compilerJS.compile(template, content.content());
    }

    public String renderLast(Content content) {
        String template = TemplateProvider.get(content);
        return compilerJS.compile(template, content.content());
    }
}
