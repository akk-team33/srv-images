package de.team33.service.images.main;

import org.springframework.http.ResponseEntity;

class RspBadRequest extends Responder {

    RspBadRequest(final Request request) {
        super(request);
    }

    @Override
    final ResponseEntity<?> response() {
        return badRequest();
    }
}
