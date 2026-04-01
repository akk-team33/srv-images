package de.team33.service.images.main;

import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;

class RspImage extends Responder {

    private final Path path;
    private final MediaType mediaType;

    RspImage(final Request request) {
        super(request);
        this.path = request.locator().resourcePath();
        this.mediaType = request.imageType().mediaType();
    }

    @Override
    final ResponseEntity<?> response() {
        if (Files.isRegularFile(path)) {
            try {
                return ResponseEntity.ok()
                                     .contentType(mediaType)
                                     .body(new UrlResource(path.toUri()));
            } catch (final MalformedURLException e) {
                throw new IllegalArgumentException("Illegal path: %s".formatted(path), e);
            }
        }
        return notFound();
    }
}
