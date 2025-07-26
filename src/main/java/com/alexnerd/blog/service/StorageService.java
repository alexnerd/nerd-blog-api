package com.alexnerd.blog.service;

import com.alexnerd.blog.service.enums.ContentType;
import com.alexnerd.blog.service.enums.Lang;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
public class StorageService {
    @Value("${nerdblog.root.storage.dir}")
    private String baseDir;

    private Path storageDirectoryPath;

    private final int SEARCH_DEPTH = 3;

    private final String FILE_EXTENSION = ".json";

    @PostConstruct
    public void init() {
        this.storageDirectoryPath = Path.of(baseDir);
    }

    String getContent(Lang lang, ContentType type, String date, String fileName) throws FileNotFoundException {
        Path contentPath = this.constructContentPath(lang, type, date, fileName);

        if (Files.notExists(contentPath) || !Files.isRegularFile(contentPath)) {
            throw new FileNotFoundException("Can't fetch content: " + fileName);
        }

        return this.readContent(contentPath);
    }

    List<String> getLastContent(Lang lang, ContentType contentType, int limit) {
        Path contentPath = this.getContentDirectoryPath(lang, contentType);

        return this.getLastContentPath(contentPath, SEARCH_DEPTH, limit).stream()
                .map(this::readContent)
                .toList();
    }

    private Path getContentDirectoryPath(Lang lang, ContentType contentType) {
        return Path.of(baseDir)
                .resolve(lang.toString().toLowerCase())
                .resolve(contentType.getBaseDir());
    }

    private List<Path> getLastContentPath(Path currentDir, int searchDepth, int limit) {
        List<Path> lastCreated = new ArrayList<>();
        List<Path> directories = this.getDirectories(currentDir);
        if (searchDepth != 0) {
            --searchDepth;
            for (Path directory : directories) {
                lastCreated.addAll(getLastContentPath(directory, searchDepth, limit - lastCreated.size()));
                if (lastCreated.size() == limit) {
                    break;
                }
            }
        } else {
            List<Path> content = this.getFiles(currentDir);
            lastCreated.addAll(content.stream()
                    .limit(limit - lastCreated.size())
                    .toList());
        }
        return lastCreated;
    }

    private List<Path> getDirectories(Path path) {
        try (Stream<Path> pathStream = Files.list(path)) {
            return pathStream.filter(Files::isDirectory)
                    .sorted((Path p1, Path p2) -> Integer.valueOf(p2.getFileName().toString())
                            .compareTo(Integer.valueOf(p1.getFileName().toString())))
                    .toList();
        } catch (IOException ex) {
            throw new RuntimeException("Can't get directories from path: " + path, ex);
        }
    }

    private List<Path> getFiles(Path path) {
        try (Stream<Path> pathStream = Files.list(path)) {
            return pathStream.filter(Files::isRegularFile)
                    .filter(p -> p.getFileName().toString().endsWith(FILE_EXTENSION))
                    .sorted((p1, p2) -> this.getCreationTime(p2).compareTo(this.getCreationTime(p1)))
                    .toList();
        } catch (IOException ex) {
            throw new RuntimeException("Can't get files from path: " + path, ex);
        }
    }

    private FileTime getCreationTime(Path path) {
        try {
            return Files.readAttributes(path, BasicFileAttributes.class).creationTime();
        } catch (IOException ex) {
            throw new RuntimeException("Can't read creation time from: " + path.getFileName(), ex);
        }
    }

    private String readContent(Path contentPath) {
        try {
            return Files.readString(contentPath);
        } catch (IOException ex) {
            throw new RuntimeException("Can't read content from file: " + contentPath.getFileName(), ex);
        }
    }

    private Path constructContentPath(Lang lang, ContentType type, String date, String fileName) {
        Path contentPath = storageDirectoryPath
                .resolve(lang.toString().toLowerCase())
                .resolve(type.getBaseDir());
        String[] split = date.split("-");
        for (String s : split) {
            contentPath = contentPath.resolve(s);
        }
        return contentPath.resolve(fileName + FILE_EXTENSION);
    }
}
