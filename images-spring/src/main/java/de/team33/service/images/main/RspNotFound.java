package de.team33.service.images.main;

import org.springframework.http.ResponseEntity;

class RspNotFound extends Responder {

    RspNotFound(final Request request) {
        super(request);
    }

    @Override
    final ResponseEntity<?> response() {
        return notFound();
    }
}
