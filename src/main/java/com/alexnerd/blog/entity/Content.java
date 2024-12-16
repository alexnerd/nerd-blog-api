package com.alexnerd.blog.entity;

import com.alexnerd.blog.service.enums.ContentType;

public record Content(String title, ContentType type, String content, String rubric, String createDate, String link) {
}
