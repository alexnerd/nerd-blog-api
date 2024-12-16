package com.alexnerd.blog.controller;

import com.alexnerd.blog.service.BlogService;
import com.alexnerd.blog.service.enums.ContentType;
import com.alexnerd.blog.service.enums.Lang;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BlogController {

    @Autowired
    private BlogService blogService;

    @GetMapping("post/{date}/{title}")
    public String getPost(@RequestParam(name = "lang", defaultValue = "RU") Lang lang,
                          @RequestParam(name = "type", defaultValue = "POST") ContentType type,
                          @PathVariable("date") String date,
                          @PathVariable("title") String title) {
        return blogService.getPost(lang, type, date, title);
    }

    @GetMapping("last")
    public String getLast(@RequestParam(name = "lang", defaultValue = "RU") Lang lang,
                          @RequestParam(name = "type", defaultValue = "POST") ContentType type,
                          @RequestParam("limit") @Min(1) @Max(10) int limit) {
        return blogService.getLast(lang, type, limit);
    }
}
