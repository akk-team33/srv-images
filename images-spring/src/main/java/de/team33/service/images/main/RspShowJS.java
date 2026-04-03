package de.team33.service.images.main;

import org.springframework.http.MediaType;

class RspShowJS extends RspClassPath {

    RspShowJS(final Request request) {
        super(request, "show.js", MediaType.valueOf("application/javascript"));
    }
}
