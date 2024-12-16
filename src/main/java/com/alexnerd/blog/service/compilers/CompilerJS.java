package com.alexnerd.blog.service.compilers;

import jakarta.annotation.PostConstruct;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.PolyglotException;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

@Service
public class CompilerJS {
    private Source handleBars;

    private final String JAVA_SCRIPT = "js";

    @PostConstruct
    public void init() {
        try (Reader handlebarsReader = this.loadHandlebars()) {
            handleBars = Source.newBuilder(JAVA_SCRIPT, handlebarsReader, "Handlebars").build();
        } catch (Exception ex) {
            throw new IllegalStateException("Cannot load Handlebars", ex);
        }
    }

    public String compile(String templateContent, String postContent) {
        try (Context context = Context.create(JAVA_SCRIPT)) {
            Value bindings = context.getBindings(JAVA_SCRIPT);
            context.eval(handleBars);
            bindings.putMember("templateContent", templateContent);
            bindings.putMember("postContent", postContent);
            return context.eval(JAVA_SCRIPT, this.getCompileLogic()).asString();
        } catch (PolyglotException ex) {
            throw new RuntimeException("JS compiling error: " + ex.getMessage(), ex);
        }
    }

    private String getCompileLogic() {
        return """
                const postAsJSON = JSON.parse(postContent);
                const compiledTemplate = Handlebars.compile(templateContent);
                compiledTemplate(postAsJSON);
                """;
    }

    private Reader loadHandlebars() throws IOException {
        try (InputStream stream = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("handlebars-v4.7.7.js")) {
            return new InputStreamReader(stream);
        }
    }
}

