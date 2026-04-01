package de.team33.service.images.main;

import org.springframework.http.ResponseEntity;

import static java.lang.System.Logger.Level.INFO;

abstract class Responder {

    private static final System.Logger LOGGER = System.getLogger(Responder.class.getCanonicalName());

    private final Request request;

    Responder(final Request request) {
        this.request = request;
    }

    abstract ResponseEntity<?> response();

    final ResponseEntity<?> notFound() {
        LOGGER.log(INFO, () -> "Not Found: " + request.locator().requestUri());
        return ResponseEntity.notFound().build();
    }
}
