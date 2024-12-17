package com.alexnerd.blog.entity;

import com.alexnerd.blog.service.enums.ContentType;

public record Content(ContentType type, String content) {
}
