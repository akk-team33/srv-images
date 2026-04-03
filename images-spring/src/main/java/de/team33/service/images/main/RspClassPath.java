package de.team33.service.images.main;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

class RspClassPath extends Responder {

    private final String name;
    private final MediaType mediaType;

    RspClassPath(final Request request, final String name, final MediaType mediaType) {
        super(request);
        this.name = name;
        this.mediaType = mediaType;
    }

    @Override
    final ResponseEntity<?> response() {
        final ClassPathResource resource = new ClassPathResource(name, RspClassPath.class);
        return ResponseEntity.ok()
                             .contentType(mediaType)
                             .body(resource);
    }
}
