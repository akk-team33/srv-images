package de.team33.service.images.main;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.stream.Stream;

abstract class RequestBase {

    private static final System.Logger LOGGER = System.getLogger(RequestBase.class.getCanonicalName());
    private static final URI HOST_ROOT = URI.create("/");

    private final HttpServletRequest httpRequest;
    private final URI requestUri;

    RequestBase(final HttpServletRequest httpRequest) {
        this.httpRequest = httpRequest;
        this.requestUri = URI.create(httpRequest.getRequestURL().toString());
    }

    final HttpServletRequest httpRequest() {
        return httpRequest;
    }

    final URI requestUri() {
        return requestUri;
    }

    final ResponseEntity<?> classPathResponse(final MediaType mediaType, final String path) {
        return ResponseEntity.ok()
                             .contentType(mediaType)
                             .body(new ClassPathResource(path, RequestBase.class));
    }

    final <T> ResponseEntity<T> jsonResponse(final T value) {
        return ResponseEntity.ok()
                             .contentType(MediaType.APPLICATION_JSON)
                             .body(value);
    }

    final ResponseEntity<?> notFound() {
        LOGGER.log(System.Logger.Level.INFO, () -> "not found: %s".formatted(requestUri));
        return ResponseEntity.notFound().build();
    }

    final boolean uriEndsWith(final String... names) {
        return Stream.of(names)
                     .map(HOST_ROOT::resolve)
                     .anyMatch(this::uriEndsWith);
    }

    private boolean uriEndsWith(final URI name) {
        return requestUri.toString().endsWith(name.toString());
    }

    final boolean isIndexCSS() {
        return uriEndsWith("index.css");
    }

    final boolean isIndexJS() {
        return uriEndsWith("index.js");
    }

    final boolean isIndexHTML() {
        return uriEndsWith("", "index", "index.htm", "index.html");
    }

    final boolean isIndexJson() {
        return uriEndsWith("index.json");
    }

    final ResponseEntity<?> toIndexCSS() {
        return classPathResponse(MediaType.valueOf("text/css"), "index.css");
    }

    final ResponseEntity<?> toIndexJS() {
        return classPathResponse(MediaType.valueOf("application/javascript"), "index.js");
    }

    final ResponseEntity<?> toIndexHTML() {
        return classPathResponse(MediaType.TEXT_HTML, "index.html");
    }

    abstract ResponseEntity<?> response();
}
