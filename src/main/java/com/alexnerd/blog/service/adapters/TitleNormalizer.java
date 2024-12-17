package com.alexnerd.blog.service.adapters;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TitleNormalizer {

    @Value("${nerdblog.title.separator}")
    private String titleSeparator;

    private int codePointSeparator;

    @PostConstruct
    public void init() {
        this.codePointSeparator = this.titleSeparator.codePoints()
                .findFirst()
                .orElseThrow();
    }

    public String normalize(String title) {
        return title.codePoints()
                .map(this::replaceDigitOrLetter)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    private int replaceDigitOrLetter(int codePoint) {
        if (Character.isLetterOrDigit(codePoint)) {
            return codePoint;
        } else {
            return codePointSeparator;
        }
    }
}
